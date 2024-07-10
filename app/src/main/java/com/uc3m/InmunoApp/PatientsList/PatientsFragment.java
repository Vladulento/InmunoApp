package com.uc3m.InmunoApp.PatientsList;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.uc3m.InmunoApp.R;

import java.util.ArrayList;
import java.util.List;


public class PatientsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patients, container, false);

        // Inicializar vistas
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPatients);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Patient> userList = new ArrayList<>();

        // Añadir los usuarios a la lista de pacientes

        userList.add(new Patient("Manuel", "Hombre", 25, 1.75, 70));
        userList.add(new Patient("Julia", "Mujer", 30, 1.60, 60));

        // Manejar el click en la campo del paciente
        patientAdapter userAdapter = new patientAdapter(userList, user -> {

            Intent intent = new Intent(getActivity(), patientDetailActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
        recyclerView.setAdapter(userAdapter);

        return view;
    }
}