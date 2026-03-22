package com.example.atividade_1_sem;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.listView);

        SharedPreferences prefs = requireActivity().getSharedPreferences(
                "data",
                getContext().MODE_PRIVATE
        );

        if (!prefs.contains("lines")) {
            InputStream is = getResources().openRawResource(R.raw.lines);
            Scanner scanner = new Scanner(is).useDelimiter("\\A");
            String json = scanner.hasNext() ? scanner.next() : "";

            prefs.edit().putString("lines", json).apply();
        }

        List<Line> lineList = new ArrayList<>();

        try {
            String json = prefs.getString("lines", "[]");
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Line l = new Line();
                l.origin = obj.getString("origin");
                l.target = obj.getString("target");
                l.start = obj.getString("start");
                l.end = obj.getString("end");
                l.interval = obj.getString("interval");

                lineList.add(l);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        LineAdapter adapter = new LineAdapter(getContext(), lineList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((
             parent,
             view1,
             position,
             id
        ) -> {

            Line linha = lineList.get(position);

            Bundle bundle = new Bundle();
            bundle.putString("origin", linha.origin);
            bundle.putString("target", linha.target);
            bundle.putString("start", linha.start);
            bundle.putString("end", linha.end);
            bundle.putString("interval", linha.interval);

            NavHostFragment.findNavController(MainFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}