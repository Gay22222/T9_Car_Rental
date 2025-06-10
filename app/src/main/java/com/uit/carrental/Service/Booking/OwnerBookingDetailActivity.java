package com.uit.carrental.Service.Booking;

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
import com.uit.carrental.Model.Notification;
import com.uit.carrental.Model.User;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OwnerBookingDetailActivity extends AppCompatActivity {

    private static final String TAG = "OwnerBookingDetailActivity";
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

        bookingId = getIntent().getStringExtra("bookingId");
        if (bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy yêu cầu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initFirebase();
        loadBookingData();

        btnBack.setOnClickListener(v -> finish());
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
                            tvAddress.setText(booking.getPickupLocation());
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
        tvPickup.setText(booking.getStartTime() != null ? sdf.format(booking.getStartTime().toDate()) : "");
        tvDropoff.setText(booking.getEndTime() != null ? sdf.format(booking.getEndTime().toDate()) : "");
        tvTotalCost.setText(String.format(Locale.getDefault(), "%,d VNĐ", (long) booking.getTotalAmount()));
    }

    private void confirmBooking() {
        if (booking == null) {
            Toast.makeText(this, "Dữ liệu booking chưa tải, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vehicle == null) {
            Toast.makeText(this, "Dữ liệu xe chưa tải, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            return;
        }

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
                    Log.d(TAG, "Cập nhật booking thành công: " + bookingId);
                    createNotification(booking.getCustomerId(), "Yêu cầu đặt xe #" + bookingId + " đã được xác nhận", "status_update");
                    createOrder();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi xác nhận yêu cầu: " + e.getMessage());
                    Toast.makeText(this, "Lỗi xác nhận: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void rejectBooking() {
        if (booking == null) {
            Toast.makeText(this, "Dữ liệu booking chưa tải, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "rejected");
        updates.put("updatedAt", Timestamp.now());

        db.collection("Bookings").document(bookingId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Từ chối booking thành công: " + bookingId);
                    createNotification(booking.getCustomerId(), "Yêu cầu đặt xe #" + bookingId + " đã bị từ chối", "status_update");
                    Toast.makeText(this, "Đã từ chối yêu cầu", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi từ chối yêu cầu: " + e.getMessage());
                    Toast.makeText(this, "Lỗi từ chối: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void createOrder() {
        if (booking == null || vehicle == null) {
            Log.e(TAG, "Booking hoặc vehicle null, không thể tạo order");
            Toast.makeText(this, "Lỗi: Dữ liệu không khả dụng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
                    String newOrderId = orderRef.getId();
                    Log.d(TAG, "Tạo order thành công: " + newOrderId);
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("orderId", newOrderId);
                    db.collection("Bookings").document(bookingId)
                            .update(updateData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Cập nhật orderId vào booking thành công: " + newOrderId);
                                Toast.makeText(this, "Đã xác nhận yêu cầu và tạo order", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Lỗi cập nhật orderId vào booking: " + e.getMessage());
                                Toast.makeText(this, "Lỗi cập nhật orderId: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tạo order: " + e.getMessage());
                    Toast.makeText(this, "Lỗi tạo order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void createNotification(String userId, String message, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setBookingId(bookingId);
        notification.setMessage(message);
        notification.setType(type);
        notification.setCreatedAt(Timestamp.now());
        notification.setIsRead(false);
        db.collection("Notifications").add(notification)
                .addOnSuccessListener(documentReference -> {
                    String notificationId = documentReference.getId();
                    db.collection("Notifications").document(notificationId)
                            .update("notificationId", notificationId);
                    Log.d(TAG, "Tạo thông báo thành công: " + notificationId);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi tạo thông báo: " + e.getMessage()));
    }
}
