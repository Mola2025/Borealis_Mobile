package com.example.borealis_mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    Button btnChooseImg, btnSaveProd;
    DatabaseReference databaseProduct;
    StorageReference storageReference;

    String productId, oldImageURL;
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
        btnChooseImg = findViewById(R.id.btnchooseimage);
        btnSaveProd = findViewById(R.id.savechange);

        databaseProduct = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        // Get data from Intent
        Intent intent = getIntent();
        productId = intent.getStringExtra("productID");
        oldImageURL = intent.getStringExtra("productImageURL");

        editTextName.setText(intent.getStringExtra("productName"));
        editTextDesc.setText(intent.getStringExtra("productDesc"));
        editTextPrice.setText(intent.getStringExtra("productPrice"));

        // Load the image
        if (oldImageURL != null && !oldImageURL.isEmpty()) {
            loadImageFromURL(oldImageURL);
        }

        btnChooseImg.setOnClickListener(v -> chooseImage());
        btnSaveProd.setOnClickListener(v -> updateProduct());
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
        Item updatedProduct = new Item(productId, name, description, price, imageUrl);

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
}