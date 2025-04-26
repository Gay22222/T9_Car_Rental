//package com.example.carrenting.Service.UserAuthentication;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.carrenting.ActivityPages.CustomerMainActivity;
//import com.example.carrenting.Model.User;
//import com.example.carrenting.R;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.snackbar.Snackbar;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.GoogleAuthProvider;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreSettings;
//import com.example.carrenting.ActivityPages.RoleSelectionActivity;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class LoginActivity extends AppCompatActivity {
//    private EditText inputEmail, inputPassword;
//    private static final String TAG = "LoginActivity";
//    private Button btnSignIn, btnSignUp, btnForget, btnGG;
//    private FirebaseAuth mAuth;
//    private ProgressDialog progressDialog;
//    private static final int RC_SIGN_IN = 0406;
//    private String email, password, username;
//    private User user = new User();
//    private FirebaseFirestore dtbUser;
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Toast.makeText(LoginActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
//
//                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//
//                FirebaseAuth.getInstance().signInWithCredential(credential)
//                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Đăng nhập thành công vào Firebase Authentication
//
//                                    // Lưu ID người dùng vào Firestore
//                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                                    String uid = user.getUid();
//
//                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                    DocumentReference docRef = db.collection("User").document(uid);
//
//                                    FirebaseFirestore.getInstance().collection("Users").document(uid)
//                                            .get()
//                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                    if (task.isSuccessful()) {
//
//                                                        DocumentSnapshot document = task.getResult();
//                                                        if (document.exists()) {
//                                                            // do something with the retrieved data
//                                                            String username = document.getString("username");
//                                                            if (username.isEmpty()) {
//                                                                Intent intent = new Intent(LoginActivity.this, GGVerrifyPhone.class);
//                                                                startActivity(intent);
//                                                            }
//                                                            else
//                                                            {
//                                                                Intent intent = new Intent(LoginActivity.this, RoleSelectionActivity.class);
//                                                                startActivity(intent);
//
//                                                                onStop();
//                                                            }
//
//                                                        } else {
//                                                            // the document does not exist
//                                                            createUser();
//                                                        }
//                                                    } else {
//                                                        // handle the error
//                                                    }
//                                                }
//                                            });
//
//                                }
//                                else {
//                                    // Đăng nhập thất bại vào Firebase Authentication
//                                }
//                            }
//                        });
//
//
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
//            }
//        }
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_in);
//
//        init();
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        btnGG.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//            }
//        });
//        overridePendingTransition(R.anim.anim_in_left,R.anim.anim_out_right);
//        //btnSignIn.setOnClickListener(new View.OnClickListener() {
//        //    @Override
//        //    public void onClick(View view) {
//        //        email = inputEmail.getText().toString();
//        //       password = inputPassword.getText().toString();
//        //        signIn(email, password);
//        //    }
//        //});
//        btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Bỏ qua login, chuyển thẳng đến chọn vai trò
//                Intent intent = new Intent(LoginActivity.this, RoleSelectionActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        btnSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//
//            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                intent.putExtra("Email", email);
//                startActivity(intent);
//            }
//        });
//        btnForget.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String iemail = inputEmail.getText().toString();
//
//                try {
//                    if(!iemail.isEmpty()){
//                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//                    if(!iemail.isEmpty())
//                    {
//                        intent.putExtra("email", iemail);
//                    }
//                    startActivity(intent);
//                }
//                else
//                {
//
//                        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//                        startActivity(intent);
//                }
//                }catch (Exception ex)
//                {
//                    Log.e(TAG, ex.toString());
//                }
//
//            }
//        });
//    }
//
//    private void signIn(String email, String password){
//        if (!validateForm()){
//            return;
//        }
//
//        progressDialog.show();
//
//            mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()){
//                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                            String uid = firebaseUser.getUid();
//
//                            FirebaseFirestore.getInstance().collection("Users").document(uid)
//                                .get()
//                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                        if (task.isSuccessful()) {
//                                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
//
//                                            DocumentSnapshot document = task.getResult();
//                                            if (document.exists()) {
//                                                // do something with the retrieved data
//                                                String username = document.getString("username");
//                                                String phonenumber = document.getString("phoneNumber");
//                                                if (username != null && username.isEmpty()) {
//                                                    Intent intent = new Intent(LoginActivity.this, ValidatePhoneActivity.class);
//                                                    intent.putExtra("phone", phonenumber);
//                                                    startActivity(intent);
//                                                }
//                                                else
//                                                {
//                                                    Intent intent = new Intent(LoginActivity.this, CustomerMainActivity.class);
//                                                    startActivity(intent);
//                                                    onStop();
//                                                }
//
//                                            } else {
//                                                // the document does not exist
//                                            }
//                                        } else {
//                                            // handle the error
//                                        }
//                                    }
//                                });
//
//                        } else {
//                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
//                            progressDialog.cancel();
//                        }
//                    }
//                });
//    }
//    private boolean validateForm() {
//        boolean valid = true;
//        String email = inputEmail.getText().toString();
//        if (TextUtils.isEmpty(email)) {
//            inputEmail.setError("Required.");
//            valid = false;
//        } else {
//            inputEmail.setError(null);
//        }
//        String password = inputPassword.getText().toString();
//        if (TextUtils.isEmpty(password)) {
//            inputPassword.setError("Required.");
//            valid = false;
//        } else {
//            inputPassword.setError(null);
//        }
//        return valid;
//    }
//    private void init(){
//        mAuth = FirebaseAuth.getInstance();
//        btnSignUp = findViewById(R.id.btn_signUp);
//        inputEmail = findViewById(R.id.email);
//        inputPassword = findViewById(R.id.password);
//        btnSignIn = findViewById(R.id.btn_signIn);
//        btnForget = findViewById(R.id.btn_forget);
//        btnGG = findViewById(R.id.btn_GGsignIn);
//
//        progressDialog = new ProgressDialog(this);
//        dtbUser = FirebaseFirestore.getInstance();
//    }
//
//    private void createUser() {
//        user.setUser_id(FirebaseAuth.getInstance().getUid());
//
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .build();
//        dtbUser.setFirestoreSettings(settings);
//
//        DocumentReference newUserRef = dtbUser
//                .collection("Users")
//                .document(FirebaseAuth.getInstance().getUid());
//        newUserRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                progressDialog.dismiss();
//                if (task.isSuccessful()) {
//
//                    Toast.makeText(LoginActivity.this, "Tạo tài khoản thành công",Toast.LENGTH_LONG).show();
//
//                } else {
//                    View parentLayout = findViewById(android.R.id.content);
//                    Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show();
//                }
//            }
//        });
//        progressDialog.cancel();
//    }
//}
package com.example.carrenting.Service.UserAuthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carrenting.ActivityPages.CustomerMainActivity;
import com.example.carrenting.Model.User;
import com.example.carrenting.Model.UserClient;
import com.example.carrenting.R;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnForget;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.btn_signIn);
        btnSignUp = findViewById(R.id.btn_signUp);
        btnForget = findViewById(R.id.btn_forget);
        progressDialog = new ProgressDialog(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo user fake
                User demoUser = new User();
                demoUser.setUser_id("demo_user_001");
                demoUser.setUsername("Trương Đức Quang Khoa");
                demoUser.setEmail("khoa.quang@example.com");
                demoUser.setPhoneNumber("+84912345678");
                demoUser.setAvatarURL("data:image/jpeg;base64,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");
                demoUser.setAddress("123 Đường số 1");
                demoUser.setCity("TP. Hồ Chí Minh");
                demoUser.setBirthday("2002-08-15");
                demoUser.setCiCardFront("https://demo.com/front.png");
                demoUser.setCiCardBehind("https://demo.com/back.png");

                // Gán vào app context
                ((UserClient) getApplicationContext()).setUser(demoUser);

                // Chuyển qua màn hình login
                Intent intent = new Intent(LoginActivity.this, CustomerMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.putExtra("Email", inputEmail.getText().toString());
            startActivity(intent);
        });

        btnForget.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            intent.putExtra("email", inputEmail.getText().toString());
            startActivity(intent);
        });
    }
}
