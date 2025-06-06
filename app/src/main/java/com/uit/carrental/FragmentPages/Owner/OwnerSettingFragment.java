package com.uit.carrental.FragmentPages.Owner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.uit.carrental.ActivityPages.CustomerMainActivity;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import com.uit.carrental.Service.UserAuthentication.ForgotPasswordActivity;
import com.uit.carrental.Service.UserAuthentication.LoginActivity;
import com.uit.carrental.Service.UserAuthentication.ProfileManagement;

import de.hdodenhof.circleimageview.CircleImageView;

public class OwnerSettingFragment extends Fragment {

    private TextView tvName;
    private CircleImageView imgAvatar;
    private TextView tvEdit;
    private ImageView editIcon;

    private FirebaseFirestore dtb_user;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private User user = new User();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.owner_fragment_setting, container, false);

        // Initialize views
        imgAvatar = view.findViewById(R.id.img_avatar);
        tvName = view.findViewById(R.id.tv_name);
        tvEdit = view.findViewById(R.id.tv_edit);
        editIcon = view.findViewById(R.id.edit_icon);

        // Initialize setting items
        View layoutInformation = view.findViewById(R.id.layout_information);
        View layoutSwitchToCustomer = view.findViewById(R.id.layout_switch_to_customer);
        View layoutChangePassword = view.findViewById(R.id.layout_change_password);
        View layoutDeleteAccount = view.findViewById(R.id.layout_delete_account);
        View layoutSettings = view.findViewById(R.id.layout_settings);
        View layoutSignOut = view.findViewById(R.id.layout_sign_out);

        // Set icons and texts for setting items
        setupSettingItem(layoutInformation, R.drawable.vector20_settings, "Thông tin tài khoản");
        setupSettingItem(layoutSwitchToCustomer, R.drawable.vector17_settings, "Giao diện khách hàng");
        setupSettingItem(layoutChangePassword, R.drawable.vector8_settings, "Thay đổi mật khẩu");
        setupSettingItemWithOverlay(layoutDeleteAccount, R.drawable.vector11_settings, R.drawable.vector10_settings, "Yêu cầu xóa tài khoản");
        setupSettingItem(layoutSettings, R.drawable.vector13_settings, "Cài đặt");

        dtb_user = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        user.setUser_id(firebaseUser != null ? firebaseUser.getUid() : "");

        if (firebaseUser != null) {
            dtb_user.collection("Users")
                    .document(firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            String avatarURL = documentSnapshot.getString("avatarURL");

                            if (username != null) {
                                tvName.setText(username);
                            }
                            if (avatarURL != null && !avatarURL.isEmpty()) {
                                Picasso.get().load(avatarURL).into(imgAvatar);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(view.getContext(), "Không thể lấy thông tin.", Toast.LENGTH_SHORT).show();
                    });
        }

        // Click listeners
        tvEdit.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileManagement.class));
            requireActivity().overridePendingTransition(0, 0);
        });

        editIcon.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileManagement.class));
            requireActivity().overridePendingTransition(0, 0);
        });

        layoutInformation.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileManagement.class));
            requireActivity().overridePendingTransition(0, 0);
        });

        layoutSwitchToCustomer.setOnClickListener(v -> {
            if (firebaseUser == null) return;

            dtb_user.collection("Users")
                    .document(firebaseUser.getUid())
                    .update("currentRole", "customer")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Đã chuyển sang giao diện khách hàng!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), CustomerMainActivity.class));
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Chuyển vai trò thất bại.", Toast.LENGTH_SHORT).show();
                    });
        });

        layoutChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ForgotPasswordActivity.class));
            requireActivity().overridePendingTransition(0, 0);
        });

        layoutDeleteAccount.setOnClickListener(v -> {
            if (firebaseUser == null) return;

            dtb_user.collection("Users").document(firebaseUser.getUid())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        firebaseUser.delete().addOnCompleteListener(task -> {
                            Toast.makeText(getContext(), "Đã xóa tài khoản!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            requireActivity().finish();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Xóa tài khoản thất bại.", Toast.LENGTH_SHORT).show();
                    });
        });

        layoutSettings.setOnClickListener(v -> {
            // TODO: Thêm hành động cho mục "Cài đặt" (ví dụ: mở SettingsActivity)
            Toast.makeText(getContext(), "Chức năng Cài đặt chưa được triển khai", Toast.LENGTH_SHORT).show();
        });

        layoutSignOut.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }

    private void setupSettingItem(View view, int iconResId, String text) {
        ImageView icon = view.findViewById(R.id.setting_icon);
        TextView textView = view.findViewById(R.id.setting_text);
        icon.setImageResource(iconResId);
        textView.setText(text);
    }

    private void setupSettingItemWithOverlay(View view, int primaryIconResId, int overlayIconResId, String text) {
        // Chỉ dùng primaryIconResId vì item_setting_row.xml không hỗ trợ overlay
        ImageView icon = view.findViewById(R.id.setting_icon);
        TextView textView = view.findViewById(R.id.setting_text);
        icon.setImageResource(primaryIconResId);
        textView.setText(text);
    }
}