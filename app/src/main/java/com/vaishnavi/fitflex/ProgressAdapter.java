package com.vaishnavi.fitflex;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder> {

    private List<ProgressData> progressList;
    private List<Integer> selectedItems = new ArrayList<>(); // Store the selected items' positions

    public ProgressAdapter(List<ProgressData> progressList) {
        this.progressList = progressList;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        ProgressData progressData = progressList.get(position);

        holder.dateTextView.setText("Date: " + progressData.getDate());
        holder.weightTextView.setText("Weight: " + progressData.getWeight());
        holder.bmiTextView.setText("BMI: " + progressData.getBmi());

        // If there is an image, display it, otherwise hide the ImageView
        if (progressData.getImage() != null) {
            holder.imageView.setImageBitmap(progressData.getImage());
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE); // Hide the ImageView if no image is present
        }

        // Checkbox listener to handle selection for deletion
        holder.checkBox.setChecked(selectedItems.contains(position));
        holder.checkBox.setOnClickListener(v -> {
            if (holder.checkBox.isChecked()) {
                selectedItems.add(position);
            } else {
                selectedItems.remove(Integer.valueOf(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return progressList.size();
    }

    public List<ProgressData> getSelectedItems() {
        List<ProgressData> selectedProgressData = new ArrayList<>();
        for (int position : selectedItems) {
            selectedProgressData.add(progressList.get(position));
        }
        return selectedProgressData;
    }

    public void deleteSelectedItems() {
        List<ProgressData> itemsToRemove = getSelectedItems();
        progressList.removeAll(itemsToRemove);
        notifyDataSetChanged();
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView, weightTextView, bmiTextView;
        ImageView imageView;
        CheckBox checkBox;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
            bmiTextView = itemView.findViewById(R.id.bmiTextView);
            imageView = itemView.findViewById(R.id.imageView);
            checkBox = itemView.findViewById(R.id.checkBox); // Add checkbox for selection
        }
    }
}
