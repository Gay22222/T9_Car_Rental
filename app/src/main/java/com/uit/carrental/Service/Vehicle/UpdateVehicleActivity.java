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
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.uit.carrental.Service.Api.CloudinaryApi;
import java.util.HashMap;
import java.util.Map;

public class UpdateVehicleActivity extends AppCompatActivity {

    private static final String TAG = "UpdateVehicleActivity";
    private ImageView vehicleImage, documentImage;
    private ImageButton btnBack;
    private EditText editTextVehicleName, editTextVehiclePrice, editTextVehicleNumber, editTextVehicleSeats;
    private EditText editTextVehicleBrand, editTextFuelType, editTextMaxSpeed, editTextTransmission, editTextDoorsAndSeats;
    private Button btnUpdate;
    private String vehicleId;
    private FirebaseFirestore dbVehicle;
    private Vehicle vehicle = new Vehicle();
    private Uri vehicleImageUri, documentImageUri;
    private String vehicleImageUrl, documentImageUrl;
    private ProgressDialog progressDialog;

    ActivityResultLauncher<String> pickVehicleImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        vehicleImageUri = result;
                        vehicleImage.setImageURI(result);
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
                        documentImage.setImageURI(result);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_vehicle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent intent = getIntent();
        vehicleId = intent.getStringExtra("vehicleId");
        if (vehicleId == null || vehicleId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID xe", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        vehicle.setVehicleId(vehicleId);

        init();
        getDetail();

        btnBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(view -> update());
        vehicleImage.setOnClickListener(v -> pickVehicleImage.launch("image/*"));
        documentImage.setOnClickListener(v -> pickDocumentImage.launch("image/*"));
    }

    private void init() {
        btnBack = findViewById(R.id.btn_back);
        btnUpdate = findViewById(R.id.btn_updatevehicle);
        editTextVehicleName = findViewById(R.id.et_vehiclename);
        editTextVehicleNumber = findViewById(R.id.et_platenumber);
        editTextVehicleSeats = findViewById(R.id.et_vehicleseats);
        editTextVehiclePrice = findViewById(R.id.et_vehicleprice);
        editTextVehicleBrand = findViewById(R.id.et_brand);
        editTextFuelType = findViewById(R.id.et_fuel_type);
        editTextMaxSpeed = findViewById(R.id.et_max_speed);
        editTextTransmission = findViewById(R.id.et_transmission);
        editTextDoorsAndSeats = findViewById(R.id.et_doors_and_seats);
        vehicleImage = findViewById(R.id.vehicle_img_view);
        documentImage = findViewById(R.id.document_img_view);
        dbVehicle = FirebaseFirestore.getInstance();
        editTextVehicleName.setEnabled(false); // Tên xe không chỉnh sửa
        CloudinaryApi.init(this);
    }

