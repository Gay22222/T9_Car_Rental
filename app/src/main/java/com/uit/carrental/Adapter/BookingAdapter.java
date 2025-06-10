package com.uit.carrental.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.carrental.Model.Booking;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private final List<Booking> bookingList;
    private final OnBookingClickListener clickListener;
    private final FirebaseFirestore db;
    private final Map<String, String> vehicleCache;

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }

    public BookingAdapter(List<Booking> bookingList, OnBookingClickListener clickListener) {
        this.bookingList = bookingList;
        this.clickListener = clickListener;
        this.db = FirebaseFirestore.getInstance();
        this.vehicleCache = new HashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        String vehicleId = booking.getVehicleId();

        // Lấy tên xe từ cache hoặc Firestore
        if (vehicleCache.containsKey(vehicleId)) {
            holder.tvVehicleName.setText(vehicleCache.get(vehicleId));
        } else {
            db.collection("Vehicles").document(vehicleId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            Vehicle vehicle = document.toObject(Vehicle.class);
                            String vehicleName = vehicle != null ? vehicle.getVehicleName() : "Xe #" + vehicleId;
                            vehicleCache.put(vehicleId, vehicleName);
                            holder.tvVehicleName.setText(vehicleName);
                        } else {
                            holder.tvVehicleName.setText("Xe #" + vehicleId);
                        }
                    });
        }

        // Hiển thị trạng thái
        String statusText;
        switch (booking.getStatus()) {
            case "pending": statusText = "Chưa xác nhận"; break;
            case "confirmed": statusText = "Đã xác nhận"; break;
            case "rejected": statusText = "Bị từ chối"; break;
            case "paid": statusText = "Đã thanh toán"; break;
            case "completed": statusText = "Đã hoàn thành"; break;
            case "cancelled": statusText = "Đã hủy"; break;
            default: statusText = booking.getStatus();
        }
        holder.tvStatus.setText(statusText);
        holder.tvBookingId.setText(booking.getBookingId());

        holder.itemView.setOnClickListener(v -> clickListener.onBookingClick(booking));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVehicleName, tvStatus, tvBookingId;

        ViewHolder(View itemView) {
            super(itemView);
            tvVehicleName = itemView.findViewById(R.id.tv_noti_name);
            tvStatus = itemView.findViewById(R.id.tv_Status);
            tvBookingId = itemView.findViewById(R.id.tv_noti_ID);
        }
    }
}
