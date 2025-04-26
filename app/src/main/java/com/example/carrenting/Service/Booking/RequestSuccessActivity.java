//package com.example.carrenting.Service.Booking;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.carrenting.ActivityPages.CustomerMainActivity;
//import com.example.carrenting.R;
//
//public class RequestSuccessActivity extends AppCompatActivity {
//    private Button btn_home;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_finished);
//
//        init();
//        btn_home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(RequestSuccessActivity.this, CustomerMainActivity.class);
//                startActivity(intent);
//            }
//        });
//
//
//    }
//    private void init()
//    {
//        btn_home = findViewById(R.id.btn_noti_Home);
//    }
//}
package com.example.carrenting.Service.Booking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carrenting.ActivityPages.CustomerMainActivity;
import com.example.carrenting.R;

public class RequestSuccessActivity extends AppCompatActivity {

    TextView tvInfCar, tvTraloi, tvThongBao;
    Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished);

        tvInfCar = findViewById(R.id.tv_InfCar);
        tvTraloi = findViewById(R.id.tv_Traloi);
        tvThongBao = findViewById(R.id.tv_ThongBao);
        btnHome = findViewById(R.id.btn_noti_Home);

        // Fake data hiển thị tĩnh theo XML đã có
        tvInfCar.setText("Gửi yêu cầu thành công!");
        tvTraloi.setText("Thường Nhà cung cấp sẽ phản hồi từ 1 đến 2 ngày.");
        tvThongBao.setText("Hãy thường xuyên kiểm tra thông báo để tiếp tục giao dịch.");

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(RequestSuccessActivity.this, CustomerMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
