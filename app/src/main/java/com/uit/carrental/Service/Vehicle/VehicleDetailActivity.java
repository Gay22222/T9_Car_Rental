package com.uit.carrental.Service.Vehicle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.uit.carrental.Service.Booking.ScheduleSelect;

public class VehicleDetailActivity extends AppCompatActivity {

    private ImageView vehicleImage, backButton;
    private TextView vehicleName, vehiclePrice, fuelValue, speedValue, transmissionValue, seatsValue;
    private TextView providerName, providerPhone, providerGmail, providerAddress, vehicleOwner, vehicleNumber;
    private Button btnBook;
    private String vehicleID;
    private FirebaseFirestore dtbVehicle;
    private Vehicle vehicle = new Vehicle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_car);

        Intent intent = getIntent();
        vehicleID = intent.getStringExtra("vehicle_id");
        vehicle.setVehicle_id(vehicleID);

        init();

        getDetail();

        btnBook.setOnClickListener(v -> {
            Intent i = new Intent(VehicleDetailActivity.this, ScheduleSelect.class);
            i.putExtra("vehicle_id", vehicleID);
            startActivity(i);
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void init() {
        btnBook = findViewById(R.id.btn_book);
        vehicleImage = findViewById(R.id.vehicle_img);
        backButton = findViewById(R.id.back_button);
        vehicleName = findViewById(R.id.vehicle_name);
        vehiclePrice = findViewById(R.id.tv_vehicle_price);
        fuelValue = findViewById(R.id.fuel_value);
        speedValue = findViewById(R.id.speed_value);
        transmissionValue = findViewById(R.id.transmission_value);
        seatsValue = findViewById(R.id.seats_value);
        providerName = findViewById(R.id.tv_provider_name);
        providerPhone = findViewById(R.id.tv_provider_phone);
        providerGmail = findViewById(R.id.tv_provider_gmail);
        providerAddress = findViewById(R.id.tv_provider_address);
        vehicleOwner = findViewById(R.id.tv_vehicle_owner);
        vehicleNumber = findViewById(R.id.tv_vehicle_number);
        dtbVehicle = FirebaseFirestore.getInstance();
    }

    private void getDetail() {
        dtbVehicle.collection("Vehicles")
                .whereEqualTo("vehicle_id", vehicle.getVehicle_id())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            vehicle.setVehicle_id(document.getString("vehicle_id"));
                            vehicle.setVehicle_name(document.getString("vehicle_name"));
                            vehicle.setVehicle_price(document.getString("vehicle_price"));
                            vehicle.setVehicle_imageURL(document.getString("vehicle_imageURL"));
                            vehicle.setFuel_type(document.getString("fuel_type"));
                            vehicle.setMax_speed(document.getString("max_speed"));
                            vehicle.setTransmission(document.getString("transmission"));
                            vehicle.setDoors_seats(document.getString("doors_seats"));
                            vehicle.setProvider_name(document.getString("provider_name"));
                            vehicle.setProvider_phone(document.getString("provider_phone"));
                            vehicle.setProvider_gmail(document.getString("provider_gmail"));
                            vehicle.setProvider_address(document.getString("provider_address"));
                            vehicle.setOwner_name(document.getString("owner_name"));
                            vehicle.setVehicle_number(document.getString("vehicle_number"));

                            // Set UI
                            vehicleName.setText(vehicle.getVehicle_name() != null ? vehicle.getVehicle_name() : "Mercedes C300 AMG");
                            vehiclePrice.setText(vehicle.getVehicle_price() != null ? vehicle.getVehicle_price() : "999.000VNĐ/Ngày");
                            fuelValue.setText(vehicle.getFuel_type() != null ? vehicle.getFuel_type() : "Xăng không chì 95+");
                            speedValue.setText(vehicle.getMax_speed() != null ? vehicle.getMax_speed() : "250 km/h");
                            transmissionValue.setText(vehicle.getTransmission() != null ? vehicle.getTransmission() : "Tự động 9 cấp");
                            seatsValue.setText(vehicle.getDoors_seats() != null ? vehicle.getDoors_seats() : "2 Cửa & 4 Ghế");
                            providerName.setText(vehicle.getProvider_name() != null ? vehicle.getProvider_name() : "Công ty ABC");
                            providerPhone.setText(vehicle.getProvider_phone() != null ? vehicle.getProvider_phone() : "0123456789");
                            providerGmail.setText(vehicle.getProvider_gmail() != null ? vehicle.getProvider_gmail() : "abc@example.com");
                            providerAddress.setText(vehicle.getProvider_address() != null ? vehicle.getProvider_address() : "123 Đường ABC, Đồng Nai");
                            vehicleOwner.setText(vehicle.getOwner_name() != null ? vehicle.getOwner_name() : "Nguyễn Văn A");
                            vehicleNumber.setText(vehicle.getVehicle_number() != null ? vehicle.getVehicle_number() : "60C2-88888");

                            if (vehicle.getVehicle_imageURL() != null && !vehicle.getVehicle_imageURL().isEmpty()) {
                                Picasso.get().load(vehicle.getVehicle_imageURL()).into(vehicleImage);
                            }
                        }
                    }
                });
    }
}