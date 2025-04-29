package com.uit.carrental.Service.UserAuthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText inputPhone, inputEmail, inputPass, reinputPass;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore dtbUser;
    private ProgressDialog progressDialog;
    private String phone, email, password, rePassword;
    private boolean isValid = true;
    private User user;

    private void initViews() {
        inputEmail = findViewById(R.id.input_email);
        inputPhone = findViewById(R.id.input_phone);
        inputPass = findViewById(R.id.input_password);
        reinputPass = findViewById(R.id.reinput_password);
        btnSignUp = findViewById(R.id.btn_signup);
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

        btnSignUp.setOnClickListener(view -> {
            checkPassword();
            if (isValid) {
                createAccount();
            }
        });
    }

    private void checkPassword() {
        password = inputPass.getText().toString().trim();
        rePassword = reinputPass.getText().toString().trim();
        if (!password.equals(rePassword)) {
            Toast.makeText(this, "Mật khẩu không khớp, mời nhập lại", Toast.LENGTH_LONG).show();
            inputPass.setText("");
            reinputPass.setText("");
            isValid = false;
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_LONG).show();
            inputPass.setText("");
            reinputPass.setText("");
            isValid = false;
        } else {
            isValid = true;
        }
    }

    private void createAccount() {
        phone = inputPhone.getText().toString();
        email = inputEmail.getText().toString();
        password = inputPass.getText().toString();

        if (!validateForm()) return;

        progressDialog.setMessage("Đang tạo tài khoản...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sendVerificationEmail();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Không thể tạo User.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
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
                            Toast.makeText(this, "Gửi email xác thực thất bại.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void createUserDocument() {
        user = new User();
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setUser_id(FirebaseAuth.getInstance().getUid());

        Map<String, Boolean> roleMap = new HashMap<>();
        roleMap.put("customer", true);
        roleMap.put("owner", true);

        user.setRoles(roleMap);
        user.setCurrentRole("customer"); // mặc định ban đầu là customer

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
                Snackbar.make(parentLayout, "Tạo user Firestore thất bại.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email bắt buộc.");
            valid = false;
        }
        if (TextUtils.isEmpty(password)) {
            inputPass.setError("Mật khẩu bắt buộc.");
            valid = false;
        }
        if (TextUtils.isEmpty(phone)) {
            inputPhone.setError("Số điện thoại bắt buộc.");
            valid = false;
        }
        return valid;
    }
}
