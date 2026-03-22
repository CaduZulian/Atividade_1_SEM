package com.example.atividade_1_sem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.atividade_1_sem.databinding.FragmentMapBinding;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            String origin = args.getString("origin");
            String target = args.getString("target");
            String start = args.getString("start");
            String interval = args.getString("interval");

            // 🔹 EXEMPLO: usar nos componentes
            // (troque pelos IDs reais do seu layout)
//            binding.textOrigemDestino.setText(origem + " - " + destino);
//            binding.textHorario.setText("Início: " + inicio);
//            binding.textIntervalo.setText("Intervalo: " + intervalo);
        }

        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(MapFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}