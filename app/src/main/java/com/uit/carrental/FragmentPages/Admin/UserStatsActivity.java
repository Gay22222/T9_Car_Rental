package com.uit.carrental.FragmentPages.Admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.uit.carrental.R;

public class UserStatsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView totalUsersText, verifiedUsersText, blockedUsersText;
    private TextView roleFilterText, weekFilterText, monthFilterText;
    private String currentRoleFilter = "all"; // Mặc định: Tất cả
    private String currentTimeFilter = "week"; // Mặc định: Tuần này

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        totalUsersText = findViewById(R.id.rbnyyf0spm7); // Tổng người dùng
        verifiedUsersText = findViewById(R.id.rhnirr1sgd5v); // Đã xác minh
        blockedUsersText = findViewById(R.id.r16az2h4g5wq); // Bị khóa
        roleFilterText = findViewById(R.id.r2wdtx5r7t1z); // Lọc vai trò
        weekFilterText = findViewById(R.id.ri2lrieii73); // Tuần này
        monthFilterText = findViewById(R.id.rqf6h1ypj6yc); // Tháng này

        // Load images with Glide
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/6u69svtl_expires_30_days.png")
                .into((ImageView) findViewById(R.id.r44o74nwoz3c));
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/x890qmzm_expires_30_days.png")
                .into((ImageView) findViewById(R.id.r7pj51g0csi3));
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/01lxgpk7_expires_30_days.png")
                .into((ImageView) findViewById(R.id.rtpvn3lra5m));

        // Setup click listeners for filters
        findViewById(R.id.rgcqxgwhq1ni).setOnClickListener(v -> {
            toggleRoleFilter();
            loadUserStats();
        });

        findViewById(R.id.rnlm6qxgv01).setOnClickListener(v -> {
            currentTimeFilter = "week";
            updateTimeFilterUI();
            loadUserStats();
        });

        findViewById(R.id.ranjw4ubsk1p).setOnClickListener(v -> {
            currentTimeFilter = "month";
            updateTimeFilterUI();
            loadUserStats();
        });

        // Load initial stats
        loadUserStats();
    }

    private void toggleRoleFilter() {
        switch (currentRoleFilter) {
            case "all":
                currentRoleFilter = "admin";
                roleFilterText.setText("Admin");
                break;
            case "admin":
                currentRoleFilter = "owner";
                roleFilterText.setText("Chủ xe");
                break;
            case "owner":
                currentRoleFilter = "renter";
                roleFilterText.setText("Người thuê");
                break;
            case "renter":
                currentRoleFilter = "all";
                roleFilterText.setText("Tất cả");
                break;
        }
    }

    private void updateTimeFilterUI() {
        if (currentTimeFilter.equals("week")) {
            weekFilterText.setBackgroundResource(R.drawable.cr8b0b54da5e);
            monthFilterText.setBackgroundResource(R.drawable.cr8b00308726);
        } else {
            weekFilterText.setBackgroundResource(R.drawable.cr8b00308726);
            monthFilterText.setBackgroundResource(R.drawable.cr8b0b54da5e);
        }
    }

    private void loadUserStats() {
        long timeThreshold = currentTimeFilter.equals("week")
                ? System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
                : System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000;

        // Tổng người dùng
        Query totalQuery = db.collection("Users");
        if (!currentRoleFilter.equals("all")) {
            totalQuery = totalQuery.whereEqualTo("roles." + currentRoleFilter, true);
        }
        totalQuery.whereGreaterThan("createdAt", timeThreshold)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    totalUsersText.setText(String.format("%,d", querySnapshot.size()));
                })
                .addOnFailureListener(e -> {
                    totalUsersText.setText("0");
                });

        // Người dùng đã xác minh
        Query verifiedQuery = db.collection("Users")
                .whereEqualTo("verificationStatus", "verified");
        if (!currentRoleFilter.equals("all")) {
            verifiedQuery = verifiedQuery.whereEqualTo("roles." + currentRoleFilter, true);
        }
        verifiedQuery.whereGreaterThan("createdAt", timeThreshold)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    verifiedUsersText.setText(String.format("%,d", querySnapshot.size()));
                })
                .addOnFailureListener(e -> {
                    verifiedUsersText.setText("0");
                });

        // Người dùng bị khóa
        Query blockedQuery = db.collection("Users")
                .whereEqualTo("verificationStatus", "blocked");
        if (!currentRoleFilter.equals("all")) {
            blockedQuery = blockedQuery.whereEqualTo("roles." + currentRoleFilter, true);
        }
        blockedQuery.whereGreaterThan("createdAt", timeThreshold)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    blockedUsersText.setText(String.format("%,d", querySnapshot.size()));
                })
                .addOnFailureListener(e -> {
                    blockedUsersText.setText("0");
                });
    }
}