    private void getDetail() {
        progressDialog = ProgressDialog.show(this, "Đang tải dữ liệu", "Vui lòng chờ...", true);

        dbVehicle.collection("Vehicles").document(vehicleId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressDialog.dismiss();
                    if (documentSnapshot.exists()) {
                        vehicle = documentSnapshot.toObject(Vehicle.class);
                        if (vehicle != null) {
                            vehicle.setVehicleId(vehicleId); // Đảm bảo vehicleId được set
                            // Set UI
                            editTextVehicleName.setText(vehicle.getVehicleName() != null ? vehicle.getVehicleName() : "");
                            editTextVehiclePrice.setText(vehicle.getVehiclePrice() != null ? vehicle.getVehiclePrice().replace(" VND", "") : "");
                            editTextVehicleSeats.setText(vehicle.getVehicleSeats() != null ? vehicle.getVehicleSeats() : "");
                            editTextVehicleNumber.setText(vehicle.getVehicleNumber() != null ? vehicle.getVehicleNumber() : "");
                            editTextVehicleBrand.setText(vehicle.getVehicleBrand() != null ? vehicle.getVehicleBrand() : "");
                            editTextFuelType.setText(vehicle.getFuelType() != null ? vehicle.getFuelType() : "");
                            editTextMaxSpeed.setText(vehicle.getMaxSpeed() != null ? vehicle.getMaxSpeed() : "");
                            editTextTransmission.setText(vehicle.getTransmission() != null ? vehicle.getTransmission() : "");
                            editTextDoorsAndSeats.setText(vehicle.getDoorsAndSeats() != null ? vehicle.getDoorsAndSeats() : "");
                            if (vehicle.getVehicleImageUrl() != null && !vehicle.getVehicleImageUrl().isEmpty()) {
                                Glide.with(this)
                                        .load(vehicle.getVehicleImageUrl())
                                        .placeholder(R.drawable.ic_car)
                                        .into(vehicleImage);
                            } else {
                                vehicleImage.setImageResource(R.drawable.ic_car);
                            }
                            if (vehicle.getDocumentImageUrl() != null && !vehicle.getDocumentImageUrl().isEmpty()) {
                                Glide.with(this)
                                        .load(vehicle.getDocumentImageUrl())
                                        .placeholder(R.drawable.ic_car)
                                        .into(documentImage);
                            } else {
                                documentImage.setImageResource(R.drawable.ic_car);
                            }
                            Log.d(TAG, "Tải chi tiết xe thành công: " + vehicle.toString());
                        } else {
                            Toast.makeText(this, "Không thể ánh xạ dữ liệu xe", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy xe", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Lỗi lấy chi tiết xe: " + e.getMessage());
                    Toast.makeText(this, "Lỗi lấy chi tiết xe: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void update() {
        if (!validateInputs()) {
            return;
        }

        Map<String, Object> data = new HashMap<>();
        boolean flag = false;
        String plateNumber = editTextVehicleNumber.getText().toString().trim();
        String seats = editTextVehicleSeats.getText().toString().trim();
        String price = editTextVehiclePrice.getText().toString().trim();
        String brand = editTextVehicleBrand.getText().toString().trim();
        String fuelType = editTextFuelType.getText().toString().trim();
        String maxSpeed = editTextMaxSpeed.getText().toString().trim();
        String transmission = editTextTransmission.getText().toString().trim();
        String doorsAndSeats = editTextDoorsAndSeats.getText().toString().trim();

        // Check for changes
        if (!plateNumber.equals(vehicle.getVehicleNumber())) {
            data.put("vehicleNumber", plateNumber);
            flag = true;
        }
        if (!seats.equals(vehicle.getVehicleSeats())) {
            data.put("vehicleSeats", seats);
            flag = true;
        }
        if (!price.equals(vehicle.getVehiclePrice() != null ? vehicle.getVehiclePrice().replace(" VND", "") : "")) {
            data.put("vehiclePrice", price + " VND");
            flag = true;
        }
        if (!brand.equals(vehicle.getVehicleBrand())) {
            data.put("vehicleBrand", brand);
            flag = true;
        }
        if (!fuelType.equals(vehicle.getFuelType())) {
            data.put("fuelType", fuelType);
            flag = true;
        }
        if (!maxSpeed.equals(vehicle.getMaxSpeed())) {
            data.put("maxSpeed", maxSpeed);
            flag = true;
        }
        if (!transmission.equals(vehicle.getTransmission())) {
            data.put("transmission", transmission);
            flag = true;
        }
        if (!doorsAndSeats.equals(vehicle.getDoorsAndSeats())) {
            data.put("doorsAndSeats", doorsAndSeats);
            flag = true;
        }

        if (vehicleImageUri != null || documentImageUri != null) {
            uploadImagesAndUpdate(data, flag);
        } else if (flag) {
            updateFirestore(data);
        } else {
            Toast.makeText(this, "Không có thay đổi để cập nhật", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImagesAndUpdate(Map<String, Object> data, boolean flag) {
        progressDialog = ProgressDialog.show(this, "Đang tải ảnh", "Vui lòng chờ...", true);

        if (vehicleImageUri != null) {
            CloudinaryApi.uploadImage(this, vehicleImageUri, new CloudinaryApi.UploadCallbackCustom() {
                @Override
                public void onSuccess(String url) {
                    vehicleImageUrl = url;
                    data.put("vehicleImageUrl", vehicleImageUrl);
                    if (documentImageUri != null) {
                        uploadDocumentImage(data, flag);
                    } else {
                        updateFirestore(data);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    if (progressDialog != null) progressDialog.dismiss();
                    Log.e(TAG, "Lỗi tải ảnh xe: " + e.getMessage());
                    toast("Lỗi tải ảnh xe: " + e.getMessage());
                }
            });
        } else if (documentImageUri != null) {
            uploadDocumentImage(data, flag);
        }
    }

    private void uploadDocumentImage(Map<String, Object> data, boolean flag) {
        CloudinaryApi.uploadImage(this, documentImageUri, new CloudinaryApi.UploadCallbackCustom() {
            @Override
            public void onSuccess(String url) {
                documentImageUrl = url;
                data.put("documentImageUrl", documentImageUrl);
                updateFirestore(data);
            }

            @Override
            public void onFailure(Exception e) {
                if (progressDialog != null) progressDialog.dismiss();
                Log.e(TAG, "Lỗi tải ảnh giấy tờ: " + e.getMessage());
                toast("Lỗi tải ảnh giấy tờ: " + e.getMessage());
            }
        });
    }

    private void updateFirestore(Map<String, Object> data) {
        data.put("verificationStatus", "pending");
        data.put("vehicleAvailability", "pending");

        dbVehicle.collection("Vehicles")
                .document(vehicleId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    createAdminNotification();
                    if (progressDialog != null) progressDialog.dismiss();
                    Toast.makeText(this, "Cập nhật thông tin thành công, đang chờ admin duyệt.", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    if (progressDialog != null) progressDialog.dismiss();
                    Log.e(TAG, "Không thể cập nhật thông tin: " + e.getMessage());
                    Toast.makeText(this, "Không thể cập nhật thông tin: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private boolean validateInputs() {
        boolean valid = true;
        String plateNumber = editTextVehicleNumber.getText().toString().trim();
        String seats = editTextVehicleSeats.getText().toString().trim();
        String price = editTextVehiclePrice.getText().toString().trim();
        String brand = editTextVehicleBrand.getText().toString().trim();
        String fuelType = editTextFuelType.getText().toString().trim();
        String maxSpeed = editTextMaxSpeed.getText().toString().trim();
        String transmission = editTextTransmission.getText().toString().trim();
        String doorsAndSeats = editTextDoorsAndSeats.getText().toString().trim();

        if (TextUtils.isEmpty(plateNumber)) {
            editTextVehicleNumber.setError("Biển số xe bắt buộc.");
            valid = false;
        } else if (!plateNumber.matches("^[0-9A-Z]{7,9}$")) {
            editTextVehicleNumber.setError("Biển số xe không hợp lệ (7-9 ký tự số hoặc chữ).");
            valid = false;
        }

        if (TextUtils.isEmpty(seats)) {
            editTextVehicleSeats.setError("Số chỗ bắt buộc.");
            valid = false;
        } else {
            try {
                int seatCount = Integer.parseInt(seats);
                if (seatCount <= 0) {
                    editTextVehicleSeats.setError("Số chỗ phải lớn hơn 0.");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                editTextVehicleSeats.setError("Số chỗ phải là số.");
                valid = false;
            }
        }

        if (TextUtils.isEmpty(price)) {
            editTextVehiclePrice.setError("Giá bắt buộc.");
            valid = false;
        } else {
            try {
                float priceValue = Float.parseFloat(price);
                if (priceValue <= 0) {
                    editTextVehiclePrice.setError("Giá phải lớn hơn 0.");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                editTextVehiclePrice.setError("Giá phải là số.");
                valid = false;
            }
        }

        if (TextUtils.isEmpty(brand)) {
            editTextVehicleBrand.setError("Thương hiệu bắt buộc.");
            valid = false;
        }

        if (TextUtils.isEmpty(fuelType)) {
            editTextFuelType.setError("Loại nhiên liệu bắt buộc.");
            valid = false;
        }

        if (TextUtils.isEmpty(maxSpeed)) {
            editTextMaxSpeed.setError("Tốc độ tối đa bắt buộc.");
            valid = false;
        } else {
            try {
                int speed = Integer.parseInt(maxSpeed);
                if (speed <= 0) {
                    editTextMaxSpeed.setError("Tốc độ phải lớn hơn 0.");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                editTextMaxSpeed.setError("Tốc độ phải là số.");
                valid = false;
            }
        }

        if (TextUtils.isEmpty(transmission)) {
            editTextTransmission.setError("Hộp số bắt buộc.");
            valid = false;
        }

        if (TextUtils.isEmpty(doorsAndSeats)) {
            editTextDoorsAndSeats.setError("Số cửa và ghế bắt buộc.");
            valid = false;
        }

        return valid;
    }

    private void createAdminNotification() {
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Xe cập nhật chờ duyệt");
        notification.put("content", "Xe " + vehicle.getVehicleName() + " đã được cập nhật, đang chờ duyệt.");
        notification.put("timestamp", Timestamp.now());
        notification.put("status", "unread");
        notification.put("type", "vehicle_verification");
        notification.put("vehicleId", vehicleId);
        notification.put("recipient", "admin");

        dbVehicle.collection("Notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    // Silent success
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi gửi thông báo cho admin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void toast(String txt) {
        Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
    }
}
