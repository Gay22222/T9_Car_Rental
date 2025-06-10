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
import com.uit.carrental.Model.Booking;
import com.uit.carrental.Model.Notification;
import com.uit.carrental.R;
import com.uit.carrental.Service.Vehicle.VehicleDetailActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private static final String TAG = "NotificationAdapter";
    private final List<Notification> notificationList = new ArrayList<>(); // Khởi tạo tại đây
    private final OnNotificationClickListener clickListener;
    private final FirebaseFirestore db;
    private final Map<String, String> userCache;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationAdapter(List<Notification> initialList, OnNotificationClickListener clickListener) {
        this.clickListener = clickListener;
        this.db = FirebaseFirestore.getInstance();
        this.userCache = new HashMap<>();
        if (initialList != null) {
            this.notificationList.addAll(initialList);
        }
        Log.d(TAG, "Khởi tạo adapter, số thông báo ban đầu: " + notificationList.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        Log.d(TAG, "Tạo ViewHolder");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        Log.d(TAG, "Binding notification: " + notification.getNotificationId());

        holder.tvTitle.setText(notification.getTitle() != null ? notification.getTitle() : "");
        holder.tvMessage.setText(notification.getMessage() != null ? notification.getMessage() : "");
        Log.d(TAG, "Set title: " + notification.getTitle() + ", message: " + notification.getMessage());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            holder.tvTime.setText(notification.getCreatedAt() != null ?
                    sdf.format(notification.getCreatedAt().toDate()) : "Không có thời gian");
            Log.d(TAG, "Set time: " + holder.tvTime.getText());
        } catch (Exception e) {
            Log.e(TAG, "Lỗi định dạng thời gian: ", e);
            holder.tvTime.setText("Lỗi thời gian");
        }

        if (!notification.isRead()) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(R.string.notifications);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.orange_pending));
            Log.d(TAG, "Set status: Chưa đọc");
        } else {
            holder.tvStatus.setVisibility(View.GONE);
            Log.d(TAG, "Set status: Đã đọc");
        }

        if (notification.getBookingId() != null && !notification.getBookingId().isEmpty()) {
            String bookingId = notification.getBookingId();
            if (userCache.containsKey(bookingId)) {
                holder.tvCustomerName.setText(userCache.get(bookingId));
                Log.d(TAG, "Set customerName from cache: " + userCache.get(bookingId));
            } else {
                db.collection("Bookings").document(bookingId)
                        .get()
                        .addOnSuccessListener(document -> {
                            if (document.exists()) {
                                Booking booking = document.toObject(Booking.class);
                                if (booking != null) {
                                    String customerId = booking.getCustomerId();
                                    db.collection("Users").document(customerId)
                                            .get()
                                            .addOnSuccessListener(userDoc -> {
                                                if (userDoc.exists()) {
                                                    String username = userDoc.getString("username");
                                                    userCache.put(bookingId, username != null ? username : "Unknown");
                                                    holder.tvCustomerName.setText(username != null ? username : "Unknown");
                                                    Log.d(TAG, "Set customerName from Firestore: " + username);
                                                } else {
                                                    holder.tvCustomerName.setText("Unknown");
                                                    Log.w(TAG, "User document not found: " + customerId);
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                holder.tvCustomerName.setText("Unknown");
                                                Log.e(TAG, "Lỗi lấy user: ", e);
                                            });
                                } else {
                                    holder.tvCustomerName.setText("Unknown");
                                    Log.w(TAG, "Booking document not found: " + bookingId);
                                }
                            } else {
                                holder.tvCustomerName.setText("Unknown");
                                Log.w(TAG, "Booking document not found: " + bookingId);
                            }
                        })
                        .addOnFailureListener(e -> {
                            holder.tvCustomerName.setText("Unknown");
                            Log.e(TAG, "Lỗi lấy booking: ", e);
                        });
            }
        } else {
            holder.tvCustomerName.setVisibility(View.GONE);
            Log.d(TAG, "Ẩn customerName vì không có bookingId");
        }

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Click notification: " + notification.getNotificationId());
            if (!notification.isRead()) {
                db.collection("Notifications").document(notification.getNotificationId())
                        .update("isRead", true)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Đánh dấu thông báo đã đọc"))
                        .addOnFailureListener(e -> Log.e(TAG, "Lỗi cập nhật isRead: ", e));
                notification.setRead(true);
                notifyItemChanged(position);
            }

            if ("vehicle_verification".equals(notification.getType()) && notification.getVehicleId() != null) {
                Intent intent = new Intent(holder.itemView.getContext(), VehicleDetailActivity.class);
                intent.putExtra("vehicleId", notification.getVehicleId());
                holder.itemView.getContext().startActivity(intent);
            }

            clickListener.onNotificationClick(notification);
        });
    }

    @Override
    public int getItemCount() {
        int count = notificationList.size();
        Log.d(TAG, "Số item trong RecyclerView: " + count);
        return count;
    }

    public void updateData(List<Notification> newNotificationList) {
        Log.d(TAG, "Cập nhật dữ liệu adapter, số thông báo: " + (newNotificationList != null ? newNotificationList.size() : 0));
        notificationList.clear();
        if (newNotificationList != null) {
            notificationList.addAll(newNotificationList);
            Log.d(TAG, "Đã thêm " + newNotificationList.size() + " thông báo vào notificationList");
        }
        notifyDataSetChanged();
        Log.d(TAG, "Gọi notifyDataSetChanged, số item hiện tại: " + notificationList.size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCustomerName, tvMessage, tvTime, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvMessage = itemView.findViewById(R.id.tv_noti_text);
            tvTime = itemView.findViewById(R.id.tv_time_range);
            tvStatus = itemView.findViewById(R.id.tv_status);
            Log.d(TAG, "Khởi tạo ViewHolder");
        }
    }
}