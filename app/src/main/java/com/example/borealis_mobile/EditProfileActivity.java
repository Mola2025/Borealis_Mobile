package com.example.borealis_mobile;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfileActivity extends AppCompatActivity {

    ImageButton editProfileImage;
    EditText editUsername, editEmail;
    Button btnSaveProfile, btnBuyPremium;


    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private Uri imageUri = null;
    private final int PREMIUM_PRICE = 200;
    private final ActivityResultLauncher<String> selectImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    editProfileImage.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        editProfileImage = findViewById(R.id.edit_profile_image);
        editUsername = findViewById(R.id.edit_username_input);
        editEmail = findViewById(R.id.edit_email_input);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
        btnBuyPremium = findViewById(R.id.btn_buy_premium);

        editProfileImage.setOnClickListener(v -> {
            selectImageLauncher.launch("image/*");
        });

        btnSaveProfile.setOnClickListener(v -> {
            saveProfile();
        });

        btnBuyPremium.setOnClickListener(v -> {
            // TODO: aca pones el coso de pagar con el PREMIUM_PRICE
        });
    }
    private void loadUserData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            editUsername.setText(user.getDisplayName());
            editEmail.setText(user.getEmail());
            editProfileImage.setImageURI(user.getPhotoUrl());
        }
    }
    private void saveProfile() {
        String newUsername = editUsername.getText().toString();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No session found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri != null) {
            StorageReference fileRef = storageReference.child(user.getUid() + ".jpg");
            UploadTask uploadTask = fileRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    updateUserProfile(user, newUsername, imageUrl);
                });
            });
        } else {
            updateUserProfile(user, newUsername, String.valueOf(user.getPhotoUrl()));
        }
    }
    private void updateUserProfile(FirebaseUser user, String username, String imageURL) {
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                .setDisplayName(username);

        if (imageURL != null) {
            builder.setPhotoUri(Uri.parse(imageURL));
        }

        user.updateProfile(builder.build())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}