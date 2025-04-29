package com.uit.carrental.ActivityPages;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.uit.carrental.FragmentPages.Customer.CustomerActivityFragment;
import com.uit.carrental.FragmentPages.Customer.CustomerHomeFragment;
import com.uit.carrental.FragmentPages.Customer.CustomerNotificationFragment;
import com.uit.carrental.FragmentPages.Customer.CustomerSettingFragment;
import com.uit.carrental.R;
import com.uit.carrental.databinding.CustomerActivityMainBinding;

public class CustomerMainActivity extends AppCompatActivity{

    CustomerActivityMainBinding binding;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = CustomerActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new CustomerHomeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(new CustomerHomeFragment());
            } else if (itemId == R.id.activity) {
                replaceFragment(new CustomerActivityFragment());
            } else if (itemId == R.id.notifications) {
                replaceFragment(new CustomerNotificationFragment());
            } else if (itemId == R.id.setting) {
                replaceFragment(new CustomerSettingFragment());
            }

            return true;
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