package com.uit.carrental.Service.Booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.uit.carrental.R;


public class CarDetailActivity extends AppCompatActivity {
    Button btnBook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_car);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        overridePendingTransition(R.anim.anim_in_right,R.anim.anim_out_left);

        btnBook =findViewById(R.id.btn_book);
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it1=new Intent(CarDetailActivity.this, ScheduleSelect.class);
            }
        });
    }

}
