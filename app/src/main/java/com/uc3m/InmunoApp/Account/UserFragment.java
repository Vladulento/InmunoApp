package com.uc3m.InmunoApp.Account;

import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.uc3m.InmunoApp.R;
import com.uc3m.InmunoApp.Main.LoginActivity;

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

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText newPasswordInput = new EditText(getActivity());
        newPasswordInput.setHint("Introduzca su nueva contraseña");
        linearLayout.addView(newPasswordInput);

        builder.setView(linearLayout);

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
                                    Toast.makeText(getActivity(), "¡Ha surgido algún error!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(getActivity(), "Rellene el campo de nueva contraseña ", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}