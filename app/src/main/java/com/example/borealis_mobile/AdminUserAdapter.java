package com.example.borealis_mobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AdminUserAdapter extends BaseAdapter {

    private Context context;
    private List<User> users;
    private DatabaseReference usersRef;
    private DatabaseReference blockedUsersRef;
    private boolean isBlocked;



    public AdminUserAdapter(Context context, List<User> users, DatabaseReference usersRef, DatabaseReference blockedUsersRef, boolean isBlocked) {
        this.context = context;
        this.users = users;
        this.usersRef = usersRef;
        this.blockedUsersRef = blockedUsersRef;
        this.isBlocked = isBlocked;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            if(isBlocked){
                convertView = inflater.inflate(R.layout.admin_blocked_users_card, parent, false);
            }else{
                convertView = inflater.inflate(R.layout.admin_userlist_card, parent, false);
            }
        }

        User user = users.get(position);

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvUsername = convertView.findViewById(R.id.tvUsername);
        TextView tvUserEmail = convertView.findViewById(R.id.tvUserEmail);
        ImageView profileImage = convertView.findViewById(R.id.profileImage);


        tvName.setText(user.getName());
        tvUsername.setText(user.getUsername());
        tvUserEmail.setText(user.getEmail());

        if(user.getImageURL() != null && !user.getImageURL().isEmpty()){
            loadImageFromURL(user.getImageURL(), profileImage);
        }
        else{
            profileImage.setImageResource(R.drawable.ic_normal_user);
        }

        if(!isBlocked){
            Button btnBlockUnblock = convertView.findViewById(R.id.btnBlockUnblock);
            Button btnDelete = convertView.findViewById(R.id.btnDelete);

            checkIfBlocked(user, btnBlockUnblock);

            btnBlockUnblock.setOnClickListener(v -> {
                toogleBlockUser(user, btnBlockUnblock);
            });
            btnDelete.setOnClickListener(v -> {
                deleteUser(user);
            });
        }
        return convertView;
    }

    // 🔹 Load image from URL
    private void loadImageFromURL(String imageUrl, ImageView profileImage) {
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                // update UI on main thread
                new Handler(Looper.getMainLooper()).post(() -> profileImage.setImageBitmap(bitmap));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void checkIfBlocked(User user, Button btnBlockUnblock){
        blockedUsersRef.child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    btnBlockUnblock.setText("Unblock User");
                    btnBlockUnblock.setBackgroundColor(Color.GREEN);
                }
                else{
                    btnBlockUnblock.setText("Block User");
                    btnBlockUnblock.setBackgroundColor(Color.RED);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void toogleBlockUser(User user, Button btnBlockUnblock){
        String currentText = btnBlockUnblock.getText().toString();

        if(currentText.equals("Block User")) {
            blockedUsersRef.child(user.getId()).setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    btnBlockUnblock.setText("Unblock User");
                    btnBlockUnblock.setBackgroundColor(Color.GREEN);
                    Toast.makeText(context, "User blocked successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to block user", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            blockedUsersRef.child(user.getId()).removeValue().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    btnBlockUnblock.setText("Block User");
                    btnBlockUnblock.setBackgroundColor(Color.RED);
                    Toast.makeText(context, "User unblocked successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Failed to unblock user", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteUser(User user){
        usersRef.child(user.getId()).removeValue().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                blockedUsersRef.child(user.getId()).removeValue();
                Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
