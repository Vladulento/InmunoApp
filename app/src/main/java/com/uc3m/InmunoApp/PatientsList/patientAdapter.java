package com.uc3m.InmunoApp.PatientsList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uc3m.InmunoApp.PatientDetail.patientDetailActivity;
import com.uc3m.InmunoApp.R;

import java.util.List;

public class patientAdapter extends RecyclerView.Adapter<patientAdapter.PatientViewHolder> {

    private final List<Patient> patientList;
    private final Context context;

    public patientAdapter(Context context, List<Patient> patientList) {
        this.context = context;
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patientList.get(position);

        // Establecer los valores de cada paciente en el Adapter
        holder.nameTextView.setText(patient.getName());
        holder.genderTextView.setText(patient.getGender());
        holder.ageTextView.setText(context.getString(R.string.bindAge, patient.getAge()));
        holder.heightTextView.setText(context.getString(R.string.bindHeight, patient.getHeight()));
        holder.weightTextView.setText(context.getString(R.string.bindWeight, patient.getWeight()));

        // Lanzar los valores a la siguiente actividad
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, patientDetailActivity.class);
            intent.putExtra("name", patient.getName());
            intent.putExtra("gender", patient.getGender());
            intent.putExtra("age", patient.getAge());
            intent.putExtra("height", patient.getHeight());
            intent.putExtra("weight", patient.getWeight());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, genderTextView, ageTextView, heightTextView, weightTextView;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar vistas
            nameTextView = itemView.findViewById(R.id.nameTextView);
            genderTextView = itemView.findViewById(R.id.genderTextView);
            ageTextView = itemView.findViewById(R.id.ageTextView);
            heightTextView = itemView.findViewById(R.id.heightTextView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
        }
    }
}