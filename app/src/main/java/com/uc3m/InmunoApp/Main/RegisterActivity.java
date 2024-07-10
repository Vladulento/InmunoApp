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

        // Inicializar vistas
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

    // Método para registrar un nuevo usuario
    private void registerNewUser() {

        // Pasar a variable cada campo
        String email, password, name, confirmPassword, number;
        email = emailTextView.getText().toString();
        number = numberTextView.getText().toString();
        password = passwordTextView.getText().toString();
        name = nameTextView.getText().toString();
        confirmPassword = confirmPasswordTextView.getText().toString();
        int selectedRadioButtonId = rolRadioGroup.getCheckedRadioButtonId();

        // Comprobaciones para el correo electrónico
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.writeEmail, Toast.LENGTH_LONG).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), R.string.validEmail, Toast.LENGTH_LONG).show();
            return;
        }

        // Comprobaciones para el número de teléfono
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(getApplicationContext(), R.string.writeNumber, Toast.LENGTH_LONG).show();
            return;
        }
        if (number.length() != 9) {
            Toast.makeText(getApplicationContext(), R.string.validNumber, Toast.LENGTH_LONG).show();
            return;
        }

        // Comprobaciones para la contraseña
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.writePassword, Toast.LENGTH_LONG).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), R.string.shortPassword, Toast.LENGTH_LONG).show();
            return;
        }

        // Comprobar que el campo de nombre no esté vacío
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), R.string.writeName, Toast.LENGTH_LONG).show();
            return;
        }

        // Comprobaciones para la confirmación de la contraseña
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getApplicationContext(), R.string.confirmPassword, Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), R.string.validPassword, Toast.LENGTH_LONG).show();
            return;
        }

        // Comprobar que se ha seleccionado un rol
        if (selectedRadioButtonId == -1) {
            Toast.makeText(getApplicationContext(), R.string.selectRol, Toast.LENGTH_LONG).show();
            return;
        }

        // Cambiar a BBDD en tiempo real

        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String rol = selectedRadioButton.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("userName", R.string.hi + name + "!");

        myEdit.apply();

        // Manejar el registro del nuevo usuario con Firebase, mediante correo electrónico y contraseña
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : String.valueOf(R.string.registerFailed);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Redigir al usuario a la pantalla de inicio de sesión
    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Método para mostrar u ocultar la contraseña
    private void onCheckedChanged(boolean isChecked) {
        if (isChecked) {
            passwordTextView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            passwordTextView.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    // Método para mostrar u ocultar la contraseña de confirmación
    private void onCheckedChanged2(boolean isChecked) {
        if (isChecked) {
            confirmPasswordTextView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            confirmPasswordTextView.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}