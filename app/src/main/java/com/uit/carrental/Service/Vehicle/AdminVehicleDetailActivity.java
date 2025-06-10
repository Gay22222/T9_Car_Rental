package com.uit.carrental.Service.Vehicle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.carrental.FragmentPages.Admin.RequestSupplementDialogFragment;
import com.uit.carrental.Model.User;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminVehicleDetailActivity extends AppCompatActivity {

    private static final String TAG = "AdminVehicleDetail";
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private String vehicleId;
    private Vehicle vehicle;
    private TextView vehicleName, vehicleNumber, registrationDate, ownerName, verificationStatus;
    private TextView approveButtonText, rejectButtonText;
    private ImageView vehicleImage, documentImage, backButton; // Thêm backButton
    private LinearLayout approveButton, rejectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_vehicle_detail);

        initViews();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        vehicleId = intent.getStringExtra("vehicleId");
        if (vehicleId == null) {
            Toast.makeText(this, R.string.vehicle_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadVehicleData();
    }

    private void initViews() {
        vehicleName = findViewById(R.id.ry1qg0h2dho);
        vehicleNumber = findViewById(R.id.ru72c47csbt);
        registrationDate = findViewById(R.id.r898i1ih4yvt);
        ownerName = findViewById(R.id.rucikt3pzv6);
        verificationStatus = findViewById(R.id.rac1kcrnb8f);
        vehicleImage = findViewById(R.id.rag6uycqqw4a);
        documentImage = findViewById(R.id.r9mpttg5k7eo);
        backButton = findViewById(R.id.rx7d0apsish); // Khởi tạo ImageView back
        approveButton = findViewById(R.id.r55jpwn8v3im);
        rejectButton = findViewById(R.id.rqczrg8gtglj);
        approveButtonText = findViewById(R.id.rnm7adnyh58b);
        rejectButtonText = findViewById(R.id.ruxnan76tqd);

        // Thêm sự kiện nhấn cho nút back
        backButton.setOnClickListener(v -> {
            Log.d(TAG, "Nút back được nhấn");
            finish(); // Quay lại giao diện trước
        });
    }

    private void loadVehicleData() {
        progressDialog = ProgressDialog.show(this, "Đang tải dữ liệu", "Vui lòng chờ...", true);

        DocumentReference vehicleRef = db.collection("Vehicles").document(vehicleId);
        vehicleRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                vehicle = documentSnapshot.toObject(Vehicle.class);
                if (vehicle != null) {
                    displayVehicleData();
                    setupButtons();
                    loadOwnerData();
                } else {
                    Toast.makeText(this, "Không thể ánh xạ dữ liệu xe", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, R.string.vehicle_not_found, Toast.LENGTH_SHORT).show();
                finish();
            }
            if (progressDialog != null) progressDialog.dismiss();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi tải dữ liệu xe: " + e.getMessage());
            Toast.makeText(this, R.string.load_vehicle_error, Toast.LENGTH_SHORT).show();
            if (progressDialog != null) progressDialog.dismiss();
            finish();
        });
    }

    private void displayVehicleData() {
        vehicleName.setText(vehicle.getVehicleName() != null ? vehicle.getVehicleName() : "Xe không tên");
        vehicleNumber.setText(vehicle.getVehicleNumber() != null ? vehicle.getVehicleNumber() : "Không có biển số");
        verificationStatus.setText(getVerificationStatusText(vehicle.getVerificationStatus()));
        if (vehicle.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            registrationDate.setText(sdf.format(vehicle.getCreatedAt().toDate()));
        } else {
            registrationDate.setText("Không có ngày đăng");
        }

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
                    .placeholder(R.drawable.ic_car).into(documentImage);
        } else {
            documentImage.setImageResource(R.drawable.ic_car);
        }
    }

    private void setupButtons() {
        if (vehicle.getVerificationStatus() != null && vehicle.getVerificationStatus().equals("verified")) {
            approveButtonText.setText("Ẩn xe");
            rejectButtonText.setText("Cảnh báo vi phạm");
            approveButton.setOnClickListener(v -> hideVehicle());
            rejectButton.setOnClickListener(v -> showViolationDialog());
        } else {
            approveButtonText.setText(R.string.confirm);
            rejectButtonText.setText(R.string.reject);
            approveButton.setOnClickListener(v -> approveVehicle());
            rejectButton.setOnClickListener(v -> showSupplementDialog());
        }
    }

    private void loadOwnerData() {
        if (vehicle.getOwnerId() == null) {
            ownerName.setText("Không có chủ xe");
            return;
        }

        DocumentReference ownerRef = db.collection("Users").document(vehicle.getOwnerId());
        ownerRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User owner = documentSnapshot.toObject(User.class);
                ownerName.setText(owner != null && owner.getUsername() != null ? owner.getUsername() : "Không có tên");
            } else {
                ownerName.setText("Không tìm thấy chủ xe");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi tải dữ liệu chủ xe: " + e.getMessage());
            ownerName.setText("Lỗi tải chủ xe");
        });
    }

    private void approveVehicle() {
        progressDialog = ProgressDialog.show(this, "Đang duyệt xe", "Vui lòng chờ...", true);

        Map<String, Object> updates = new HashMap<>();
        updates.put("verificationStatus", "verified");
        updates.put("vehicleAvailability", "available");

        DocumentReference vehicleRef = db.collection("Vehicles").document(vehicleId);
        vehicleRef.update(updates).addOnSuccessListener(aVoid -> {
            sendNotification(vehicle.getOwnerId(), "Xe đã được duyệt",
                    "Xe " + vehicle.getVehicleName() + " của bạn đã được duyệt và sẵn sàng cho thuê.",
                    "vehicle_verification", vehicleId);
            Toast.makeText(this, "Xe đã được duyệt", Toast.LENGTH_SHORT).show();
            if (progressDialog != null) progressDialog.dismiss();
            finish();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi duyệt xe: " + e.getMessage());
            Toast.makeText(this, "Lỗi duyệt xe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            if (progressDialog != null) progressDialog.dismiss();
        });
    }

    private void hideVehicle() {
        progressDialog = ProgressDialog.show(this, "Đang ẩn xe", "Vui lòng chờ...", true);

        Map<String, Object> updates = new HashMap<>();
        updates.put("vehicleAvailability", "hidden");

        DocumentReference vehicleRef = db.collection("Vehicles").document(vehicleId);
        vehicleRef.update(updates).addOnSuccessListener(aVoid -> {
            sendNotification(vehicle.getOwnerId(), "Xe đã bị ẩn",
                    "Xe " + vehicle.getVehicleName() + " của bạn đã bị ẩn khỏi nền tảng.",
                    "vehicle_verification", vehicleId);
            Toast.makeText(this, "Xe đã bị ẩn", Toast.LENGTH_SHORT).show();
            if (progressDialog != null) progressDialog.dismiss();
            finish();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi ẩn xe: " + e.getMessage());
            Toast.makeText(this, "Lỗi ẩn xe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            if (progressDialog != null) progressDialog.dismiss();
        });
    }

    private void showSupplementDialog() {
        RequestSupplementDialogFragment dialog = RequestSupplementDialogFragment.newInstance(vehicle.getOwnerId(), vehicleId, "supplement_request_vehicle");
        dialog.setOnSubmitListener(message -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("verificationStatus", "rejected");
            db.collection("Vehicles").document(vehicleId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Map<String, Object> notification = new HashMap<>();
                        notification.put("userId", vehicle.getOwnerId());
                        notification.put("title", "Yêu cầu bổ sung thông tin xe");
                        notification.put("message", "Xe " + vehicle.getVehicleName() + " cần bổ sung: " + message);
                        notification.put("type", "supplement_request_vehicle");
                        notification.put("vehicleId", vehicleId);
                        notification.put("createdAt", Timestamp.now());
                        notification.put("isRead", false);
                        notification.put("role", "owner");

                        db.collection("Notifications")
                                .add(notification)
                                .addOnSuccessListener(doc -> db.collection("Notifications").document(doc.getId()).update("notificationId", doc.getId()));
                        Toast.makeText(this, "Đã gửi yêu cầu bổ sung", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Lỗi cập nhật trạng thái xe: ", e);
                        Toast.makeText(this, "Lỗi gửi yêu cầu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        dialog.show(getSupportFragmentManager(), "RequestSupplementDialog");
    }

    private void showViolationDialog() {
        RequestSupplementDialogFragment dialog = RequestSupplementDialogFragment.newInstance(vehicle.getOwnerId(), vehicleId, "violation_warning");
        dialog.setOnSubmitListener(message -> {
            sendNotification(vehicle.getOwnerId(), "Cảnh báo vi phạm",
                    "Xe " + vehicle.getVehicleName() + " vi phạm: " + message,
                    "vehicle_verification", vehicleId);
            Toast.makeText(this, "Đã gửi cảnh báo vi phạm", Toast.LENGTH_SHORT).show();
            finish();
        });
        dialog.show(getSupportFragmentManager(), "RequestSupplementDialog");
    }

    private void sendNotification(String userId, String title, String message, String type, String vehicleId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", userId);
        notification.put("title", title);
        notification.put("message", message);
        notification.put("type", type);
        notification.put("vehicleId", vehicleId != null ? vehicleId : "");
        notification.put("createdAt", Timestamp.now());
        notification.put("isRead", false);

        db.collection("Notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    String notificationId = documentReference.getId();
                    db.collection("Notifications").document(notificationId)
                            .update("notificationId", notificationId);
                    Log.d(TAG, "Gửi thông báo thành công");
                })
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi gửi thông báo: " + e.getMessage()));
    }

    private String getVerificationStatusText(String verificationStatus) {
        if (verificationStatus == null) return "Không xác định";
        switch (verificationStatus) {
            case "verified": return getString(R.string.confirm);
            case "pending": return "Chưa duyệt";
            case "rejected": return getString(R.string.reject);
            default: return "Không xác định";
        }
    }
}