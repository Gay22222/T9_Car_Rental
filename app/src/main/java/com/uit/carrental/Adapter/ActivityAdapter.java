//package com.uit.carrental.Adapter;
//
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.uit.carrental.FragmentPages.Customer.CustomerActivityFragment;
//import com.uit.carrental.Model.User;
//import com.uit.carrental.R;
//import com.uit.carrental.Service.Activity.CustomerActivityDetail;
//
//import java.util.ArrayList;
//
//public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.MyViewHolder> {
//
//    private final CustomerActivityFragment customerActivityFragment;
//    private Activity noti;
//    private final ArrayList<Activity> mNoti;
//    private final FirebaseFirestore dtb;
//    private String name, ownerId; // Thay ProvideID bằng ownerId
//
//    public ActivityAdapter(CustomerActivityFragment mContext, ArrayList<Activity> mNoti) {
//        this.customerActivityFragment = mContext;
//        this.mNoti = mNoti;
//        this.dtb = FirebaseFirestore.getInstance();
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(customerActivityFragment.getActivity()).inflate(R.layout.item_activity, parent, false);
//        return new MyViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        noti = mNoti.get(position);
//        ownerId = noti.getOwnerId(); // Thay getProvider_id bằng getOwnerId
//        getUser(ownerId); // Thay ProvideID bằng ownerId
//        holder.name.setText(name != null ? name : "Đang tải...");
//        holder.id.setText(noti.getNoti_id());
//
//        // Hiển thị trạng thái
//        switch (noti.getStatus()) {
//            case "Dang cho":
//                holder.status.setText("Nhà cung cấp chưa xác nhận");
//                break;
//            case "Thanh toan":
//                holder.status.setText("Đang chờ thanh toán");
//                break;
//            case "Khong xac nhan":
//                holder.status.setText("Nhà cung cấp không xác nhận");
//                break;
//            case "Da thanh toan":
//                holder.status.setText("Đã thanh toán");
//                break;
//            default:
//                holder.status.setText("Đã xác nhận");
//                break;
//        }
//
//        // Sự kiện nhấn item
//        holder.itemView.setOnClickListener(view -> {
//            Intent intent = new Intent(customerActivityFragment.getActivity(), CustomerActivityDetail.class);
//            intent.putExtra("NotiID", noti.getNoti_id());
//            customerActivityFragment.startActivity(intent);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mNoti.size();
//    }
//
//    public static class MyViewHolder extends RecyclerView.ViewHolder {
//        TextView name, status, id;
//
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            name = itemView.findViewById(R.id.tv_noti_name);
//            status = itemView.findViewById(R.id.tv_Status);
//            id = itemView.findViewById(R.id.tv_noti_ID);
//        }
//    }
//
//    private void getUser(String ownerId) { // Thay ProvideID bằng ownerId
//        dtb.collection("Users")
//                .whereEqualTo("user_id", ownerId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            User user = new User();
//                            user.setUserId(document.getString("user_id"));
//                            user.setUsername(document.getString("username"));
//                            user.setEmail(document.getString("email"));
//                            user.setPhoneNumber(document.getString("phoneNumber"));
//                            name = user.getUsername();
//                            notifyDataSetChanged(); // Cập nhật giao diện khi có tên
//                        }
//                    }
//                });
//    }
//}