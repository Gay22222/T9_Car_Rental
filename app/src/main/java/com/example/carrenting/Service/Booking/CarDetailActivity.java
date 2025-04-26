package com.example.carrenting.Service.Booking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.carrenting.R;

import java.io.ByteArrayInputStream;

public class CarDetailActivity extends AppCompatActivity {

    private ImageView vehicleImage;
    private TextView vehicleName, vehiclePrice, vehicleSeats, vehicleNumber;
    private TextView vehicleOwner;
    private TextView ownerName, ownerEmail, ownerPhone, ownerAddress;
    private Button btnBook;

    private boolean hasLoaded = false;
    private String imgData, name, price, seats, number, owner, email, phone, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_car);

        vehicleImage = findViewById(R.id.vehicle_img);
        vehicleName = findViewById(R.id.tv_vehicle_name);
        vehiclePrice = findViewById(R.id.tv_vehicle_price);
        vehicleSeats = findViewById(R.id.tv_vehicle_seats);
        vehicleNumber = findViewById(R.id.tv_vehicle_number);
        vehicleOwner = findViewById(R.id.tv_vehicle_owner);
        ownerName = findViewById(R.id.tv_provider_name);
        ownerEmail = findViewById(R.id.tv_provider_gmail);
        ownerPhone = findViewById(R.id.tv_provider_phone);
        ownerAddress = findViewById(R.id.tv_provider_address);
        btnBook = findViewById(R.id.btn_book);

        // Lấy intent
        Intent intent = getIntent();
        imgData = intent.getStringExtra("vehicle_imageURL");
        name = intent.getStringExtra("vehicle_name");
        price = intent.getStringExtra("vehicle_price");
        seats = intent.getStringExtra("vehicle_seats");
        number = intent.getStringExtra("vehicle_number");
        owner = intent.getStringExtra("vehicle_owner");
        email = intent.getStringExtra("vehicle_email");
        phone = intent.getStringExtra("vehicle_phone");
        address = intent.getStringExtra("vehicle_address");

        // Nếu thiếu, fake dữ liệu
        if (imgData == null)
            imgData = "https://media.vov.vn/sites/default/files/styles/large/public/2021-01/2016-koenigsegg-regera-v1-1080-768x432.jpg";
        if (name == null) name = "Xe Bugatti Chiron Super Sport";
        if (price == null) price = "2.500.000 VND/ngày";
        if (seats == null) seats = "4";
        if (number == null) number = "60A-000.01";
        if (owner == null) owner = "Trương Đăng Khôi";
        if (email == null) email = "khoitruong.dev@gmail.com";
        if (phone == null) phone = "0934567890";
        if (address == null) address = "Quận 1, TP.HCM";

        btnBook.setOnClickListener(v -> {
            Intent i = new Intent(CarDetailActivity.this, ScheduleSelect.class);
            i.putExtra("vehicle_name", name);
            i.putExtra("vehicle_imageURL", imgData);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!hasLoaded) {
            hasLoaded = true;

            vehicleName.setText(name);
            vehiclePrice.setText(price);
            vehicleSeats.setText(seats);
            vehicleNumber.setText(number);
            vehicleOwner.setText(owner);
            ownerName.setText(owner);
            ownerEmail.setText(email);
            ownerPhone.setText(phone);
            ownerAddress.setText(address);

            // Hiển thị ảnh
            if (imgData.startsWith("data:image")) {
                try {
                    String base64Data = imgData.substring(imgData.indexOf(",") + 1);
                    byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    vehicleImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Glide.with(this)
                        .load(imgData)
                        .placeholder(R.drawable.ic_car)
                        .into(vehicleImage);
            }
        }
    }
}
