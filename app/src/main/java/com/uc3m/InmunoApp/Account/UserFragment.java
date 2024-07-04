package com.uc3m.InmunoApp.Account;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.SharedPreferences;

import com.uc3m.InmunoApp.R;

public class UserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.fragment_user, container, false);

        TextView textViewUser = view.findViewById(R.id.text_user);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("userName", "nada");
        //String rol = sharedPreferences.getString("userRol", "nada");

        textViewUser.setText(name);


        return view;
    }


}