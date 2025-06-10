package com.uit.carrental.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uit.carrental.FragmentPages.Owner.OwnerActivityFragment;
import com.uit.carrental.Model.Booking;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import com.uit.carrental.Service.Activity.OwnerActivityDetail;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwnerActivityAdapter extends RecyclerView.Adapter<OwnerActivityAdapter.MyViewHolder> {

    private final OwnerActivityFragment ownerActivityFragment;
    private final List<Booking> bookingList;
    private final Map<String, String> userCache; // Cache tên người dùng
    private final FirebaseFirestore db;

    public OwnerActivityAdapter(OwnerActivityFragment context, List<Booking> bookingList) {
        this.ownerActivityFragment = context;
        this.bookingList = bookingList != null ? bookingList : new ArrayList<>();
        this.userCache = new HashMap<>();
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ownerActivityFragment.getActivity()).inflate(R.layout.item_activity, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        String customerId = booking.getCustomerId();

        // Lấy tên người dùng từ cache hoặc Firestore
        if (userCache.containsKey(customerId)) {
            holder.name.setText(userCache.get(customerId));
        } else {
            db.collection("Users").document(customerId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String username = document.getString("username");
                            userCache.put(customerId, username != null ? username : "Unknown");
                            holder.name.setText(username != null ? username : "Unknown");
                        }
                    });
        }

        holder.id.setText(booking.getBookingId());
        switch (booking.getStatus()) {
            case "pending":
                holder.status.setText("Nhà cung cấp chưa xác nhận");
                break;
            case "confirmed":
                holder.status.setText("Đã xác nhận");
                break;
            case "rejected":
                holder.status.setText("Nhà cung cấp không xác nhận");
                break;
            case "paid":
                holder.status.setText("Đã thanh toán");
                break;
            case "completed":
                holder.status.setText("Đã hoàn thành");
                break;
            default:
                holder.status.setText(booking.getStatus());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(ownerActivityFragment.getActivity(), OwnerActivityDetail.class);
            intent.putExtra("NotiID", booking.getBookingId());
            ownerActivityFragment.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void updateData(List<Booking> newBookingList) {
        bookingList.clear();
        if (newBookingList != null) {
            bookingList.addAll(newBookingList);
        }
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, status, id;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_noti_name);
            status = itemView.findViewById(R.id.tv_Status);
            id = itemView.findViewById(R.id.tv_noti_ID);
        }
    }
}
