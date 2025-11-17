package com.example.borealis_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
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

import java.util.ArrayList;

public class AdminHomePageActivity extends AppCompatActivity {

    // UI Drawable Lateral Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FloatingActionButton btnAddProduct;

    // Firebase
    DatabaseReference databaseProduct;
    ListView listViewProduct;
    ArrayList<ShopItem> productList;
    AdminItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home_page);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        listViewProduct = findViewById(R.id.listViewProducts);
        databaseProduct = FirebaseDatabase.getInstance().getReference("products");

        productList = new ArrayList<>();
        adapter = new AdminItemAdapter(this, productList);
        listViewProduct.setAdapter(adapter);


        databaseProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot possnapshot : snapshot.getChildren()) {
                    ShopItem product = possnapshot.getValue(ShopItem.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminAddProductActivity.class);
            startActivity(intent);
        });

        // Menu
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_users_list) {
                    Intent intent2 = new Intent(this, AdminUserListActivity.class);
                    startActivity(intent2);
            } else if (id == R.id.nav_order_history) {
                    Intent intent3 = new Intent(this, OrderHistoryActivity.class);
                    startActivity(intent3);
            } else if (id == R.id.nav_blocked_users) {
                    Intent intent4 = new Intent(this, AdminBlockedUsersList.class);
                    startActivity(intent4);
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent5 = new Intent(this, AuthActivity.class);
                startActivity(intent5);
                Toast.makeText(this, "Logout successful, Thank you for using our app :D", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


        // Enable Drawer // Esto sirve para crear un icono en la toolbar de arriba para poder abrir la barra lateral :)))))))) casi que no
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                // This es la actividad donde esta el menu, drawerLayout es el layout que contiene el menu, toolbar es la toolbar que contiene el icono ↓↓
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        // Conectar el icono con el menu
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


    }
}