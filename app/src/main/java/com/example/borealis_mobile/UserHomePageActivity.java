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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.borealis_mobile.data.CharacterRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.example.borealis_mobile.model.Character;

public class UserHomePageActivity extends AppCompatActivity
        implements CharacterAdapter.OnCharacterClickListener, CharacterAdapter.OnCharacterDeleteListener {

    // UI Drawable Lateral Menu
    DrawerLayout drawerLayout_User;
    NavigationView navigationView_User;
    RecyclerView charRecycler;
    ImageView imgProfile, btnShop;
    TextView username;
    FloatingActionButton btnCreateCharacter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference db;

    private CharacterRepository charRepo;
    private CharacterAdapter charAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home_page);

        File appDir = getFilesDir();
        charRepo = new CharacterRepository(appDir);
        List<Character> characters = charRepo.loadCharacters();

        drawerLayout_User = findViewById(R.id.drawerLayout_user);
        navigationView_User = findViewById(R.id.navigationView_user);
        View headerView = navigationView_User.getHeaderView(0);
        imgProfile = headerView.findViewById(R.id.imgProfile);
        username = headerView.findViewById(R.id.username);
        btnCreateCharacter = findViewById(R.id.btnCreateCharacter);
        btnShop = findViewById(R.id.btnShop);
        charRecycler = findViewById(R.id.charRecycler);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("users");

        loadUserData();


        btnCreateCharacter.setOnClickListener(v -> {
            Intent intent = new Intent(this, ModifyCharacterActivity.class);
            startActivity(intent);
        });

        btnShop.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShopActivity.class);
            startActivity(intent);
        });

        // Menu
        navigationView_User.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_settings) {
                    Intent intent2 = new Intent(this, EditProfileActivity.class);
                    startActivity(intent2);
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent5 = new Intent(this, AuthActivity.class);
                startActivity(intent5);
                Toast.makeText(this, "Logout successful, Thank you for using our app :D", Toast.LENGTH_SHORT).show();
            }
            drawerLayout_User.closeDrawer(GravityCompat.START);
            return true;
        });

        Toolbar toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout_User, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout_User.addDrawerListener(toggle);
        toggle.syncState();

        charAdapter = new CharacterAdapter(characters, this, this);
        charRecycler.setLayoutManager(new LinearLayoutManager(this));
        charRecycler.setAdapter(charAdapter);
    }

    @Override
    public void onCharacterClick(Character character) {
        Intent intent = new Intent(this, ModifyCharacterActivity.class);
        intent.putExtra("character", character);
        startActivity(intent);
    }
    @Override
    public void onCharacterDelete(Character character) {
        try {
            List<Character> allCharacters = charRepo.loadCharacters();
            for (int i = 0; i < allCharacters.size(); i++) {
                Character c = allCharacters.get(i);

                if (c.getId().equals(character.getId())) {
                    allCharacters.remove(i);
                    break;
                }
            }
            charRepo.saveCharacters(allCharacters);
            charAdapter.updateCharacters(allCharacters);
            charAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Character deleted", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error deleting character", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Character> updatedCharacters = charRepo.loadCharacters();
        charAdapter.updateCharacters(updatedCharacters);
    }

    private void loadUserData() {
        String currentUser = firebaseAuth.getCurrentUser().getUid();

        db.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String profileImage = snapshot.child("profileImageURL").getValue(String.class);

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

    // Load image from URL
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