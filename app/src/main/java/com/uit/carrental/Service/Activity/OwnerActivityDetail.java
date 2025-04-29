package com.uit.carrental.Service.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.uit.carrental.ActivityPages.OwnerMainActivity;
import com.uit.carrental.Model.Activity;
import com.uit.carrental.Model.User;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OwnerActivityDetail extends AppCompatActivity {

    FirebaseFirestore dtb;
    Intent intent;
    String ProvideID, vehicle_id, vehiclename, vehicleprice, vehicleaddress, vehiclepickup, vehicledrop, totalcost;
    String CustomerID;
    String NotiID,noti_status;
    ImageView vehicleImage;
    private Activity temp = new Activity();


    private ArrayList<Vehicle> ls = new ArrayList<Vehicle>();
    private TextView tv_id,name,email,phoneNumber, tv_status;// Thông tin nhà cung cấp
    private TextView tv_BrandCar,tv_Gia,tv_DiaDiem,pickup,dropoff,totalCost;// Thông tin xe
    private Button btn_xacnhan,btn_huy,btn_back;

//    APIService apiService;
//
//    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_booking_detail);
        intent = getIntent();

        String OrderID = intent.getStringExtra("NotiID");
        NotiID = OrderID;

        init();

        dtb = FirebaseFirestore.getInstance();
        dtb.collection("Notification")
                .whereEqualTo("noti_id", NotiID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

//                            Notification temp = new Notification();
                                temp.setNoti_id(document.getId());
                                temp.setCustomer_id(document.get("customer_id").toString());
                                temp.setVehicle_id(document.get("vehicle_id").toString());
                                temp.setStatus(document.get("status").toString());

                                vehiclepickup = document.get("pickup").toString();
                                vehicledrop = document.get("dropoff").toString();

                                CustomerID = temp.getCustomer_id();
                                vehicle_id = temp.getVehicle_id();
                                noti_status=temp.getStatus();

                                tv_id.setText(NotiID);

                                if(noti_status.equals( "Dang cho"))
                                {
                                    tv_status.setText("Chưa được xác nhận");
                                }
                                else
                                {
                                    if(tv_status.equals( "Thanh toan"))
                                    {
                                        tv_status.setText("Đang chờ thanh toán");
                                    }
                                    else
                                    if(noti_status.equals("Xac nhan"))
                                    {
                                        tv_status.setText("Đã xác nhận");
                                    }
                                    else
                                    if (noti_status.equals("Khong duoc xac nhan"))
                                    {
                                        tv_status.setText("Không được xác nhận");
                                    }
                                    else {
                                        tv_status.setText("Đã thanh toán");
                                    }
                                }
                                getuser(CustomerID);
                                getvehicle(vehicle_id);

                            }
                        } else {
                            Toast.makeText(OwnerActivityDetail.this, "Không thể lấy thông báo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OwnerActivityDetail.this, OwnerMainActivity.class);
                startActivity(intent);
            }
        });
        btn_huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_noti_huy();
            }
        });

        btn_xacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                update_noti_xacnhan();
                createOrderFromNotification(NotiID);

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
    private void getuser(String ProvideID){
        dtb.collection("Users")
                .whereEqualTo("user_id", CustomerID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                User user = new User();
                                user.setUser_id(document.get("user_id").toString());
                                user.setUsername(document.get("username").toString());
                                user.setEmail(document.get("email").toString());
                                user.setPhoneNumber(document.get("phoneNumber").toString());
                                name.setText(user.getUsername());
                                email.setText(user.getEmail());
                                phoneNumber.setText(user.getPhoneNumber());
                            }
                        } else {
                            Toast.makeText(OwnerActivityDetail.this, "Không thể lấy thông tin nhà cung cấp", Toast.LENGTH_SHORT).show();
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

                                temp.setVehicle_name(document.get("vehicle_name").toString());
                                temp.setVehicle_availability(document.get("vehicle_availability").toString());
                                temp.setVehicle_price(document.get("vehicle_price").toString());


                                tv_BrandCar.setText(temp.getVehicle_name());
                                tv_Gia.setText(temp.getVehicle_price() + "/ngày");
                                tv_DiaDiem.setText(temp.getProvider_address());
                                temp.setProvider_address(document.get("provider_address").toString());

                                tv_DiaDiem.setText(vehicleaddress);
                                pickup.setText(vehiclepickup);
                                dropoff.setText(vehicledrop);
                                totalcost = calculate(vehiclepickup, vehicledrop);
                                totalCost.setText(totalcost);

                                temp.setVehicle_imageURL(document.get("vehicle_imageURL").toString());
                                if (!document.get("vehicle_imageURL").toString().isEmpty()) {
                                    Picasso.get().load(temp.getVehicle_imageURL()).into(vehicleImage);
                                }
                                else {
                                    temp.setVehicle_imageURL("");
                                }
                            }
                        } else {
                            Toast.makeText(OwnerActivityDetail.this, "Không thể lấy thông tin xe", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void update_noti_huy(){

        Map<String, Object> data = new HashMap<>();
        data.put("status", "Khong xac nhan");
        dtb.collection("Notification").document(temp.getNoti_id()).update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OwnerActivityDetail.this, "Đã hủy đơn hàng", Toast.LENGTH_LONG).show();
                        tv_status.setText("Không được xác nhận");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OwnerActivityDetail.this, "Lỗi hủy đơn hàng", Toast.LENGTH_LONG).show();
                    }
                });

    }
    private void update_noti_xacnhan(){

        Map<String, Object> data = new HashMap<>();
        data.put("status", "Xac nhan");
        dtb.collection("Notification").document(temp.getNoti_id()).update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OwnerActivityDetail.this, "Đã xác nhận", Toast.LENGTH_LONG).show();
                        tv_status.setText("Đã xác nhận");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OwnerActivityDetail.this, "Lỗi hủy đơn hàng", Toast.LENGTH_LONG).show();
                    }
                });

    }
    public void init(){
        tv_id=findViewById(R.id.txtview_noti_id);
        email=findViewById(R.id.txtview_noti_email);
        name=findViewById(R.id.txtview_noti_name);
        phoneNumber=findViewById(R.id.txtview_noti_phoneNumber);
        tv_BrandCar=findViewById(R.id.txtview_noti_BrandCar);
        tv_DiaDiem=findViewById(R.id.txt_checkout_address);

        tv_Gia=findViewById(R.id.txtview_noti_price);
        pickup=findViewById(R.id.tv_noti_pickup);
        dropoff=findViewById(R.id.tv_noti_dropoff);
        totalCost=findViewById(R.id.txtview_noti_totalCost);
        tv_status=findViewById(R.id.txtview_noti_status);

        btn_xacnhan=findViewById(R.id.btn_noti_XacNhan);
        btn_huy=findViewById(R.id.btn_noti_huy);
        btn_back=findViewById(R.id.btn_noti_back);
        vehicleImage=findViewById(R.id.img_noti_car);
    }

    // OwnerActivityDetail.java

    private void createOrderFromNotification(String notiId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notification").document(notiId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> notiData = documentSnapshot.getData();
                        if (notiData == null) return;

                        Map<String, Object> orderData = new HashMap<>();
                        orderData.put("customer_id", notiData.get("customer_id"));
                        orderData.put("provider_id", notiData.get("provider_id"));
                        orderData.put("vehicle_id", notiData.get("vehicle_id"));
                        orderData.put("pickup_time", notiData.get("pickup"));
                        orderData.put("dropoff_time", notiData.get("dropoff"));
                        orderData.put("status", "pending");
                        orderData.put("payment_status", "pending");

                        db.collection("Orders").add(orderData)
                                .addOnSuccessListener(orderRef -> {
                                    Log.d("CreateOrder", "Order created: " + orderRef.getId());

                                    // Update notification with order_id
                                    Map<String, Object> updateData = new HashMap<>();
                                    updateData.put("order_id", orderRef.getId());
                                    db.collection("Notification").document(notiId)
                                            .update(updateData)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("Notification Update", "order_id updated in notification!");
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("Notification Update", "Failed to update notification", e);
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("CreateOrder", "Failed to create order", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FetchNoti", "Failed to fetch notification", e);
                });
    }





}
