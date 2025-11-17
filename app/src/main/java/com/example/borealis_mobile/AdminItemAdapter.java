package com.example.borealis_mobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AdminItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ShopItem> productList;
    private DatabaseReference databaseProduct;

    public AdminItemAdapter(Context c, ArrayList<ShopItem> list) {
        context = c;
        productList = list;
        databaseProduct = FirebaseDatabase.getInstance().getReference("products");
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.admin_item_card,parent,false);
        }
        ShopItem product = productList.get(position);

        ImageView imageView = view.findViewById(R.id.profileImage);
        TextView nameText = view.findViewById(R.id.tvUsername);
        TextView descriptionText = view.findViewById(R.id.tvUserEmail);
        TextView priceText = view.findViewById(R.id.tvItemPrice);
        ImageView imageButtonPremium = view.findViewById(R.id.imageButtonPremium);
        Spinner spinnerActions = view.findViewById(R.id.spinnerActions);


        nameText.setText(product.getBaseElement().getName());
        descriptionText.setText(product.getBaseElement().getDescription());
        priceText.setText("$ " + product.getPrice());

        if (product.getImageURL() !=null && !product.getImageURL().isEmpty()){
            new ImageLoadTask(product.getImageURL(),imageView).execute();
        }else {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        // Premium Item

        if (product.isPremium()){
            imageButtonPremium.setVisibility(View.VISIBLE);
        }else {
            imageButtonPremium.setVisibility(View.GONE);
        }

        // Spinner
        String[] actions = {"Select Action","Update", "Delete"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, actions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActions.setAdapter(adapter);



        spinnerActions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean selectoption = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (selectoption){
                    selectoption = false;
                    return;
                }

                String action = parent.getItemAtPosition(position).toString();

                if (action.equals("Update")){
                    UpdateProduct(product);
                    spinnerActions.setSelection(0);
                }else if (action.equals("Delete")){
                    DeleteProduct(product);
                    spinnerActions.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
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

    private void UpdateProduct(ShopItem product){
        Intent intent = new Intent(context, AdminUpdateProductActivity.class);
        intent.putExtra("productID", product.getId());
        intent.putExtra("productName", product.getBaseElement().getName());
        intent.putExtra("productDesc", product.getBaseElement().getDescription());
        intent.putExtra("productPrice", product.getPrice());
        intent.putExtra("productImageURL", product.getImageURL());
        intent.putExtra("productPremium", product.isPremium());
        context.startActivity(intent);
    }

    private void DeleteProduct(ShopItem product){
        if (product.getImageURL() != null && !product.getImageURL().isEmpty()){
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getImageURL());
            imageRef.delete().addOnSuccessListener(aVoid -> {
                DeleteProductDB(product.getId());
            });
        }
    }

    private void DeleteProductDB(String id){
        FirebaseDatabase.getInstance().getReference("products")
                .child(id).removeValue().addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                });
    }
}
