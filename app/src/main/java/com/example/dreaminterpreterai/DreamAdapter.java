package com.example.dreaminterpreterai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DreamAdapter extends RecyclerView.Adapter<DreamAdapter.DreamViewHolder> {
    private List<Dream> dreamList;
    private OnDreamDeleteListener onDreamDeleteListener;

    public DreamAdapter(List<Dream> dreamList, OnDreamDeleteListener onDreamDeleteListener) {
        this.dreamList = dreamList;
        this.onDreamDeleteListener = onDreamDeleteListener;
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

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDreamDeleteListener.onDelete(dream);
            }
        });

        // Add a divider line between different dream groups
        if (position > 0 && dreamList.get(position - 1).groupId != dream.groupId) {
            holder.dividerView.setVisibility(View.VISIBLE);
        } else {
            holder.dividerView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dreamList.size();
    }

    public static class DreamViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, dreamTextView, interpretationTextView;
        Button deleteButton;
        View dividerView;

        public DreamViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            dreamTextView = itemView.findViewById(R.id.dreamTextView);
            interpretationTextView = itemView.findViewById(R.id.interpretationTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            dividerView = itemView.findViewById(R.id.dividerView);
        }
    }

    public interface OnDreamDeleteListener {
        void onDelete(Dream dream);
    }
}
