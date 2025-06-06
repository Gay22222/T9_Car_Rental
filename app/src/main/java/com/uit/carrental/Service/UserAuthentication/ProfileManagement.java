package com.uit.carrental.Service.UserAuthentication;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.squareup.picasso.Picasso;
import com.uit.carrental.Model.User;
import com.uit.carrental.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileManagement extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private Button dateButton, btnUpdate;
    private Uri mImageURI;
    private CircleImageView imgAvatar;
    private ImageView backButton, editAvatar;
    private String imageID;
    private String documentId, downloadUrl, uploadtype;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore dtb_user;
    private EditText phonenumber, email, fullname, address, city;
    private TextView emailDisplay, updateCccd;
    private User user = new User();

    private static final String MOCK_AVATAR = "https://media4.giphy.com/media/v1.Y2lkPTZjMDliOTUyd2owNnAxcXR5YmJhMmh3ZDlvY3hoOXFhaWN2aXY3cm1tMXkwMnBlNyZlcD12MV9naWZzX3NlYXJjaCZjdD1n/FY8c5SKwiNf1EtZKGs/giphy_s.gif";

    ActivityResultLauncher<String> AvatarpickImagesFromGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        mImageURI = result;
                        imgAvatar.setImageURI(result);
                        uploadImage(uploadtype);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        if (dtb_user != null) {
            getinfo();
        } else {
            Toast.makeText(this, "Lỗi khởi tạo Firebase Firestore", Toast.LENGTH_LONG).show();
        }

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileManagement.this, UserProfile.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        btnUpdate.setOnClickListener(v -> updateinfo());

        imgAvatar.setOnClickListener(v -> {
            uploadtype = "UsersAvatar/";
            AvatarpickImagesFromGallery.launch("image/*");
        });

        editAvatar.setOnClickListener(v -> {
            uploadtype = "UsersAvatar/";
            AvatarpickImagesFromGallery.launch("image/*");
        });

        dateButton.setOnClickListener(v -> openDatePicker(v));

        updateCccd.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileManagement.this, CCCDActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    private void init() {
        phonenumber = findViewById(R.id.profile_input_phone);
        email = findViewById(R.id.profile_input_email);
        phonenumber.setEnabled(false);
        email.setEnabled(false);

        fullname = findViewById(R.id.profile_input_fullname);
        address = findViewById(R.id.profile_input_address);
        city = findViewById(R.id.profile_input_city);
        btnUpdate = findViewById(R.id.btn_update);
        imgAvatar = findViewById(R.id.img_avatar_profile_input_fragment);
        backButton = findViewById(R.id.back_button);
        editAvatar = findViewById(R.id.edit_avatar);
        emailDisplay = findViewById(R.id.email_display);
        updateCccd = findViewById(R.id.update_cccd);

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

        initDatePicker();
        dateButton = findViewById(R.id.profile_input_dateofbirth);
        dateButton.setText(getTodaysDate());
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

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private void uploadImage(String type) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        if (mImageURI != null) {
            imageID = UUID.randomUUID().toString();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(type + "/" + imageID);
            ref.putFile(mImageURI)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            downloadUrl = uri.toString();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileManagement.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }
    }

    private void updateinfo() {
        user.setUsername(fullname.getText().toString());
        user.setAddress(address.getText().toString());
        user.setCity(city.getText().toString());
        user.setBirthday(dateButton.getText().toString());
        user.setAvatarURL(downloadUrl);
        user.setEmail(email.getText().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("address", user.getAddress());
        data.put("city", user.getCity());
        data.put("birthday", user.getBirthday());
        data.put("email", user.getEmail());

        if (downloadUrl != null) {
            data.put("avatarURL", user.getAvatarURL());
        }

        if (dtb_user == null) {
            Toast.makeText(this, "Lỗi Firebase Firestore, không thể cập nhật", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            dtb_user.collection("Users").document(user.getUser_id())
                    .update(data)
                    .addOnSuccessListener(aVoid -> {
                        runOnUiThread(() -> {
                            Intent intent = new Intent(ProfileManagement.this, UserProfile.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();
                        });
                    })
                    .addOnFailureListener(e -> {
                        runOnUiThread(() -> {
                            Toast.makeText(ProfileManagement.this, "Error updating document", Toast.LENGTH_LONG).show();
                        });
                    });
        }).start();
    }

    private void getinfo() {
        if (dtb_user == null) {
            Toast.makeText(this, "Lỗi Firebase Firestore, không thể lấy thông tin", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            dtb_user.collection("Users")
                    .whereEqualTo("user_id", user.getUser_id())
                    .get()
                    .addOnCompleteListener(task -> {
                        runOnUiThread(() -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    fullname.setText(document.getString("username"));
                                    email.setText(document.getString("email"));
                                    phonenumber.setText(document.getString("phoneNumber"));
                                    address.setText(document.getString("address"));
                                    city.setText(document.getString("city"));
                                    dateButton.setText(document.getString("birthday"));
                                    emailDisplay.setText(document.getString("email"));

                                    user.setAvatarURL(document.getString("avatarURL"));
                                    if (user.getAvatarURL() != null && !user.getAvatarURL().isEmpty()) {
                                        Picasso.get().load(user.getAvatarURL()).into(imgAvatar);
                                    } else {
                                        user.setAvatarURL("");
                                        Picasso.get().load(MOCK_AVATAR).into(imgAvatar);
                                    }
                                }
                            } else {
                                Toast.makeText(ProfileManagement.this, "Không thể lấy thông tin", Toast.LENGTH_LONG).show();
                                Picasso.get().load(MOCK_AVATAR).into(imgAvatar);
                            }
                            if (email.getText().toString().isEmpty()) email.setEnabled(true);
                        });
                    });
        }).start();
    }
}