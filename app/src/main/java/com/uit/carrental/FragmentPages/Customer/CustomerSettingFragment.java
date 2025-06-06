package com.uit.carrental.FragmentPages.Customer;

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

import com.uit.carrental.ActivityPages.OwnerMainActivity;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import com.uit.carrental.Service.UserAuthentication.LoginActivity;
import com.uit.carrental.Service.UserAuthentication.UpdatePassword;
import com.uit.carrental.Service.UserAuthentication.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerSettingFragment extends Fragment {

    private TextView tvName;
    private CircleImageView imgAvatar;
    private TextView tvEdit;
    private ImageView editIcon;

    private FirebaseFirestore dtb_user;
    private FirebaseUser firebaseUser;
    private User user = new User();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_setting, container, false);

        // Initialize views
        imgAvatar = view.findViewById(R.id.img_avatar);
        tvName = view.findViewById(R.id.tv_name);
        tvEdit = view.findViewById(R.id.tv_edit);
        editIcon = view.findViewById(R.id.edit_icon);

        // Initialize setting items
        View layoutInformation = view.findViewById(R.id.layout_information);
        View layoutConnect = view.findViewById(R.id.layout_connect);
        View layoutChangePassword = view.findViewById(R.id.layout_change_password);
        View layoutDeleteAccount = view.findViewById(R.id.layout_delete_account);
        View layoutSettings = view.findViewById(R.id.layout_settings);
        View layoutSignOut = view.findViewById(R.id.layout_sign_out);

        // Set icons and texts for setting items
        setupSettingItem(layoutInformation, R.drawable.vector20_settings, "Thông tin tài khoản");
        setupSettingItem(layoutConnect, R.drawable.vector17_settings, "Giao diện nhà cung cấp");
        setupSettingItem(layoutChangePassword, R.drawable.vector8_settings, "Thay đổi mật khẩu");
        setupSettingItemWithOverlay(layoutDeleteAccount, R.drawable.vector11_settings, R.drawable.vector10_settings, "Yêu cầu xóa tài khoản");
        setupSettingItem(layoutSettings, R.drawable.vector13_settings, "Cài đặt");

        dtb_user = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user.setUser_id(firebaseUser.getUid());

        // Load user data
        dtb_user.collection("Users")
                .whereEqualTo("user_id", user.getUser_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                tvName.setText(document.get("username").toString());
                                user.setAvatarURL(document.get("avatarURL").toString());
                                if (!document.get("avatarURL").toString().isEmpty()) {
                                    Picasso.get().load(user.getAvatarURL()).into(imgAvatar);
                                } else {
                                    user.setAvatarURL("");
                                }
                            }
                        } else {
                            Toast.makeText(view.getContext(), "Không thể lấy thông tin", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // Click listeners
        tvEdit.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), UserProfile.class);
            startActivity(i);
            requireActivity().overridePendingTransition(0, 0);
        });

        editIcon.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), UserProfile.class);
            startActivity(i);
            requireActivity().overridePendingTransition(0, 0);
        });

        layoutInformation.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), UserProfile.class);
            startActivity(i);
            requireActivity().overridePendingTransition(0, 0);
        });

        layoutConnect.setOnClickListener(v -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser == null) return;

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(firebaseUser.getUid())
                    .update("currentRole", "owner")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Đã chuyển sang giao diện chủ xe!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), OwnerMainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Chuyển vai trò thất bại.", Toast.LENGTH_SHORT).show();
                    });
        });

        layoutChangePassword.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), UpdatePassword.class);
            startActivity(i);
            requireActivity().overridePendingTransition(0, 0);
        });

        layoutDeleteAccount.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(view.getContext(), "User account deleted.", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                    requireActivity().overridePendingTransition(0, 0);
                                } else {
                                    Toast.makeText(view.getContext(), "Failed to delete user account.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        layoutSettings.setOnClickListener(v -> {
            // TODO: Thêm hành động cho mục "Cài đặt" (ví dụ: mở SettingsActivity)
            Toast.makeText(getContext(), "Chức năng Cài đặt chưa được triển khai", Toast.LENGTH_SHORT).show();
        });

        layoutSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(0, 0);
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
        // Kiểm tra khả năng đè vector10_settings lên vector11_settings
        // Vì item_setting_row.xml chỉ có 1 ImageView, dùng primaryIconResId (vector11_settings)
        // Nếu cần overlay, cần sửa item_setting_row.xml để thêm ImageView thứ hai
        ImageView icon = view.findViewById(R.id.setting_icon);
        TextView textView = view.findViewById(R.id.setting_text);
        icon.setImageResource(primaryIconResId); // Chỉ dùng vector11_settings để tránh xung đột
        textView.setText(text);
    }
}