package com.uit.carrental.Service.UserAuthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.uit.carrental.Model.User;
import com.uit.carrental.Model.UserClient;
import com.uit.carrental.R;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp;
    private TextView btnForgot;
    private LinearLayout btnGG, btnFacebook; // Đổi từ Button sang LinearLayout
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 406;

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
            // TODO: Implement Facebook login logic (e.g., using Facebook SDK and Firebase)
        });
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.btnSignIn); // Cập nhật ID
        btnSignUp = findViewById(R.id.btnSignUp); // Cập nhật ID
        btnForgot = findViewById(R.id.btnForgot); // Cập nhật ID
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
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (!validateForm(email, password)) return;

        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        fetchUserData();
                    } else {
                        Toast.makeText(this, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Đăng nhập Google thất bại.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fetchUserData();
                    } else {
                        Toast.makeText(this, "Đăng nhập Google thất bại.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserData() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) return;

        if (!firebaseUser.isEmailVerified()) {
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
                    ((UserClient) getApplicationContext()).setUser(user);
                    navigateToMain(user);
                } else {
                    createNewUser(uid);
                }
            } else {
                Toast.makeText(this, "Không thể lấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewUser(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        User user = new User();
        user.setUser_id(uid);
        user.setUsername("");
        user.setEmail("");
        user.setRole("customer");

        db.collection("Users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    ((UserClient) getApplicationContext()).setUser(user);
                    navigateToMain(user);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể tạo tài khoản người dùng mới.", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToMain(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            Intent intent = new Intent(this, ProfileManagement.class);
            startActivity(intent);
        } else {
            if (user.getCurrentRole() != null) {
                if (user.getCurrentRole().equalsIgnoreCase("customer")) {
                    startActivity(new Intent(this, CustomerMainActivity.class));
                } else if (user.getCurrentRole().equalsIgnoreCase("owner")) {
                    startActivity(new Intent(this, OwnerMainActivity.class));
                } else {
                    Toast.makeText(this, "Vai trò không xác định.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Không tìm thấy vai trò hiện tại.", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
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