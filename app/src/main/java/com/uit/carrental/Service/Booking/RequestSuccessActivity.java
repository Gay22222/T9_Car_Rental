package com.uit.carrental.Service.Booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.uit.carrental.ActivityPages.CustomerMainActivity;
import com.uit.carrental.R;

public class RequestSuccessActivity extends AppCompatActivity {
    private Button btn_home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_finished);

        init();
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestSuccessActivity.this, CustomerMainActivity.class);
                startActivity(intent);
            }
        });


    }
    private void init()
    {
        btn_home = findViewById(R.id.btn_noti_Home);
    }
}
