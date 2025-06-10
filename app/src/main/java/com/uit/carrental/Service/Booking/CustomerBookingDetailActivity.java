package com.uit.carrental.Service.Booking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.uit.carrental.ActivityPages.CustomerMainActivity;
import com.uit.carrental.Model.Booking;
import com.uit.carrental.Model.User;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.uit.carrental.Service.ZaloPay.Api.CreateOrder;
import com.uit.carrental.Service.ZaloPay.Constant.AppInfo;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class CustomerBookingDetailActivity extends AppCompatActivity {

    private static final String TAG = "CustomerBookingDetailActivity";
    private ImageView imgBack, imgCar;
    private TextView tvBookingId, tvVehicleName, tvOwnerName, tvOwnerEmail, tvOwnerPhone, tvPickupLocation, tvPickupTime, tvDropoffTime, tvPrice, tvTotalCost, tvStatus;
    private Button btnPay, btnCancel;
    private FirebaseFirestore db;
    private String bookingId;
    private Booking booking;
    private String orderId;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_booking_detail);

        db = FirebaseFirestore.getInstance();
        bookingId = getIntent().getStringExtra("bookingId");
        if (bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy bookingId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo ZaloPay SDK
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);

        initViews();
        loadBookingDetails();
        setupButtons();
    }

    private void initViews() {
        imgBack = findViewById(R.id.btn_noti_back);
        imgCar = findViewById(R.id.img_noti_car);
        tvBookingId = findViewById(R.id.txtview_noti_id);
        tvVehicleName = findViewById(R.id.txtview_noti_BrandCar);
        tvOwnerName = findViewById(R.id.txtview_noti_name);
        tvOwnerEmail = findViewById(R.id.txtview_noti_email);
        tvOwnerPhone = findViewById(R.id.txtview_noti_phoneNumber);
        tvPickupLocation = findViewById(R.id.txt_checkout_address);
        tvPickupTime = findViewById(R.id.tv_noti_pickup);
        tvDropoffTime = findViewById(R.id.tv_noti_dropoff);
        tvPrice = findViewById(R.id.txtview_noti_price);
        tvTotalCost = findViewById(R.id.txtview_noti_totalCost);
        tvStatus = findViewById(R.id.txtview_noti_status);
        btnPay = findViewById(R.id.btn_customer_pay);
        btnCancel = findViewById(R.id.customer_details_btn);
    }

    private void loadBookingDetails() {
        db.collection("Bookings").document(bookingId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        booking = document.toObject(Booking.class);
                        if (booking != null) {
                            tvBookingId.setText(booking.getBookingId());
                            if (document.contains("orderId")) {
                                orderId = document.getString("orderId");
                                Log.d(TAG, "orderId: " + orderId);
                            } else {
                                Toast.makeText(this, "Không tìm thấy orderId trong booking", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }

                            // Lấy thông tin xe
                            db.collection("Vehicles").document(booking.getVehicleId())
                                    .get()
                                    .addOnSuccessListener(vehicleDoc -> {
                                        if (vehicleDoc.exists()) {
                                            Vehicle vehicle = vehicleDoc.toObject(Vehicle.class);
                                            if (vehicle != null) {
                                                tvVehicleName.setText(vehicle.getVehicleName());
                                                tvPrice.setText(vehicle.getVehiclePrice());
                                                if (!vehicle.getVehicleImageUrl().isEmpty()) {
                                                    Picasso.get().load(vehicle.getVehicleImageUrl()).into(imgCar);
                                                }
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Lỗi lấy vehicle: " + e.getMessage()));

                            // Lấy thông tin owner
                            db.collection("Users").document(booking.getOwnerId())
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        if (userDoc.exists()) {
                                            User owner = userDoc.toObject(User.class);
                                            if (owner != null) {
                                                tvOwnerName.setText(owner.getUsername());
                                                tvOwnerEmail.setText(owner.getEmail());
                                                tvOwnerPhone.setText(owner.getPhoneNumber());
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Lỗi lấy owner: " + e.getMessage()));

                            tvPickupLocation.setText(booking.getPickupLocation());
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            tvPickupTime.setText(booking.getStartTime() != null ? sdf.format(booking.getStartTime().toDate()) : "");
                            tvDropoffTime.setText(booking.getEndTime() != null ? sdf.format(booking.getEndTime().toDate()) : "");
                            DecimalFormat formatter = new DecimalFormat("#,### VNĐ");
                            tvTotalCost.setText(formatter.format(booking.getTotalAmount()));
                            tvStatus.setText(getStatusText(booking.getStatus()));

                            // Hiển thị nút thanh toán
                            btnPay.setVisibility(booking.getStatus().equals("confirmed") && booking.getPaymentStatus().equals("unpaid") ? View.VISIBLE : View.GONE);
                        } else {
                            Toast.makeText(this, "Không thể ánh xạ dữ liệu booking", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy booking", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải chi tiết: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "Chưa xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "rejected": return "Bị từ chối";
            case "paid": return "Đã thanh toán";
            case "completed": return "Đã hoàn thành";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }

    private void setupButtons() {
        imgBack.setOnClickListener(v -> finish());

        btnPay.setOnClickListener(v -> {
            if (orderId != null && !orderId.isEmpty()) {
                createOrderAndPay();
            } else {
                Toast.makeText(this, "Không tìm thấy orderId!", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("status", "cancelled");
            updates.put("updatedAt", Timestamp.now());

            db.collection("Bookings").document(bookingId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        createNotification(booking.getOwnerId(), "Booking #" + bookingId + " đã bị hủy", "status_update");
                        Toast.makeText(this, "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi hủy đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void createOrderAndPay() {
        long amount = (long) booking.getTotalAmount();
        if (amount <= 0) {
            Toast.makeText(this, "Số tiền không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            try {
                CreateOrder createOrder = new CreateOrder();
                JSONObject data = createOrder.createOrder(String.valueOf(amount));
                mainHandler.post(() -> {
                    if (data == null) {
                        Toast.makeText(this, "Không thể tạo đơn hàng!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "ZaloPay response: null");
                        return;
                    }
                    try {
                        Log.d(TAG, "ZaloPay response: " + data.toString());
                        String code = data.getString("returncode");
                        if ("1".equals(code)) {
                            String token = data.getString("zptranstoken");
                            checkout(token);
                        } else {
                            Toast.makeText(this, "Lỗi tạo đơn hàng: returncode " + code, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "ZaloPay returncode: " + code);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi xử lý response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "ZaloPay Exception: " + e.getMessage());
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    Toast.makeText(this, "Lỗi tạo đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "ZaloPay Exception: " + e.getMessage());
                });
            }
        });
    }

    private void checkout(String token) {
        ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(String transId, String zpTransToken, String appTransId) {
                runOnUiThread(() -> {
                    postPayment(transId, appTransId);
                });
            }

            @Override
            public void onPaymentCanceled(String zpTransToken, String appTransId) {
                runOnUiThread(() -> Toast.makeText(CustomerBookingDetailActivity.this, "Bạn đã hủy thanh toán.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransId) {
                runOnUiThread(() -> Toast.makeText(CustomerBookingDetailActivity.this, "Thanh toán thất bại: " + zaloPayError, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void postPayment(String transId, String appTransId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("paymentStatus", "paid");
        updates.put("paymentMethod", "ZaloPay");
        updates.put("paymentId", transId);
        updates.put("status", "paid");
        updates.put("updatedAt", Timestamp.now());
        updates.put("orderId", orderId);

        db.collection("Bookings").document(bookingId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    createPaymentSuccessNotification(booking.getOwnerId());
                    Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CustomerBookingDetailActivity.this, CustomerMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi cập nhật booking: " + e.getMessage());
                    Toast.makeText(this, "Lỗi cập nhật booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createPaymentSuccessNotification(String ownerId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", ownerId);
        notification.put("title", "Thanh toán thành công");
        notification.put("message", "Khách hàng đã thanh toán đơn thuê xe #" + bookingId);
        notification.put("type", "payment_success");
        notification.put("bookingId", bookingId);
        notification.put("createdAt", Timestamp.now());
        notification.put("isRead", false);
        notification.put("notificationId", "");

        db.collection("Notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    String notificationId = documentReference.getId();
                    db.collection("Notifications").document(notificationId)
                            .update("notificationId", notificationId);
                    Log.d(TAG, "Tạo thông báo thanh toán thành công: " + notificationId);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi tạo thông báo: " + e.getMessage()));
    }

    private void createNotification(String userId, String message, String type) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", userId);
        notification.put("title", type.equals("payment_success") ? "Thanh toán thành công" : "Cập nhật trạng thái");
        notification.put("message", message);
        notification.put("type", type);
        notification.put("bookingId", bookingId);
        notification.put("createdAt", Timestamp.now());
        notification.put("isRead", false);
        notification.put("notificationId", "");

        db.collection("Notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    String notificationId = documentReference.getId();
                    db.collection("Notifications").document(notificationId)
                            .update("notificationId", notificationId);
                    Log.d(TAG, "Tạo thông báo: " + notificationId);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi tạo thông báo: " + e.getMessage()));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
