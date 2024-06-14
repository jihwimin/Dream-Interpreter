package com.example.dreaminterpreterai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DreamAdapter extends RecyclerView.Adapter<DreamAdapter.DreamViewHolder> {
    private List<Dream> dreamList;

    public DreamAdapter(List<Dream> dreamList) {
        this.dreamList = dreamList;
    }

    @NonNull
    @Override
    public DreamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dream, parent, false);
        return new DreamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DreamViewHolder holder, int position) {
        Dream dream = dreamList.get(position);
        holder.dateTextView.setText(dream.date);
        holder.dreamTextView.setText(dream.dream);
        holder.interpretationTextView.setText(dream.interpretation);
    }

    @Override
    public int getItemCount() {
        return dreamList.size();
    }

    public static class DreamViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, dreamTextView, interpretationTextView;

        public DreamViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            dreamTextView = itemView.findViewById(R.id.dreamTextView);
            interpretationTextView = itemView.findViewById(R.id.interpretationTextView);
        }
    }
}
