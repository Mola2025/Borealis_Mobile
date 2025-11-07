package com.example.borealis_mobile;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        // si se le da al boton de register aparecen el resto de campos
        // y como se le dio a ese button cambia el onClickListener del submit
        // para que haga un registro en vez de iniciar sesión
        // Button Submit tiene login de default
        // Button toogle cambia entre login y register del boton de submit
        // Button toogle cambia el texto del boton de register a login y viceversa

        View.OnClickListener toogle = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        View.OnClickListener register = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        View.OnClickListener login = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

    }


}