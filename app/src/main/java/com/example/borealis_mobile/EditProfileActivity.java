package com.example.borealis_mobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditProfileActivity extends AppCompatActivity {

    ImageView editProfileImage;
    EditText editUsername, editEmail;
    Button btnSaveProfile, btnBuyPremium;
    private final int PREMIUM_PRICE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        editProfileImage = findViewById(R.id.edit_profile_image);
        editUsername = findViewById(R.id.edit_username_input);
        editEmail = findViewById(R.id.edit_email_input);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
        btnBuyPremium = findViewById(R.id.btn_buy_premium);

        btnSaveProfile.setOnClickListener(v -> {

        });

        btnBuyPremium.setOnClickListener(v -> {

        });

    }
}