package com.uit.carrental.Adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uit.carrental.FragmentPages.Admin.AdminVehicleListFragment;
import com.uit.carrental.FragmentPages.Customer.CustomerHomeFragment;
import com.uit.carrental.FragmentPages.Owner.OwnerVehicleFragment;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.Model.onClickInterface;
import com.uit.carrental.R;
import com.uit.carrental.Service.Vehicle.AdminVehicleDetailActivity;
import com.uit.carrental.Service.Vehicle.UpdateVehicleActivity;
import com.uit.carrental.Service.Vehicle.VehicleDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {

    private static final String TAG = "VehicleAdapter";
    private final Fragment fragment;
    private ArrayList<Vehicle> vehicles;
    private final onClickInterface onClickInterface;

    public VehicleAdapter(Fragment fragment, ArrayList<Vehicle> vehicles, onClickInterface onClickInterface) {
        this.fragment = fragment;
        this.vehicles = vehicles != null ? vehicles : new ArrayList<>();
        this.onClickInterface = onClickInterface;
        Log.d(TAG, "Khởi tạo adapter với " + this.vehicles.size() + " xe");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = fragment instanceof AdminVehicleListFragment ? R.layout.admin_vehicle_card : R.layout.vehicle_card;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new ViewHolder(view, fragment instanceof AdminVehicleListFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (vehicles == null || position >= vehicles.size()) {
            Log.e(TAG, "Danh sách xe rỗng hoặc vị trí không hợp lệ: " + position);
            return;
        }

        Vehicle vehicle = vehicles.get(position);
        Log.d(TAG, "Ánh xạ xe: " + vehicle.getVehicleName() + ", ID: " + vehicle.getVehicleId() +
                ", verificationStatus: " + vehicle.getVerificationStatus());

        // Ánh xạ chung
        holder.vehicleName.setText(vehicle.getVehicleName() != null ? vehicle.getVehicleName() : "Xe không tên");
        holder.vehiclePrice.setText(vehicle.getVehiclePrice() != null ? vehicle.getVehiclePrice() : "0 VNĐ");
        Float rating = vehicle.getVehicleRating();
        holder.vehicleRating.setText(String.format("%.1f (0 Đánh giá)", rating != null ? rating : 0.0f));

        // Tải ảnh
        if (fragment.getContext() != null) {
            if (vehicle.getVehicleImageUrl() != null && !vehicle.getVehicleImageUrl().isEmpty()) {
                Glide.with(fragment.getContext())
                        .load(vehicle.getVehicleImageUrl())
                        .placeholder(R.drawable.ic_car)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.vehicleImage);
            } else {
                holder.vehicleImage.setImageResource(R.drawable.ic_car);
            }
        }

        // Ánh xạ riêng cho admin
        if (holder.isAdminView) {
            holder.vehicleNumber.setText(vehicle.getVehicleNumber() != null ? vehicle.getVehicleNumber() : "Không có biển số");
            holder.vehicleBrand.setText(vehicle.getVehicleBrand() != null ? vehicle.getVehicleBrand() : "Không có thương hiệu");
            holder.vehicleAvailability.setText(getAvailabilityText(vehicle.getVehicleAvailability()));
            holder.verificationStatus.setText(getVerificationStatusText(vehicle.getVerificationStatus()));
            holder.verificationStatus.setTextColor(fragment.getResources().getColor(
                    vehicle.getVerificationStatus() != null && vehicle.getVerificationStatus().equals("verified") ?
                            R.color.green_verified : R.color.orange_pending));
        }

        // Xử lý nhấn xe
        holder.itemView.setOnClickListener(v -> {
            if (fragment.getContext() == null || vehicle.getVehicleId() == null) {
                Log.e(TAG, "Context or vehicleId is null");
                if (fragment.getContext() != null) {
                    Toast.makeText(fragment.getContext(), "Lỗi: Không thể mở chi tiết xe", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            Intent intent;
            if (fragment instanceof AdminVehicleListFragment) {
                intent = new Intent(fragment.getContext(), AdminVehicleDetailActivity.class);
            } else if (fragment instanceof OwnerVehicleFragment) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null && vehicle.getOwnerId() != null && vehicle.getOwnerId().equals(currentUser.getUid())) {
                    intent = new Intent(fragment.getContext(), UpdateVehicleActivity.class);
                } else {
                    intent = new Intent(fragment.getContext(), VehicleDetailActivity.class);
                }
            } else if (fragment instanceof CustomerHomeFragment) {
                intent = new Intent(fragment.getContext(), VehicleDetailActivity.class);
            } else {
                intent = new Intent(fragment.getContext(), VehicleDetailActivity.class);
            }
            intent.putExtra("vehicleId", vehicle.getVehicleId());
            fragment.startActivity(intent);
            onClickInterface.setClick(position);
        });
    }

    private String getAvailabilityText(String availability) {
        if (availability == null) return "Không xác định";
        switch (availability) {
            case "renting": return "Đang cho thuê";
            case "available": return "Có sẵn";
            case "maintenance": return "Đang bảo trì";
            case "pending": return "Đang chờ duyệt";
            default: return "Không xác định";
        }
    }

    private String getVerificationStatusText(String verificationStatus) {
        if (verificationStatus == null) return "Không xác định";
        switch (verificationStatus) {
            case "verified": return "Đã duyệt";
            case "pending": return "Chưa duyệt";
            default: return "Không xác định";
        }
    }

    @Override
    public int getItemCount() {
        int count = vehicles != null ? vehicles.size() : 0;
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    public void updateData(List<Vehicle> newVehicleList) {
        vehicles.clear();
        if (newVehicleList != null) {
            vehicles.addAll(newVehicleList);
        }
        notifyDataSetChanged();
        Log.d(TAG, "Cập nhật dữ liệu adapter, số xe: " + vehicles.size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView vehicleImage;
        TextView vehicleName, vehicleNumber, vehiclePrice, vehicleBrand, vehicleAvailability, verificationStatus, vehicleRating;
        boolean isAdminView;

        ViewHolder(View itemView, boolean isAdminView) {
            super(itemView);
            this.isAdminView = isAdminView;
            vehicleImage = itemView.findViewById(isAdminView ? R.id.vehicle_image : R.id.img_vehicle);
            vehicleName = itemView.findViewById(R.id.vehicle_name);
            vehiclePrice = itemView.findViewById(isAdminView ? R.id.vehicle_price : R.id.tv_vehicle_price);
            vehicleRating = itemView.findViewById(R.id.vehicle_rating);
            if (isAdminView) {
                vehicleNumber = itemView.findViewById(R.id.vehicle_number);
                vehicleBrand = itemView.findViewById(R.id.vehicle_brand);
                vehicleAvailability = itemView.findViewById(R.id.vehicle_availability);
                verificationStatus = itemView.findViewById(R.id.verification_status);
            }
        }
    }
}