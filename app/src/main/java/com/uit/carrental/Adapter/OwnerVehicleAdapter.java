package com.uit.carrental.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.carrental.Model.User;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.uit.carrental.Service.Vehicle.UpdateVehicleActivity;
import java.util.ArrayList;

public class OwnerVehicleAdapter extends RecyclerView.Adapter<OwnerVehicleAdapter.MyViewHolder> {
    private static final String TAG = "OwnerVehicleAdapter";
    private final Context context;
    private final ArrayList<Vehicle> vehicleList;
    private final FirebaseFirestore db;

    public OwnerVehicleAdapter(Context context, ArrayList<Vehicle> vehicleList) {
        this.context = context;
        this.vehicleList = vehicleList != null ? vehicleList : new ArrayList<>();
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.owner_vehicle_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);
        Log.d(TAG, "Bind xe tại vị trí " + position + ": " + vehicle.toString());

        // Bind data
        holder.textViewVehicleName.setText(vehicle.getVehicleName() != null && !vehicle.getVehicleName().isEmpty() ? vehicle.getVehicleName() : "Xe mẫu");
        holder.textViewPrice.setText(vehicle.getVehiclePrice() != null && !vehicle.getVehiclePrice().isEmpty() ? vehicle.getVehiclePrice() : "500.000 VNĐ/Ngày");
        holder.textViewRating.setText(String.format("%.1f (0 Đánh giá)", vehicle.getVehicleRating()));

        // Hiển thị trạng thái xác minh
        String verificationStatus = vehicle.getVerificationStatus() != null ? vehicle.getVerificationStatus() : "pending";
        holder.verificationStatus.setText(verificationStatus.equals("verified") ? "Đã duyệt" : "Đang chờ duyệt");
        holder.verificationStatus.setTextColor(verificationStatus.equals("verified") ?
                context.getResources().getColor(R.color.green_verified) :
                context.getResources().getColor(R.color.orange_pending));

        // Load provider name
        String ownerId = vehicle.getOwnerId();
        if (ownerId != null && !ownerId.isEmpty()) {
            db.collection("Users").document(ownerId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            String username = user != null && user.getUsername() != null && !user.getUsername().isEmpty() ? user.getUsername() : "Nhà cung cấp mẫu";
                            holder.textViewProviderName.setText(username);
                            Log.d(TAG, "Provider name cho xe " + vehicle.getVehicleName() + ": " + username);
                        } else {
                            holder.textViewProviderName.setText("Nhà cung cấp mẫu");
                            Log.w(TAG, "Không tìm thấy user: " + ownerId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        holder.textViewProviderName.setText("Nhà cung cấp mẫu");
                        Log.e(TAG, "Lỗi tải user: " + e.getMessage());
                    });
        } else {
            holder.textViewProviderName.setText("Nhà cung cấp mẫu");
            Log.w(TAG, "ownerId null cho xe: " + vehicle.getVehicleName());
        }

        // Load image
        if (context != null) {
            Glide.with(context)
                    .load(vehicle.getVehicleImageUrl() != null && !vehicle.getVehicleImageUrl().isEmpty() ? vehicle.getVehicleImageUrl() : R.drawable.ic_car)
                    .thumbnail(0.25f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_car)
                    .error(R.drawable.ic_car)
                    .into(holder.imageViewVehicle);
            Log.d(TAG, "Tải ảnh cho xe " + vehicle.getVehicleName() + ": " + vehicle.getVehicleImageUrl());
        }

        // Handle click
        holder.itemView.setOnClickListener(view -> {
            if (vehicle.getVehicleId() != null) {
                Intent intent = new Intent(context, UpdateVehicleActivity.class);
                intent.putExtra("vehicleId", vehicle.getVehicleId()); // Sửa key thành vehicleId
                context.startActivity(intent);
                Log.d(TAG, "Nhấn vào xe: " + vehicle.getVehicleId());
            } else {
                Toast.makeText(context, "Lỗi: Không tìm thấy ID xe", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "vehicleId null cho xe: " + vehicle.getVehicleName());
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "Số item trong RecyclerView: " + vehicleList.size());
        return vehicleList.size();
    }

    public void updateData(ArrayList<Vehicle> newVehicleList) {
        Log.d(TAG, "Cập nhật dữ liệu adapter, số xe: " + (newVehicleList != null ? newVehicleList.size() : 0));
        vehicleList.clear();
        if (newVehicleList != null) {
            vehicleList.addAll(newVehicleList);
        }
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewVehicleName, textViewPrice, textViewProviderName, textViewRating, verificationStatus;
        ImageView imageViewVehicle;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewVehicleName = itemView.findViewById(R.id.vehicle_name);
            textViewPrice = itemView.findViewById(R.id.tv_vehicle_price);
            textViewProviderName = itemView.findViewById(R.id.provider_name);
            textViewRating = itemView.findViewById(R.id.vehicle_rating);
            verificationStatus = itemView.findViewById(R.id.verification_status);
            imageViewVehicle = itemView.findViewById(R.id.img_vehicle);
            Log.d(TAG, "Khởi tạo ViewHolder");
        }
    }
}
