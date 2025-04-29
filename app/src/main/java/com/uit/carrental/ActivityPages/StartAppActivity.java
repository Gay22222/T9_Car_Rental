package com.uit.carrental.ActivityPages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.uit.carrental.Model.User;
import com.uit.carrental.Model.UserClient;
import com.uit.carrental.R;
import com.uit.carrental.Service.UserAuthentication.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class StartAppActivity extends AppCompatActivity {

    private Button btn_startApp;
    private ImageSlider imageSlider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);

        imageSlider = findViewById(R.id.imageView3);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.start_app_background, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.start_app_background_1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.start_app_background_2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.start_app_background_3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.start_app_background_4, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels);

        btn_startApp = findViewById(R.id.btn_startApp);
        btn_startApp.setOnClickListener(view -> nextActivity());
    }

    private void nextActivity() {
        overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Users").document(currentUser.getUid());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    User user = task.getResult().toObject(User.class);
                    ((UserClient) getApplicationContext()).setUser(user);

                    if (user != null && user.getRole() != null) {
                        if (user.getRole().equalsIgnoreCase("customer")) {
                            startActivity(new Intent(StartAppActivity.this, CustomerMainActivity.class));
                        } else if (user.getRole().equalsIgnoreCase("owner")) {
                            startActivity(new Intent(StartAppActivity.this, OwnerMainActivity.class));
                        } else {
                            startActivity(new Intent(StartAppActivity.this, LoginActivity.class));
                        }
                    } else {
                        startActivity(new Intent(StartAppActivity.this, LoginActivity.class));
                    }
                } else {
                    startActivity(new Intent(StartAppActivity.this, LoginActivity.class));
                }
                finish();
            });
        }
    }
}
