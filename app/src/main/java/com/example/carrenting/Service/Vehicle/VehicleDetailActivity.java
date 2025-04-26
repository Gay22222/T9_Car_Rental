//package com.example.carrenting.Service.Vehicle;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.example.carrenting.Model.Vehicle;
//import com.example.carrenting.R;
//import com.example.carrenting.Service.Booking.ScheduleSelect;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.squareup.picasso.Picasso;
//
//public class VehicleDetailActivity extends AppCompatActivity {
//
//    private ImageView vehicleImage;
//    private TextView providerName, providerGmail, providerPhone, providerAddress;
//    private TextView vehicleName, vehiclePrice, vehicleNumber, vehicleSeats, vehicleOwner;
//    private Button btnBook;
//    private String vehicleID;
//    private FirebaseFirestore dtb_vehicle;
//    private Vehicle vehicle = new Vehicle();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_detail_car);
//
//        Intent intent = getIntent();
//        vehicleID = intent.getStringExtra("vehicle_id");
//        vehicle.setVehicle_id(vehicleID);
//
//        init();
//
//        getDetail();
//
//        FirebaseFirestore dtb = FirebaseFirestore.getInstance();
//        btnBook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(VehicleDetailActivity.this, ScheduleSelect.class);
//                i.putExtra("vehicle_id", vehicleID);
//                startActivity(i);
//            }
//        });
//
//    }
//
//    private void init()
//    {
//        btnBook = findViewById(R.id.btn_book);
//
//        vehicleImage = findViewById(R.id.vehicle_img);
//        providerName = findViewById(R.id.tv_provider_name);
//        providerGmail = findViewById(R.id.tv_provider_gmail);
//        providerAddress = findViewById(R.id.tv_provider_address);
//        providerPhone = findViewById(R.id.tv_provider_phone);
//
//        vehicleName = findViewById(R.id.tv_vehicle_name);
//        vehicleNumber = findViewById(R.id.tv_vehicle_number);
//        vehicleSeats = findViewById(R.id.tv_vehicle_seats);
//        vehiclePrice = findViewById(R.id.tv_vehicle_price);
//        vehicleOwner = findViewById(R.id.tv_vehicle_owner);
//
//        dtb_vehicle = FirebaseFirestore.getInstance();
//    }
//
//    private void getDetail() {
//        dtb_vehicle.collection("Vehicles")
//                .whereEqualTo("vehicle_id", vehicle.getVehicle_id())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()){
//                            for (QueryDocumentSnapshot document : task.getResult()){
//
//                                    vehicle.setOwner_id(document.get("provider_id").toString());
//                                    vehicle.setVehicle_id(document.get("vehicle_id").toString());
//                                    vehicle.setVehicle_status(document.get("vehicle_availability").toString());
//
//                                    vehicle.setOwner_name(document.get("provider_name").toString());
//                                    providerName.setText(vehicle.getOwner_name());
//
//                                    vehicle.setOwner_phone(document.get("provider_phone").toString());
//                                    providerPhone.setText(vehicle.getOwner_phone());
//
//                                    vehicle.setOwner_address(document.get("provider_address").toString());
//                                    providerAddress.setText(vehicle.getOwner_address());
//
//                                    vehicle.setOwner_email(document.get("provider_gmail").toString());
//                                    providerGmail.setText(vehicle.getOwner_email());
//
//                                    vehicle.setVehicle_name(document.get("vehicle_name").toString());
//                                    vehicleName.setText(vehicle.getVehicle_name());
//
//                                    vehicle.setVehicle_price(document.get("vehicle_price").toString());
//                                    vehiclePrice.setText(vehicle.getVehicle_price());
//
//                                    vehicle.setVehicle_seats(document.get("vehicle_seats").toString());
//                                    vehicleSeats.setText(vehicle.getVehicle_seats());
//
//                                    vehicle.setOwner_name(document.get("owner_name").toString());
//                                    vehicleOwner.setText(vehicle.getOwner_name());
//
//                                    vehicle.setVehicle_number(document.get("vehicle_number").toString());
//                                    vehicleNumber.setText(vehicle.getVehicle_number());
//
//                                    if (!document.get("vehicle_imageURL").toString().isEmpty()) {
//                                        vehicle.setVehicle_imageURL(document.get("vehicle_imageURL").toString());
//                                        Picasso.get().load(vehicle.getVehicle_imageURL()).into(vehicleImage);
//                                    }
//                                    else {
//                                        vehicle.setVehicle_imageURL("");
//                                    }
//
//                                }
//
//                            }
//                    }
//                });
//
//    }
//}
package com.example.carrenting.Service.Vehicle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carrenting.Model.Vehicle;
import com.example.carrenting.R;
import com.example.carrenting.Service.Booking.ScheduleSelect;
import com.squareup.picasso.Picasso;

public class VehicleDetailActivity extends AppCompatActivity {

    private ImageView vehicleImage;
    private TextView providerName, providerGmail, providerPhone, providerAddress;
    private TextView vehicleName, vehiclePrice, vehicleNumber, vehicleSeats, vehicleOwner;
    private Button btnBook;
    private String vehicleID;
    private Vehicle vehicle = new Vehicle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_car);

        Intent intent = getIntent();
        vehicleID = intent.getStringExtra("vehicle_id");
        vehicle.setVehicle_id(vehicleID);

        init();
        fakeData();

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VehicleDetailActivity.this, ScheduleSelect.class);
                i.putExtra("vehicle_id", vehicleID);
                startActivity(i);
            }
        });
    }

    private void init() {
        btnBook = findViewById(R.id.btn_book);
        vehicleImage = findViewById(R.id.vehicle_img);
        providerName = findViewById(R.id.tv_provider_name);
        providerGmail = findViewById(R.id.tv_provider_gmail);
        providerAddress = findViewById(R.id.tv_provider_address);
        providerPhone = findViewById(R.id.tv_provider_phone);
        vehicleName = findViewById(R.id.tv_vehicle_name);
        vehicleNumber = findViewById(R.id.tv_vehicle_number);
        vehicleSeats = findViewById(R.id.tv_vehicle_seats);
        vehiclePrice = findViewById(R.id.tv_vehicle_price);
        vehicleOwner = findViewById(R.id.tv_vehicle_owner);
    }

    private void fakeData() {
        vehicle.setOwner_name("Nguyễn Văn A");
        vehicle.setOwner_email("nguyenvana@gmail.com");
        vehicle.setOwner_phone("0123456789");
        vehicle.setOwner_address("123 Đường Lê Lợi, Quận 1");
        vehicle.setVehicle_name("Lamborghini Huracan");
        vehicle.setVehicle_price("10,000,000 VND/day");
        vehicle.setVehicle_seats("2");
        vehicle.setVehicle_number("51G-123.45");
        vehicle.setVehicle_imageURL("https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/Lamborghini_Huracan_LP610-4_%2821110325122%29.jpg/1200px-Lamborghini_Huracan_LP610-4_%2821110325122%29.jpg");

        providerName.setText(vehicle.getOwner_name());
        providerGmail.setText(vehicle.getOwner_email());
        providerPhone.setText(vehicle.getOwner_phone());
        providerAddress.setText(vehicle.getOwner_address());
        vehicleName.setText(vehicle.getVehicle_name());
        vehiclePrice.setText(vehicle.getVehicle_price());
        vehicleSeats.setText(vehicle.getVehicle_seats());
        vehicleNumber.setText(vehicle.getVehicle_number());
        vehicleOwner.setText(vehicle.getOwner_name());

        Picasso.get().load(vehicle.getVehicle_imageURL()).into(vehicleImage);
    }
}
