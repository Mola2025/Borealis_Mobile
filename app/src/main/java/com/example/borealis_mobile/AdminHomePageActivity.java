package com.example.borealis_mobile;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminHomePageActivity extends AppCompatActivity {

    ListView listViewProduct;
    DatabaseReference databaseProduct;
    ArrayList<Item> productList;

    AdminItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home_page);

        listViewProduct = findViewById(R.id.listViewProducts);
        databaseProduct = FirebaseDatabase.getInstance().getReference("products");

        productList = new ArrayList<>();
        adapter = new AdminItemAdapter(this,productList);
        listViewProduct.setAdapter(adapter);


        databaseProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot possnapshot : snapshot.getChildren()){
                    Item product = possnapshot.getValue(Item.class);
                    if (product !=null){
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


    }
}