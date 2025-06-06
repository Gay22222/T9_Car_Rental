package com.uit.carrental.Service.UserAuthentication;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import com.uit.carrental.Service.Api.CloudinaryApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CCCDActivity extends AppCompatActivity {
    private ImageView backButton;
    private FrameLayout frontContainer, behindContainer;
    private ImageView front, behind;
    private String uploadtype, frontUrl, behindUrl;
    private Uri frontURI, behindURI;
    private Button btnUpdate;
    private FirebaseFirestore dtb_user;
    private FirebaseUser firebaseUser;
    private User user = new User();
    private ProgressDialog progressDialog;

    ActivityResultLauncher<String> pickImagesFromGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        if (uploadtype.equals("IDCards/Front/")) {
                            frontURI = result;
                            front.setImageURI(result);
                        } else if (uploadtype.equals("IDCards/Behind/")) {
                            behindURI = result;
                            behind.setImageURI(result);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cccd);
        init();

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CCCDActivity.this, ProfileManagement.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        frontContainer.setOnClickListener(v -> {
            uploadtype = "IDCards/Front/";
            pickImagesFromGallery.launch("image/*");
        });

        behindContainer.setOnClickListener(v -> {
            uploadtype = "IDCards/Behind/";
            pickImagesFromGallery.launch("image/*");
        });

        btnUpdate.setOnClickListener(v -> saveImageInFirestore());
    }

    private void init() {
        front = findViewById(R.id.img_front_CCCD);
        behind = findViewById(R.id.img_behind_CCCD);
        frontContainer = findViewById(R.id.front_cccd_container);
        behindContainer = findViewById(R.id.behind_cccd_container);
        btnUpdate = findViewById(R.id.update_img);
        backButton = findViewById(R.id.back_button);

        try {
            dtb_user = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                user.setUser_id(firebaseUser.getUid());
            } else {
                Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
            dtb_user = null;
        }

        // Khởi tạo CloudinaryApi
        CloudinaryApi.init(this);
    }

    private void uploadImage(Uri uri, OnUploadCompleteListener listener) {
        if (uri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
            listener.onComplete(null);
            return;
        }

        CloudinaryApi.uploadImage(this, uri, new CloudinaryApi.UploadCallbackCustom() {
            @Override
            public void onSuccess(String url) {
                listener.onComplete(url);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(CCCDActivity.this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show();
                listener.onComplete(null);
            }
        });
    }

    private void saveImageInFirestore() {
        if (dtb_user == null) {
            Toast.makeText(this, "Lỗi Firebase Firestore, không thể lưu ảnh", Toast.LENGTH_LONG).show();
            return;
        }

        if (frontURI == null && behindURI == null) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một ảnh CCCD", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog = ProgressDialog.show(this, "Uploading Images", "Please wait...", true);

        // Đếm số ảnh cần upload
        AtomicInteger pendingUploads = new AtomicInteger((frontURI != null ? 1 : 0) + (behindURI != null ? 1 : 0));
        Map<String, Object> data = new HashMap<>();

        if (frontURI != null) {
            uploadImage(frontURI, url -> {
                frontUrl = url;
                if (frontUrl != null) {
                    user.setCiCardFront(frontUrl);
                    data.put("ciCardFront", user.getCiCardFront());
                }
                if (pendingUploads.decrementAndGet() == 0) {
                    saveToFirestore(data);
                }
            });
        }

        if (behindURI != null) {
            uploadImage(behindURI, url -> {
                behindUrl = url;
                if (behindUrl != null) {
                    user.setCiCardBehind(behindUrl);
                    data.put("ciCardBehind", user.getCiCardBehind());
                }
                if (pendingUploads.decrementAndGet() == 0) {
                    saveToFirestore(data);
                }
            });
        }
    }

    private void saveToFirestore(Map<String, Object> data) {
        if (data.isEmpty()) {
            if (progressDialog != null) progressDialog.dismiss();
            Toast.makeText(this, "Không có ảnh nào được upload thành công", Toast.LENGTH_LONG).show();
            return;
        }

        dtb_user.collection("Users").document(firebaseUser.getUid())
                .update(data)
                .addOnCompleteListener(task -> {
                    if (progressDialog != null) progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(CCCDActivity.this, ProfileManagement.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        finish();
                    } else {
                        Toast.makeText(this, "Error updating document", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private interface OnUploadCompleteListener {
        void onComplete(String url);
    }
}