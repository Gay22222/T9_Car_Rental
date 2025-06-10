package com.uit.carrental.FragmentPages.Admin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import java.util.ArrayList;
import java.util.List;

public class AdminStatisticsFragment extends Fragment {

    private TextView textViewTotalUsers, textViewTotalOwners, textViewTotalRevenue, textViewMonthlyRevenue;
    private LinearLayout layoutUserStats;
    private RelativeLayout layoutRevenueStats; // Sửa từ LinearLayout thành RelativeLayout
    private RecyclerView recyclerViewTopVehicles;
    private TopVehicleAdapter topVehicleAdapter;
    private FirebaseFirestore db;
    private List<VehicleStat> topVehicles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_statistics, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        textViewTotalUsers = view.findViewById(R.id.rp42wc0fjdv);
        textViewTotalOwners = view.findViewById(R.id.rtcscqww51js);
        textViewTotalRevenue = view.findViewById(R.id.r26rof30hetx);
        textViewMonthlyRevenue = view.findViewById(R.id.rk10ooehcyo);
        layoutUserStats = view.findViewById(R.id.r4bw82dxnrcg);
        layoutRevenueStats = view.findViewById(R.id.r15sr6ipgnjs_rl); // Sửa ánh xạ
        recyclerViewTopVehicles = view.findViewById(R.id.rutuu0f5fk3);

        // Setup RecyclerView
        recyclerViewTopVehicles.setLayoutManager(new LinearLayoutManager(getContext()));
        topVehicles = new ArrayList<>();
        topVehicleAdapter = new TopVehicleAdapter(topVehicles);
        recyclerViewTopVehicles.setAdapter(topVehicleAdapter);

        // Load static images
        loadStaticImages(view);

        // Load statistics
        loadStatistics();

