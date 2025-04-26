//package com.example.carrenting.ActivityPages;
//
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.example.carrenting.FragmentPages.Owner.OwnerActivityFragment;
//import com.example.carrenting.FragmentPages.Owner.OwnerNotificationsFragment;
//import com.example.carrenting.FragmentPages.Owner.OwnerSettingFragment;
//import com.example.carrenting.FragmentPages.Owner.OwnerVehicleFragment;
//import com.example.carrenting.R;
//import com.example.carrenting.databinding.OwnerActivityMainBinding;
//
//public class OwnerMainActivity extends AppCompatActivity {
//
//    OwnerActivityMainBinding binding;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = OwnerActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        replaceFragment(new OwnerVehicleFragment());
//        binding.bottomNavigationView.setBackground(null);
//
//        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
//            int id = item.getItemId();
//
//            if (id == R.id.vehicle) {
//                replaceFragment(new OwnerVehicleFragment());
//            } else if (id == R.id.activity) {
//                replaceFragment(new OwnerActivityFragment());
//            } else if (id == R.id.notifications) {
//                replaceFragment(new OwnerNotificationsFragment());
//            } else if (id == R.id.setting) {
//                replaceFragment(new OwnerSettingFragment());
//            }
//
//            return true;
//        });
//
//    }
//
//    private void replaceFragment(Fragment fragment)
//    {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout_owner, fragment);
//        fragmentTransaction.commit();
//    }
//}

package com.example.carrenting.ActivityPages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.carrenting.FragmentPages.Owner.OwnerActivityFragment;
import com.example.carrenting.FragmentPages.Owner.OwnerNotificationsFragment;
import com.example.carrenting.FragmentPages.Owner.OwnerSettingFragment;
import com.example.carrenting.FragmentPages.Owner.OwnerVehicleFragment;
import com.example.carrenting.R;
import com.example.carrenting.databinding.OwnerActivityMainBinding;

public class OwnerMainActivity extends AppCompatActivity {

    OwnerActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        String role = getIntent().getStringExtra("role");
        if (role == null || !role.equals("owner")) {
            Toast.makeText(this, "Không có quyền truy cập giao diện nhà cung cấp", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding = OwnerActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new OwnerVehicleFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.vehicle) {
                replaceFragment(new OwnerVehicleFragment());
            } else if (id == R.id.activity) {
                replaceFragment(new OwnerActivityFragment());
            } else if (id == R.id.notifications) {
                replaceFragment(new OwnerNotificationsFragment());
            } else if (id == R.id.setting) {
                replaceFragment(new OwnerSettingFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_owner, fragment);
        fragmentTransaction.commit();
    }
}
