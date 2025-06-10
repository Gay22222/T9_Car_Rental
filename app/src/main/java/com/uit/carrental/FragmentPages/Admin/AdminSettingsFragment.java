package com.uit.carrental.FragmentPages.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.carrental.R;
import com.uit.carrental.Service.UserAuthentication.LoginActivity;

public class AdminSettingsFragment extends Fragment {

    private ImageView imageViewAvatar, imageViewEdit;
    private TextView textViewName, textViewEdit;
    private View layoutAdminAccount, layoutCommission, layoutNotifications, layoutAdminHistory, layoutSignOut;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_settings, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        imageViewAvatar = view.findViewById(R.id.rqqkbht2nw9);
        imageViewEdit = view.findViewById(R.id.ryhyrsup3suq);
        textViewName = view.findViewById(R.id.rzxbt2b9du3b);
        textViewEdit = view.findViewById(R.id.rwemqtod737b);
        layoutAdminAccount = view.findViewById(R.id.layout_admin_account);
        layoutCommission = view.findViewById(R.id.layout_commission);
        layoutNotifications = view.findViewById(R.id.layout_notifications);
        layoutAdminHistory = view.findViewById(R.id.layout_admin_history);
        layoutSignOut = view.findViewById(R.id.r6uqs59f1co);

        // Set text for item_setting_row
        setupSettingRow(layoutAdminAccount, "Quản lý tài khoản quản trị viên");
        setupSettingRow(layoutCommission, "Cấu hình hoa hồng sàn");
        setupSettingRow(layoutNotifications, "Cấu hình hệ thống thông báo");
        setupSettingRow(layoutAdminHistory, "Lịch sử thao tác admin");

        // Load admin info
        loadAdminInfo();

        // Setup click listeners
        textViewEdit.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chỉnh sửa thông tin admin", Toast.LENGTH_SHORT).show();
        });

        layoutAdminAccount.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Quản lý tài khoản quản trị viên", Toast.LENGTH_SHORT).show();
        });

        layoutCommission.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cấu hình hoa hồng sàn", Toast.LENGTH_SHORT).show();
        });

        layoutNotifications.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Cấu hình hệ thống thông báo", Toast.LENGTH_SHORT).show();
        });

        layoutAdminHistory.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Lịch sử thao tác admin", Toast.LENGTH_SHORT).show();
        });

        layoutSignOut.setOnClickListener(v -> signOut());

        return view;
    }

    private void setupSettingRow(View layout, String text) {
        TextView textView = layout.findViewById(R.id.setting_text);
        if (textView != null) {
            textView.setText(text);
        }
    }

    private void loadAdminInfo() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            db.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            String avatarUrl = documentSnapshot.getString("avatarUrl");
                            textViewName.setText(username != null ? username : "Admin");
                            if (avatarUrl != null && !avatarUrl.isEmpty() && getContext() != null) {
                                Glide.with(getContext()).load(avatarUrl).into(imageViewAvatar);
                            }
                        } else {
                            loadMockAdminInfo();
                        }
                    })
                    .addOnFailureListener(e -> loadMockAdminInfo());
        } else {
            loadMockAdminInfo();
        }
    }

    private void loadMockAdminInfo() {
        textViewName.setText("Chị em chúng mình");
        if (getContext() != null) {
            Glide.with(getContext()).load(R.drawable.ic_person).into(imageViewAvatar);
        }
    }

    private void signOut() {
        auth.signOut();
        if (getActivity() != null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }
}
