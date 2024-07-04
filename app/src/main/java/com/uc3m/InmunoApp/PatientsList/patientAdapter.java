package com.uc3m.InmunoApp.PatientsList;  //

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uc3m.InmunoApp.R;

import java.util.List;

public class patientAdapter extends RecyclerView.Adapter<patientAdapter.UserViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Patient user);
    }

    private final List<Patient> userList;
    private final OnItemClickListener listener;

    public patientAdapter(List<Patient> userList, OnItemClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Patient user = userList.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView ageTextView;
        private final TextView heightTextView;
        private final TextView weightTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            ageTextView = itemView.findViewById(R.id.ageTextView);
            heightTextView = itemView.findViewById(R.id.heightTextView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
        }

        public void bind(final Patient user, final OnItemClickListener listener) {
            nameTextView.setText(user.getName());
            ageTextView.setText("Edad: " + user.getAge());
            heightTextView.setText("Estatura: " + user.getHeight() + " m");
            weightTextView.setText("Peso: " + user.getWeight() + " kg");

            itemView.setOnClickListener(v -> listener.onItemClick(user));
        }
    }
}
