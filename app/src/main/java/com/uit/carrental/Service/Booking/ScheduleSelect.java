package com.uit.carrental.Service.Booking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.uit.carrental.Model.Activity;
import com.uit.carrental.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class ScheduleSelect extends AppCompatActivity {
    Button btn_request, btn_back;
    TextView NgayNhan, NgayTra;
    TextView GioNhan, GioTra;
    private DatePickerDialog picker;
    private TimePickerDialog tpicker;
    Intent intent;
    String vehicle_id;
    FirebaseFirestore dtb_Vehicle, dtb_Noti,dtb_update;
    private Activity noti = new Activity();
    String current_user_id;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;

    //
//    APIService apiService;
//    boolean notify = false;
//    String provideID;
    //


    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_schedule_select);

        intent = getIntent();

        String Vehicle_ID = intent.getStringExtra("vehicle_id");
        vehicle_id = Vehicle_ID;

        storageReference = FirebaseStorage.getInstance().getReference();
        dtb_Vehicle = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        dtb_Noti = FirebaseFirestore.getInstance();
        dtb_update = FirebaseFirestore.getInstance();

        //
//        user= FirebaseAuth.getInstance().getCurrentUser();
//        apiService= Client.getClient("http:/fcm.googleapis.com/").create(APIService.class);
        //

        initComponents();

        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);


        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                notify=true;
                setNotiFirebase();
                Intent Writeinfor=new Intent(ScheduleSelect.this, RequestSuccessActivity.class);
                startActivity(Writeinfor);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStop();
            }
        });
    }
    private void setNotiFirebase(){
        dtb_Vehicle.collection("Vehicles")
                .whereEqualTo("vehicle_id", vehicle_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                noti.setVehicle_id(document.get("vehicle_id").toString());
                                noti.setProvider_id(document.get("provider_id").toString());
                                noti.setPickup(NgayNhan.getText().toString() + " " + GioNhan.getText().toString());
                                noti.setDropoff(NgayTra.getText().toString() + " " + GioTra.getText().toString());
                                noti.setStatus("Dang cho");

                                noti.setCustomer_id(current_user_id);
//                                provideID=noti.getProvider_id();

                                dtb_Noti.collection("Notification")
                                        .add(noti)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                noti.setNoti_id(documentReference.getId());
                                                Log.e("NotiID", noti.getNoti_id());
                                                updateData(noti.getNoti_id());
                                                Intent intent = new Intent(ScheduleSelect.this, RequestSuccessActivity.class);
                                                startActivity(intent);
/*
                                                toast("Thêm noti thành công");
*/
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                toast("Thêm noti thất bại");
                                            }
                                        });

                            }
                        } else {
                            Toast.makeText(ScheduleSelect.this, "Không thể lấy thông báo", Toast.LENGTH_SHORT).show();
                        }


                    }
                    private void updateData(String NotiID) {
                        Log.e("NotificationID", NotiID);
                        Map<String, Object> data = new HashMap<>();
                        data.put("noti_id", NotiID);

                        dtb_update.collection("Notification")
                                .document(NotiID)
                                .update(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
/*
                                        Toast.makeText(ScheduleSelect.this, "DocumentSnapshot successfully updated!", Toast.LENGTH_LONG).show();
*/
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ScheduleSelect.this, "Error updating document", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
    }
    private void toast(String txt){
        Toast toast = Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_LONG);
        toast.show();
    }
    private void setonclick(){
        NgayNhan.setFocusable(false);
        NgayNhan.setClickable(true);
        NgayTra.setFocusable(false);
        NgayTra.setClickable(true);
        GioNhan.setFocusable(false);
        GioNhan.setClickable(true);
        GioTra.setFocusable(false);
        GioTra.setClickable(true);

        final Calendar calendar = Calendar.getInstance();

        NgayNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                picker = new DatePickerDialog(ScheduleSelect.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year1, int month1, int dayOfMonth) {
                        String formattedDate = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        NgayNhan.setText(formattedDate);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        NgayTra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                picker = new DatePickerDialog(ScheduleSelect.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year1, int month1, int dayOfMonth) {
                        String formattedDate = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        NgayTra.setText(formattedDate);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        GioNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                tpicker = new TimePickerDialog(ScheduleSelect.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                        GioNhan.setText(formattedTime);
                    }
                }, hour, minute, true);
                tpicker.show();
            }
        });

        GioTra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                tpicker = new TimePickerDialog(ScheduleSelect.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                        GioTra.setText(formattedTime);
                    }
                }, hour, minute, true);
                tpicker.show();
            }
        });
    }

    private void initComponents(){
        btn_request = findViewById(R.id.btn_requestbooking);
        btn_back = findViewById(R.id.btn_noti_back);
        NgayNhan = findViewById(R.id.edt_NgayNhan);
        NgayTra = findViewById(R.id.edt_NgayTra);
        GioNhan= findViewById(R.id.edt_GioNhan);
        GioTra= findViewById(R.id.edt_GioTra);
        setonclick();
    }

}
