//package com.uit.carrental.Service.Activity;
//
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.StrictMode;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.squareup.picasso.Picasso;
//import com.uit.carrental.ActivityPages.CustomerMainActivity;
//import com.uit.carrental.Model.User;
//import com.uit.carrental.Model.Vehicle;
//import com.uit.carrental.R;
//import com.uit.carrental.Service.ZaloPay.Api.CreateOrder;
//import com.uit.carrental.Service.ZaloPay.Constant.AppInfo;
//
//import org.json.JSONObject;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import vn.zalopay.sdk.Environment;
//import vn.zalopay.sdk.ZaloPayError;
//import vn.zalopay.sdk.ZaloPaySDK;
//import vn.zalopay.sdk.listeners.PayOrderListener;
//
//public class CustomerActivityDetail extends AppCompatActivity {
//
//    private FirebaseFirestore db;
//    private CreateOrder orderApi;
//    private Intent intent;
//    private String ownerId, vehicleId, ownerName, ownerEmail, ownerPhone, ownerAddress, vehicleName, vehiclePrice, vehiclePickup, vehicleDrop, totalCost; // Thay provideId bằng ownerId
//    private String notiId, notiStatus, orderId;
//    private String amount = "1000";
//    private ImageView vehicleImage;
//    private TextView tvId, tvStatus, name, email, phoneNumber, tvBrandCar, tvGia, tvDiaDiem, pickup, dropoff, tvTotalCost;
//    private Button btnPayment, btnCancel, btnBack;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.customer_booking_detail);
//        intent = getIntent();
//        notiId = intent.getStringExtra("NotiID");
//
//        init();
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);
//
//        db = FirebaseFirestore.getInstance();
//        db.collection("Notification")
//                .whereEqualTo("noti_id", notiId)
//                .get()
//                .addOnSuccessListener(querySnapshot -> {
//                    for (QueryDocumentSnapshot document : querySnapshot) {
//                        Activity temp = new Activity();
//                        temp.setNoti_id(document.getId());
//                        temp.setOwnerId(document.getString("ownerId")); // Thay provider_id bằng ownerId
//                        temp.setVehicle_id(document.getString("vehicle_id"));
//                        temp.setStatus(document.getString("status"));
//                        temp.setPickup(document.getString("pickup"));
//                        temp.setDropoff(document.getString("dropoff"));
//
//                        vehiclePickup = temp.getPickup();
//                        vehicleDrop = temp.getDropoff();
//                        ownerId = temp.getOwnerId(); // Thay provideId bằng ownerId
//                        vehicleId = temp.getVehicle_id();
//                        notiStatus = temp.getStatus();
//
//                        if (document.contains("order_id")) {
//                            orderId = document.getString("order_id");
//                            Log.e("OrderID", "order_id: " + orderId);
//                        }
//
//                        tvId.setText(notiId);
//                        updateStatusAndButton(notiStatus);
//
//                        getUser(ownerId); // Thay provideId bằng ownerId
//                        getVehicle(vehicleId);
//                    }
//                })
//                .addOnFailureListener(e -> Toast.makeText(this, "Không thể lấy thông báo: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//
//        btnPayment.setOnClickListener(v -> {
//            if (orderId != null) {
//                createOrderAndPay();
//            } else {
//                Toast.makeText(this, "Không tìm thấy order_id!", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        btnBack.setOnClickListener(v -> finish());
//
//        btnCancel.setOnClickListener(v -> handleCancel());
//    }
//
//    private void init() {
//        tvId = findViewById(R.id.txtview_noti_id);
//        tvStatus = findViewById(R.id.txtview_noti_status);
//        email = findViewById(R.id.txtview_noti_email);
//        name = findViewById(R.id.txtview_noti_name);
//        phoneNumber = findViewById(R.id.txtview_noti_phoneNumber);
//        tvBrandCar = findViewById(R.id.txtview_noti_BrandCar);
//        tvDiaDiem = findViewById(R.id.txt_checkout_address);
//        btnPayment = findViewById(R.id.btn_customer_pay);
//        btnCancel = findViewById(R.id.customer_details_btn);
//        btnBack = findViewById(R.id.btn_noti_back);
//        tvGia = findViewById(R.id.txtview_noti_price);
//        pickup = findViewById(R.id.tv_noti_pickup);
//        dropoff = findViewById(R.id.tv_noti_dropoff);
//        tvTotalCost = findViewById(R.id.txtview_noti_totalCost);
//        vehicleImage = findViewById(R.id.img_noti_car);
//    }
//
//    private void updateStatusAndButton(String status) {
//        switch (status) {
//            case "Dang cho":
//                tvStatus.setText("Chưa được xác nhận");
//                btnPayment.setVisibility(View.GONE);
//                break;
//            case "Xac nhan":
//                tvStatus.setText("Đã xác nhận");
//                btnPayment.setVisibility(View.VISIBLE);
//                break;
//            case "Thanh toan":
//                tvStatus.setText("Đang chờ thanh toán");
//                btnPayment.setVisibility(View.VISIBLE);
//                break;
//            case "Khong xac nhan":
//                tvStatus.setText("Không được xác nhận");
//                btnPayment.setVisibility(View.GONE);
//                break;
//            default:
//                tvStatus.setText("Đã thanh toán");
//                btnPayment.setVisibility(View.GONE);
//                break;
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void createOrderAndPay() {
//        try {
//            CreateOrder createOrder = new CreateOrder();
//            JSONObject data = createOrder.createOrder(amount);
//
//            if (data == null) {
//                Toast.makeText(this, "Không thể tạo đơn hàng!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            String code = data.getString("returncode");
//            if ("1".equals(code)) {
//                String token = data.getString("zptranstoken");
//                checkout(token);
//            } else {
//                Toast.makeText(this, "Lỗi tạo đơn hàng!", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            Log.e("ZaloPay", "Exception: " + e.getMessage());
//            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void checkout(String token) {
//        ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", new PayOrderListener() {
//            @Override
//            public void onPaymentSucceeded(String transId, String zpTransToken, String appTransId) {
//                runOnUiThread(() -> {
//                    postPayment();
//                    updateOrderAfterPayment(orderId, ownerId); // Thay provideId bằng ownerId
//                });
//            }
//
//            @Override
//            public void onPaymentCanceled(String zpTransToken, String appTransId) {
//                runOnUiThread(() -> Toast.makeText(CustomerActivityDetail.this, "Bạn đã hủy thanh toán.", Toast.LENGTH_SHORT).show());
//            }
//
//            @Override
//            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransId) {
//                runOnUiThread(() -> Toast.makeText(CustomerActivityDetail.this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show());
//            }
//        });
//    }
//
//    private void postPayment() {
//        Map<String, Object> updateData = new HashMap<>();
//        updateData.put("payment_status", "paid");
//        updateData.put("status", "processing");
//
//        db.collection("Orders")
//                .document(orderId)
//                .update(updateData)
//                .addOnSuccessListener(unused -> Log.e("Payment Update", "Cập nhật trạng thái thành công!"))
//                .addOnFailureListener(e -> Log.e("Payment Update", "Cập nhật thất bại: " + e.getMessage()));
//    }
//
//    private void updateOrderAfterPayment(String orderId, String ownerId) { // Thay providerId bằng ownerId
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("status", "Đã thanh toán");
//        updates.put("payment_method", "zalo_pay");
//
//        db.collection("Orders").document(orderId)
//                .update(updates)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
//                    createPaymentSuccessNotification(ownerId); // Thay providerId bằng ownerId
//                    Intent intent = new Intent(this, CustomerMainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//                })
//                .addOnFailureListener(e -> Toast.makeText(this, "Có lỗi khi cập nhật đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    private void createPaymentSuccessNotification(String ownerId) { // Thay providerId bằng ownerId
//        Map<String, Object> notification = new HashMap<>();
//        notification.put("user_id", ownerId);
//        notification.put("title", "Thanh toán thành công");
//        notification.put("content", "Khách hàng đã thanh toán đơn thuê xe của bạn.");
//        notification.put("timestamp", new Timestamp(new Date()));
//        notification.put("status", "unread");
//
//        db.collection("Notifications")
//                .add(notification)
//                .addOnFailureListener(e -> Log.e("Notification", "Lỗi tạo thông báo: " + e.getMessage()));
//    }
//
//    private void getUser(String ownerId) { // Thay provideId bằng ownerId
//        db.collection("Users")
//                .whereEqualTo("user_id", ownerId)
//                .get()
//                .addOnSuccessListener(querySnapshot -> {
//                    for (QueryDocumentSnapshot document : querySnapshot) {
//                        User user = document.toObject(User.class);
//                        ownerName = user.getUsername();
//                        ownerEmail = user.getEmail();
//                        ownerPhone = user.getPhoneNumber();
//                        ownerAddress = user.getAddress();
//                        name.setText(ownerName);
//                        email.setText(ownerEmail);
//                        phoneNumber.setText(ownerPhone);
//                        tvDiaDiem.setText(ownerAddress != null ? ownerAddress : "Chưa xác định");
//                    }
//                })
//                .addOnFailureListener(e -> Toast.makeText(this, "Không thể lấy thông tin nhà cung cấp: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    private void getVehicle(String vehicleId) {
//        db.collection("Vehicles")
//                .whereEqualTo("vehicleId", vehicleId)
//                .get()
//                .addOnSuccessListener(querySnapshot -> {
//                    for (QueryDocumentSnapshot document : querySnapshot) {
//                        Vehicle vehicle = document.toObject(Vehicle.class);
//                        vehicle.setVehicleId(document.getId());
//                        vehicleName = vehicle.getVehicleName();
//                        vehiclePrice = vehicle.getVehiclePrice();
//                        tvBrandCar.setText(vehicleName);
//                        tvGia.setText(vehiclePrice + "/ngày");
//                        pickup.setText(vehiclePickup);
//                        dropoff.setText(vehicleDrop);
//                        totalCost = calculate(vehiclePickup, vehicleDrop);
//                        tvTotalCost.setText(totalCost);
//                        amount = totalCost;
//
//                        if (!vehicle.getVehicleImageUrl().isEmpty()) {
//                            Picasso.get().load(vehicle.getVehicleImageUrl()).into(vehicleImage);
//                        }
//
//                        ownerId = vehicle.getOwnerId(); // Đã đúng với model Vehicle
//                        getUser(ownerId); // Thay provideId bằng ownerId
//                    }
//                })
//                .addOnFailureListener(e -> Toast.makeText(this, "Không thể lấy thông tin xe: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    private String calculate(String startDate, String endDate) {
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date start = sdf.parse(startDate);
//            Date end = sdf.parse(endDate);
//
//            long diffInMillies = end.getTime() - start.getTime();
//            long daysBetween = diffInMillies / (1000 * 60 * 60 * 24);
//
//            if (daysBetween <= 0) {
//                Toast.makeText(this, "Ngày trả phải sau ngày nhận!", Toast.LENGTH_SHORT).show();
//                return "0";
//            }
//
//            String priceText = tvGia.getText().toString().replace("/ngày", "");
//            int pricePerDay = Integer.parseInt(priceText.replaceAll("[^0-9]", ""));
//            long totalPrice = pricePerDay * daysBetween;
//
//            Log.e("Total Days", String.valueOf(daysBetween));
//            Log.e("Total Price", String.valueOf(totalPrice));
//
//            return String.valueOf(totalPrice);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Định dạng ngày không hợp lệ!", Toast.LENGTH_SHORT).show();
//            return "0";
//        }
//    }
//
//    private void handleCancel() {
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("status", "cancelled");
//
//        db.collection("Notification").document(notiId)
//                .update(updates)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Đã hủy đơn đặt xe", Toast.LENGTH_SHORT).show();
//                    finish();
//                })
//                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi hủy đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        ZaloPaySDK.getInstance().onResult(intent);
//    }
//}