package com.uit.carrental.ActivityPages;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.uit.carrental.FragmentPages.Owner.OwnerActivityFragment;
import com.uit.carrental.FragmentPages.Owner.OwnerNotificationsFragment;
import com.uit.carrental.FragmentPages.Owner.OwnerSettingFragment;
import com.uit.carrental.FragmentPages.Owner.OwnerVehicleFragment;
import com.uit.carrental.R;
import com.uit.carrental.databinding.OwnerActivityMainBinding;

public class OwnerMainActivity extends AppCompatActivity {

    OwnerActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OwnerActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        replaceFragment(new OwnerVehicleFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.vehicle) {
                replaceFragment(new OwnerVehicleFragment());
            } else if (itemId == R.id.activity) {
                replaceFragment(new OwnerActivityFragment());
            } else if (itemId == R.id.notifications) {
                replaceFragment(new OwnerNotificationsFragment());
            } else if (itemId == R.id.setting) {
                replaceFragment(new OwnerSettingFragment());
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_owner, fragment);
        fragmentTransaction.commit();
    }
}