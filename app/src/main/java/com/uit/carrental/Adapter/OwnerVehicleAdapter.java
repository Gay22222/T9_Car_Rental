package com.uit.carrental.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uit.carrental.FragmentPages.Owner.OwnerVehicleFragment;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.Model.onClickInterface;
import com.uit.carrental.R;
import com.uit.carrental.Service.Vehicle.UpdateVehicle;

import java.util.ArrayList;

public class OwnerVehicleAdapter extends RecyclerView.Adapter<OwnerVehicleAdapter.MyViewHolder> {
    private final OwnerVehicleFragment ownerVehicleFragment;
    private final ArrayList<Vehicle> vehicles;
    private final onClickInterface onClickInterface;

    public OwnerVehicleAdapter(OwnerVehicleFragment context, ArrayList<Vehicle> vehicles, onClickInterface onClickInterface) {
        this.ownerVehicleFragment = context;
        this.vehicles = vehicles;
        this.onClickInterface = onClickInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ownerVehicleFragment.getActivity()).inflate(R.layout.owner_vehicle_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final int pos = position;
        Vehicle vehicle = vehicles.get(position);

        // Mock data if null
        holder.name.setText(vehicle.getVehicle_name() != null ? vehicle.getVehicle_name() : "Xe mẫu");
        holder.price.setText(vehicle.getVehicle_price() != null ? vehicle.getVehicle_price() : "500.000VNĐ/Ngày");
        holder.provider.setText(vehicle.getProvider_name() != null ? vehicle.getProvider_name() : "Nhà cung cấp mẫu");
        holder.rating.setText(vehicle.getVehicle_rating() != null ? vehicle.getVehicle_rating() : "4.0 (0 Đánh giá)");

        // Load image with Glide
        if (vehicle.getVehicle_imageURL() != null && !vehicle.getVehicle_imageURL().isEmpty()) {
            Glide.with(ownerVehicleFragment.getActivity())
                    .load(vehicle.getVehicle_imageURL())
                    .into(holder.vehicleImage);
        } else {
            holder.vehicleImage.setImageResource(R.drawable.a2_0_1); // Mock image
        }

        holder.itemView.setOnClickListener(v -> {
            onClickInterface.setClick(pos);
            Vehicle clickedVehicle = vehicles.get(pos);
            Intent intent = new Intent(ownerVehicleFragment.getActivity(), UpdateVehicle.class);
            intent.putExtra("vehicle_id", clickedVehicle.getVehicle_id());
            ownerVehicleFragment.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, provider, rating;
        ImageView vehicleImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.vehicle_name);
            price = itemView.findViewById(R.id.tv_vehicle_price);
            provider = itemView.findViewById(R.id.provider_name);
            rating = itemView.findViewById(R.id.vehicle_rating);
            vehicleImage = itemView.findViewById(R.id.img_vehicle);
        }
    }
}