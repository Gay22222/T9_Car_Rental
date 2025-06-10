package com.uit.carrental.Service.Vehicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VehicleCardActivity extends AppCompatActivity {

    private FirebaseFirestore dbVehicle;
    private String vehicleId;
    private TextView textViewVehicleName, textViewVehiclePrice;
    private ImageView imageViewVehicle;
    private Vehicle vehicle = new Vehicle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_card);

        vehicleId = getIntent().getStringExtra("vehicleId");
        if (vehicleId == null) {
            Toast.makeText(this, "Không tìm thấy ID xe", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        init();
        getDetail();
    }

    private void init() {
        dbVehicle = FirebaseFirestore.getInstance();
        textViewVehicleName = findViewById(R.id.vehicle_name);
        textViewVehiclePrice = findViewById(R.id.tv_vehicle_price);
        imageViewVehicle = findViewById(R.id.img_vehicle);
    }

    private void getDetail() {
        dbVehicle.collection("Vehicles")
                .document(vehicleId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        vehicle = documentSnapshot.toObject(Vehicle.class);
                        if (vehicle != null) {
                            textViewVehicleName.setText(vehicle.getVehicleName() != null ? vehicle.getVehicleName() : "Xe không tên");
                            textViewVehiclePrice.setText(vehicle.getVehiclePrice() != null ? vehicle.getVehiclePrice() : "0 VNĐ");
                            if (vehicle.getVehicleImageUrl() != null && !vehicle.getVehicleImageUrl().isEmpty()) {
                                Glide.with(this)
                                        .load(vehicle.getVehicleImageUrl())
                                        .placeholder(R.drawable.ic_car)
                                        .into(imageViewVehicle);
                            } else {
                                imageViewVehicle.setImageResource(R.drawable.ic_car);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy xe", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi lấy chi tiết xe: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }
}