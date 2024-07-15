package com.uc3m.InmunoApp.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.uc3m.InmunoApp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Comprobamos la autentificación del usuario

        if (mAuth.getCurrentUser() != null) {

            // Rellenar aquí comprobación para paciente y llevar a la aplicación de María

            // else{
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            // }

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}