package com.uit.carrental.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uit.carrental.FragmentPages.Customer.CustomerHomeFragment;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.Model.onClickInterface;
import com.uit.carrental.R;

import java.util.ArrayList;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.MyViewHolder> {
    private final CustomerHomeFragment customerHomeFragment;
    private final ArrayList<Vehicle> vehicles;
    private final onClickInterface onClickInterface;

    public VehicleAdapter(CustomerHomeFragment context, ArrayList<Vehicle> vehicles, onClickInterface onClickInterface) {
        this.customerHomeFragment = context;
        this.vehicles = vehicles;
        this.onClickInterface = onClickInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(customerHomeFragment.getActivity()).inflate(R.layout.vehicle_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Vehicle vehicle = vehicles.get(position);
        holder.name.setText(vehicle.getVehicle_name());
        holder.price.setText(vehicle.getVehicle_price());
        holder.rating.setText(vehicle.getVehicle_rating() != null ? vehicle.getVehicle_rating() : "4.0 (0 Đánh giá)");
        Glide.with(customerHomeFragment.getView()).load(vehicle.getVehicle_imageURL()).into(holder.vehicleImage);
        holder.itemView.setOnClickListener(v -> onClickInterface.setClick(position));
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, rating;
        ImageView vehicleImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.vehicle_name);
            price = itemView.findViewById(R.id.tv_vehicle_price);
            rating = itemView.findViewById(R.id.vehicle_rating);
            vehicleImage = itemView.findViewById(R.id.img_vehicle);
        }
    }
}