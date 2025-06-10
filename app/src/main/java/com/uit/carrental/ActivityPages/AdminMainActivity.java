package com.uit.carrental.ActivityPages;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uit.carrental.FragmentPages.Admin.AdminSettingsFragment;
import com.uit.carrental.FragmentPages.Admin.AdminStatisticsFragment;
import com.uit.carrental.FragmentPages.Admin.AdminUserListFragment;
import com.uit.carrental.FragmentPages.Admin.AdminVehicleListFragment;
import com.uit.carrental.R;

public class AdminMainActivity extends AppCompatActivity {

    private static final String TAG_USERS = "users";
    private static final String TAG_VEHICLES = "vehicles";
    private static final String TAG_STATISTICS = "statistics";
    private static final String TAG_SETTINGS = "settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            String tag;
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.users) {
                tag = TAG_USERS;
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_USERS);
                if (fragment == null) fragment = new AdminUserListFragment();
            } else if (itemId == R.id.vehicles) {
                tag = TAG_VEHICLES;
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_VEHICLES);
                if (fragment == null) fragment = new AdminVehicleListFragment();
            } else if (itemId == R.id.statistics) {
                tag = TAG_STATISTICS;
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_STATISTICS);
                if (fragment == null) fragment = new AdminStatisticsFragment();
            } else if (itemId == R.id.settings) {
                tag = TAG_SETTINGS;
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_SETTINGS);
                if (fragment == null) fragment = new AdminSettingsFragment();
            } else {
                return false;
            }

            showFragment(fragment, tag);
            return true;
        });

        // Load default (Users)
        if (savedInstanceState == null) {
            showFragment(new AdminUserListFragment(), TAG_USERS);
            bottomNavigationView.setSelectedItemId(R.id.users);
        }
    }

    private void showFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout_admin, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }
}