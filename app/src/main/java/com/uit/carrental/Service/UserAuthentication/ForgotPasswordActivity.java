package com.uit.carrental.Service.UserAuthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uit.carrental.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnContinue;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();

        Intent intent = getIntent();
        String emailFromIntent = intent.getStringExtra("email");
        if (emailFromIntent != null) {
            inputEmail.setText(emailFromIntent);
        }

        btnContinue.setOnClickListener(view -> sendResetPasswordEmail());
    }

    private void initViews() {
        inputEmail = findViewById(R.id.input_email);
        btnContinue = findViewById(R.id.btn_continue);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }

    private void sendResetPasswordEmail() {
        String email = inputEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Vui lòng nhập email.");
            return;
        }

        progressDialog.setMessage("Đang gửi email khôi phục...");
        progressDialog.show();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đã gửi email xác nhận tới " + email, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Log.e(TAG, "sendPasswordResetEmail thất bại: " + task.getException());
                        Toast.makeText(this, "Không thể gửi email xác nhận.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
