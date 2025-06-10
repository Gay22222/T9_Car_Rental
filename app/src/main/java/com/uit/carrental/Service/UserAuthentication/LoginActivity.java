package com.uit.carrental.Service.UserAuthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.carrental.ActivityPages.CustomerMainActivity;
import com.uit.carrental.ActivityPages.OwnerMainActivity;
import com.uit.carrental.ActivityPages.AdminMainActivity;
import com.uit.carrental.Model.User;
import com.uit.carrental.Model.UserClient;
import com.uit.carrental.R;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp;
    private TextView btnForgot;
    private LinearLayout btnGG, btnFacebook;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 406;
    private static final String ADMIN_USERNAME = "quangdeptrai";
    private static final String ADMIN_PASSWORD = "21522516";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initViews();
        configureGoogleSignIn();

        btnSignIn.setOnClickListener(view -> loginWithEmail());
        btnGG.setOnClickListener(view -> loginWithGoogle());
        btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        btnForgot.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        btnFacebook.setOnClickListener(view -> {
            Toast.makeText(this, "Đăng nhập với Facebook chưa được triển khai.", Toast.LENGTH_SHORT).show();
        });
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnForgot = findViewById(R.id.btnForgot);
        btnGG = findViewById(R.id.btn_GGsignIn);
        btnFacebook = findViewById(R.id.btn_facebook_signIn);
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void loginWithEmail() {
        String email = inputEmail.getText().toString().trim().toLowerCase();
        String password = inputPassword.getText().toString().trim();

        if (!validateForm(email, password)) return;

        // Kiểm tra admin credentials
        if (email.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            Log.d(TAG, "Đăng nhập admin cứng: " + email);
            User adminUser = new User();
            adminUser.setUserId("admin_quangdeptrai");
            adminUser.setUsername(ADMIN_USERNAME);
            adminUser.setEmail("");
            adminUser.setCurrentRole("admin");
            Map<String, Boolean> roles = new HashMap<>();
            roles.put("admin", true);
            roles.put("customer", false);
            roles.put("owner", false);
            adminUser.setRoles(roles);

            ((UserClient) getApplicationContext()).setUser(adminUser);
            startActivity(new Intent(this, AdminMainActivity.class));
            finish();
            return;
        }

        // Đăng nhập Firebase thông thường
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Đăng nhập Firebase thành công: " + email);
                        fetchUserData();
                    } else {
                        Log.e(TAG, "Đăng nhập Firebase thất bại: " + task.getException().getMessage());
                        Toast.makeText(this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e(TAG, "Đăng nhập Google thất bại: " + e.getMessage());
                Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Đăng nhập Google thành công: " + acct.getEmail());
                        fetchUserData();
                    } else {
                        Log.e(TAG, "Đăng nhập Google thất bại: " + task.getException().getMessage());
                        Toast.makeText(this, "Đăng nhập Google thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserData() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Log.e(TAG, "Không tìm thấy FirebaseUser");
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!firebaseUser.isEmailVerified() && !firebaseUser.getEmail().equals(ADMIN_USERNAME)) {
            Log.w(TAG, "Email chưa được xác thực: " + firebaseUser.getEmail());
            Toast.makeText(this, "Vui lòng xác nhận email trước khi đăng nhập.", Toast.LENGTH_LONG).show();
            mAuth.signOut();
            return;
        }

        String uid = firebaseUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    if (user != null) {
                        Log.d(TAG, "Tìm thấy tài liệu người dùng: " + user.getEmail() + ", vai trò: " + user.getCurrentRole());
                        ((UserClient) getApplicationContext()).setUser(user);
                        checkUserInfo(user);
                    } else {
                        Log.e(TAG, "Không thể ánh xạ tài liệu người dùng");
                        Toast.makeText(this, "Lỗi ánh xạ dữ liệu người dùng.", Toast.LENGTH_SHORT).show();
                        navigateToProfileManagement(true);
                    }
                } else {
                    Log.w(TAG, "Tài liệu người dùng không tồn tại: " + uid);
                    navigateToProfileManagement(true);
                }
            } else {
                Log.e(TAG, "Lấy tài liệu người dùng thất bại: " + task.getException().getMessage());
                Toast.makeText(this, "Không thể lấy thông tin người dùng: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                navigateToProfileManagement(true);
            }
        });
    }

    private void checkUserInfo(User user) {
        // Kiểm tra các trường bắt buộc
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getAddress() == null || user.getAddress().isEmpty() ||
                user.getCity() == null || user.getCity().isEmpty() ||
                user.getDescription() == null || user.getDescription().isEmpty() ||
                user.getBirthday() == null || user.getBirthday().isEmpty()) {
            Log.d(TAG, "Thông tin người dùng chưa đầy đủ");
            navigateToProfileManagement(true);
        } else {
            Log.d(TAG, "Thông tin người dùng đầy đủ, chuyển đến giao diện chính");
            navigateToMain(user);
        }
    }

    private void navigateToMain(User user) {
        if (user.getCurrentRole() != null) {
            Log.d(TAG, "Chuyển hướng với vai trò: " + user.getCurrentRole());
            Intent intent;
            switch (user.getCurrentRole().toLowerCase()) {
                case "customer":
                    intent = new Intent(this, CustomerMainActivity.class);
                    break;
                case "owner":
                    intent = new Intent(this, OwnerMainActivity.class);
                    break;
                case "admin":
                    intent = new Intent(this, AdminMainActivity.class);
                    break;
                default:
                    Log.w(TAG, "Vai trò không xác định: " + user.getCurrentRole());
                    Toast.makeText(this, "Vai trò không xác định.", Toast.LENGTH_SHORT).show();
                    return;
            }
            startActivity(intent);
            finish();
        } else {
            Log.w(TAG, "Không tìm thấy vai trò hiện tại");
            Toast.makeText(this, "Không tìm thấy vai trò hiện tại.", Toast.LENGTH_SHORT).show();
            navigateToProfileManagement(true);
        }
    }

    private void navigateToProfileManagement(boolean isFirstLogin) {
        Intent intent = new Intent(this, ProfileManagement.class);
        intent.putExtra("is_first_login", isFirstLogin);
        startActivity(intent);
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email bắt buộc.");
            valid = false;
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Mật khẩu bắt buộc.");
            valid = false;
        }
        return valid;
    }
}
