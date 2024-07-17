package com.uc3m.InmunoApp.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;

import android.text.TextUtils;

import android.util.Patterns;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.uc3m.InmunoApp.R;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText nameTextView, emailTextView, numberTextView, passwordTextView, confirmPasswordTextView, heightTextView, weightTextView, ageTextView;
    private TextInputLayout passwordTextInputLayout, confirmPasswordTextInputLayout;
    private RadioGroup rolRadioGroup;
    private LinearLayout patientFields;
    private Spinner genderSpinner;
    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

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
        patientFields = findViewById(R.id.patientFields);
        heightTextView = findViewById(R.id.regHeight);
        weightTextView = findViewById(R.id.regWeight);
        ageTextView = findViewById(R.id.regDateBirth);
        genderSpinner = findViewById(R.id.genderSpinner);
        heightTextView = findViewById(R.id.regHeight);
        weightTextView = findViewById(R.id.regWeight);
        ageTextView = findViewById(R.id.regDateBirth);

        passwordTextInputLayout = findViewById(R.id.regPasswordLayout);
        confirmPasswordTextInputLayout = findViewById(R.id.regPasswordLayout2);

        Button btn = findViewById(R.id.registerButton);
        Button btnLogin = findViewById(R.id.goLoginButton);

        // Establecer el icono inicial de visibilidad de la contraseña
        passwordTextInputLayout.setEndIconDrawable(R.drawable.hide_password_layer);
        confirmPasswordTextInputLayout.setEndIconDrawable(R.drawable.hide_password_layer);

        // Configurar el Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genderArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        //Listeners
        passwordTextInputLayout.setEndIconOnClickListener(v -> togglePasswordVisibility());
        confirmPasswordTextInputLayout.setEndIconOnClickListener(v -> toggleConfirmPasswordVisibility());
        btn.setOnClickListener(v -> registerNewUser());
        btnLogin.setOnClickListener(v -> openLoginActivity());

        rolRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.Paciente) {
                patientFields.setVisibility(View.VISIBLE);
            } else {
                patientFields.setVisibility(View.GONE);
            }
        });
    }

    // Método para registrar un nuevo usuario
    private void registerNewUser() {

        // Pasar a variable cada campo
        String email, password, name, confirmPassword, number, height, weight, dateBirth;
        email = Objects.requireNonNull(emailTextView.getText()).toString().trim();
        number = Objects.requireNonNull(numberTextView.getText()).toString().trim();
        password = Objects.requireNonNull(passwordTextView.getText()).toString().trim();
        name = Objects.requireNonNull(nameTextView.getText()).toString().trim();
        confirmPassword = Objects.requireNonNull(confirmPasswordTextView.getText()).toString().trim();
        height = Objects.requireNonNull(heightTextView.getText()).toString().trim();
        weight = Objects.requireNonNull(weightTextView.getText()).toString().trim();
        dateBirth = Objects.requireNonNull(ageTextView.getText()).toString().trim();
        //gender = genderSpinner.getSelectedItem().toString().trim();

        int selectedRadioButtonId = rolRadioGroup.getCheckedRadioButtonId();
        String dateBirthFormat = "\\d{2}/\\d{2}/\\d{4}";

        // Comprobar que el campo de nombre no esté vacío
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), R.string.writeName, Toast.LENGTH_LONG).show();
            return;
        }

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

        // Comprobar los campos del paciente
        if (selectedRadioButtonId == R.id.Paciente){

            // Comprobar que se ha seleccionado un género
            if (TextUtils.isEmpty(height)) {
                Toast.makeText(getApplicationContext(), R.string.writeHeight, Toast.LENGTH_LONG).show();
                return;
            }

            // Comprobar que se ha seleccionado un género
            if (TextUtils.isEmpty(weight)) {
                Toast.makeText(getApplicationContext(), R.string.writeWeight, Toast.LENGTH_LONG).show();
                return;
            }

            // Comprobaciones para la fecha de nacimiento
            if (TextUtils.isEmpty(dateBirth)) {
                Toast.makeText(getApplicationContext(), R.string.writeAge, Toast.LENGTH_LONG).show();
                return;
            }
            if(!dateBirth.matches(dateBirthFormat)){
                Toast.makeText(getApplicationContext(), R.string.validAge, Toast.LENGTH_LONG).show();
                return;
            }

        }

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

    // Método para mostrar u ocultar la contraseña de confirmación
    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            confirmPasswordTextView.setInputType(129); // Password hidden
            confirmPasswordTextInputLayout.setEndIconDrawable(R.drawable.hide_password_layer);
        } else {
            confirmPasswordTextView.setInputType(144); // Password visible
            confirmPasswordTextInputLayout.setEndIconDrawable(R.drawable.show_password_layer);
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        confirmPasswordTextView.setSelection(Objects.requireNonNull(confirmPasswordTextView.getText()).length());
    }
}