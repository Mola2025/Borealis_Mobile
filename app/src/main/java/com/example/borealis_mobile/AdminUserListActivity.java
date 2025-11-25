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
import java.util.List;

public class AdminUserListActivity extends AppCompatActivity {
    private ListView listViewUsers;
    private AdminUserAdapter adapter;
    private List<User> users;
    private DatabaseReference usersRef;
    private DatabaseReference blockedUsersRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_user_list);

        listViewUsers = findViewById(R.id.listViewUsers);
        users = new ArrayList<>();

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        blockedUsersRef = FirebaseDatabase.getInstance().getReference("blockedUsers");

        adapter = new AdminUserAdapter(this, users, usersRef, blockedUsersRef, false);
        listViewUsers.setAdapter(adapter);

        loadAllUsers();
    }

    private void loadAllUsers(){
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}