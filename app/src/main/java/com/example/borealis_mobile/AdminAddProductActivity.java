package com.example.borealis_mobile;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AdminAddProductActivity extends AppCompatActivity {

    EditText editTextName, editTextDescription, editTextPrice;

    Button buttonSave, buttonSelectImage;
    ImageView imageViewProduct;

    DatabaseReference databaseProduct;
    StorageReference storageReference;

    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_add_product);

        databaseProduct = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        editTextName = findViewById(R.id.editTextProductName);
        editTextDescription = findViewById(R.id.editTextProductDescription);
        editTextPrice = findViewById(R.id.editTextProductPrice);
        imageViewProduct = findViewById(R.id.imageViewProduct);

        buttonSave = findViewById(R.id.buttonSaveProduct);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);


        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageLauncher.launch("image/*");
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });


    }

    private final ActivityResultLauncher<String> selectImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    imageViewProduct.setImageURI(uri);
                }
            });


    private void saveProduct() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceStr) || imageUri == null) {
            Toast.makeText(this, "All fields need to be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        String id = databaseProduct.push().getKey();//generate uniq id for you

        if (id == null) return;

        StorageReference fileRef = storageReference.child(id + ".jpg");
        UploadTask uploadTask = fileRef.putFile(imageUri);

        uploadTask.addOnCompleteListener(task ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    //create my object
                    Item product = new Item(id, name, description, price, imageUrl);
                    databaseProduct.child(id).setValue(product).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Success Product added", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                })
        );

    }
}