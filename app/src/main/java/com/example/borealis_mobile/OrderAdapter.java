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
import java.util.ArrayList;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Order> orderList;

    public OrderAdapter(Context c, ArrayList<Order> list) {
        context = c;
        orderList = list;
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

        ImageView imageView = convertView.findViewById(R.id.profileImage);
        TextView nameText = convertView.findViewById(R.id.tvUsername);
        TextView priceText = convertView.findViewById(R.id.tvItemPrice);
        TextView userText = convertView.findViewById(R.id.tvUserEmail);


        nameText.setText(order.getProductName());
        priceText.setText("$ " + order.getTotalAmount());
        userText.setText("Comprado por: " + order.getUserId());


        if (order.getProductImageURL() != null && !order.getProductImageURL().isEmpty()) {
            new ImageLoadTask(order.getProductImageURL(), imageView).execute();
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        return convertView;
    }

    private static class ImageLoadTask extends AsyncTask<Void,Void, Bitmap> {
        private String url;
        private ImageView imageView;
        public ImageLoadTask(String url, ImageView imageView){
            this.url = url;
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(Void... voids) {
            try{
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }else{
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        }
    }

}
