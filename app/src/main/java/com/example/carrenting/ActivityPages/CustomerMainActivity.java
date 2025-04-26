package com.example.carrenting.ActivityPages;
import com.google.android.material.navigation.NavigationBarView;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import androidx.annotation.NonNull;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.carrenting.FragmentPages.Customer.CustomerActivityFragment;
import com.example.carrenting.FragmentPages.Customer.CustomerHomeFragment;
import com.example.carrenting.FragmentPages.Customer.CustomerNotificationFragment;
import com.example.carrenting.FragmentPages.Customer.CustomerSettingFragment;
import com.example.carrenting.R;
import com.example.carrenting.databinding.CustomerActivityMainBinding;

public class CustomerMainActivity extends AppCompatActivity{

   CustomerActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = CustomerActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new CustomerHomeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.home) {
                    replaceFragment(new CustomerHomeFragment());
                    return true;
                } else if (id == R.id.activity) {
                    replaceFragment(new CustomerActivityFragment());
                    return true;
                } else if (id == R.id.notifications) {
                    replaceFragment(new CustomerNotificationFragment());
                    return true;
                } else if (id == R.id.setting) {
                    replaceFragment(new CustomerSettingFragment());
                    return true;
                }
                return false;

            }
        });

    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_customer, fragment);
        fragmentTransaction.commit();
    }

}