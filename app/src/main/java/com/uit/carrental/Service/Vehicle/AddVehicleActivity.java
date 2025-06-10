package com.uit.carrental.Service.Vehicle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.uit.carrental.Service.Api.CloudinaryApi;
import java.util.HashMap;
import java.util.Map;

public class AddVehicleActivity extends AppCompatActivity {

    private static final String TAG = "AddVehicleActivity";
    private FirebaseUser firebaseUser;
    private String vehicleImageUrl, documentImageUrl;
    private Uri vehicleImageUri, documentImageUri;
    private EditText vehicleName, vehicleSeats, vehiclePrice, vehicleNumber, vehicleBrand, fuelType, maxSpeed, transmission, doorsAndSeats;
    private Button btnAdd;
    private ImageButton btnBack;
    private ImageView vehicleImgView, documentImgView;
    private FirebaseFirestore db;
    private Vehicle vehicle;
    private ProgressDialog progressDialog;

    ActivityResultLauncher<String> pickVehicleImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        vehicleImageUri = result;
                        vehicleImgView.setImageURI(result);
                    }
                }
            });

    ActivityResultLauncher<String> pickDocumentImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        documentImageUri = result;
                        documentImgView.setImageURI(result);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        init();

        btnBack.setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(view -> {
            if (fullFill()) {
                if (vehicleImageUri != null && documentImageUri != null) {
                    uploadImagesAndAddVehicle();
                } else {
                    toast("Vui lòng chọn cả ảnh xe và giấy tờ xe.");
                }
            } else {
                toast("Vui lòng nhập đầy đủ thông tin.");
            }
        });

        vehicleImgView.setOnClickListener(v -> pickVehicleImage.launch("image/*"));
        documentImgView.setOnClickListener(v -> pickDocumentImage.launch("image/*"));
    }

    private void init() {
        vehicleName = findViewById(R.id.et_name);
        vehicleSeats = findViewById(R.id.et_seats);
        vehiclePrice = findViewById(R.id.et_price);
        vehicleNumber = findViewById(R.id.et_number);
        vehicleBrand = findViewById(R.id.et_brand);
        fuelType = findViewById(R.id.et_fuel_type);
        maxSpeed = findViewById(R.id.et_max_speed);
        transmission = findViewById(R.id.et_transmission);
        doorsAndSeats = findViewById(R.id.et_doors_and_seats);
        vehicleImgView = findViewById(R.id.vehicle_img_view);
        documentImgView = findViewById(R.id.document_img_view);
        btnAdd = findViewById(R.id.btn_add);
        btnBack = findViewById(R.id.btn_back);

        try {
            db = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser == null) {
                toast("Người dùng chưa đăng nhập");
                finish();
            }
            vehicle = new Vehicle();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khởi tạo Firebase: " + e.getMessage());
            toast("Lỗi khởi tạo Firebase: " + e.getMessage());
            finish();
        }

        CloudinaryApi.init(this);
    }

    private void uploadImagesAndAddVehicle() {
        if (vehicleImageUri == null || documentImageUri == null) {
            toast("Vui lòng chọn cả ảnh xe và giấy tờ xe.");
            return;
        }

        progressDialog = ProgressDialog.show(this, "Đang tải ảnh", "Vui lòng chờ...", true);

        // Tải ảnh xe
        CloudinaryApi.uploadImage(this, vehicleImageUri, new CloudinaryApi.UploadCallbackCustom() {
            @Override
            public void onSuccess(String url) {
                vehicleImageUrl = url;
                uploadDocumentImage();
            }

            @Override
            public void onFailure(Exception e) {
                if (progressDialog != null) progressDialog.dismiss();
                Log.e(TAG, "Lỗi tải ảnh xe: " + e.getMessage());
                toast("Lỗi tải ảnh xe: " + e.getMessage());
            }
        });
    }

    private void uploadDocumentImage() {
        CloudinaryApi.uploadImage(this, documentImageUri, new CloudinaryApi.UploadCallbackCustom() {
            @Override
            public void onSuccess(String url) {
                documentImageUrl = url;
                if (progressDialog != null) progressDialog.dismiss();
                if (vehicleImageUrl != null && documentImageUrl != null) {
                    addVehicle();
                } else {
                    toast("Lỗi: Không nhận được URL ảnh.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (progressDialog != null) progressDialog.dismiss();
                Log.e(TAG, "Lỗi tải ảnh giấy tờ: " + e.getMessage());
                toast("Lỗi tải ảnh giấy tờ: " + e.getMessage());
            }
        });
    }

    private void addVehicle() {
        if (db == null) {
            toast("Lỗi Firebase Firestore, không thể thêm xe.");
            return;
        }

        vehicle.setVehicleName(vehicleName.getText().toString().trim());
        vehicle.setVehicleSeats(vehicleSeats.getText().toString().trim());
        vehicle.setVehiclePrice(vehiclePrice.getText().toString().trim() + " VND");
        vehicle.setVehicleNumber(vehicleNumber.getText().toString().trim());
        vehicle.setVehicleBrand(vehicleBrand.getText().toString().trim());
        vehicle.setFuelType(fuelType.getText().toString().trim());
        vehicle.setMaxSpeed(maxSpeed.getText().toString().trim());
        vehicle.setTransmission(transmission.getText().toString().trim());
        vehicle.setDoorsAndSeats(doorsAndSeats.getText().toString().trim());
        vehicle.setVehicleAvailability("pending");
        vehicle.setVehicleImageUrl(vehicleImageUrl);
        vehicle.setDocumentImageUrl(documentImageUrl);
        vehicle.setVerificationStatus("pending");
        vehicle.setCreatedAt(Timestamp.now());
        if (firebaseUser != null) {
            vehicle.setOwnerId(firebaseUser.getUid());
        } else {
            toast("Không thể thêm xe: Người dùng chưa đăng nhập.");
            return;
        }

        progressDialog = ProgressDialog.show(this, "Đang thêm xe", "Vui lòng chờ...", true);

        db.collection("Vehicles")
                .add(vehicle)
                .addOnSuccessListener(documentReference -> {
                    String vehicleId = documentReference.getId();
                    vehicle.setVehicleId(vehicleId);
                    updateVehicleId(vehicleId);
                    createAdminNotification(vehicleId);
                    if (progressDialog != null) progressDialog.dismiss();
                    toast("Xe đã được thêm thành công, đang chờ duyệt.");
                    setResult(RESULT_OK); // Gửi tín hiệu thành công
                    finish(); // Kết thúc activity
                })
                .addOnFailureListener(e -> {
                    if (progressDialog != null) progressDialog.dismiss();
                    Log.e(TAG, "Thêm xe thất bại: " + e.getMessage());
                    toast("Thêm xe thất bại: " + e.getMessage());
                });
    }

    private void updateVehicleId(String vehicleId) {
        Map<String, Object> data = new HashMap<>();
        data.put("vehicleId", vehicleId);

        db.collection("Vehicles").document(vehicleId)
                .update(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Cập nhật ID xe thành công"))
                .addOnFailureListener(e -> Log.e(TAG, "Cập nhật ID xe thất bại: " + e.getMessage()));
    }

    private void createAdminNotification(String vehicleId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Xe mới chờ duyệt");
        notification.put("content", "Một xe mới (" + vehicle.getVehicleName() + ") đã được thêm, đang chờ duyệt.");
        notification.put("timestamp", Timestamp.now());
        notification.put("status", "unread");
        notification.put("type", "vehicle_verification");
        notification.put("vehicleId", vehicleId);
        notification.put("recipient", "admin");

        db.collection("Notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Tạo thông báo cho admin thành công"))
                .addOnFailureListener(e -> Log.e(TAG, "Tạo thông báo cho admin thất bại: " + e.getMessage()));
    }

    private boolean fullFill() {
        boolean valid = true;
        if (TextUtils.isEmpty(vehicleName.getText().toString().trim())) {
            vehicleName.setError("Tên xe bắt buộc.");
            valid = false;
        }
        if (TextUtils.isEmpty(vehicleSeats.getText().toString().trim())) {
            vehicleSeats.setError("Số chỗ bắt buộc.");
            valid = false;
        } else {
            try {
                int seats = Integer.parseInt(vehicleSeats.getText().toString().trim());
                if (seats <= 0) {
                    vehicleSeats.setError("Số chỗ phải lớn hơn 0.");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                vehicleSeats.setError("Số chỗ phải là số.");
                valid = false;
            }
        }
        if (TextUtils.isEmpty(vehiclePrice.getText().toString().trim())) {
            vehiclePrice.setError("Giá bắt buộc.");
            valid = false;
        } else {
            try {
                float price = Float.parseFloat(vehiclePrice.getText().toString().trim());
                if (price <= 0) {
                    vehiclePrice.setError("Giá phải lớn hơn 0.");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                vehiclePrice.setError("Giá phải là số.");
                valid = false;
            }
        }
        if (TextUtils.isEmpty(vehicleNumber.getText().toString().trim())) {
            vehicleNumber.setError("Biển số xe bắt buộc.");
            valid = false;
        } else if (!vehicleNumber.getText().toString().trim().matches("^[0-9A-Z]{7,9}$")) {
            vehicleNumber.setError("Biển số xe không hợp lệ (7-9 ký tự số hoặc chữ).");
            valid = false;
        }
        if (TextUtils.isEmpty(vehicleBrand.getText().toString().trim())) {
            vehicleBrand.setError("Thương hiệu bắt buộc.");
            valid = false;
        }
        if (TextUtils.isEmpty(fuelType.getText().toString().trim())) {
            fuelType.setError("Loại nhiên liệu bắt buộc.");
            valid = false;
        }
        if (TextUtils.isEmpty(maxSpeed.getText().toString().trim())) {
            maxSpeed.setError("Tốc độ tối đa bắt buộc.");
            valid = false;
        } else {
            try {
                int speed = Integer.parseInt(maxSpeed.getText().toString().trim());
                if (speed <= 0) {
                    maxSpeed.setError("Tốc độ phải lớn hơn 0.");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                maxSpeed.setError("Tốc độ phải là số.");
                valid = false;
            }
        }
        if (TextUtils.isEmpty(transmission.getText().toString().trim())) {
            transmission.setError("Hộp số bắt buộc.");
            valid = false;
        }
        if (TextUtils.isEmpty(doorsAndSeats.getText().toString().trim())) {
            doorsAndSeats.setError("Số cửa và ghế bắt buộc.");
            valid = false;
        }
        return valid;
    }

    private void toast(String txt) {
        Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
    }
}