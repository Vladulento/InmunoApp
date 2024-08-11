package com.uc3m.InmunoApp.PatientDetail;

import com.uc3m.InmunoApp.R;

import android.content.Context;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import java.util.List;
import java.util.Map;

public class ToxAdapter extends RecyclerView.Adapter<ToxAdapter.ToxViewHolder> {

    private final List<Map<String, String>> toxList;

    public ToxAdapter(List<Map<String, String>> toxList) {
        this.toxList = toxList;
    }

    @NonNull
    @Override
    public ToxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_toxicity, parent, false);
        return new ToxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToxViewHolder holder, int position) {
        Map<String, String> toxData = toxList.get(position);
        Context context = holder.itemView.getContext();

        holder.textViewToxicity.setText(context.getString(R.string.toxicity, toxData.get("Toxicidad")));
        holder.textViewPresentation.setText(context.getString(R.string.presentation, toxData.get("Presentation")));
        holder.textViewBaselineMonitoring.setText(context.getString(R.string.baseline_monitoring, toxData.get("Baseline monitoring")));
        holder.textViewDiagnosis.setText(context.getString(R.string.diagnosis, toxData.get("Diagnosis")));
    }

    @Override
    public int getItemCount() {
        return toxList.size();
    }

    public static class ToxViewHolder extends RecyclerView.ViewHolder {
        TextView textViewToxicity;
        TextView textViewBaselineMonitoring;
        TextView textViewDiagnosis;
        TextView textViewPresentation;

        public ToxViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewToxicity = itemView.findViewById(R.id.textViewToxicity);
            textViewBaselineMonitoring = itemView.findViewById(R.id.textViewBaselineMonitoring);
            textViewDiagnosis = itemView.findViewById(R.id.textViewDiagnosis);
            textViewPresentation = itemView.findViewById(R.id.textViewPresentation);
        }
    }
}
