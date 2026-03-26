package com.example.atividade_1_sem;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.atividade_1_sem.databinding.FragmentMapBinding;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;

    private final android.os.Handler handler = new android.os.Handler();
    private Runnable timeRunnable;

    private List<GeoPoint> routePoints = new ArrayList<>();

    private Marker busMarker;

    private MyLocationNewOverlay locationOverlay;

    private String origin, target, start, end, interval;
    private GeoPoint originGeo, targetGeo;

    private boolean isOriginToTarget;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkLocationPermission();
        initializeMap();
        setupUserLocation();

        binding.buttonCancel.setOnClickListener(v -> navigateToList());

        Bundle args = getArguments();
        if (args != null && args.containsKey("origin")) {
            setupRoute(args);
        }
    }

    private void navigateToList() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment);
    }

    private void checkLocationPermission() {
        if (requireContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void initializeMap() {
        Configuration.getInstance().load(
                requireContext(),
                requireContext().getSharedPreferences("osmdroid", 0)
        );
        binding.map.setMultiTouchControls(true);
    }

    private void setupUserLocation() {
        locationOverlay = new MyLocationNewOverlay(
            new GpsMyLocationProvider(requireContext()),
            binding.map
        );

        locationOverlay.enableMyLocation();

        binding.map.getOverlays().add(locationOverlay);

        binding.buttonMyLocation.setOnClickListener(v -> {
            GeoPoint myLocation = locationOverlay.getMyLocation();
            if (myLocation != null) {
                binding.map.getController().animateTo(myLocation);
            }
        });
    }

    private void setupRoute(Bundle args) {

        origin = args.getString("origin");
        target = args.getString("target");
        start = args.getString("start");
        end = args.getString("end");
        interval = args.getString("interval");

        double originLat = args.getDouble("originLat");
        double originLon = args.getDouble("originLon");
        double targetLat = args.getDouble("targetLat");
        double targetLon = args.getDouble("targetLon");

        binding.editTextFrom.setText(origin);
        binding.editTextTo.setText(target);

        originGeo = new GeoPoint(originLat, originLon);
        targetGeo = new GeoPoint(targetLat, targetLon);

        addMarkers(origin, originGeo, target, targetGeo);

        fetchAndDrawRoute(originLat, originLon, targetLat, targetLon);
    }

    private void fetchAndDrawRoute(double originLat, double originLon, double targetLat, double targetLon) {

        String url = "https://router.project-osrm.org/route/v1/driving/"
                + originLon + "," + originLat + ";"
                + targetLon + "," + targetLat + ";"
                + originLon + "," + originLat
                + "?overview=full&geometries=geojson";

        new Thread(() -> {
            try {
                URL u = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.connect();

                InputStream is = conn.getInputStream();
                Scanner s = new Scanner(is).useDelimiter("\\A");
                String response = s.hasNext() ? s.next() : "";

                JSONObject json = new JSONObject(response);
                JSONArray coords = json
                        .getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONArray("coordinates");

                List<GeoPoint> points = new ArrayList<>();
                for (int i = 0; i < coords.length(); i++) {
                    JSONArray p = coords.getJSONArray(i);
                    points.add(new GeoPoint(p.getDouble(1), p.getDouble(0)));
                }

                requireActivity().runOnUiThread(() -> {
                    routePoints = points;

                    drawPolyline(points);

                    createBusMarker();
                    startTimeUpdater();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void drawPolyline(List<GeoPoint> points) {
        Polyline line = new Polyline();
        line.setPoints(points);
        line.setWidth(6f);
        line.setColor(Color.BLUE);

        binding.map.getOverlays().add(line);

        BoundingBox box = BoundingBox.fromGeoPoints(points);
        binding.map.zoomToBoundingBox(box, true, 100);

        binding.map.invalidate();
    }

    private void createBusMarker() {
        if (busMarker != null) return;

        busMarker = new Marker(binding.map);
        busMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        busMarker.setIcon(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_directions_bus));

        binding.map.getOverlays().add(busMarker);
    }

    private void startTimeUpdater() {

        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateBusTime();
                updateBusPosition();

                handler.postDelayed(this, 15000);
            }
        };

        handler.post(timeRunnable);
    }

    private void updateBusTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date startTime = sdf.parse(start);
            Date endTime = sdf.parse(end);

            Calendar calNow = Calendar.getInstance();
            Calendar calStart = Calendar.getInstance();
            Calendar calEnd = Calendar.getInstance();

            calStart.setTime(startTime);
            calEnd.setTime(endTime);

            calStart.set(calNow.get(Calendar.YEAR), calNow.get(Calendar.MONTH), calNow.get(Calendar.DAY_OF_MONTH));

            int dayOffset = startTime.getTime() >= endTime.getTime() ? 1 : 0;
            calEnd.set(calNow.get(Calendar.YEAR), calNow.get(Calendar.MONTH), calNow.get(Calendar.DAY_OF_MONTH) + dayOffset);

            if (calNow.before(calStart) || calNow.after(calEnd)) {
                binding.textTime.setText("Fora de operação");
                binding.textTime.setTextColor(Color.GRAY);
                return;
            }

            int intervalMin = Integer.parseInt(interval.replaceAll("\\D+", ""));
            long diff = calNow.getTimeInMillis() - calStart.getTimeInMillis();
            long passedMinutes = diff / (1000 * 60);

            isOriginToTarget = (passedMinutes / intervalMin) % 2 == 0;

            long mod = passedMinutes % intervalMin;
            long nextBus = intervalMin - mod;

            if (nextBus == intervalMin) {
                binding.textTime.setText("Ônibus passando agora");
                binding.textTime.setTextColor(Color.GREEN);
            } else {
                binding.textTime.setText("Próximo ônibus em " + nextBus + " min");

                if (nextBus <= 5) binding.textTime.setTextColor(Color.RED);
                else if (nextBus <= 10) binding.textTime.setTextColor(Color.parseColor("#FFA500"));
                else binding.textTime.setTextColor(Color.BLACK);
            }

        } catch (Exception e) {
            binding.textTime.setText("Erro");
        }
    }

    private void updateBusPosition() {
        if (busMarker == null || routePoints.isEmpty()) return;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date startTime = sdf.parse(start);
            Date endTime = sdf.parse(end);

            Calendar calNow = Calendar.getInstance();
            Calendar calStart = Calendar.getInstance();
            Calendar calEnd = Calendar.getInstance();

            calStart.setTime(startTime);
            calEnd.setTime(endTime);

            calStart.set(calNow.get(Calendar.YEAR), calNow.get(Calendar.MONTH), calNow.get(Calendar.DAY_OF_MONTH));

            int dayOffset = startTime.getTime() >= endTime.getTime() ? 1 : 0;
            calEnd.set(calNow.get(Calendar.YEAR), calNow.get(Calendar.MONTH), calNow.get(Calendar.DAY_OF_MONTH) + dayOffset);

            if (calNow.before(calStart) || calNow.after(calEnd)) {
                busMarker.setVisible(false);
                return;
            }

            busMarker.setVisible(true);

            int intervalMin = Integer.parseInt(interval.replaceAll("\\D+", ""));

            long diffMillis = calNow.getTimeInMillis() - calStart.getTimeInMillis();

            long intervalMillis = intervalMin * 60L * 1000L;

            long cycleTime = diffMillis % intervalMillis;

            double progress = cycleTime / (double) intervalMillis;

            int originToTargetIndex = findClosestPointIndex(targetGeo);

            if (originToTargetIndex == -1) return;

            int currentSize = isOriginToTarget
                    ? originToTargetIndex + 1
                    : routePoints.size() - (originToTargetIndex + 1);

            int index = (int) (progress * currentSize);

            if (index >= currentSize) index = currentSize - 1;

            if (!isOriginToTarget)
                index = index + originToTargetIndex;

            GeoPoint position = routePoints.get(index);

            busMarker.setPosition(position);
            binding.map.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int findClosestPointIndex(GeoPoint target) {

        if (routePoints == null || routePoints.isEmpty()) return -1;

        int closestIndex = -1;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < routePoints.size(); i++) {
            GeoPoint point = routePoints.get(i);

            double distance = distanceBetween(
                    target.getLatitude(),
                    target.getLongitude(),
                    point.getLatitude(),
                    point.getLongitude()
            );

            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    private double distanceBetween(double lat1, double lon1, double lat2, double lon2) {

        double R = 6371000; // raio da terra em metros

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private void addMarkers(String originName, GeoPoint origin, String targetName, GeoPoint target) {
        Marker m1 = new Marker(binding.map);
        m1.setPosition(origin);
        m1.setTitle(originName);

        Marker m2 = new Marker(binding.map);
        m2.setPosition(target);
        m2.setTitle(targetName);

        binding.map.getOverlays().add(m1);
        binding.map.getOverlays().add(m2);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.map.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (handler != null && timeRunnable != null) {
            handler.removeCallbacks(timeRunnable);
        }

        binding = null;
    }
}
