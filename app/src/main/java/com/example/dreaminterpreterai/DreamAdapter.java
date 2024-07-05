package com.example.dreaminterpreterai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DreamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_DREAM = 0;
    private static final int VIEW_TYPE_SEPARATOR = 1;

    private List<Object> dreamList;
    private OnDreamDeleteListener onDreamDeleteListener;

    public DreamAdapter(List<Object> dreamList, OnDreamDeleteListener onDreamDeleteListener) {
        this.dreamList = dreamList;
        this.onDreamDeleteListener = onDreamDeleteListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (dreamList.get(position) instanceof Dream) {
            return VIEW_TYPE_DREAM;
        } else {
            return VIEW_TYPE_SEPARATOR;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DREAM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dream, parent, false);
            return new DreamViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_separator, parent, false);
            return new SeparatorViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_DREAM) {
            DreamViewHolder dreamViewHolder = (DreamViewHolder) holder;
            Dream dream = (Dream) dreamList.get(position);
            dreamViewHolder.dateTextView.setText(dream.date);
            dreamViewHolder.dreamTextView.setText(dream.dream);
            dreamViewHolder.interpretationTextView.setText(dream.interpretation);

            dreamViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDreamDeleteListener.onDelete(dream);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dreamList.size();
    }

    public static class DreamViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, dreamTextView, interpretationTextView;
        Button deleteButton;

        public DreamViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            dreamTextView = itemView.findViewById(R.id.dreamTextView);
            interpretationTextView = itemView.findViewById(R.id.interpretationTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public static class SeparatorViewHolder extends RecyclerView.ViewHolder {
        public SeparatorViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface OnDreamDeleteListener {
        void onDelete(Dream dream);
    }
}
