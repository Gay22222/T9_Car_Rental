package com.uit.carrental.Service.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.uit.carrental.ActivityPages.CustomerMainActivity;
import com.uit.carrental.Service.ZaloPay.Api.CreateOrder;
import com.uit.carrental.Model.Activity;
import com.uit.carrental.Model.User;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.uit.carrental.Service.ZaloPay.Constant.AppInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

import com.google.firebase.Timestamp;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomerActivityDetail extends AppCompatActivity {

    FirebaseFirestore dtb;
    CreateOrder orderApi;
    Intent intent;
    String ProvideID, vehicle_id, ownername, owneremail, ownerphone, vehiclename, vehicleprice, vehicleaddress, vehiclepickup, vehicledrop, totalcost;
    String NotiID,noti_status;
    String amount = "1000";
    String token;
    String orderId ;
    int bit;
    ImageView vehicleImage;
    String vnp_url, vnp_tmnCode;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Vehicle> ls = new ArrayList<Vehicle>();
    private TextView tv_id,name,email,phoneNumber, tv_status;// Thông tin nhà cung cấp
    private TextView tv_BrandCar,tv_Gia,tv_DiaDiem,pickup,dropoff,totalCost;// Thông tin xe
    private Button btn_payment, btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_booking_detail);
        intent = getIntent();

        String OrderID = intent.getStringExtra("NotiID");
        NotiID = OrderID;

        init();

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);

        dtb = FirebaseFirestore.getInstance();
        dtb.collection("Notification")
                .whereEqualTo("noti_id", NotiID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Activity temp = new Activity();
                                temp.setNoti_id(document.getId());
                                temp.setProvider_id(document.getString("provider_id"));
                                temp.setVehicle_id(document.getString("vehicle_id"));
                                temp.setStatus(document.getString("status"));
                                temp.setPickup(document.getString("pickup"));
                                temp.setDropoff(document.getString("dropoff"));

                                vehiclepickup = temp.getPickup();
                                vehicledrop = temp.getDropoff();
                                ProvideID = temp.getProvider_id();
                                vehicle_id = temp.getVehicle_id();
                                noti_status = temp.getStatus();

                                if (document.contains("order_id")) {
                                    orderId = document.getString("order_id");
                                    Log.e("OrderID", "order_id: " + orderId);
                                }

                                tv_id.setText(NotiID);
                                updateStatusAndButton(noti_status);

                                getuser(ProvideID);
                                getvehicle(vehicle_id);
                            }
                        } else {
                            Toast.makeText(CustomerActivityDetail.this, "Không thể lấy thông báo", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void updateStatusAndButton(String status) {
                        switch (status) {
                            case "Dang cho":
                                tv_status.setText("Chưa được xác nhận");
                                btn_payment.setVisibility(View.GONE);
                                break;
                            case "Xac nhan":
                                tv_status.setText("Đã xác nhận");
                                btn_payment.setVisibility(View.VISIBLE);
                                break;
                            case "Thanh toan":
                                tv_status.setText("Đang chờ thanh toán");
                                btn_payment.setVisibility(View.VISIBLE);
                                break;
                            case "Khong duoc xac nhan":
                                tv_status.setText("Không được xác nhận");
                                btn_payment.setVisibility(View.GONE);
                                break;
                            default:
                                tv_status.setText("Đã thanh toán");
                                btn_payment.setVisibility(View.GONE);
                                break;
                        }
                    }

                });


        btn_payment.setOnClickListener(v -> {
            if (orderId != null) {
                createOrderAndPay(); // --- CHỈNH SỬA ---
            } else {
                Toast.makeText(this, "Không tìm thấy order_id!", Toast.LENGTH_SHORT).show();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void postpayment() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String orderId = intent.getStringExtra("order_id");

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("payment_status", "paid");
        updateData.put("status", "processing");

        db.collection("Orders")
                .document(orderId)
                .update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("Payment Update", "Cập nhật trạng thái thành công!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Payment Update", "Cập nhật thất bại: " + e.getMessage());
                    }
                });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createOrderAndPay() { // --- CHỈNH SỬA ---
        try {
            CreateOrder createOrder = new CreateOrder();
            JSONObject data = createOrder.createOrder(amount);

            if (data == null) {
                Toast.makeText(this, "Không thể tạo đơn hàng!", Toast.LENGTH_SHORT).show();
                return;
            }

            String code = data.getString("returncode");
            if ("1".equals(code)) {
                String token = data.getString("zptranstoken");
                checkout(token);
            } else {
                Toast.makeText(this, "Lỗi tạo đơn hàng!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("ZaloPay", "Exception: " + e.getMessage());
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void updateOrderAfterPayment(String orderId, String providerId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Update Order
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Đã thanh toán");
        updates.put("payment_method", "zalo_pay");

        db.collection("Orders").document(orderId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

                    // Tạo Notification cho chủ xe
                    createPaymentSuccessNotification(providerId);

                    // Chuyển màn
                    Intent intent = new Intent(this, CustomerMainActivity.class); // hoặc RequestSuccessActivity.class
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Có lỗi khi cập nhật đơn hàng.", Toast.LENGTH_SHORT).show();
                });
    }

    private void createPaymentSuccessNotification(String providerId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> notification = new HashMap<>();
        notification.put("user_id", providerId);
        notification.put("title", "Thanh toán thành công");
        notification.put("content", "Khách hàng đã thanh toán đơn thuê xe của bạn.");
        notification.put("timestamp", new Timestamp(new Date()));
        notification.put("status", "unread");

        db.collection("Notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    // Notification đã được tạo cho chủ xe
                })
                .addOnFailureListener(e -> {
                    // Có lỗi xảy ra
                });
    }

    private void checkout(String token) {
        ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(String transId, String zpTransToken, String appTransId) {
                runOnUiThread(() -> {
                    postpayment();
                    updateOrderAfterPayment(orderId, ProvideID);
                });
            }

            @Override
            public void onPaymentCanceled(String zpTransToken, String appTransId) {
                runOnUiThread(() -> Toast.makeText(CustomerActivityDetail.this, "Bạn đã hủy thanh toán.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransId) {
                runOnUiThread(() -> Toast.makeText(CustomerActivityDetail.this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void setstatus(){
        if(noti_status.equals( "Dang cho"))
        {
            tv_status.setText("Nhà cung cấp chưa xác nhận");
        }
        else
        {
            if(noti_status.equals("Thanh toan"))
            {
                tv_status.setText("Đang chờ thanh toán");
            }
            else
            if (noti_status.equals("Khong xac nhan")) {
                tv_status.setText("Nhà cung cấp không xác nhận");
            }
            else{
                tv_status.setText("Đã xác nhận thuê xe");
            }
        }
    }
    private void getuser(String ProvideID){
        dtb.collection("Users")
                .whereEqualTo("user_id", ProvideID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                User user = new User();
                                ownername = document.get("username").toString();
                                owneremail = document.get("email").toString();
                                ownerphone = document.get("phoneNumber").toString();
                                user.setUser_id(document.get("user_id").toString());
                                user.setUsername(ownername);
                                user.setEmail(owneremail);
                                user.setPhoneNumber(ownerphone);
                                name.setText(user.getUsername());
                                email.setText(user.getEmail());
                                phoneNumber.setText(user.getPhoneNumber());
                            }
                        } else {
                            Toast.makeText(CustomerActivityDetail.this, "Không thể lấy thông tin nhà cung cấp", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void getvehicle(String vehicle_id){
        dtb.collection("Vehicles")
                .whereEqualTo("vehicle_id", vehicle_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Vehicle temp = new Vehicle();
                                temp.setVehicle_id(document.getId());

                                vehiclename = document.get("vehicle_name").toString();
                                vehicleprice = document.get("vehicle_price").toString();
                                vehicleaddress = document.get("provider_address").toString();

                                temp.setVehicle_name(vehiclename);
                                temp.setVehicle_availability(document.get("vehicle_availability").toString());
                                temp.setVehicle_price(vehicleprice);
                                temp.setProvider_address(vehicleaddress);

                                tv_BrandCar.setText(temp.getVehicle_name());
                                tv_Gia.setText(temp.getVehicle_price() + "/ngày");
                                tv_DiaDiem.setText(temp.getProvider_address());

                                pickup.setText(vehiclepickup);
                                dropoff.setText(vehicledrop);
                                totalcost = calculate(vehiclepickup, vehicledrop);
                                totalCost.setText(totalcost);

                                amount = totalcost;

                                temp.setVehicle_imageURL(document.get("vehicle_imageURL").toString());
                                if (!document.get("vehicle_imageURL").toString().isEmpty()) {
                                    Picasso.get().load(temp.getVehicle_imageURL()).into(vehicleImage);
                                }
                                else {
                                    temp.setVehicle_imageURL("");
                                }
                            }
                        } else {
                            Toast.makeText(CustomerActivityDetail.this, "Không thể lấy thông tin xe", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private String calculate(String a, String b) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date startDate = sdf.parse(a);
            Date endDate = sdf.parse(b);

            long diffInMillies = endDate.getTime() - startDate.getTime();
            long daysBetween = diffInMillies / (1000 * 60 * 60 * 24);

            if (daysBetween <= 0) {
                Toast.makeText(this, "Ngày trả phải sau ngày nhận!", Toast.LENGTH_SHORT).show();
                return "0";
            }

            int pricePerDay = Integer.parseInt(tv_Gia.getText().toString().substring(0, tv_Gia.getText().toString().indexOf(" ")));
            long totalPrice = pricePerDay * daysBetween;

            Log.e("Total Days", String.valueOf(daysBetween));
            Log.e("Total Price", String.valueOf(totalPrice));

            return String.valueOf(totalPrice);

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Định dạng ngày không hợp lệ!", Toast.LENGTH_SHORT).show();
            return "0";
        }
    }
    public void init(){
        tv_id=findViewById(R.id.txtview_noti_id);
        tv_status=findViewById(R.id.txtview_noti_status);

        email=findViewById(R.id.txtview_noti_email);
        name=findViewById(R.id.txtview_noti_name);
        phoneNumber=findViewById(R.id.txtview_noti_phoneNumber);
        tv_BrandCar=findViewById(R.id.txtview_noti_BrandCar);
        tv_DiaDiem=findViewById(R.id.txt_checkout_address);

        btn_payment=findViewById(R.id.btn_customer_pay);
        btn_back=findViewById(R.id.btn_noti_back);

        tv_Gia=findViewById(R.id.txtview_noti_price);
        pickup=findViewById(R.id.tv_noti_pickup);
        dropoff=findViewById(R.id.tv_noti_dropoff);
        totalCost=findViewById(R.id.txtview_noti_totalCost);
        vehicleImage=findViewById(R.id.img_noti_car);

        //btn_payment.setVisibility(View.GONE);
        //btn_payment.setEnabled(false);
    }
    private int getday(String date){
        int day = 31;
        //
        return day;
    }
    private int getmonth(String date){
        int month = 12;
        //
        return month;
    }
    private int getyear(String date){
        int year = 2022;
        //
        return year;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}
