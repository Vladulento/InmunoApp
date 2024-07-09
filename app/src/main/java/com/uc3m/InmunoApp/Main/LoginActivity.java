package com.uc3m.InmunoApp.Main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.Button;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

import com.google.firebase.auth.FirebaseAuth;
import com.uc3m.InmunoApp.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Initialising all views through id defined above
        emailTextView = findViewById(R.id.logEmail);
        passwordTextView = findViewById(R.id.logPassword);
        CheckBox showPassword = findViewById(R.id.showLogPassword);
        Button btn = findViewById(R.id.loginButton);
        Button btnReg = findViewById(R.id.logToRegister);
        Button btnPassword = findViewById(R.id.forgotPassword);

        // Listeners

        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> onCheckedChanged(isChecked));
        btn.setOnClickListener(v -> loginUserAccount());
        btnReg.setOnClickListener(v -> openRegisterActivity());

        btnPassword.setOnClickListener(v -> {

            final EditText resetMail = new EditText(v.getContext());
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle(R.string.changePasswordQuestion);
            passwordResetDialog.setMessage(R.string.writeEmailPassword);
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton(R.string.yes, (dialog, which) -> {
                // extract the email and send reset link
                String mail = resetMail.getText().toString();
                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, R.string.emailSent, Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, R.string.errorOccurred + e.getMessage(), Toast.LENGTH_SHORT).show());

            });

            passwordResetDialog.setNegativeButton(R.string.no, (dialog, which) -> {
                // close the dialog
            });

            passwordResetDialog.create().show();

        });
    }

    private void loginUserAccount() {

        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // validations for input email and password

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.writeEmail, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {Toast.makeText(getApplicationContext(), R.string.writePassword, Toast.LENGTH_LONG).show();
            return;
        }

        // sign in existing user

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            else {
                                Toast.makeText(getApplicationContext(), R.string.incorrectData, Toast.LENGTH_LONG).show();
                            }
                        });
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
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
}