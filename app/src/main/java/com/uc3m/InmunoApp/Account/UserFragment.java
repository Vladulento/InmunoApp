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

import android.text.TextUtils;
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

        // Elementos de la vista

        TextView textViewUser = view.findViewById(R.id.text_user);
        Button logoutButton = view.findViewById(R.id.logoutButton);
        Button passwordButton = view.findViewById(R.id.changePasswrdButton);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button emailButton = view.findViewById(R.id.changeEmail);

        // Listeners de los botones

        logoutButton.setOnClickListener(v -> logoutUser());
        deleteButton.setOnClickListener(v -> deleteUser());
        passwordButton.setOnClickListener(v -> changePassword());
        emailButton.setOnClickListener(v -> changeEmail());

        //Base de datos en tiempo real en vez de SharedPreferences

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("userName", String.valueOf(R.string.noInfo));
        //String rol = sharedPreferences.getString("userRol", "nada");

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
                .setTitle(R.string.confirmDeleteAccount)
                .setMessage(R.string.deleteAccountQuestion)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
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
                                        Toast.makeText(getActivity(), R.string.errorDeleteAccount + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void changePassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.titleChangePassword);

        // Crear el layout para el diálogo
        LinearLayout mainLayout = new LinearLayout(getActivity());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 40, 50, 10); // Añadir padding para espacio extra

        // Layout para el primer EditText y su CheckBox
        LinearLayout newPasswordLayout = new LinearLayout(getActivity());
        newPasswordLayout.setOrientation(LinearLayout.HORIZONTAL);

        final EditText newPasswordInput = new EditText(getActivity());
        newPasswordInput.setHint(R.string.newPassword);
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
        confirmPasswordInput.setHint(R.string.confirm);
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

        builder.setPositiveButton(R.string.change, (dialogInterface, i) -> {
            String newPassword = newPasswordInput.getText().toString().trim();
            if (!newPassword.isEmpty()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), R.string.passwordChanged, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), R.string.passwordNotChanged, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(getActivity(), R.string.errorChangePassword, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getActivity(), R.string.userNotRegistered, Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.changeEmailTitle);

        // Crear el layout para el diálogo
        LinearLayout mainLayout = new LinearLayout(getActivity());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 40, 50, 10); // Añadir padding para espacio extra

        // Mostrar el email actual
        TextView currentEmailView = new TextView(getActivity());
        currentEmailView.setText(String.format(getString(R.string.current_email), user.getEmail()));
        mainLayout.addView(currentEmailView);

        // Añadir un espacio entre el texto y el primer EditText
        Space space = new Space(getActivity());
        LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 20);
        space.setLayoutParams(spaceParams);
        mainLayout.addView(space);

        // Crear el primer EditText para el nuevo email
        final EditText newEmailInput = new EditText(getActivity());
        newEmailInput.setHint(R.string.writeNewEmail);
        newEmailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        newEmailInput.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mainLayout.addView(newEmailInput);

        // Crear el segundo EditText para confirmar el nuevo email
        final EditText confirmEmailInput = new EditText(getActivity());
        confirmEmailInput.setHint(R.string.confirmNewEmail);
        confirmEmailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        confirmEmailInput.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mainLayout.addView(confirmEmailInput);

        builder.setView(mainLayout);

        builder.setPositiveButton(R.string.change, (dialogInterface, i) -> {
            String newEmail = newEmailInput.getText().toString().trim();
            String confirmEmail = confirmEmailInput.getText().toString().trim();

            if (!TextUtils.isEmpty(newEmail) && android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches() && newEmail.equals(confirmEmail)) {
                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                if (user1 != null) {
                    user1.verifyBeforeUpdateEmail(newEmail)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), String.format(getString(R.string.verification_email_sent), newEmail), Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();  // Cerrar el diálogo actual
                                    // Mostrar el diálogo nuevamente para reflejar el cambio
                                    changeEmail();
                                } else {
                                    Toast.makeText(getActivity(), R.string.errorOccurred, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } else {
                Toast.makeText(getActivity(), R.string.errorEmailChange, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}