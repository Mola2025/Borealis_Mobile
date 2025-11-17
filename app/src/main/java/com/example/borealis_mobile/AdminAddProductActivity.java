package com.example.borealis_mobile;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.borealis_mobile.model.BaseElement;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AdminAddProductActivity extends AppCompatActivity {

    EditText editTextName, editTextDescription, editTextPrice;
    Button buttonSave, buttonSelectImage;
    ImageView imageViewProduct;
    ImageButton imageButtonPremium;
    private boolean isPremium = false;
//    Spinner spinnerType;
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
        imageButtonPremium = findViewById(R.id.imageButtonPremium);
//        spinnerType = findViewById(R.id.spinnerType);

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
//        String type = spinnerType.getSelectedItem().toString();


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

                    // Type = selection del spinner

                    // if (type == "Class"){
                    //  ShopItem product = new ShopItem(id, new Class(name, type, id, "", description, null, null), price, imageUrl);
                    // }else if (type == "Item"){
                    //  ShopItem product = new ShopItem(id, new Item(name, type, id, "", description, null, null), price, imageUrl);
                    // }else if (type == "Race"){
                    //  ShopItem product = new ShopItem(id, new Race(name, type, id, "", description, null, null), price, imageUrl);
                    // }else if (type == "Spell"){
                    //  ShopItem product = new ShopItem(id, new Spell(name, type, id, "", description, null, null), price, imageUrl);
                    // }

                    ShopItem product = new ShopItem(id, new BaseElement(name, null, id, "", description, null, null), price, imageUrl, isPremium);
                    databaseProduct.child(id).setValue(product).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Success Product added", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                })
        );

    }
}