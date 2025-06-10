package com.uit.carrental.Service.Vehicle;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.carrental.ActivityPages.CustomerMainActivity;
import com.uit.carrental.Model.User;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.uit.carrental.Service.Booking.ScheduleSelect;
import com.uit.carrental.Service.UserAuthentication.CCCDActivity;

public class VehicleDetailActivity extends AppCompatActivity {

    private static final String TAG = "VehicleDetailActivity";
    private ImageView imageViewVehicle, backButton;
    private TextView textViewName, textViewPrice, textViewFuel, textViewSpeed, textViewTransmission, textViewSeats, textViewNumber;
    private TextView textViewProviderName, textViewProviderPhone, textViewProviderEmail, textViewProviderAddress, textViewOwner;
    private Button btnBook;
    private String vehicleId;
    private VehicleDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_car);

        vehicleId = getIntent().getStringExtra("vehicleId");
        if (vehicleId == null) {
            Toast.makeText(this, "Không tìm thấy xe", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initViewModel();
        observeLiveData();

        btnBook.setOnClickListener(v -> checkCccdAndBook());
    }

    private void initViews() {
        btnBook = findViewById(R.id.btn_book);
        imageViewVehicle = findViewById(R.id.vehicle_img);
        backButton = findViewById(R.id.back_button);
        textViewName = findViewById(R.id.vehicle_name);
        textViewPrice = findViewById(R.id.tv_vehicle_price);
        textViewFuel = findViewById(R.id.fuel_value);
        textViewSpeed = findViewById(R.id.speed_value);
        textViewTransmission = findViewById(R.id.transmission_value);
        textViewSeats = findViewById(R.id.seats_value);
        textViewNumber = findViewById(R.id.tv_vehicle_number);
        textViewProviderName = findViewById(R.id.tv_provider_name);
        textViewProviderPhone = findViewById(R.id.tv_provider_phone);
        textViewProviderEmail = findViewById(R.id.tv_provider_gmail);
        textViewProviderAddress = findViewById(R.id.tv_provider_address);
        textViewOwner = findViewById(R.id.tv_vehicle_owner);

        backButton.setOnClickListener(v -> {
            Log.d(TAG, "Nút Back giao diện được nhấn");
            navigateBackToHome();
        });
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(VehicleDetailViewModel.class);
        viewModel.loadVehicleData(vehicleId);
    }

    private void observeLiveData() {
        viewModel.getVehicleLiveData().observe(this, vehicle -> {
            if (vehicle != null) {
                textViewName.setText(vehicle.getVehicleName() != null ? vehicle.getVehicleName() : "Mercedes C300 AMG");
                textViewPrice.setText(vehicle.getVehiclePrice() != null ? vehicle.getVehiclePrice() : "999.000VNĐ/Ngày");
                textViewFuel.setText(vehicle.getFuelType() != null ? vehicle.getFuelType() : "Xăng không chì 95+");
                textViewSpeed.setText(vehicle.getMaxSpeed() != null ? vehicle.getMaxSpeed() : "250 km/h");
                textViewTransmission.setText(vehicle.getTransmission() != null ? vehicle.getTransmission() : "Tự động 9 cấp");
                textViewSeats.setText(vehicle.getDoorsAndSeats() != null ? vehicle.getDoorsAndSeats() : "2 Cửa và 4 Ghế");
                textViewNumber.setText(vehicle.getVehicleNumber() != null ? vehicle.getVehicleNumber() : "60C2-88888");

                if (vehicle.getVehicleImageUrl() != null && !vehicle.getVehicleImageUrl().isEmpty()) {
                    Glide.with(this)
                            .load(vehicle.getVehicleImageUrl())
                            .placeholder(R.drawable.ic_car)
                            .into(imageViewVehicle);
                }

                if (vehicle.getOwnerId() != null) {
                    viewModel.loadOwnerData(vehicle.getOwnerId());
                }
            }
        });

        viewModel.getOwnerLiveData().observe(this, owner -> {
            if (owner != null) {
                textViewProviderName.setText(owner.getUsername() != null ? owner.getUsername() : "Công ty ABC");
                textViewProviderPhone.setText(owner.getPhoneNumber() != null ? owner.getPhoneNumber() : "0123456789");
                textViewProviderEmail.setText(owner.getEmail() != null ? owner.getEmail() : "abc@example.com");
                textViewProviderAddress.setText(owner.getAddress() != null ? owner.getAddress() : "123 Đường ABC, Đồng Nai");
                textViewOwner.setText(owner.getUsername() != null ? owner.getUsername() : "Nguyễn Văn A");
            }
        });

        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        viewModel.getUserLiveData().observe(this, user -> {
            // Dữ liệu người dùng đã tải, sẵn sàng để kiểm tra CCCD
        });
    }

    private void checkCccdAndBook() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt xe", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = viewModel.getUserLiveData().getValue();
        if (user == null ||
                user.getCiCardFront() == null || user.getCiCardFront().isEmpty() ||
                user.getCiCardBehind() == null || user.getCiCardBehind().isEmpty() ||
                user.getLicenseUrl() == null || user.getLicenseUrl().isEmpty() ||
                !"verified".equals(user.getVerificationStatus())) {
            showVerificationRequiredDialog();
        } else {
            Intent intent = new Intent(VehicleDetailActivity.this, ScheduleSelect.class);
            intent.putExtra("vehicleId", vehicleId);
            startActivity(intent);
        }
    }

    private void showVerificationRequiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu xác minh")
                .setMessage("Bạn cần cập nhật căn cước công dân và bằng lái xe, đồng thời được admin duyệt để thuê xe. Bạn có muốn cập nhật ngay không?")
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    Intent intent = new Intent(VehicleDetailActivity.this, CCCDActivity.class);
                    intent.putExtra("fromVehicleDetail", true); // Thêm extra để theo dõi
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void navigateBackToHome() {
        Intent intent = new Intent(this, CustomerMainActivity.class);
        intent.putExtra("fragment", "home");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Nút Back vật lý được nhấn");
        navigateBackToHome();
    }

    public static class VehicleDetailViewModel extends ViewModel {
        private final FirebaseFirestore db;
        private final FirebaseUser firebaseUser;
        private final MutableLiveData<Vehicle> vehicleLiveData;
        private final MutableLiveData<User> userLiveData;
        private final MutableLiveData<User> ownerLiveData;
        private final MutableLiveData<String> errorLiveData;

        public VehicleDetailViewModel() {
            db = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            vehicleLiveData = new MutableLiveData<>();
            userLiveData = new MutableLiveData<>();
            ownerLiveData = new MutableLiveData<>();
            errorLiveData = new MutableLiveData<>();
        }

        public LiveData<Vehicle> getVehicleLiveData() {
            return vehicleLiveData;
        }

        public LiveData<User> getUserLiveData() {
            return userLiveData;
        }

        public LiveData<User> getOwnerLiveData() {
            return ownerLiveData;
        }

        public LiveData<String> getErrorLiveData() {
            return errorLiveData;
        }

        public void loadVehicleData(String vehicleId) {
            DocumentReference vehicleRef = db.collection("Vehicles").document(vehicleId);
            vehicleRef.addSnapshotListener((document, error) -> {
                if (error != null) {
                    errorLiveData.setValue("Lỗi lấy chi tiết xe: " + error.getMessage());
                    return;
                }
                if (document != null && document.exists()) {
                    Vehicle vehicle = document.toObject(Vehicle.class);
                    vehicleLiveData.setValue(vehicle);
                } else {
                    errorLiveData.setValue("Không tìm thấy xe");
                }
            });

            if (firebaseUser != null) {
                DocumentReference userRef = db.collection("Users").document(firebaseUser.getUid());
                userRef.addSnapshotListener((document, error) -> {
                    if (error != null) {
                        errorLiveData.setValue("Lỗi tải thông tin người dùng: " + error.getMessage());
                        return;
                    }
                    if (document != null && document.exists()) {
                        User user = document.toObject(User.class);
                        userLiveData.setValue(user);
                    }
                });
            }
        }

        public void loadOwnerData(String ownerId) {
            DocumentReference ownerRef = db.collection("Users").document(ownerId);
            ownerRef.addSnapshotListener((document, error) -> {
                if (error != null) {
                    errorLiveData.setValue("Lỗi tải thông tin chủ xe: " + error.getMessage());
                    return;
                }
                if (document != null && document.exists()) {
                    User owner = document.toObject(User.class);
                    ownerLiveData.setValue(owner);
                }
            });
        }
    }
}