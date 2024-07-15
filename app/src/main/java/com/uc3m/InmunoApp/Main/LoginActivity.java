package com.uc3m.InmunoApp.Main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Toast;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.uc3m.InmunoApp.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private FirebaseAuth mAuth;
    private TextInputLayout passwordTextInputLayout;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        emailTextView = findViewById(R.id.logEmail);
        passwordTextView = findViewById(R.id.logPassword);
        passwordTextInputLayout = findViewById(R.id.logPasswordLayout);
        Button btn = findViewById(R.id.loginButton);
        Button btnReg = findViewById(R.id.logToRegister);
        Button btnPassword = findViewById(R.id.forgotPassword);

        passwordTextInputLayout.setEndIconDrawable(R.drawable.hide_password_layer);

        // Listeners
        passwordTextInputLayout.setEndIconOnClickListener(v -> togglePasswordVisibility());
        btn.setOnClickListener(v -> loginUserAccount());
        btnReg.setOnClickListener(v -> openRegisterActivity());


        // Listener para manejar el evento de olvido de contraseña
        btnPassword.setOnClickListener(v -> {

            // Crear el diálogo de alerta
            final EditText resetMail = new EditText(v.getContext());
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle(R.string.changePasswordQuestion);
            passwordResetDialog.setMessage(R.string.writeEmailPassword);
            passwordResetDialog.setView(resetMail);

            // Manejo del evento
            passwordResetDialog.setPositiveButton(R.string.yes, (dialog, which) -> {
                // enviar enlace de reinicio de contraseña para el correo electrónico proporcionado
                String mail = resetMail.getText().toString();
                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, R.string.emailSent, Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, R.string.errorOccurred + e.getMessage(), Toast.LENGTH_SHORT).show());

            });

            passwordResetDialog.setNegativeButton(R.string.no, (dialog, which) -> {
                // cerrar el diálogo
            });

            passwordResetDialog.create().show();
        });
    }

    // Método para iniciar sesión en la cuenta de usuario
    private void loginUserAccount() {

        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // Comprobar que los campos no estén vacíos

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.writeEmail, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {Toast.makeText(getApplicationContext(), R.string.writePassword, Toast.LENGTH_LONG).show();
            return;
        }

        // Manejar el inicio de sesión con Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.incorrectData, Toast.LENGTH_LONG).show();
                            }
                        });
    }

    // Método para redirigir al usuario a la pantalla de registro
    private void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    // Método para mostrar u ocultar la contraseña
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordTextView.setInputType(129); // Password hidden
            passwordTextInputLayout.setEndIconDrawable(R.drawable.hide_password_layer);
        } else {
            passwordTextView.setInputType(144); // Password visible
            passwordTextInputLayout.setEndIconDrawable(R.drawable.show_password_layer);
        }
        isPasswordVisible = !isPasswordVisible;
        passwordTextView.setSelection(Objects.requireNonNull(passwordTextView.getText()).length());
    }
}