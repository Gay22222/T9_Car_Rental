package com.uit.carrental.ActivityPages;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uit.carrental.FragmentPages.Customer.CustomerActivityFragment;
import com.uit.carrental.FragmentPages.Customer.CustomerHomeFragment;
import com.uit.carrental.FragmentPages.Customer.CustomerNotificationFragment;
import com.uit.carrental.FragmentPages.Customer.CustomerSettingFragment;
import com.uit.carrental.R;
import com.uit.carrental.databinding.CustomerActivityMainBinding;

public class CustomerMainActivity extends AppCompatActivity {

    private CustomerActivityMainBinding binding;
    private static final String TAG_HOME = "home";
    private static final String TAG_ACTIVITY = "activity";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTING = "setting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            String tag;
            Fragment fragment;
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                tag = TAG_HOME;
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_HOME);
                if (fragment == null) fragment = new CustomerHomeFragment();
            } else if (itemId == R.id.activity) {
                tag = TAG_ACTIVITY;
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_ACTIVITY);
                if (fragment == null) fragment = new CustomerActivityFragment();
            } else if (itemId == R.id.notifications) {
                tag = TAG_NOTIFICATIONS;
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_NOTIFICATIONS);
                if (fragment == null) fragment = new CustomerNotificationFragment();
            } else if (itemId == R.id.setting) {
                tag = TAG_SETTING;
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_SETTING);
                if (fragment == null) fragment = new CustomerSettingFragment();
            } else {
                return false;
            }

            showFragment(fragment, tag);
            return true;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            showFragment(new CustomerHomeFragment(), TAG_HOME);
            binding.bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }

    private void showFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout_customer, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }
}