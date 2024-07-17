package com.uc3m.InmunoApp.PatientsList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uc3m.InmunoApp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        // Añadir lista de pacientes
        patientList = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients");

        if (user != null) {
            final String userEmail = user.getEmail();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    patientList.clear();

                    for (DataSnapshot patientSnapshot : dataSnapshot.getChildren()) {
                        String name = patientSnapshot.child("Name").getValue(String.class);
                        String gender = patientSnapshot.child("Gender").getValue(String.class);
                        Integer age = getIntegerValue(patientSnapshot.child("Age").getValue());
                        Integer height = getIntegerValue(patientSnapshot.child("Height").getValue());
                        Integer weight = getIntegerValue(patientSnapshot.child("Weight").getValue());

                        // Validar si los datos esenciales están presentes
                        if (name == null || gender == null || age == null || height == null || weight == null) {
                            continue; // Saltar este paciente si falta información esencial
                        }

                        // Extraer la información del doctor
                        DataSnapshot doctorSnapshot = patientSnapshot.child("Medical information").child("Oncology team contact").child("Doctor 1");
                        String doctorName = doctorSnapshot.child("Name").getValue(String.class);
                        String doctorMail = doctorSnapshot.child("Mail").getValue(String.class);
                        Integer doctorPhone = getIntegerValue(doctorSnapshot.child("Phone").getValue());

                        // Validar si los datos del doctor están presentes
                        if (doctorName == null || doctorMail == null || doctorPhone == null) {
                            continue; // Saltar este paciente si falta información del doctor
                        }

                        Doctor doctor = new Doctor(doctorName, doctorMail, doctorPhone);

                        // Crear un nuevo objeto Patient y añadirlo a la lista
                        Patient patient = new Patient(name, gender, age, height, weight, doctor);

                        assert userEmail != null;
                        if(userEmail.equals(doctorMail)){
                            patientList.add(patient);
                        }
                    }

                    patientAdapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar los errores de lectura de la base de datos
                    Log.e("DatabaseError", databaseError.getMessage());
                }
            });
        }

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

    private Integer getIntegerValue(Object value) {
        if (value instanceof Long) {
            return ((Long) value).intValue();
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}