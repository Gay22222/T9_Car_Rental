package com.example.carrenting.FragmentPages.Customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrenting.Adapter.NotificationAdapter;
import com.example.carrenting.R;

import java.util.ArrayList;

public class CustomerNotificationFragment extends Fragment {

    RecyclerView recyclerView;
    NotificationAdapter adapter;
    ArrayList<String> notiList;
    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.customer_fragment_notification, container, false);

        recyclerView = mView.findViewById(R.id.frame_layout_noti);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        notiList = new ArrayList<>();
        notiList.add("Bạn có một yêu cầu mới!");
        notiList.add("Yêu cầu của bạn đã được xác nhận.");
        notiList.add("Xe đã sẵn sàng để nhận.");
        notiList.add("Cảm ơn bạn đã sử dụng dịch vụ!");
        notiList.add("Yêu cầu bị từ chối, vui lòng thử lại.");

        adapter = new NotificationAdapter(notiList);
        recyclerView.setAdapter(adapter);

        return mView;
    }

}
