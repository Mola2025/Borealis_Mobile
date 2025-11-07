package com.example.borealis_mobile;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {


    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
