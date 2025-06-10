package com.uit.carrental.FragmentPages.Owner;

import android.content.Intent;
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
import com.uit.carrental.Adapter.OwnerNotificationAdapter;
import com.uit.carrental.Model.Booking;
import com.uit.carrental.R;
import com.uit.carrental.Service.Activity.OwnerActivityDetail;
import java.util.ArrayList;
import java.util.List;

public class OwnerNotificationsFragment extends Fragment {

    private static final String TAG = "OwnerNotifications";
    private RecyclerView recyclerView;
    private OwnerNotificationAdapter notificationAdapter;
    private List<Booking> bookingList;
    private FirebaseFirestore db;
    private TextView tvEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.owner_fragment_notification, container, false);


        db = FirebaseFirestore.getInstance();
        bookingList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.notification_list);
        tvEmpty = view.findViewById(R.id.tv_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);
        notificationAdapter = new OwnerNotificationAdapter(this, bookingList);
        recyclerView.setAdapter(notificationAdapter);

        loadBookings();
        recyclerView.post(() -> Log.d(TAG, "RecyclerView hiển thị: " + recyclerView.isShown()));
        return view;
    }

    private void loadBookings() {
        String ownerId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        Log.d(TAG, "Current ownerId: " + ownerId);

        if (ownerId == null) {
            Toast.makeText(getContext(), "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        try {
            db.collection("Bookings")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        Log.d(TAG, "Số tài liệu nhận được: " + querySnapshot.size());
                        bookingList.clear();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            try {
                                Booking booking = doc.toObject(Booking.class);
                                booking.setBookingId(doc.getId());
                                if (booking.getOwnerId() != null && booking.getOwnerId().equals(ownerId)) {
                                    bookingList.add(booking);
                                    Log.d(TAG, "Thêm booking: " + booking.getBookingId());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Lỗi ánh xạ tài liệu: " + doc.getId(), e);
                            }
                        }
                        Log.d(TAG, "Số booking sau lọc: " + bookingList.size());
                        if (!bookingList.isEmpty()) {
                            recyclerView.setVisibility(View.VISIBLE);
                            tvEmpty.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        notificationAdapter.updateData(bookingList);
                        recyclerView.invalidate();
                        Log.d(TAG, "Cập nhật giao diện, RecyclerView visibility: " + recyclerView.getVisibility());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Lỗi tải booking: ", e);
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Lỗi tải booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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