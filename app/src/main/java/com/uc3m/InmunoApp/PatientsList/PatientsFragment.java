package com.uc3m.InmunoApp.PatientsList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.uc3m.InmunoApp.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class PatientsFragment extends Fragment {
    private patientAdapter patientAdapter;
    private List<Patient> patientList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patients, container, false);

        // Inicializar vistas
        Spinner sortSpinner = view.findViewById(R.id.spinnerOrderPatients);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPatients);

        // Añadir pacientes a la lista
        patientList = new ArrayList<>();
        patientList.add(new Patient("Vlad", "Hombre", 45, 172, 70));
        patientList.add(new Patient("Ines", "Mujer", 24, 160, 60));

        // Establecer el adaptador y el administrador de diseño
        patientAdapter = new patientAdapter(getContext(), patientList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(patientAdapter);

        // Establecer el adaptador del spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.orderPatients, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        // Listener para manejar el evento de selección del spinner
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // Nombre (A-Z)
                        patientList.sort(Comparator.comparing(Patient::getName));
                        break;
                    case 1: // Nombre (Z-A)
                        patientList.sort((p1, p2) -> p2.getName().compareTo(p1.getName()));
                        break;
                    case 2: // Edad (Ascendente)
                        patientList.sort(Comparator.comparingInt(Patient::getAge));
                        break;
                    case 3: // Edad (Descendente)
                        patientList.sort((p1, p2) -> p2.getAge() - p1.getAge());
                        break;
                }
                patientAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se requiere acción
            }
        });

        return view;
    }
}