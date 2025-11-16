package com.example.borealis_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextName, editTextUsername;
    Button buttonSubmit, buttonToggle, buttonForgotPassword;
    TextView title;
    LinearLayout register_fields;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    // Variable to control if true = login, false = register ↓↓
    private boolean isLoginMode = true;

    // Admin Credentials
    private static final String ADMIN_EMAIL = "juan0213ca@gmail.com";
    private static final String ADMIN_PASSWORD = "12345678";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.name);
        editTextUsername = findViewById(R.id.username);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonToggle = findViewById(R.id.buttonToggle);
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword);
        title = findViewById(R.id.title);
        register_fields = findViewById(R.id.register_fields);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        setupLoginMode();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoginMode){
                    Login();
                }else{
                    Register();
                }
            }
        });

        buttonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAuthMode();
            }
        });

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

        private void toggleAuthMode() {
            isLoginMode = !isLoginMode;

            if (isLoginMode) {
                setupLoginMode();
            } else {
                setupRegisterMode();
            }
        }
        private void setupLoginMode(){
            title.setText("LOGIN PAGE");
            buttonSubmit.setText("Login");
            buttonToggle.setText("Don't have an account? Register");
            buttonForgotPassword.setVisibility(View.VISIBLE);
            register_fields.setVisibility(View.GONE);

            editTextName.setText("");
            editTextUsername.setText("");
        }

        private void setupRegisterMode(){
            title.setText("REGISTER PAGE");
            buttonSubmit.setText("Register");
            buttonToggle.setText("Already have an account? Login");
            buttonForgotPassword.setVisibility(View.GONE);
            register_fields.setVisibility(View.VISIBLE);
        }

        private void Login(){
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editTextEmail.setError("Invalid email");
                return;
            }

            if(password.length() < 8){
                editTextPassword.setError("Password must be at least 8 characters");
                return;
            }

            if(email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)){
                Toast.makeText(this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, AdminHomePageActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AuthActivity.this, "User Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AuthActivity.this, UserHomePageActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(AuthActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void Register(){
            String name = editTextName.getText().toString().trim();
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if(email.equals(ADMIN_EMAIL)){
                Toast.makeText(this, "Cannot Register With This Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editTextEmail.setError("Invalid email");
                return;
            }

            if(password.length() < 8){
                editTextPassword.setError("Password must be at least 8 characters");
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isComplete()){
                        // User created in Auth, now save it in the realtime database
                        FirebaseUser firebaseuser = firebaseAuth.getCurrentUser();
                        if (firebaseuser != null){
                            String userId = firebaseuser.getUid();

                            User user = new User(userId,name, username, email, password);

                            // Save in realtime database
                            databaseReference.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isComplete()){
                                        Toast.makeText(AuthActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AuthActivity.this, UserHomePageActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(AuthActivity.this, "Registration Failed: Could not save user to database", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(AuthActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
}