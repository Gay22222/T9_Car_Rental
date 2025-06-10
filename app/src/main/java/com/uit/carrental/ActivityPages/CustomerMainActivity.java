package com.uit.carrental.ActivityPages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "CustomerMainActivity";
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

        // Xử lý intent từ các activity khác
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String fragment = intent.getStringExtra("fragment");
        Log.d(TAG, "Xử lý intent, fragment: " + fragment);
        Fragment selectedFragment = null;
        String tag = TAG_HOME;
        int navItemId = R.id.home;

        if ("home".equals(fragment)) {
            selectedFragment = getSupportFragmentManager().findFragmentByTag(TAG_HOME);
            if (selectedFragment == null) selectedFragment = new CustomerHomeFragment();
            tag = TAG_HOME;
            navItemId = R.id.home;
        } else if ("notifications".equals(fragment)) {
            selectedFragment = getSupportFragmentManager().findFragmentByTag(TAG_NOTIFICATIONS);
            if (selectedFragment == null) selectedFragment = new CustomerNotificationFragment();
            tag = TAG_NOTIFICATIONS;
            navItemId = R.id.notifications;
        } else if ("activity".equals(fragment)) {
            selectedFragment = getSupportFragmentManager().findFragmentByTag(TAG_ACTIVITY);
            if (selectedFragment == null) selectedFragment = new CustomerActivityFragment();
            tag = TAG_ACTIVITY;
            navItemId = R.id.activity;
        } else if ("setting".equals(fragment)) {
            selectedFragment = getSupportFragmentManager().findFragmentByTag(TAG_SETTING);
            if (selectedFragment == null) selectedFragment = new CustomerSettingFragment();
            tag = TAG_SETTING;
            navItemId = R.id.setting;
        } else if (getSupportFragmentManager().findFragmentByTag(TAG_HOME) == null) {
            selectedFragment = new CustomerHomeFragment();
        }

        if (selectedFragment != null) {
            showFragment(selectedFragment, tag);
            binding.bottomNavigationView.setSelectedItemId(navItemId);
        }
    }

    private void showFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout_customer, fragment, tag)
                .commit();
    }
}