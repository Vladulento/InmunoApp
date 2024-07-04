package com.uc3m.InmunoApp.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uc3m.InmunoApp.PatientsList.PatientsFragment;
import com.uc3m.InmunoApp.R;
import com.uc3m.InmunoApp.Account.UserFragment;


public class HomeActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // Set up bottom navigation

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                Fragment selectedFragment = null;

                if(item.getItemId() == R.id.nav_patients){
                    selectedFragment = new PatientsFragment();

                }else if(item.getItemId() == R.id.nav_user){
                    selectedFragment = new UserFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            });

            // Set default selection
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PatientsFragment()).commit();
                bottomNavigationView.setSelectedItemId(R.id.nav_patients);
            }
        }
}
