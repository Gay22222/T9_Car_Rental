//package com.example.carrenting.FragmentPages.Owner;
//
//import android.app.ProgressDialog;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.example.carrenting.Adapter.OwnerActivityAdapter;
//import com.example.carrenting.Model.Activity;
//import com.example.carrenting.R;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.ArrayList;
//
//public class OwnerActivityFragment extends Fragment {
//
//    RecyclerView recyclerView;
//    OwnerActivityAdapter ownerActivityAdapter;
//    ArrayList<Activity> activities;
//    FirebaseFirestore dtb_noti;
//    ProgressDialog progressDialog;
//    String current_user_id;
//    StorageReference storageReference;
//    FirebaseAuth firebaseAuth;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.customer_fragment_activity, container, false);
//        recyclerView = view.findViewById(R.id.activity_list);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Đang lấy dữ liệu...");
//        progressDialog.show();
//
//        storageReference = FirebaseStorage.getInstance().getReference();
//        dtb_noti = FirebaseFirestore.getInstance();
//        firebaseAuth = FirebaseAuth.getInstance();
//        current_user_id = firebaseAuth.getCurrentUser().getUid();
//
//        activities = new ArrayList<Activity>();
//        ownerActivityAdapter = new OwnerActivityAdapter(OwnerActivityFragment.this, activities);
//        recyclerView.setAdapter(ownerActivityAdapter);
//
//        EventChangeListener();
//
//        progressDialog.dismiss();
//        return view;
//    }
//    private void EventChangeListener()
//    {
//
//        dtb_noti.collection("Notification")
//                .whereEqualTo("provider_id", current_user_id)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Activity temp = new Activity();
//                                temp.setNoti_id(document.get("noti_id").toString());
//                                temp.setProvider_id(document.get("provider_id").toString());
//                                temp.setCustomer_id(document.get("customer_id").toString());
//                                temp.setStatus(document.get("status").toString());
//                                temp.setVehicle_id(document.get("vehicle_id").toString());
//                                activities.add(temp);
//                                ownerActivityAdapter.notifyDataSetChanged();
//                            }
//                        } else {
//                            Toast.makeText(getContext(), "Không thể lấy thông tin đơn hàng ", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//    }
//}
package com.example.carrenting.FragmentPages.Owner;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrenting.R;

import java.util.ArrayList;
import java.util.List;

public class OwnerActivityFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private List<ActivityItem> activityItems = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.owner_fragment_activity, container, false);

        recyclerView = view.findViewById(R.id.activity_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ActivityAdapter(getContext(), activityItems);
        recyclerView.setAdapter(adapter);

        loadFakeData();

        return view;
    }

    private void loadFakeData() {
        activityItems.add(new ActivityItem("Nguyễn Văn A", "A001", "08:00 a.m January 15, 2025", "10:00 a.m January 16, 2025", "Đã xác nhận"));
        activityItems.add(new ActivityItem("Trần Thị B", "B002", "09:15 a.m February 2, 2025", "11:45 a.m February 5, 2025", "Chờ xác nhận"));
        activityItems.add(new ActivityItem("Lê Văn C", "C003", "02:00 p.m March 10, 2025", "04:00 p.m March 11, 2025", "Đã huỷ"));
        adapter.notifyDataSetChanged();
    }

    public static class ActivityItem {
        String name, id, pickup, returnDate, status;

        public ActivityItem(String name, String id, String pickup, String returnDate, String status) {
            this.name = name;
            this.id = id;
            this.pickup = pickup;
            this.returnDate = returnDate;
            this.status = status;
        }
    }

    public static class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
        private final Context context;
        private final List<ActivityItem> list;

        public ActivityAdapter(Context context, List<ActivityItem> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_activity, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ActivityItem item = list.get(position);
            holder.tvName.setText(item.name);
            holder.tvID.setText(item.id);
            holder.tvPickup.setText(item.pickup);
            holder.tvReturn.setText(item.returnDate);
            holder.tvStatus.setText(item.status);
            holder.btnDetail.setOnClickListener(v -> {
                // Bạn có thể mở dialog hoặc activity chi tiết tại đây
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvID, tvPickup, tvReturn, tvStatus;
            Button btnDetail;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_noti_name);
                tvID = itemView.findViewById(R.id.tv_noti_ID);
                tvPickup = itemView.findViewById(R.id.pickupDate);
                tvReturn = itemView.findViewById(R.id.returnDate);
                tvStatus = itemView.findViewById(R.id.tv_Status);
                btnDetail = itemView.findViewById(R.id.btn_Detail);
            }
        }
    }
}
