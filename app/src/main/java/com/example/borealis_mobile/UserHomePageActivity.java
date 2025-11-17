package com.example.borealis_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserHomePageActivity extends AppCompatActivity {

    // UI Drawable Lateral Menu
    DrawerLayout drawerLayout_User;
    NavigationView navigationView_User;

    ImageView imgProfile, btnshop;
    TextView username;
    FloatingActionButton btncreatecharacter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home_page);

        drawerLayout_User = findViewById(R.id.drawerLayout_user);
        navigationView_User = findViewById(R.id.navigationView_user);
        View headerView = navigationView_User.getHeaderView(0);
        imgProfile = headerView.findViewById(R.id.imgProfile);
        username = headerView.findViewById(R.id.username);
        btncreatecharacter = findViewById(R.id.btnCreateCharacter);
        btnshop = findViewById(R.id.btnShop);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("users");

        loadUserData();


        btncreatecharacter.setOnClickListener(v -> {
            Intent intent = new Intent(this, ModifyCharacterActivity.class);
            startActivity(intent);
        });

        btnshop.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShopActivity.class);
            startActivity(intent);
        });

        // Menu
        navigationView_User.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_settings) {
                    Intent intent2 = new Intent(this, EditProfileActivity.class);
                    startActivity(intent2);
            } else if (id == R.id.nav_favorite_items) {
//                    Intent intent3 = new Intent(this, AdminOrderHistoryActivity.class);
//                    startActivity(intent3);
            } else if (id == R.id.nav_user_characters) {
//                    Intent intent4 = new Intent(this, AdminBlockUsersActivity.class);
//                    startActivity(intent4);
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent5 = new Intent(this, AuthActivity.class);
                startActivity(intent5);
                Toast.makeText(this, "Logout successful, Thank you for using our app :D", Toast.LENGTH_SHORT).show();
            }
            drawerLayout_User.closeDrawer(GravityCompat.START);
            return true;
        });


        // Enable Drawer // Esto sirve para crear un icono en la toolbar de arriba para poder abrir la barra lateral :)))))))) casi que no
        Toolbar toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                // This es la actividad donde esta el menu, drawerLayout es el layout que contiene el menu, toolbar es la toolbar que contiene el icono ↓↓
                this, drawerLayout_User, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        // Conectar el icono con el menu
        drawerLayout_User.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void loadUserData() {
        String currentUser = firebaseAuth.getCurrentUser().getUid();

        db.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String profileImage = snapshot.child("imageURL").getValue(String.class);

                    if (username != null) {
                        UserHomePageActivity.this.username.setText(username);
                    }
                    if (profileImage != null) {
                        loadImageFromURL(profileImage);
                    }
                    else{
                        imgProfile.setImageResource(R.drawable.ic_normal_user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
                new Handler(Looper.getMainLooper()).post(() -> imgProfile.setImageBitmap(bitmap));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}