package com.example.borealis_mobile;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminBlockedUsersList extends AppCompatActivity {

    private ListView listViewBlockedUsers;
    private AdminUserAdapter adapter;
    private List<User> BlockedUsersList;
    private DatabaseReference blockedUsersRef;
    private DatabaseReference usersRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_blocked_users_list);

        listViewBlockedUsers = findViewById(R.id.listViewBlockedUsers);
        BlockedUsersList = new ArrayList<>();

        blockedUsersRef = FirebaseDatabase.getInstance().getReference("blockedUsers");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        adapter = new AdminUserAdapter(this, BlockedUsersList, usersRef, blockedUsersRef, true);
        listViewBlockedUsers.setAdapter(adapter);

        loadBlockedUsers();

    }

    private void loadBlockedUsers(){
        blockedUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BlockedUsersList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    BlockedUsersList.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}