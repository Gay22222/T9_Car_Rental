package com.uit.carrental.Service.UserAuthentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;
import com.uit.carrental.ActivityPages.CustomerMainActivity;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import com.uit.carrental.Service.Api.CloudinaryApi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CCCDActivity extends AppCompatActivity {

    private static final String TAG = "CCCDActivity";
    private ImageView backButton, front, behind, license;
    private FrameLayout frontContainer, behindContainer, licenseContainer;
    private Button btnUpdate;
    private String uploadType;
    private Uri frontUri, behindUri, licenseUri;
    private CccdViewModel viewModel;
    private ProgressDialog progressDialog;

    ActivityResultLauncher<String> pickImagesFromGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        switch (uploadType) {
                            case "IDCards/Front/":
                                frontUri = result;
                                front.setImageURI(result);
                                break;
                            case "IDCards/Behind/":
                                behindUri = result;
                                behind.setImageURI(result);
                                break;
                            case "Licenses/":
                                licenseUri = result;
                                license.setImageURI(result);
                                break;
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cccd);

        initViews();
        initViewModel();
        observeLiveData();
        viewModel.loadUserData();
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        front = findViewById(R.id.img_front_CCCD);
        behind = findViewById(R.id.img_behind_CCCD);
        license = findViewById(R.id.img_license);
        frontContainer = findViewById(R.id.front_cccd_container);
        behindContainer = findViewById(R.id.behind_cccd_container);
        licenseContainer = findViewById(R.id.license_container);
        btnUpdate = findViewById(R.id.update_img);

        CloudinaryApi.init(this);

        backButton.setOnClickListener(v -> {
            Log.d(TAG, "Nút Back được nhấn");
            navigateBack();
        });

        frontContainer.setOnClickListener(v -> {
            uploadType = "IDCards/Front/";
            pickImagesFromGallery.launch("image/*");
        });

        behindContainer.setOnClickListener(v -> {
            uploadType = "IDCards/Behind/";
            pickImagesFromGallery.launch("image/*");
        });

        licenseContainer.setOnClickListener(v -> {
            uploadType = "Licenses/";
            pickImagesFromGallery.launch("image/*");
        });

        btnUpdate.setOnClickListener(v -> saveImages());
    }

    private void initViewModel() {
        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                if (modelClass.isAssignableFrom(CccdViewModel.class)) {
                    return (T) new CccdViewModel(CCCDActivity.this);
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        };
        viewModel = new ViewModelProvider(this, factory).get(CccdViewModel.class);
    }

    private void observeLiveData() {
        viewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                if (user.getCiCardFront() != null && !user.getCiCardFront().isEmpty()) {
                    Picasso.get().load(user.getCiCardFront()).into(front);
                }
                if (user.getCiCardBehind() != null && !user.getCiCardBehind().isEmpty()) {
                    Picasso.get().load(user.getCiCardBehind()).into(behind);
                }
                if (user.getLicenseUrl() != null && !user.getLicenseUrl().isEmpty()) {
                    Picasso.get().load(user.getLicenseUrl()).into(license);
                }
            }
        });

        viewModel.getErrorLiveData().observe(this, error -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (error != null) {
                Toast.makeText(CCCDActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getUpdateSuccessLiveData().observe(this, success -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (success != null && success) {
                Toast.makeText(CCCDActivity.this, "Thông tin của bạn sẽ được duyệt trong vòng 30 phút.", Toast.LENGTH_LONG).show();
                navigateBack();
            }
        });
    }

    private void saveImages() {
        if (frontUri == null && behindUri == null && licenseUri == null) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = ProgressDialog.show(this, "Uploading Images", "Please wait...", true);
        viewModel.uploadImages(frontUri, behindUri, licenseUri);
    }

    private void navigateBack() {
        if (getIntent().getBooleanExtra("fromVehicleDetail", false)) {
            Intent intent = new Intent(this, CustomerMainActivity.class);
            intent.putExtra("fragment", "home");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ProfileManagement.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Nút Back vật lý được nhấn");
        navigateBack();
    }

    public static class CccdViewModel extends ViewModel {
        private final Context context;
        private final FirebaseFirestore db;
        private final FirebaseUser firebaseUser;
        private final MutableLiveData<User> userLiveData;
        private final MutableLiveData<String> errorLiveData;
        private final MutableLiveData<Boolean> updateSuccessLiveData;

        public CccdViewModel(Context context) {
            this.context = context;
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
                    userLiveData.setValue(user);
                }
            });
        }

        public void uploadImages(Uri frontUri, Uri behindUri, Uri licenseUri) {
            int uploadCount = (frontUri != null ? 1 : 0) + (behindUri != null ? 1 : 0) + (licenseUri != null ? 1 : 0);
            if (uploadCount == 0) {
                errorLiveData.setValue("Không có ảnh nào được chọn");
                return;
            }

            AtomicInteger pendingUploads = new AtomicInteger(uploadCount);
            Map<String, Object> data = new HashMap<>();
            User user = userLiveData.getValue() != null ? userLiveData.getValue() : new User();
            user.setUserId(firebaseUser.getUid());

            if (frontUri != null) {
                CloudinaryApi.uploadImage(context, frontUri, new CloudinaryApi.UploadCallbackCustom() {
                    @Override
                    public void onSuccess(String url) {
                        user.setCiCardFront(url);
                        data.put("ciCardFront", url);
                        checkUploadComplete(pendingUploads, data, user);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        errorLiveData.setValue("Lỗi tải CCCD mặt trước: " + e.getMessage());
                        checkUploadComplete(pendingUploads, data, user);
                    }
                });
            }

            if (behindUri != null) {
                CloudinaryApi.uploadImage(context, behindUri, new CloudinaryApi.UploadCallbackCustom() {
                    @Override
                    public void onSuccess(String url) {
                        user.setCiCardBehind(url);
                        data.put("ciCardBehind", url);
                        checkUploadComplete(pendingUploads, data, user);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        errorLiveData.setValue("Lỗi tải CCCD mặt sau: " + e.getMessage());
                        checkUploadComplete(pendingUploads, data, user);
                    }
                });
            }

            if (licenseUri != null) {
                CloudinaryApi.uploadImage(context, licenseUri, new CloudinaryApi.UploadCallbackCustom() {
                    @Override
                    public void onSuccess(String url) {
                        user.setLicenseUrl(url);
                        data.put("licenseUrl", url);
                        checkUploadComplete(pendingUploads, data, user);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        errorLiveData.setValue("Lỗi tải bằng lái xe: " + e.getMessage());
                        checkUploadComplete(pendingUploads, data, user);
                    }
                });
            }
        }

        private void checkUploadComplete(AtomicInteger pendingUploads, Map<String, Object> data, User user) {
            if (pendingUploads.decrementAndGet() == 0) {
                if (data.isEmpty()) {
                    errorLiveData.setValue("Không có ảnh nào được upload thành công");
                    return;
                }
                data.put("verificationStatus", "pending");
                db.collection("Users").document(user.getUserId())
                        .update(data)
                        .addOnSuccessListener(aVoid -> {
                            userLiveData.setValue(user);
                            updateSuccessLiveData.setValue(true);
                        })
                        .addOnFailureListener(e -> errorLiveData.setValue("Lỗi cập nhật thông tin: " + e.getMessage()));
            }
        }
    }
}