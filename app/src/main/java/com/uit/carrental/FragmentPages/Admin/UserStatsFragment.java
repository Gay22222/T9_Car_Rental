package com.uit.carrental.FragmentPages.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import java.util.HashMap;
import java.util.Map;

public class UserStatsFragment extends Fragment {

    private static final String TAG = "UserStatsFragment";
    private FirebaseFirestore db;
    private String userId;
    private User user;
    private TextView tvUsername, tvUserInfo, tvCccdFrontLabel, tvCccdBackLabel, tvLicenseLabel;
    private ImageView imgCccdFront, imgCccdBack, imgLicense, imgAvatar, imgBack; // Thêm imgBack
    private LinearLayout btnApprove, btnReject, btnRequestSupplement;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_stats, container, false);

        db = FirebaseFirestore.getInstance();
        userId = getArguments() != null ? getArguments().getString("userId") : null;
        if (userId == null) {
            Log.e(TAG, "Không tìm thấy userId");
            return view;
        }

        initViews(view);
        loadUserData();
        setupButtons();
        return view;
    }

    private void initViews(View view) {
        tvUsername = view.findViewById(R.id.rxntl0e8qr7m);
        tvUserInfo = view.findViewById(R.id.rhj4q0f9p29s);
        tvCccdFrontLabel = view.findViewById(R.id.rxem3gpl3efc);
        tvCccdBackLabel = view.findViewById(R.id.rf9ah2n56ocp);
        tvLicenseLabel = view.findViewById(R.id.resyxc24w8x);
        imgAvatar = view.findViewById(R.id.rjorhrg7xhw);
        imgCccdFront = view.findViewById(R.id.r6ijhjkh2w4x);
        imgCccdBack = view.findViewById(R.id.ri7x9glaq17);
        imgLicense = view.findViewById(R.id.rnou1o5qbz7l);
        btnApprove = view.findViewById(R.id.rygcfobvaozi);
        btnReject = view.findViewById(R.id.rgfc3wpe0gx);
        btnRequestSupplement = view.findViewById(R.id.ry07qbxzq9t);
        imgBack = view.findViewById(R.id.rj72dc7rtuc); // Khởi tạo nút back

        // Thêm sự kiện nhấn cho nút back
        imgBack.setOnClickListener(v -> {
            Log.d(TAG, "Nút back được nhấn");
            getParentFragmentManager().popBackStack(); // Quay lại AdminUserListFragment
        });
    }

    private void loadUserData() {
        db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        user = document.toObject(User.class);
                        if (user != null) {
                            tvUsername.setText(user.getUsername() != null ? user.getUsername() : "Không xác định");
                            tvUserInfo.setText(String.format("Email: %s\nSĐT: %s",
                                    user.getEmail() != null ? user.getEmail() : "N/A",
                                    user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A"));
                            Glide.with(this)
                                    .load(user.getAvatarUrl() != null ? user.getAvatarUrl() : R.drawable.ic_person)
                                    .into(imgAvatar);
                            Glide.with(this)
                                    .load(user.getCiCardFront() != null ? user.getCiCardFront() : R.drawable.ic_car)
                                    .into(imgCccdFront);
                            Glide.with(this)
                                    .load(user.getCiCardBehind() != null ? user.getCiCardBehind() : R.drawable.ic_car)
                                    .into(imgCccdBack);
                            Glide.with(this)
                                    .load(user.getLicenseUrl() != null ? user.getLicenseUrl() : R.drawable.ic_car)
                                    .into(imgLicense);
                            // Kiểm tra trạng thái xác minh để ẩn nút Duyệt nếu đã duyệt
                            if ("verified".equals(user.getVerificationStatus())) {
                                btnApprove.setVisibility(View.GONE);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi tải dữ liệu user: " + e.getMessage()));
    }

    private void setupButtons() {
        btnApprove.setOnClickListener(v -> approveUser());
        btnReject.setOnClickListener(v -> rejectUser());
        btnRequestSupplement.setOnClickListener(v -> showSupplementDialog());
    }

    private void approveUser() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("verificationStatus", "verified");
        db.collection("Users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    sendNotification(userId, "Tài khoản đã được duyệt",
                            "Tài khoản của bạn đã được duyệt thành công.", "user_verification", null, "customer");
                    btnApprove.setVisibility(View.GONE); // Ẩn nút Duyệt sau khi duyệt thành công
                    Log.d(TAG, "Đã duyệt tài khoản và ẩn nút Duyệt");
                })
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi duyệt user: " + e.getMessage()));
    }

    private void rejectUser() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("verificationStatus", "rejected");
        db.collection("Users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    sendNotification(userId, "Tài khoản bị từ chối",
                            "Tài khoản của bạn đã bị từ chối.", "user_verification", null, "customer");
                    getActivity().finish(); // Giữ nguyên logic gốc
                })
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi từ chối user: " + e.getMessage()));
    }

    private void showSupplementDialog() {
        RequestSupplementDialogFragment dialog = RequestSupplementDialogFragment.newInstance(userId, null, "supplement_request_user");
        dialog.setOnSubmitListener(message -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("verificationStatus", "rejected");
            db.collection("Users").document(userId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        sendNotification(userId, "Yêu cầu bổ sung thông tin",
                                "Tài khoản của bạn cần bổ sung: " + message, "supplement_request_user", null, "customer");
                        getActivity().finish(); // Giữ nguyên logic gốc
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Lỗi gửi yêu cầu bổ sung: " + e.getMessage()));
        });
        dialog.show(getParentFragmentManager(), "RequestSupplementDialog");
    }

    private void sendNotification(String userId, String title, String message, String type, String vehicleId, String role) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", userId);
        notification.put("title", title);
        notification.put("message", message);
        notification.put("type", type);
        notification.put("vehicleId", vehicleId != null ? vehicleId : "");
        notification.put("createdAt", Timestamp.now());
        notification.put("isRead", false);
        notification.put("role", role); // Thêm trường role

        db.collection("Notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    String notificationId = documentReference.getId();
                    db.collection("Notifications").document(notificationId)
                            .update("notificationId", notificationId)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Gửi thông báo thành công, ID: " + notificationId))
                            .addOnFailureListener(e -> Log.e(TAG, "Lỗi cập nhật notificationId: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi gửi thông báo: " + e.getMessage()));
    }
}
