package com.uit.carrental.FragmentPages.Customer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uit.carrental.Adapter.NotificationAdapter;
import com.uit.carrental.Model.Notification;
import com.uit.carrental.R;
import java.util.ArrayList;
import java.util.List;

public class CustomerNotificationFragment extends Fragment {

    private static final String TAG = "CustomerNotification";
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private FirebaseFirestore db;
    private TextView tvEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_notification, container, false);


        db = FirebaseFirestore.getInstance();
        notificationList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv_notifications);
        tvEmpty = view.findViewById(R.id.tv_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);
        notificationAdapter = new NotificationAdapter(notificationList, notification -> {
            Log.d(TAG, "Thông báo được nhấn: " + notification.getNotificationId());
        });
        recyclerView.setAdapter(notificationAdapter);

        loadNotifications();
        recyclerView.post(() -> Log.d(TAG, "RecyclerView hiển thị: " + recyclerView.isShown()));
        return view;
    }

    private void loadNotifications() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        Log.d(TAG, "Current userId: " + userId);

        if (userId == null) {
            Toast.makeText(getContext(), "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        try {
            db.collection("Notifications")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        Log.d(TAG, "Số tài liệu nhận được: " + querySnapshot.size());
                        notificationList.clear();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            try {
                                Notification notification = doc.toObject(Notification.class);
                                notification.setNotificationId(doc.getId());
                                // Lọc thông báo cho customer
                                if (notification.getUserId() != null && notification.getUserId().equals(userId) &&
                                        "customer".equals(notification.getRole()) &&
                                        (notification.getType().equals("user_verification") ||
                                                notification.getType().equals("new_booking") ||
                                                notification.getType().equals("status_update") ||
                                                notification.getType().equals("supplement_request_user"))) {
                                    notificationList.add(notification);
                                    Log.d(TAG, "Thêm notification: " + notification.getNotificationId());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Lỗi ánh xạ tài liệu: " + doc.getId(), e);
                            }
                        }
                        Log.d(TAG, "Số thông báo sau lọc: " + notificationList.size());
                        if (!notificationList.isEmpty()) {
                            recyclerView.setVisibility(View.VISIBLE);
                            tvEmpty.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        notificationAdapter.updateData(notificationList);
                        recyclerView.invalidate();
                        Log.d(TAG, "Cập nhật giao diện, RecyclerView visibility: " + recyclerView.getVisibility());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Lỗi tải thông báo: ", e);
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Lỗi tải thông báo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Lỗi runtime: ", e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Lỗi runtime: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}