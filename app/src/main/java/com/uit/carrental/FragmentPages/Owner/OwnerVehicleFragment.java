package com.uit.carrental.FragmentPages.Owner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uit.carrental.Adapter.OwnerVehicleAdapter;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.uit.carrental.Service.Vehicle.AddVehicleActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OwnerVehicleFragment extends Fragment {

    private static final String TAG = "OwnerVehicleFragment";
    private RecyclerView recyclerView;
    private OwnerVehicleAdapter adapter;
    private ArrayList<Vehicle> vehicleList;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Button buttonAddVehicle;
    private TextView textViewEmpty;
    private View rootView;

    private static final int REQUEST_ADD_VEHICLE = 100;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.owner_fragment_vehicle, container, false);

        // Initialize views
        buttonAddVehicle = rootView.findViewById(R.id.btn_add);
        textViewEmpty = rootView.findViewById(R.id.text_empty);
        recyclerView = rootView.findViewById(R.id.vehicle_list);
        recyclerView.setHasFixedSize(false); // Tắt fixed size để hỗ trợ thay đổi số item
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firebase and ProgressDialog
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Đang lấy dữ liệu...");

        // Initialize adapter
        vehicleList = new ArrayList<>();
        adapter = new OwnerVehicleAdapter(requireContext(), vehicleList);
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "Khởi tạo RecyclerView và adapter");

        // Set button listener
        buttonAddVehicle.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddVehicleActivity.class);
            startActivityForResult(intent, REQUEST_ADD_VEHICLE);
        });

        // Load data
        loadVehicles();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_VEHICLE && resultCode == Activity.RESULT_OK) {
            loadVehicles();
            Log.d(TAG, "Nhận kết quả từ AddVehicleActivity, làm mới dữ liệu");
        }
    }

    private void loadVehicles() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getContext(), "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            updateEmptyState();
            return;
        }

        String ownerId = firebaseUser.getUid();
        Log.d(TAG, "UID người dùng: " + ownerId);

        progressDialog.show();
        firestore.collection("Vehicles")
                .addSnapshotListener((snapshots, e) -> {
                    progressDialog.dismiss();
                    if (e != null) {
                        Log.e(TAG, "Lỗi tải xe: " + e.getMessage());
                        Toast.makeText(getContext(), "Không thể lấy danh sách xe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        updateEmptyState();
                        return;
                    }

                    vehicleList.clear();
                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : snapshots) {
                            Vehicle vehicle = document.toObject(Vehicle.class);
                            if (vehicle.getVehicleId() == null) {
                                vehicle.setVehicleId(document.getId());
                            }
                            if (ownerId.equals(vehicle.getOwnerId())) {
                                vehicleList.add(vehicle);
                                Log.d(TAG, "Tải xe: " + vehicle.toString());
                            }
                        }
                        Collections.sort(vehicleList, new Comparator<Vehicle>() {
                            @Override
                            public int compare(Vehicle v1, Vehicle v2) {
                                if (v1.getCreatedAt() == null || v2.getCreatedAt() == null) {
                                    return 0;
                                }
                                return v2.getCreatedAt().compareTo(v1.getCreatedAt());
                            }
                        });
                        Log.d(TAG, "Số xe tải được: " + vehicleList.size());
                    } else {
                        Log.d(TAG, "Không có xe nào trong Vehicles.");
                    }
                    adapter.updateData(new ArrayList<>(vehicleList)); // Tạo bản sao để tránh tham chiếu
                    recyclerView.invalidate(); // Buộc vẽ lại RecyclerView
                    updateEmptyState();
                    Log.d(TAG, "Cập nhật giao diện, RecyclerView visibility: " + recyclerView.getVisibility());
                });
    }

    private void updateEmptyState() {
        Log.d(TAG, "Cập nhật trạng thái rỗng, số xe: " + vehicleList.size());
        if (vehicleList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText(R.string.no_vehicles);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }
    }
}