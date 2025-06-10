package com.uit.carrental.Adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.carrental.FragmentPages.Owner.OwnerNotificationsFragment;
import com.uit.carrental.Model.Booking;
import com.uit.carrental.R;
import com.uit.carrental.Service.Activity.OwnerActivityDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OwnerNotificationAdapter extends RecyclerView.Adapter<OwnerNotificationAdapter.MyViewHolder> {

    private static final String TAG = "OwnerNotificationAdapter";
    private final OwnerNotificationsFragment ownerNotificationsFragment;
    private final List<Booking> bookingList = new ArrayList<>();
    private final Map<String, String> userCache;
    private final FirebaseFirestore db;

    public OwnerNotificationAdapter(OwnerNotificationsFragment context, List<Booking> initialList) {
        this.ownerNotificationsFragment = context;
        if (initialList != null) {
            this.bookingList.addAll(initialList);
        }
        this.userCache = new HashMap<>();
        this.db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Khởi tạo adapter, số booking ban đầu: " + bookingList.size());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ownerNotificationsFragment.getActivity()).inflate(R.layout.item_notification, parent, false);
        Log.d(TAG, "Tạo ViewHolder");
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        String customerId = booking.getCustomerId();
        Log.d(TAG, "Binding booking: " + booking.getBookingId() + ", customerId: " + customerId);

        if (userCache.containsKey(customerId)) {
            holder.customerName.setText(userCache.get(customerId));
            Log.d(TAG, "Set customerName from cache: " + userCache.get(customerId));
        } else {
            db.collection("Users").document(customerId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String username = document.getString("username");
                            userCache.put(customerId, username != null ? username : "Unknown");
                            holder.customerName.setText(username != null ? username : "Unknown");
                            Log.d(TAG, "Set customerName from Firestore: " + username);
                        } else {
                            holder.customerName.setText("Unknown");
                            Log.w(TAG, "User document not found: " + customerId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        holder.customerName.setText("Unknown");
                        Log.e(TAG, "Lỗi lấy user: ", e);
                    });
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            String timeRange = String.format("Từ %s đến %s",
                    sdf.format(booking.getStartTime().toDate()),
                    sdf.format(booking.getEndTime().toDate()));
            holder.timeRange.setText(timeRange);
            Log.d(TAG, "Set timeRange: " + timeRange);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi định dạng thời gian: ", e);
            holder.timeRange.setText("Lỗi thời gian");
        }

        String statusText;
        switch (booking.getStatus()) {
            case "pending": statusText = "Bạn vừa nhận một yêu cầu mới"; break;
            case "confirmed": statusText = "Yêu cầu đã được xác nhận"; break;
            case "rejected": statusText = "Yêu cầu đã bị từ chối"; break;
            case "paid": statusText = "Yêu cầu đã được thanh toán"; break;
            case "completed": statusText = "Yêu cầu đã hoàn thành"; break;
            default: statusText = booking.getStatus();
        }
        holder.status.setText(statusText);
        Log.d(TAG, "Set status: " + statusText);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(ownerNotificationsFragment.getActivity(), OwnerActivityDetail.class);
            intent.putExtra("NotiID", booking.getBookingId());
            ownerNotificationsFragment.startActivity(intent);
            Log.d(TAG, "Click booking: " + booking.getBookingId());
        });
    }

    @Override
    public int getItemCount() {
        int count = bookingList.size();
        Log.d(TAG, "Số item trong RecyclerView: " + count);
        return count;
    }

    public void updateData(List<Booking> newBookingList) {
        Log.d(TAG, "Cập nhật dữ liệu adapter, số booking: " + (newBookingList != null ? newBookingList.size() : 0));
        bookingList.clear();
        if (newBookingList != null) {
            bookingList.addAll(newBookingList);
            Log.d(TAG, "Đã thêm " + newBookingList.size() + " booking vào bookingList");
        }
        notifyDataSetChanged();
        Log.d(TAG, "Gọi notifyDataSetChanged, số item hiện tại: " + bookingList.size());
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, timeRange, status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.tv_customer_name);
            timeRange = itemView.findViewById(R.id.tv_time_range);
            status = itemView.findViewById(R.id.tv_noti_text);
            Log.d(TAG, "Khởi tạo ViewHolder");
        }
    }
}