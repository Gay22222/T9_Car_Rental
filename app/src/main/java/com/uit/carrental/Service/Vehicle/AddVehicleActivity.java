package com.uit.carrental.Service.Vehicle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uit.carrental.ActivityPages.OwnerMainActivity;
import com.uit.carrental.Model.User;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.uit.carrental.Service.Api.CloudinaryApi;

import java.util.HashMap;
import java.util.Map;

public class AddVehicleActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private String downloadUrl;
    private Uri mImageURI;
    private EditText vehicle_name, vehicle_seats, vehicle_price, vehicle_owner, vehicle_number;
    private Button btnAdd;
    private ImageView vehicle_imgView;
    private FirebaseFirestore dtb_vehicle, dtb_user, dtb_update;
    private User user = new User();
    private Vehicle vehicle = new Vehicle();
    private ProgressDialog progressDialog;

    ActivityResultLauncher<String> pickImagesFromGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        mImageURI = result;
                        vehicle_imgView.setImageURI(result);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        init();

        btnAdd.setOnClickListener(view -> {
            if (FullFill()) {
                if (mImageURI != null) {
                    uploadImageAndAddVehicle();
                } else {
                    toast("Vui lòng chọn hình ảnh cho xe.");
                }
            } else {
                toast("Vui lòng nhập đủ thông tin.");
            }
        });

        vehicle_imgView.setOnClickListener(v -> pickImagesFromGallery.launch("image/*"));
    }

    private void init() {
        vehicle_name = findViewById(R.id.et_name);
        vehicle_seats = findViewById(R.id.et_seats);
        vehicle_price = findViewById(R.id.et_price);
        vehicle_owner = findViewById(R.id.et_owner);
        vehicle_number = findViewById(R.id.et_number);
        vehicle_imgView = findViewById(R.id.img_view);
        btnAdd = findViewById(R.id.btn_add);

        try {
            dtb_vehicle = FirebaseFirestore.getInstance();
            dtb_user = FirebaseFirestore.getInstance();
            dtb_update = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                user.setUser_id(firebaseUser.getUid());
            } else {
                toast("Người dùng chưa đăng nhập");
            }
        } catch (Exception e) {
            toast("Lỗi khởi tạo Firebase: " + e.getMessage());
            dtb_vehicle = null;
            dtb_user = null;
            dtb_update = null;
        }

        // Khởi tạo CloudinaryApi
        CloudinaryApi.init(this);
    }

    private void uploadImageAndAddVehicle() {
        if (mImageURI == null) {
            toast("Vui lòng chọn hình ảnh cho xe.");
            return;
        }

        progressDialog = ProgressDialog.show(this, "Uploading Image", "Please wait...", true);

        CloudinaryApi.uploadImage(this, mImageURI, new CloudinaryApi.UploadCallbackCustom() {
            @Override
            public void onSuccess(String url) {
                downloadUrl = url;
                if (progressDialog != null) progressDialog.dismiss();
                if (downloadUrl != null) {
                    addVehicle();
                } else {
                    toast("Lỗi: Không nhận được URL ảnh.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (progressDialog != null) progressDialog.dismiss();
                toast("Lỗi tải ảnh: " + e.getMessage());
            }
        });
    }

    private void addVehicle() {
        if (dtb_user == null || dtb_vehicle == null) {
            toast("Lỗi Firebase Firestore, không thể thêm xe.");
            return;
        }

        new Thread(() -> {
            dtb_user.collection("Users")
                    .whereEqualTo("user_id", user.getUser_id())
                    .get()
                    .addOnCompleteListener(task -> {
                        runOnUiThread(() -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    vehicle.setProvider_id(document.getString("user_id"));
                                    vehicle.setProvider_name(document.getString("username"));
                                    vehicle.setProvider_address(document.getString("address") + " " + document.getString("city"));
                                    vehicle.setProvider_gmail(document.getString("email"));
                                    vehicle.setProvider_phone(document.getString("phoneNumber"));

                                    vehicle.setVehicle_name(vehicle_name.getText().toString());
                                    vehicle.setVehicle_seats(vehicle_seats.getText().toString());
                                    vehicle.setVehicle_price(vehicle_price.getText().toString() + " VND");
                                    vehicle.setOwner_name(vehicle_owner.getText().toString());
                                    vehicle.setVehicle_number(vehicle_number.getText().toString());
                                    vehicle.setVehicle_availability("available");
                                    vehicle.setVehicle_imageURL(downloadUrl);

                                    dtb_vehicle.collection("Vehicles")
                                            .add(vehicle)
                                            .addOnSuccessListener(documentReference -> {
                                                vehicle.setVehicle_id(documentReference.getId());
                                                updateVehicleId(vehicle.getVehicle_id());
                                                startActivity(new Intent(AddVehicleActivity.this, OwnerMainActivity.class));
                                                finish();
                                                toast("Thêm xe thành công");
                                            })
                                            .addOnFailureListener(e -> toast("Thêm xe thất bại: " + e.getMessage()));
                                }
                            } else {
                                toast("Không thể lấy thông tin người dùng");
                            }
                        });
                    });
        }).start();
    }

    private void updateVehicleId(String vehicle_id) {
        if (dtb_update == null) {
            toast("Lỗi Firebase Firestore, không thể cập nhật ID xe.");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("vehicle_id", vehicle_id);

        dtb_update.collection("Vehicles").document(vehicle_id)
                .update(data)
                .addOnSuccessListener(aVoid -> toast("Cập nhật ID xe thành công"))
                .addOnFailureListener(e -> toast("Cập nhật ID xe thất bại: " + e.getMessage()));
    }

    private boolean FullFill() {
        return !vehicle_name.getText().toString().isEmpty()
                && !vehicle_owner.getText().toString().isEmpty()
                && !vehicle_number.getText().toString().isEmpty()
                && !vehicle_price.getText().toString().isEmpty()
                && !vehicle_seats.getText().toString().isEmpty();
    }

    private void toast(String txt) {
        Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
    }
}