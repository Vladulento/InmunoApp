package com.uc3m.InmunoApp.Account;

import com.uc3m.InmunoApp.R;
import com.uc3m.InmunoApp.Main.LoginActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

public class UserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_user, container, false);

        TextView textViewUser = view.findViewById(R.id.text_user);
        Button logoutButton = view.findViewById(R.id.logoutButton);
        Button passwordButton = view.findViewById(R.id.changePasswrdButton);
        Button deleteButton = view.findViewById(R.id.deleteButton);

        logoutButton.setOnClickListener(v -> logoutUser());
        deleteButton.setOnClickListener(v -> deleteUser());
        passwordButton.setOnClickListener(v -> changePassword());

        //Base de datos en tiempo real en vez de SharedPreferences

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("userName", "Sin información");
        //String rol = sharedPreferences.getString("userRol", "nada");

        //Introducir vista del fragmento

        textViewUser.setText(name);

        return view;
    }

    private void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        // Redirigir al usuario a la pantalla de inicio de sesión o main activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void deleteUser(){
        // Crear una alerta de confirmación
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirmar Borrado de Cuenta")
                .setMessage("¿Estás seguro de que deseas borrar tu cuenta? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Proceder con el borrado de la cuenta
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Redirigir al usuario a la pantalla de inicio de sesión o main activity
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        requireActivity().finish();
                                    } else {
                                        // Manejar el error
                                        Toast.makeText(getActivity(), "Error al borrar la cuenta: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void changePassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cambio de contraseña");

        // Crear el layout para el diálogo
        LinearLayout mainLayout = new LinearLayout(getActivity());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 40, 50, 10); // Añadir padding para espacio extra

        // Layout para el primer EditText y su CheckBox
        LinearLayout newPasswordLayout = new LinearLayout(getActivity());
        newPasswordLayout.setOrientation(LinearLayout.HORIZONTAL);

        final EditText newPasswordInput = new EditText(getActivity());
        newPasswordInput.setHint("Nueva contraseña");
        newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final CheckBox showNewPasswordCheckBox = new CheckBox(getActivity());
        showNewPasswordCheckBox.setButtonDrawable(R.drawable.checkbox_password);

        newPasswordLayout.addView(newPasswordInput, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        newPasswordLayout.addView(showNewPasswordCheckBox);

        mainLayout.addView(newPasswordLayout);

        // Añadir un espacio entre los EditTexts
        Space space = new Space(getActivity());
        LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 20);
        space.setLayoutParams(spaceParams);
        mainLayout.addView(space);

        // Layout para el segundo EditText y su CheckBox
        LinearLayout confirmPasswordLayout = new LinearLayout(getActivity());
        confirmPasswordLayout.setOrientation(LinearLayout.HORIZONTAL);

        final EditText confirmPasswordInput = new EditText(getActivity());
        confirmPasswordInput.setHint("Confirmar");
        confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        final CheckBox showConfirmPasswordCheckBox = new CheckBox(getActivity());
        showConfirmPasswordCheckBox.setButtonDrawable(R.drawable.checkbox_password);

        confirmPasswordLayout.addView(confirmPasswordInput, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        confirmPasswordLayout.addView(showConfirmPasswordCheckBox);

        mainLayout.addView(confirmPasswordLayout);

        // Listener para el CheckBox del nuevo password
        showNewPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                newPasswordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                newPasswordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            // Mover el cursor al final
            newPasswordInput.setSelection(newPasswordInput.getText().length());
        });

        // Listener para el CheckBox de confirmación de password
        showConfirmPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                confirmPasswordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                confirmPasswordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            // Mover el cursor al final
            confirmPasswordInput.setSelection(confirmPasswordInput.getText().length());
        });

        builder.setView(mainLayout);

        builder.setPositiveButton("Cambiar", (dialogInterface, i) -> {
            String newPassword = newPasswordInput.getText().toString().trim();
            if (!newPassword.isEmpty()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Contraseña cambiada con éxito", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Escoja otra contraseña y revise los campos", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(getActivity(), "Las contraseñas no coinciden o están vacias", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}