package com.example.borealis_mobile;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    ImageButton editProfileImage;
    EditText editUsername, editEmail;
    Button btnSaveProfile, btnBuyPremium;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseUsers;
    private Uri imageUri = null;
    private final int PREMIUM_PRICE = 200;
    // Esta es para el stripe porque se necesitan en cents
    private final long premiumAmountCents = PREMIUM_PRICE * 100;

    APIKeys apiKeys = new APIKeys();
    PaymentSheet paymentSheet;
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

        storageReference = FirebaseStorage.getInstance().getReference("profile_images");
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        PaymentConfiguration.init(this, apiKeys.getPublishableKey());
        paymentSheet = new PaymentSheet(this, this::onPaymentResultPremium);

        editProfileImage.setOnClickListener(v -> {
            selectImageLauncher.launch("image/*");
        });

        btnSaveProfile.setOnClickListener(v -> {
            saveProfile();
        });

        btnBuyPremium.setOnClickListener(v -> {
            getClientSecretPremium();
        });

        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            editEmail.setText(user.getEmail());
            editEmail.setEnabled(false);

            databaseUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String currentUsername = snapshot.child("username").getValue(String.class);
                        editUsername.setText(currentUsername);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditProfileActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show();
                }
            });

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

                        DatabaseReference userRef = FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(user.getUid());

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("username", username);
                        updates.put("profileImageURL", imageURL);

                        userRef.updateChildren(updates)
                                .addOnSuccessListener(a -> {
                                    Toast.makeText(this,
                                            "Profile Updated!",
                                            Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this,
                                            "Error updating DB",
                                            Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getClientSecretPremium() {

        String url = "https://api.stripe.com/v1/payment_intents";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String client_secret = obj.getString("client_secret");

                        paymentSheet.presentWithPaymentIntent(
                                client_secret,
                                new PaymentSheet.Configuration("Stripe"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + apiKeys.getSecretKey());
                return h;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("amount", String.valueOf(premiumAmountCents)); // 200 USD
                p.put("currency", "usd");
                p.put("automatic_payment_methods[enabled]", "true");
                return p;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void onPaymentResultPremium(PaymentSheetResult result) {
        if (result instanceof PaymentSheetResult.Completed) {
            upgradeUserToPremium();
        }
    }

    private void upgradeUserToPremium() {

        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("isPremium")
                .setValue(true)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Premium activated! Thank You For Upgrading!", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error upgrading premium", Toast.LENGTH_SHORT).show();
                });
    }
}