package com.uit.carrental.Service.UserAuthentication;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private static final String MOCK_AVATAR = "https://media4.giphy.com/media/v1.Y2lkPTZjMDliOTUyd2owNnAxcXR5YmJhMmh3ZDlvY3hoOXFhaWN2aXY3cm1tMXkwMnBlNyZlcD12MV9naWZzX3NlYXJjaCZjdD1n/FY8c5SKwiNf1EtZKGs/giphy_s.gif";

    private Button btnUpdate;
    private ImageView backButton, imgFrontCCCD, imgBehindCCCD, imgLicense;
    private CircleImageView imgAvatar;
    private TextView tvName, tvEmail, tvPhone, tvAddress, tvCity, tvBirthday;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management);

        initViews();
        initViewModel();
        observeLiveData();

        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfile.this, ProfileManagement.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    private void initViews() {
        btnUpdate = findViewById(R.id.btn_update);
        backButton = findViewById(R.id.back_button);
        imgAvatar = findViewById(R.id.img_avatar);
        imgFrontCCCD = findViewById(R.id.img_front_CCCD);
        imgBehindCCCD = findViewById(R.id.img_behind_CCCD);
        imgLicense = findViewById(R.id.img_license);
        tvName = findViewById(R.id.fullname);
        tvEmail = findViewById(R.id.email);
        tvPhone = findViewById(R.id.phone);
        tvAddress = findViewById(R.id.address);
        tvCity = findViewById(R.id.city);
        tvBirthday = findViewById(R.id.birthday);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModel.loadUserData();
    }

    private void observeLiveData() {
        viewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                tvName.setText(user.getUsername());
                tvEmail.setText(user.getEmail());
                tvPhone.setText(user.getPhoneNumber());
                tvAddress.setText(user.getAddress());
                tvCity.setText(user.getCity());
                tvBirthday.setText(user.getBirthday());

                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                    Picasso.get().load(user.getAvatarUrl()).into(imgAvatar);
                } else {
                    Picasso.get().load(MOCK_AVATAR).into(imgAvatar);
                }

                if (user.getCiCardFront() != null && !user.getCiCardFront().isEmpty()) {
                    Picasso.get().load(user.getCiCardFront()).into(imgFrontCCCD);
                } else {
                    imgFrontCCCD.setImageDrawable(null); // Không hiển thị ảnh nếu không có dữ liệu
                }

                if (user.getCiCardBehind() != null && !user.getCiCardBehind().isEmpty()) {
                    Picasso.get().load(user.getCiCardBehind()).into(imgBehindCCCD);
                } else {
                    imgBehindCCCD.setImageDrawable(null); // Không hiển thị ảnh nếu không có dữ liệu
                }

                if (user.getLicenseUrl() != null && !user.getLicenseUrl().isEmpty()) {
                    Picasso.get().load(user.getLicenseUrl()).into(imgLicense);
                } else {
                    imgLicense.setImageDrawable(null); // Không hiển thị ảnh nếu không có dữ liệu
                }
            }
        });

        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(UserProfile.this, error, Toast.LENGTH_LONG).show();
                Picasso.get().load(MOCK_AVATAR).into(imgAvatar);
                imgFrontCCCD.setImageDrawable(null);
                imgBehindCCCD.setImageDrawable(null);
                imgLicense.setImageDrawable(null);
            }
        });
    }

    public static class ProfileViewModel extends ViewModel {
        private final FirebaseFirestore db;
        private final FirebaseUser firebaseUser;
        private final MutableLiveData<User> userLiveData;
        private final MutableLiveData<String> errorLiveData;

        public ProfileViewModel() {
            db = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userLiveData = new MutableLiveData<>();
            errorLiveData = new MutableLiveData<>();
        }

        public LiveData<User> getUserLiveData() {
            return userLiveData;
        }

        public LiveData<String> getErrorLiveData() {
            return errorLiveData;
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
                } else {
                    errorLiveData.setValue("Không tìm thấy thông tin người dùng");
                }
            });
        }
    }
}
