package com.example.borealis_mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.borealis_mobile.model.BaseElement;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class AdminUpdateProductActivity extends AppCompatActivity {

    EditText editTextName, editTextDesc, editTextPrice;
    ImageView imageView;

    ImageButton imageButtonPremium;
    private boolean isPremium = false;
    Button btnChooseImg, btnSaveProd;
    DatabaseReference databaseProduct;
    StorageReference storageReference;

    String productId, oldImageURL;
    double oldPrice;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_update_product);

        editTextName = findViewById(R.id.etNameEp);
        editTextDesc = findViewById(R.id.etDescEp);
        editTextPrice = findViewById(R.id.etPriceEp);
        imageView = findViewById(R.id.imageViewProductEdit);
        imageButtonPremium = findViewById(R.id.imageButtonPremium);
        btnChooseImg = findViewById(R.id.btnchooseimage);
        btnSaveProd = findViewById(R.id.savechange);

        databaseProduct = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        // Get data from Intent
        Intent intent = getIntent();
        productId = intent.getStringExtra("productID");
        oldImageURL = intent.getStringExtra("productImageURL");
        oldPrice = intent.getDoubleExtra("productPrice", 0.0);

        editTextName.setText(intent.getStringExtra("productName"));
        editTextDesc.setText(intent.getStringExtra("productDesc"));
        editTextPrice.setText(String.valueOf(oldPrice));


        // Premium Item State
        isPremium = intent.getBooleanExtra("productPremium", false);

        if (isPremium) {
            imageButtonPremium.setImageResource(R.drawable.ic_crown_on);
        } else {
            imageButtonPremium.setImageResource(R.drawable.ic_crown_off);
        }

        // Load the image
        if (oldImageURL != null && !oldImageURL.isEmpty()) {
            loadImageFromURL(oldImageURL);
        }

        btnChooseImg.setOnClickListener(v -> chooseImage());
        btnSaveProd.setOnClickListener(v -> updateProduct());

        imageButtonPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPremium = !isPremium;
                if (isPremium) {
                    imageButtonPremium.setImageResource(R.drawable.ic_crown_on);
                } else {
                    imageButtonPremium.setImageResource(R.drawable.ic_crown_off);
                }
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Product Image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }

    private void updateProduct() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDesc.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");
        progressDialog.show();

        if (selectedImageUri != null) {
            StorageReference fileRef = storageReference.child(UUID.randomUUID().toString());
            fileRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        deleteOldImage();
                        updateProductInDatabase(name, description, price, uri.toString());
                        progressDialog.dismiss();
                    }))
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            updateProductInDatabase(name, description, price, oldImageURL);
            progressDialog.dismiss();
        }
    }

    private void updateProductInDatabase(String name, String description, double price, String imageUrl) {
        ShopItem updatedProduct = new ShopItem(productId, new BaseElement(name, null, productId, "", description, null, null), price, imageUrl, isPremium);

        databaseProduct.child(productId).setValue(updatedProduct)
                .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Product updated!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, AdminHomePageActivity.class);
                        startActivity(intent);
                        finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }

    // 🔹 Load image from URL
    private void loadImageFromURL(String imageUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                // update UI on main thread
                new Handler(Looper.getMainLooper()).post(() -> imageView.setImageBitmap(bitmap));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void deleteOldImage(){
        if(oldImageURL != null && !oldImageURL.isEmpty()){
            try{
                StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                oldImageRef.delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Old image deleted successfully", Toast.LENGTH_SHORT).show();
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}