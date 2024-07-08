package com.uc3m.InmunoApp.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.TextUtils;

import android.util.Patterns;

import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.firebase.auth.FirebaseAuth;
import com.uc3m.InmunoApp.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameTextView, emailTextView, numberTextView, passwordTextView, confirmPasswordTextView;
    private RadioGroup rolRadioGroup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        nameTextView = findViewById(R.id.regName);
        emailTextView = findViewById(R.id.regEmail);
        numberTextView = findViewById(R.id.regNumber);
        passwordTextView = findViewById(R.id.regPassword);
        confirmPasswordTextView = findViewById(R.id.regPassword2);
        rolRadioGroup = findViewById(R.id.regRol);
        CheckBox showPassword = findViewById(R.id.showRegPassword);
        CheckBox showPassword2 = findViewById(R.id.showRegPassword2);
        Button btn = findViewById(R.id.registerButton);
        Button btnLogin = findViewById(R.id.goLoginButton);

        //Listeners

        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedChanged(isChecked));
        showPassword2.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedChanged2(isChecked));
        btn.setOnClickListener(v -> registerNewUser());
        btnLogin.setOnClickListener(v -> openLoginActivity());
    }
    private void registerNewUser() {

        // Check every field

        String email, password, name, confirmPassword, number;
        email = emailTextView.getText().toString();
        number = numberTextView.getText().toString();
        password = passwordTextView.getText().toString();
        name = nameTextView.getText().toString();
        confirmPassword = confirmPasswordTextView.getText().toString();
        int selectedRadioButtonId = rolRadioGroup.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Introduzca su correo electrónico", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "Introduzca un correo electrónico válido", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(number)) {
            Toast.makeText(getApplicationContext(), "Introduzca su número de teléfono", Toast.LENGTH_LONG).show();
            return;
        }
        if (number.length() != 9) {
            Toast.makeText(getApplicationContext(), "Introduzca un número de teléfono válido", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Introduzca su contraseña, por favor", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Introduzca su nombre, por favor", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Confirme su contraseña, por favor", Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedRadioButtonId == -1) {
            Toast.makeText(getApplicationContext(), "Por favor seleccione su rol", Toast.LENGTH_LONG).show();
            return;
        }

        // SharedPreferences para guardar el nombre y el rol

        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String rol = selectedRadioButton.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("userName", "¡Hola, " + name + "!");
        myEdit.putString("userRol", "Tu rol es " + rol + ".");
        myEdit.apply();

        // Create user with email and password

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registro fallido, vuelva a intentarlo más tarde";
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void onCheckedChanged(boolean isChecked) {
        if (isChecked) {
            passwordTextView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            passwordTextView.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
    private void onCheckedChanged2(boolean isChecked) {
        if (isChecked) {
            confirmPasswordTextView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            confirmPasswordTextView.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}