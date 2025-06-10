package com.uit.carrental.Service.UserAuthentication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;
import com.uit.carrental.ActivityPages.AdminMainActivity;
import com.uit.carrental.ActivityPages.CustomerMainActivity;
import com.uit.carrental.ActivityPages.OwnerMainActivity;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import com.uit.carrental.Service.Api.CloudinaryApi;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileManagement extends AppCompatActivity {

    private CircleImageView imgAvatar;
    private ImageView backButton, editAvatar;
    private EditText etFullname, etEmail, etPhone, etAddress, etCity, etDescription;
    private Button btnUpdate, btnDateOfBirth;
    private TextView tvUpdateCccd, tvCurrentRole, tvVerificationStatus, tvStatus;
    private DatePickerDialog datePickerDialog;
    private ProfileViewModel viewModel;
    private Uri avatarUri;
    private ProgressDialog progressDialog;
    private boolean isFirstLogin;

    ActivityResultLauncher<String> pickImagesFromGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        avatarUri = result;
                        imgAvatar.setImageURI(result);
                        uploadAvatar();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        isFirstLogin = getIntent().getBooleanExtra("is_first_login", false);

        initViews();
        initViewModel();
        observeLiveData();
        viewModel.loadUserData();

        backButton.setOnClickListener(v -> navigateBackBasedOnRole());

        btnUpdate.setOnClickListener(v -> updateInfo());

        imgAvatar.setOnClickListener(v -> pickImagesFromGallery.launch("image/*"));

        editAvatar.setOnClickListener(v -> pickImagesFromGallery.launch("image/*"));

        btnDateOfBirth.setOnClickListener(this::openDatePicker);

        tvUpdateCccd.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileManagement.this, CCCDActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        CloudinaryApi.init(this);
    }

    private void initViews() {
        imgAvatar = findViewById(R.id.img_avatar_profile_input_fragment);
        backButton = findViewById(R.id.back_button);
        editAvatar = findViewById(R.id.edit_avatar);
        etFullname = findViewById(R.id.profile_input_fullname);
        etEmail = findViewById(R.id.profile_input_email);
        etPhone = findViewById(R.id.profile_input_phone);
        etAddress = findViewById(R.id.profile_input_address);
        etCity = findViewById(R.id.profile_input_city);
        etDescription = findViewById(R.id.profile_input_description);
        btnUpdate = findViewById(R.id.btn_update);
        btnDateOfBirth = findViewById(R.id.profile_input_dateofbirth);
        tvUpdateCccd = findViewById(R.id.update_cccd);
        tvCurrentRole = findViewById(R.id.tv_current_role);
        tvVerificationStatus = findViewById(R.id.tv_verification_status);
        tvStatus = findViewById(R.id.tv_status);

        etEmail.setEnabled(false);
        etPhone.setEnabled(false);

        initDatePicker();
        btnDateOfBirth.setText(getTodaysDate());
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    private void observeLiveData() {
        viewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                etFullname.setText(user.getUsername());
                etEmail.setText(user.getEmail());
                etPhone.setText(user.getPhoneNumber());
                etAddress.setText(user.getAddress());
                etCity.setText(user.getCity());
                etDescription.setText(user.getDescription());
                btnDateOfBirth.setText(user.getBirthday() != null && !user.getBirthday().isEmpty() ? user.getBirthday() : getTodaysDate());
                tvCurrentRole.setText(getRoleDisplayText(user.getCurrentRole()));
                tvVerificationStatus.setText(getVerificationDisplayText(user.getVerificationStatus()));
                tvStatus.setText(getStatusDisplayText(user.getStatus()));

                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                    Picasso.get().load(user.getAvatarUrl()).placeholder(R.drawable.ic_person).into(imgAvatar);
                } else {
                    imgAvatar.setImageResource(R.drawable.ic_person);
                }
            }
        });

        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getUpdateSuccessLiveData().observe(this, success -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (success != null && success) {
                navigateAfterUpdate();
            }
        });
    }

    private void navigateAfterUpdate() {
        User user = viewModel.getUserLiveData().getValue();
        Intent intent;
        if (user == null || user.getCurrentRole() == null) {
            // Nếu user hoặc currentRole null, lấy từ Firestore lần nữa
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                User fetchedUser = documentSnapshot.toObject(User.class);
                                if (fetchedUser != null && fetchedUser.getCurrentRole() != null) {
                                    navigateToMain(fetchedUser);
                                } else {
                                    navigateToDefault();
                                }
                            } else {
                                navigateToDefault();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi lấy thông tin người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            navigateToDefault();
                        });
            } else {
                navigateToDefault();
            }
            return;
        }

        navigateToMain(user);
    }

    private void navigateToMain(User user) {
        Intent intent;
        switch (user.getCurrentRole().toLowerCase()) {
            case "customer":
                intent = new Intent(this, CustomerMainActivity.class);
                if (!isFirstLogin) {
                    intent.putExtra("navigate_to_fragment", "CustomerSettingFragment");
                }
                break;
            case "owner":
                intent = new Intent(this, OwnerMainActivity.class);
                if (!isFirstLogin) {
                    intent.putExtra("navigate_to_fragment", "OwnerSettingFragment");
                }
                break;
            case "admin":
                intent = new Intent(this, AdminMainActivity.class);
                if (!isFirstLogin) {
                    intent.putExtra("navigate_to_fragment", "AdminSettingFragment");
                }
                break;
            default:
                Toast.makeText(this, "Vai trò không xác định", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, LoginActivity.class); // Fallback
                break;
        }
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void navigateToDefault() {
        // Fallback điều hướng khi không lấy được thông tin người dùng
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void navigateBackBasedOnRole() {
        if (isFirstLogin) {
            Toast.makeText(this, "Vui lòng cập nhật thông tin trước khi tiếp tục.", Toast.LENGTH_SHORT).show();
            return;
        }
        navigateAfterUpdate();
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year) {
        return String.format("%02d/%02d/%d", day, month, year);
    }

    private void openDatePicker(View view) {
        datePickerDialog.show();
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            btnDateOfBirth.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private void uploadAvatar() {
        if (avatarUri != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Đang tải ảnh...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            CloudinaryApi.uploadImage(this, avatarUri, new CloudinaryApi.UploadCallbackCustom() {
                @Override
                public void onSuccess(String url) {
                    viewModel.setAvatarUrl(url);
                    progressDialog.dismiss();
                    Toast.makeText(ProfileManagement.this, "Tải ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileManagement.this, "Tải ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateInfo() {
        String username = etFullname.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String birthday = btnDateOfBirth.getText().toString().trim();

        if (username.isEmpty()) {
            etFullname.setError("Vui lòng nhập họ và tên");
            etFullname.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            etAddress.setError("Vui lòng nhập địa chỉ");
            etAddress.requestFocus();
            return;
        }
        if (city.isEmpty()) {
            etCity.setError("Vui lòng nhập thành phố");
            etCity.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            etDescription.setError("Vui lòng nhập mô tả");
            etDescription.requestFocus();
            return;
        }
        if (birthday.isEmpty()) {
            btnDateOfBirth.setError("Vui lòng chọn ngày sinh");
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang cập nhật thông tin...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        User user = new User();
        user.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        user.setUsername(username);
        user.setAddress(address);
        user.setCity(city);
        user.setDescription(description);
        user.setBirthday(birthday);
        user.setEmail(etEmail.getText().toString());
        user.setPhoneNumber(etPhone.getText().toString());
        // Lấy currentRole và roles từ user hiện tại
        User currentUser = viewModel.getUserLiveData().getValue();
        if (currentUser != null) {
            user.setCurrentRole(currentUser.getCurrentRole());
            user.setRoles(currentUser.getRoles());
        }

        viewModel.updateUser(user);
    }

    private String getRoleDisplayText(String role) {
        switch (role) {
            case "customer":
                return "Khách hàng";
            case "owner":
                return "Chủ xe";
            case "admin":
                return "Quản trị viên";
            default:
                return role;
        }
    }

    private String getVerificationDisplayText(String verificationStatus) {
        if (verificationStatus == null) return "Chưa xác minh";
        switch (verificationStatus) {
            case "verified":
                return "Đã xác minh";
            case "pending":
                return "Đang chờ xác minh";
            default:
                return "Chưa xác minh";
        }
    }

    private String getStatusDisplayText(String status) {
        if (status == null) return "Hoạt động";
        switch (status) {
            case "active":
                return "Hoạt động";
            case "inactive":
                return "Không hoạt động";
            default:
                return status;
        }
    }

    public static class ProfileViewModel extends ViewModel {
        private final FirebaseFirestore db;
        private final FirebaseUser firebaseUser;
        private final MutableLiveData<User> userLiveData;
        private final MutableLiveData<String> errorLiveData;
        private final MutableLiveData<Boolean> updateSuccessLiveData;
        private String avatarUrl;

        public ProfileViewModel() {
            db = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userLiveData = new MutableLiveData<>();
            errorLiveData = new MutableLiveData<>();
            updateSuccessLiveData = new MutableLiveData<>();
        }

        public LiveData<User> getUserLiveData() {
            return userLiveData;
        }

        public LiveData<String> getErrorLiveData() {
            return errorLiveData;
        }

        public LiveData<Boolean> getUpdateSuccessLiveData() {
            return updateSuccessLiveData;
        }

        public void setAvatarUrl(String url) {
            this.avatarUrl = url;
        }

        public void loadUserData() {
            if (firebaseUser == null) {
                errorLiveData.setValue("Người dùng chưa đăng nhập");
                return;
            }

            DocumentReference userRef = db.collection("Users").document(firebaseUser.getUid());
            userRef.addSnapshotListener((document, error) -> {
                if (error != null) {
                    errorLiveData.setValue("Lỗi tải thông tin: " + error.getMessage());
                    return;
                }
                if (document != null && document.exists()) {
                    User user = document.toObject(User.class);
                    if (avatarUrl != null) {
                        user.setAvatarUrl(avatarUrl);
                    }
                    userLiveData.setValue(user);
                } else {
                    User newUser = new User();
                    newUser.setUserId(firebaseUser.getUid());
                    newUser.setEmail(firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "");
                    newUser.setCurrentRole("customer"); // Mặc định vai trò customer
                    Map<String, Boolean> roles = new HashMap<>();
                    roles.put("customer", true);
                    roles.put("owner", true);
                    roles.put("admin", false);
                    newUser.setRoles(roles);
                    userRef.set(newUser)
                            .addOnSuccessListener(aVoid -> userLiveData.setValue(newUser))
                            .addOnFailureListener(e -> errorLiveData.setValue("Lỗi tạo người dùng mới: " + e.getMessage()));
                }
            });
        }

        public void updateUser(User user) {
            if (avatarUrl != null) {
                user.setAvatarUrl(avatarUrl);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("phoneNumber", user.getPhoneNumber());
            data.put("address", user.getAddress());
            data.put("city", user.getCity());
            data.put("description", user.getDescription());
            data.put("birthday", user.getBirthday());
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                data.put("avatarUrl", user.getAvatarUrl());
            }
            if (user.getCurrentRole() != null) {
                data.put("currentRole", user.getCurrentRole());
            }
            if (user.getRoles() != null) {
                data.put("roles", user.getRoles());
            }

            db.collection("Users").document(user.getUserId())
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        userLiveData.setValue(user); // Cập nhật LiveData để phản ánh thay đổi
                        updateSuccessLiveData.setValue(true);
                    })
                    .addOnFailureListener(e -> errorLiveData.setValue("Lỗi cập nhật thông tin: " + e.getMessage()));
        }
    }
}