        // Setup click listeners
        layoutUserStats.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Xem chi tiết thống kê người dùng", Toast.LENGTH_SHORT).show();
        });
        layoutRevenueStats.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Xem chi tiết thống kê doanh thu", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadStaticImages(View view) {
        Context context = getContext();
        if (context != null) {
            Glide.with(context)
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/x074yc50_expires_30_days.png")
                    .into((ImageView) view.findViewById(R.id.r3qtlrf08vac));
            Glide.with(context)
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/l5omzz94_expires_30_days.png")
                    .into((ImageView) view.findViewById(R.id.rerhjwf5h2qm));
            Glide.with(context)
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/f1rhppxs_expires_30_days.png")
                    .into((ImageView) view.findViewById(R.id.r15sr6ipgnjs));
        }
    }

    private void loadStatistics() {
        // Tổng số người dùng
        db.collection("Users").get().addOnSuccessListener(querySnapshot -> {
            textViewTotalUsers.setText(String.format("%,d", querySnapshot.size()));
        }).addOnFailureListener(e -> loadMockData());

        // Tổng số chủ xe
        db.collection("Users")
                .whereEqualTo("roles.owner", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    textViewTotalOwners.setText(String.format("%,d", querySnapshot.size()));
                });

        // Doanh thu tổng
        db.collection("Orders").get().addOnSuccessListener(querySnapshot -> {
            long totalRevenue = 0;
            for (QueryDocumentSnapshot document : querySnapshot) {
                Long revenue = document.getLong("totalAmount");
                if (revenue != null) totalRevenue += revenue;
            }
            textViewTotalRevenue.setText(String.format("%,dM", totalRevenue / 1_000_000));
        });

        // Doanh thu tháng này
        long oneMonthAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000;
        db.collection("Orders")
                .whereGreaterThan("timestamp", oneMonthAgo)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    long monthlyRevenue = 0;
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Long revenue = document.getLong("totalAmount");
                        if (revenue != null) monthlyRevenue += revenue;
                    }
                    textViewMonthlyRevenue.setText(String.format("%,d", monthlyRevenue));
                });

        // Top 5 xe được thuê
        db.collection("Orders")
                .whereGreaterThan("timestamp", oneMonthAgo)
                .orderBy("vehicleId", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    topVehicles.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String vehicleId = document.getString("vehicleId");
                        long totalOrders = document.getLong("totalOrders") != null ? document.getLong("totalOrders") : 1;
                        long revenue = document.getLong("totalAmount") != null ? document.getLong("totalAmount") : 0;
                        db.collection("Vehicles").document(vehicleId).get().addOnSuccessListener(vehicleDoc -> {
                            if (vehicleDoc.exists()) {
                                Vehicle vehicle = vehicleDoc.toObject(Vehicle.class);
                                if (vehicle != null) {
                                    topVehicles.add(new VehicleStat(
                                            vehicle.getVehicleName(),
                                            vehicle.getVehicleNumber(),
                                            totalOrders,
                                            revenue
                                    ));
                                    topVehicleAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });
    }

    private void loadMockData() {
        textViewTotalUsers.setText("25,000");
        textViewTotalOwners.setText("1,000");
        textViewTotalRevenue.setText("900M");
        textViewMonthlyRevenue.setText("90,000,000");
        topVehicles.clear();
        for (int i = 1; i <= 5; i++) {
            topVehicles.add(new VehicleStat(
                    "Vinfast Lux A2.0 " + i,
                    "CS254" + i,
                    187L,
                    8_522_000L
            ));
        }
        topVehicleAdapter.notifyDataSetChanged();
    }

    private static class TopVehicleAdapter extends RecyclerView.Adapter<TopVehicleAdapter.ViewHolder> {
        private final List<VehicleStat> vehicles;

        TopVehicleAdapter(List<VehicleStat> vehicles) {
            this.vehicles = vehicles;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_stat_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            VehicleStat stat = vehicles.get(position);
            holder.textViewName.setText(stat.name);
            holder.textViewNumber.setText(stat.vehicleNumber);
            holder.textViewTotalOrders.setText(String.valueOf(stat.totalOrders));
            holder.textViewRevenue.setText(String.format("%,d", stat.revenue));
            holder.itemView.setBackgroundColor(position % 2 == 0 ? Color.parseColor("#C9F3FF") : Color.WHITE);
        }

        @Override
        public int getItemCount() {
            return vehicles.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName, textViewNumber, textViewTotalOrders, textViewRevenue;

            ViewHolder(View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.vehicle_name);
                textViewNumber = itemView.findViewById(R.id.vehicle_number);
                textViewTotalOrders = itemView.findViewById(R.id.total_orders);
                textViewRevenue = itemView.findViewById(R.id.revenue);
            }
        }
    }

    private static class VehicleStat {
        String name;
        String vehicleNumber;
        long totalOrders;
        long revenue;

        VehicleStat(String name, String vehicleNumber, long totalOrders, long revenue) {
            this.name = name;
            this.vehicleNumber = vehicleNumber;
            this.totalOrders = totalOrders;
            this.revenue = revenue;
        }
    }

    public static class SimpleBarChart extends View {
        private final Paint paint;
        private float[] values = {800, 2000, 600, 1200, 400, 3000, 100};

        public SimpleBarChart(Context context) {
            super(context);
            paint = new Paint();
            paint.setAntiAlias(true);
        }

        public SimpleBarChart(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            paint = new Paint();
            paint.setAntiAlias(true);
        }

        public SimpleBarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            paint = new Paint();
            paint.setAntiAlias(true);
        }

        public void setValues(float[] newValues) {
            this.values = newValues;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float width = getWidth();
            float height = getHeight();
            float barWidth = width / 10f;
            float maxHeight = height * 0.8f;

            int[] colors = {Color.parseColor("#3DD34C"), Color.parseColor("#6D62F7")};

            for (int i = 0; i < values.length; i++) {
                paint.setColor(colors[i % 2]);
                float left = i * barWidth * 1.2f + barWidth * 0.1f;
                float top = height - (values[i] / 3000 * maxHeight);
                float right = left + barWidth;
                float bottom = height;
                canvas.drawRect(left, top, right, bottom, paint);
            }
        }
    }
}
