package com.example.atividade_1_sem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.atividade_1_sem.databinding.FragmentMainBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    private List<Line> originalList = new ArrayList<>();
    private List<Line> filteredList = new ArrayList<>();
    private LineAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ensureDataIsInitialized();

        originalList = loadLinesFromPreferences();
        filteredList = new ArrayList<>(originalList);

        setupListView();
        setupFilters();
    }

    private void ensureDataIsInitialized() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE);

        if (!prefs.contains("lines")) {
            try {
                InputStream is = getResources().openRawResource(R.raw.lines);
                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String json = scanner.hasNext() ? scanner.next() : "";
                prefs.edit().putString("lines", json).apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<Line> loadLinesFromPreferences() {
        List<Line> lineList = new ArrayList<>();
        SharedPreferences prefs = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        String json = prefs.getString("lines", "[]");

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                lineList.add(parseLineFromJson(array.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lineList;
    }

    private Line parseLineFromJson(JSONObject obj) throws Exception {
        JSONObject originCoords = obj.getJSONObject("origin_coords");
        JSONObject targetCoords = obj.getJSONObject("target_coords");

        Line l = new Line();
        l.origin = obj.getString("origin");
        l.target = obj.getString("target");
        l.start = obj.getString("start");
        l.end = obj.getString("end");
        l.interval = obj.getString("interval");

        l.originCoords = new Line.Coord();
        l.originCoords.lat = originCoords.getDouble("lat");
        l.originCoords.lon = originCoords.getDouble("lon");

        l.targetCoords = new Line.Coord();
        l.targetCoords.lat = targetCoords.getDouble("lat");
        l.targetCoords.lon = targetCoords.getDouble("lon");

        return l;
    }

    private void setupListView() {
        adapter = new LineAdapter(getContext(), filteredList);
        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener((parent, view, position, id) -> {
            Line selectedLine = filteredList.get(position);
            navigateToDetails(selectedLine);
        });
    }

    private void setupFilters() {

        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        };

        binding.editTextFrom.addTextChangedListener(watcher);
        binding.editTextTo.addTextChangedListener(watcher);
    }

    private void applyFilter() {

        String from = binding.editTextFrom.getText().toString().toLowerCase().trim();
        String to = binding.editTextTo.getText().toString().toLowerCase().trim();

        filteredList.clear();

        for (Line line : originalList) {

            boolean matchesFrom = from.isEmpty() ||
                    line.origin.toLowerCase().contains(from);

            boolean matchesTo = to.isEmpty() ||
                    line.target.toLowerCase().contains(to);

            if (matchesFrom && matchesTo) {
                filteredList.add(line);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void navigateToDetails(Line line) {
        Bundle bundle = new Bundle();
        bundle.putString("origin", line.origin);
        bundle.putString("target", line.target);
        bundle.putString("start", line.start);
        bundle.putString("end", line.end);
        bundle.putString("interval", line.interval);
        bundle.putDouble("originLat", line.originCoords.lat);
        bundle.putDouble("originLon", line.originCoords.lon);
        bundle.putDouble("targetLat", line.targetCoords.lat);
        bundle.putDouble("targetLon", line.targetCoords.lon);

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}