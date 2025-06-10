package com.uit.carrental.FragmentPages.Customer;

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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.uit.carrental.ActivityPages.OwnerMainActivity;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import com.uit.carrental.Service.UserAuthentication.LoginActivity;
import com.uit.carrental.Service.UserAuthentication.UpdatePassword;
import com.uit.carrental.Service.UserAuthentication.UserProfile;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerSettingFragment extends Fragment {

    private TextView tvName;
    private CircleImageView imgAvatar;
    private TextView tvEdit;
    private ImageView editIcon;
    private SettingViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_setting, container, false);

        initViews(view);
        initViewModel();
        observeLiveData();

        setupClickListeners(view);
        return view;
    }

    private void initViews(View view) {
        imgAvatar = view.findViewById(R.id.img_avatar);
        tvName = view.findViewById(R.id.tv_name);
        tvEdit = view.findViewById(R.id.tv_edit);
        editIcon = view.findViewById(R.id.edit_icon);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        viewModel.loadUserData();
    }

    private void observeLiveData() {
        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvName.setText(user.getUsername());
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                    Picasso.get().load(user.getAvatarUrl()).into(imgAvatar);
                }
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupClickListeners(View view) {
        View layoutInformation = view.findViewById(R.id.layout_information);
        View layoutConnect = view.findViewById(R.id.layout_connect);
        View layoutChangePassword = view.findViewById(R.id.layout_change_password);
        View layoutDeleteAccount = view.findViewById(R.id.layout_delete_account);
        View layoutSettings = view.findViewById(R.id.layout_settings);
        View layoutSignOut = view.findViewById(R.id.layout_sign_out);

        setupSettingItem(layoutInformation, R.drawable.vector20_settings, "Thông tin tài khoản");
        setupSettingItem(layoutConnect, R.drawable.vector17_settings, "Giao diện nhà cung cấp");
        setupSettingItem(layoutChangePassword, R.drawable.vector8_settings, "Thay đổi mật khẩu");
        setupSettingItemWithOverlay(layoutDeleteAccount, R.drawable.vector11_settings, R.drawable.vector10_settings, "Yêu cầu xóa tài khoản");
        setupSettingItem(layoutSettings, R.drawable.vector13_settings, "Cài đặt");

        tvEdit.setOnClickListener(v -> navigateToUserProfile());
        editIcon.setOnClickListener(v -> navigateToUserProfile());

        layoutInformation.setOnClickListener(v -> navigateToUserProfile());

        layoutConnect.setOnClickListener(v -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser == null) return;

            User user = viewModel.getUserLiveData().getValue();
            if (user != null && user.hasRole("owner")) {
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
            } else {
                Toast.makeText(getContext(), "Bạn không có quyền truy cập giao diện chủ xe.", Toast.LENGTH_SHORT).show();
            }
        });

        layoutChangePassword.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), UpdatePassword.class);
            startActivity(i);
            requireActivity().overridePendingTransition(0, 0);
        });

        layoutDeleteAccount.setOnClickListener(v -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                firebaseUser.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Tài khoản đã được xóa.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                                requireActivity().overridePendingTransition(0, 0);
                            } else {
                                Toast.makeText(getContext(), "Xóa tài khoản thất bại.", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        layoutSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng Cài đặt chưa được triển khai", Toast.LENGTH_SHORT).show();
        });

        layoutSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(0, 0);
        });
    }

    private void navigateToUserProfile() {
        Intent i = new Intent(getActivity(), UserProfile.class);
        startActivity(i);
        requireActivity().overridePendingTransition(0, 0);
    }

    private void setupSettingItem(View view, int iconResId, String text) {
        ImageView icon = view.findViewById(R.id.setting_icon);
        TextView textView = view.findViewById(R.id.setting_text);
        icon.setImageResource(iconResId);
        textView.setText(text);
    }

    private void setupSettingItemWithOverlay(View view, int primaryIconResId, int overlayIconResId, String text) {
        ImageView icon = view.findViewById(R.id.setting_icon);
        TextView textView = view.findViewById(R.id.setting_text);
        icon.setImageResource(primaryIconResId);
        textView.setText(text);
    }

    public static class SettingViewModel extends ViewModel {
        private final FirebaseFirestore db;
        private final FirebaseUser firebaseUser;
        private final MutableLiveData<User> userLiveData;
        private final MutableLiveData<String> errorLiveData;

        public SettingViewModel() {
            db = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userLiveData = new MutableLiveData<>();
            errorLiveData = new MutableLiveData<>();
        }

        public LiveData<User> getUserLiveData() {
            return userLiveData;
        }

        public LiveData<String> getErrorLiveData() {
            return errorLiveData;
        }

        public void loadUserData() {
            if (firebaseUser == null) {
                errorLiveData.setValue("Người dùng chưa đăng nhập");
                return;
            }

            DocumentReference userRef = db.collection("Users").document(firebaseUser.getUid());
            userRef.addSnapshotListener((document, error) -> {
                if (error != null) {
                    errorLiveData.setValue("Lỗi tải thông tin: " + error.getMessage());
                    return;
                }
                if (document != null && document.exists()) {
                    User user = document.toObject(User.class);
                    userLiveData.setValue(user);
                } else {
                    errorLiveData.setValue("Không tìm thấy thông tin người dùng");
                }
            });
        }
    }
}
