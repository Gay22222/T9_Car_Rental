package com.uit.carrental.Service.UserAuthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int RC_SIGN_IN = 406; // Khớp với LoginActivity

    private EditText inputUsername, inputPhone, inputEmail, inputPass, inputConfirmPass;
    private Button btnSignUp;
    private LinearLayout btnFacebook, btnGoogle;
    private FirebaseAuth mAuth;
    private FirebaseFirestore dtbUser;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;
    private String username, phone, email, password, confirmPassword;
    private boolean isValid = true;
    private User user;

    private void initViews() {
        inputUsername = findViewById(R.id.username_input);
        inputPhone = findViewById(R.id.phone_input);
        inputEmail = findViewById(R.id.email_input);
        inputPass = findViewById(R.id.password_input);
        inputConfirmPass = findViewById(R.id.confirm_password_input);
        btnSignUp = findViewById(R.id.btn_signUp2);
        btnFacebook = findViewById(R.id.facebook_button);
        btnGoogle = findViewById(R.id.google_button);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);
        initViews();

        mAuth = FirebaseAuth.getInstance();
        dtbUser = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        configureGoogleSignIn();

        btnSignUp.setOnClickListener(view -> {
            checkPassword();
            if (isValid) {
                createAccount();
            }
        });

        btnFacebook.setOnClickListener(view -> {
            Toast.makeText(this, "Đăng nhập với Facebook chưa được triển khai.", Toast.LENGTH_SHORT).show();
            // TODO: Implement Facebook login logic
        });

        btnGoogle.setOnClickListener(view -> loginWithGoogle());
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void checkPassword() {
        password = inputPass.getText().toString().trim();
        confirmPassword = inputConfirmPass.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            inputPass.setError("Vui lòng nhập mật khẩu");
            inputPass.requestFocus();
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            inputConfirmPass.setError("Mật khẩu không khớp");
            inputConfirmPass.requestFocus();
            isValid = false;
        } else {
            isValid = true;
        }
    }

    private void createAccount() {
        username = inputUsername.getText().toString().trim();
        phone = inputPhone.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        password = inputPass.getText().toString().trim();

        if (!validateForm()) return;

        progressDialog.setMessage("Đang tạo tài khoản...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sendVerificationEmail();
                    } else {
                        progressDialog.dismiss();
                        String errorMessage = "Không thể tạo tài khoản.";
                        if (task.getException() instanceof FirebaseAuthException) {
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            switch (e.getErrorCode()) {
                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    errorMessage = "Email đã được sử dụng.";
                                    inputEmail.setError(errorMessage);
                                    inputEmail.requestFocus();
                                    break;
                                case "ERROR_INVALID_EMAIL":
                                    errorMessage = "Email không hợp lệ.";
                                    inputEmail.setError(errorMessage);
                                    inputEmail.requestFocus();
                                    break;
                                case "ERROR_WEAK_PASSWORD":
                                    errorMessage = "Mật khẩu quá yếu (ít nhất 6 ký tự).";
                                    inputPass.setError(errorMessage);
                                    inputPass.requestFocus();
                                    break;
                                default:
                                    errorMessage = e.getMessage();
                            }
                        }
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationEmail() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Đã gửi email xác thực đến " + email, Toast.LENGTH_LONG).show();
                            createUserDocument();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Gửi email xác thực thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void createUserDocument() {
        user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setUser_id(FirebaseAuth.getInstance().getUid());

        Map<String, Boolean> roleMap = new HashMap<>();
        roleMap.put("customer", true);
        roleMap.put("owner", true);

        user.setRoles(roleMap);
        user.setCurrentRole("customer"); // Mặc định ban đầu là customer

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        dtbUser.setFirestoreSettings(settings);

        DocumentReference newUserRef = dtbUser.collection("Users")
                .document(FirebaseAuth.getInstance().getUid());

        newUserRef.set(user).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, "Đăng ký thành công. Vui lòng xác thực Email trước khi đăng nhập.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Tạo tài liệu người dùng thất bại: " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        if (TextUtils.isEmpty(username)) {
            inputUsername.setError("Tên đăng nhập bắt buộc.");
            inputUsername.requestFocus();
            valid = false;
        }
        if (TextUtils.isEmpty(phone)) {
            inputPhone.setError("Số điện thoại bắt buộc.");
            inputPhone.requestFocus();
            valid = false;
        }
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email bắt buộc.");
            inputEmail.requestFocus();
            valid = false;
        }
        if (TextUtils.isEmpty(password)) {
            inputPass.setError("Mật khẩu bắt buộc.");
            inputPass.requestFocus();
            valid = false;
        }
        return valid;
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
                Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Lưu thông tin người dùng với username và phone mặc định trống
                        username = acct.getDisplayName() != null ? acct.getDisplayName() : "";
                        email = acct.getEmail();
                        phone = "";
                        createUserDocument();
                    } else {
                        Toast.makeText(this, "Đăng nhập Google thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}