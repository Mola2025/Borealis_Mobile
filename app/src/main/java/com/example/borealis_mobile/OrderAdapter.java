package com.example.borealis_mobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<Order> orderList;
    private LayoutInflater inflater;

    public OrderAdapter(Context c, List<Order> list) {
        context = c;
        orderList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.order_card, parent, false);
        }

        Order order = orderList.get(position);

        TextView tvOrderId    = convertView.findViewById(R.id.tvOrderId);
        TextView tvOrderUser  = convertView.findViewById(R.id.tvOrderUser);
        TextView tvOrderDate  = convertView.findViewById(R.id.tvOrderDate);
        TextView tvOrderItems = convertView.findViewById(R.id.tvOrderItems);
        TextView tvOrderTotal = convertView.findViewById(R.id.tvOrderTotal);


        tvOrderId.setText("Order ID: " + order.getOrderId());
        tvOrderUser.setText("User: " + order.getUserId());

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date(order.getTimestamp()));
        tvOrderDate.setText("Date: " + formattedDate);

        StringBuilder itemsBuilder = new StringBuilder();
        double total = 0;

        if (order.getItems() != null) {
            for (String key : order.getItems().keySet()) {
                Order.OrderItem item = order.getItems().get(key);

                double subtotal = item.getPrice() * item.getQuantity();
                total += subtotal;

                itemsBuilder.append("• ")
                        .append(item.getName())
                        .append(" (x")
                        .append(item.getQuantity())
                        .append(")  — $")
                        .append(item.getPrice())
                        .append(" | Subtotal: $")
                        .append(subtotal)
                        .append("\n");
            }
        }

        tvOrderItems.setText(itemsBuilder.toString().trim());
        tvOrderTotal.setText("Total: $" + total);

        return convertView;
    }

}
