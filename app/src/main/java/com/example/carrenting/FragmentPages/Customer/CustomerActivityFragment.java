//package com.example.carrenting.FragmentPages.Customer;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.carrenting.Adapter.ActivityAdapter;
//import com.example.carrenting.Model.Activity;
//import com.example.carrenting.R;
//
//import java.util.ArrayList;
//
//public class CustomerActivityFragment extends Fragment {
//
//    RecyclerView recyclerView;
//    ActivityAdapter activityAdapter;
//    ArrayList<Activity> activities;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.customer_fragment_activity, container, false);
//
//        recyclerView = view.findViewById(R.id.activity_list);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        // Tạo danh sách rỗng để hiển thị UI
//        activities = new ArrayList<>();
//        activityAdapter = new ActivityAdapter(CustomerActivityFragment.this, activities);
//        recyclerView.setAdapter(activityAdapter);
//
//        // Bạn có thể thêm dữ liệu demo nếu muốn
//        return view;
//    }
//}

package com.example.carrenting.FragmentPages.Customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrenting.Adapter.ActivityAdapter;
import com.example.carrenting.Model.Activity;
import com.example.carrenting.R;

import java.util.ArrayList;

public class CustomerActivityFragment extends Fragment {

    RecyclerView recyclerView;
    ActivityAdapter activityAdapter;
    ArrayList<Activity> activities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_activity, container, false);

        recyclerView = view.findViewById(R.id.activity_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        activities = new ArrayList<>();

        // ✅ Dữ liệu giả (fake data)
        activities.add(new Activity("1234", "07:30 AM - Jan 1, 2025", "05:00 PM - Jan 1, 2025", "Nhà cung cấp đã xác nhận", "Nguyễn Văn A"));
        activities.add(new Activity("5678", "08:00 AM - Jan 10, 2025", "04:00 PM - Jan 15, 2025", "Chờ xác nhận", "Trương Đăng Khôi"));
        activities.add(new Activity("9101", "09:00 AM - Feb 5, 2025", "06:00 PM - Feb 6, 2025", "Yêu cầu đã bị từ chối", "Lê Văn B"));
        activities.add(new Activity("1213", "01:00 PM - Mar 3, 2025", "01:00 PM - Mar 5, 2025", "Xe đã được giao", "Nguyễn Thị C"));

        activityAdapter = new ActivityAdapter(CustomerActivityFragment.this, activities);
        recyclerView.setAdapter(activityAdapter);

        return view;
    }
}

