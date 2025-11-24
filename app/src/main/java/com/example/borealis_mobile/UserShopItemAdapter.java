package com.example.borealis_mobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UserShopItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ShopItem> productList;
    private DatabaseReference userRef;
    private FirebaseAuth auth;
    private boolean isUserPremium = false;

    public UserShopItemAdapter(Context c, ArrayList<ShopItem> list) {
        context = c;
        productList = list;
        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        // Verificar si el usuario es premium // Toca añadir a la clase User el bool de isPremium!!!!
        checkUserPremiumStatus();
    }

    private void checkUserPremiumStatus() {
        String currentUserId = auth.getCurrentUser().getUid();
        userRef.child(currentUserId).child("isPremium").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    isUserPremium = Boolean.TRUE.equals(dataSnapshot.getValue(Boolean.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
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
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.shop_item_card, parent, false);
        }

        ShopItem product = productList.get(position);

        ImageView productImage = view.findViewById(R.id.productImage);
        ImageView premiumIcon = view.findViewById(R.id.imageButtonPremium);
        TextView productName = view.findViewById(R.id.tvProductName);
        TextView productDescription = view.findViewById(R.id.tvProductDescription);
        TextView productPrice = view.findViewById(R.id.tvProductPrice);
        TextView quantityText = view.findViewById(R.id.tvQuantity);
        ImageButton btnDecrease = view.findViewById(R.id.btnDecrease);
        ImageButton btnIncrease = view.findViewById(R.id.btnIncrease);

        productName.setText(product.getBaseElement().getName());
        productDescription.setText(product.getBaseElement().getDescription());
        productPrice.setText("$" + product.getPrice());

        if (product.getImageURL() != null && !product.getImageURL().isEmpty()) {
            new UserShopItemAdapter.ImageLoadTask(product.getImageURL(), productImage).execute();
        } else {
            productImage.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        if (product.isPremium()) {
            premiumIcon.setVisibility(View.VISIBLE);
        } else {
            premiumIcon.setVisibility(View.GONE);
        }

        // Inicializar cantidad
        quantityText.setText("Quantity = 0");

        // Listeners para los botones de cantidad
        btnIncrease.setOnClickListener(v -> {
            if (product.isPremium() && !isUserPremium) {
                Toast.makeText(context, "You need a premium account to purchase premium items", Toast.LENGTH_SHORT).show();
                return;
            }

            int currentQuantity = Integer.parseInt(quantityText.getText().toString().replace("Quantity = ", ""));
            quantityText.setText("Quantity = " + (currentQuantity + 1));
        });

        btnDecrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(quantityText.getText().toString().replace("Quantity = ", ""));
            if (currentQuantity > 0) {
                quantityText.setText("Quantity = " + (currentQuantity - 1));
            }
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
}