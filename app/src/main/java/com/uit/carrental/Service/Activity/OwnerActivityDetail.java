package com.uit.carrental.Service.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.uit.carrental.ActivityPages.OwnerMainActivity;
import com.uit.carrental.Model.Booking;
import com.uit.carrental.Model.User;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OwnerActivityDetail extends AppCompatActivity {

    private static final String TAG = "OwnerActivityDetail";

    private FirebaseFirestore db;
    private String bookingId, vehicleId;
    private Booking booking;
    private Vehicle vehicle;
    private User customer;
    private ImageView vehicleImage, btnBack;
    private TextView tvId, tvName, tvEmail, tvPhoneNumber, tvStatus, tvBrandCar, tvPrice, tvAddress, tvPickup, tvDropoff, tvTotalCost;
    private Button btnConfirm, btnReject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_booking_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bookingId = getIntent().getStringExtra("NotiID");
        if (bookingId == null) {
            Toast.makeText(this, "Không tìm thấy yêu cầu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initFirebase();
        loadBookingData();

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, OwnerMainActivity.class));
            finish();
        });

        btnConfirm.setOnClickListener(v -> confirmBooking());
        btnReject.setOnClickListener(v -> rejectBooking());
    }

    private void initViews() {
        tvId = findViewById(R.id.txtview_noti_id);
        tvName = findViewById(R.id.txtview_noti_name);
        tvEmail = findViewById(R.id.txtview_noti_email);
        tvPhoneNumber = findViewById(R.id.txtview_noti_phoneNumber);
        tvStatus = findViewById(R.id.txtview_noti_status);
        tvBrandCar = findViewById(R.id.txtview_noti_BrandCar);
        tvPrice = findViewById(R.id.txtview_noti_price);
        tvAddress = findViewById(R.id.txt_checkout_address);
        tvPickup = findViewById(R.id.tv_noti_pickup);
        tvDropoff = findViewById(R.id.tv_noti_dropoff);
        tvTotalCost = findViewById(R.id.txtview_noti_totalCost);
        btnConfirm = findViewById(R.id.btn_noti_XacNhan);
        btnReject = findViewById(R.id.btn_noti_huy);
        btnBack = findViewById(R.id.btn_noti_back);
        vehicleImage = findViewById(R.id.img_noti_car);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void loadBookingData() {
        db.collection("Bookings").document(bookingId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        booking = document.toObject(Booking.class);
                        if (booking != null) {
                            vehicleId = booking.getVehicleId();
                            tvId.setText(bookingId);
                            updateStatusView();
                            loadVehicleData();
                            loadCustomerData();
                            updateTimeAndPrice();
                        } else {
                            Toast.makeText(this, "Không thể đọc dữ liệu yêu cầu", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Yêu cầu không tồn tại", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải dữ liệu yêu cầu: " + e.getMessage());
                    Toast.makeText(this, "Lỗi tải: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void updateStatusView() {
        String status = booking.getStatus();
        switch (status) {
            case "pending":
                tvStatus.setText("Chưa được xác nhận");
                break;
            case "confirmed":
                tvStatus.setText("Đã xác nhận");
                btnConfirm.setEnabled(false);
                btnReject.setEnabled(false);
                break;
            case "rejected":
                tvStatus.setText("Không được xác nhận");
                btnConfirm.setEnabled(false);
                btnReject.setEnabled(false);
                break;
            case "paid":
                tvStatus.setText("Đã thanh toán");
                btnConfirm.setEnabled(false);
                btnReject.setEnabled(false);
                break;
            case "completed":
                tvStatus.setText("Đã hoàn thành");
                btnConfirm.setEnabled(false);
                btnReject.setEnabled(false);
                break;
            default:
                tvStatus.setText(status);
        }
    }

    private void loadVehicleData() {
        db.collection("Vehicles").document(vehicleId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        vehicle = document.toObject(Vehicle.class);
                        if (vehicle != null) {
                            tvBrandCar.setText(vehicle.getVehicleName());
                            tvPrice.setText(vehicle.getVehiclePrice());
                            if (!vehicle.getVehicleImageUrl().isEmpty()) {
                                Picasso.get().load(vehicle.getVehicleImageUrl()).into(vehicleImage);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải dữ liệu xe: " + e.getMessage());
                    Toast.makeText(this, "Lỗi tải xe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCustomerData() {
        db.collection("Users").document(booking.getCustomerId())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        customer = document.toObject(User.class);
                        if (customer != null) {
                            tvName.setText(customer.getUsername());
                            tvEmail.setText(customer.getEmail());
                            tvPhoneNumber.setText(customer.getPhoneNumber());
                            tvAddress.setText(customer.getAddress() != null ? customer.getAddress() : "Không có địa chỉ");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải dữ liệu khách hàng: " + e.getMessage());
                    Toast.makeText(this, "Lỗi tải khách hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateTimeAndPrice() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        tvPickup.setText(sdf.format(booking.getStartTime().toDate()));
        tvDropoff.setText(sdf.format(booking.getEndTime().toDate()));
        tvTotalCost.setText(String.format(Locale.getDefault(), "%,d VNĐ", booking.getTotalAmount()));
    }

    private void confirmBooking() {
        // Tính thời gian bảo trì (1 ngày sau endTime)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(booking.getEndTime().toDate());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Timestamp maintenanceEndTime = new Timestamp(calendar.getTime());

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "confirmed");
        updates.put("updatedAt", Timestamp.now());
        updates.put("maintenanceEndTime", maintenanceEndTime);

        db.collection("Bookings").document(bookingId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    sendCustomerNotification("Yêu cầu được xác nhận", "Yêu cầu đặt xe của bạn đã được xác nhận.");
                    Toast.makeText(this, "Đã xác nhận yêu cầu", Toast.LENGTH_SHORT).show();
                    createOrder();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi xác nhận yêu cầu: " + e.getMessage());
                    Toast.makeText(this, "Lỗi xác nhận: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectBooking() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "rejected");
        updates.put("updatedAt", Timestamp.now());

        db.collection("Bookings").document(bookingId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    sendCustomerNotification("Yêu cầu bị từ chối", "Yêu cầu đặt xe của bạn đã bị từ chối.");
                    Toast.makeText(this, "Đã từ chối yêu cầu", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi từ chối yêu cầu: " + e.getMessage());
                    Toast.makeText(this, "Lỗi từ chối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createOrder() {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("customer_id", booking.getCustomerId());
        orderData.put("provider_id", vehicle.getOwnerId());
        orderData.put("vehicle_id", vehicleId);
        orderData.put("pickup_time", booking.getStartTime());
        orderData.put("dropoff_time", booking.getEndTime());
        orderData.put("total_amount", booking.getTotalAmount());
        orderData.put("status", "pending");
        orderData.put("payment_status", "pending");
        orderData.put("created_at", Timestamp.now());

        db.collection("Orders").add(orderData)
                .addOnSuccessListener(orderRef -> {
                    Log.d(TAG, "Order created: " + orderRef.getId());
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("order_id", orderRef.getId());
                    db.collection("Bookings").document(bookingId)
                            .update(updateData)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "order_id updated in booking"))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to update booking", e));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to create order", e));
    }

    private void sendCustomerNotification(String title, String content) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("recipient", booking.getCustomerId());
        notification.put("title", title);
        notification.put("content", content);
        notification.put("timestamp", Timestamp.now());
        notification.put("status", "unread");
        notification.put("type", "booking_status");
        notification.put("bookingId", bookingId);
        notification.put("vehicleId", vehicleId);

        db.collection("Notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Gửi thông báo thành công"))
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi gửi thông báo: " + e.getMessage()));
    }
}
