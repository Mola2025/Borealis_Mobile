package com.example.borealis_mobile;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminItemAdapter extends RecyclerView.Adapter<AdminItemAdapter.ItemViewHolder>{
    @NonNull
    @Override
    public AdminItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminItemAdapter.ItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
