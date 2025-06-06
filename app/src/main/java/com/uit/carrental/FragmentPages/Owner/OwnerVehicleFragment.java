package com.uit.carrental.FragmentPages.Owner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.uit.carrental.Adapter.OwnerVehicleAdapter;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.Model.onClickInterface;
import com.uit.carrental.R;
import com.uit.carrental.Service.Vehicle.AddVehicleActivity;

import java.util.ArrayList;

public class OwnerVehicleFragment extends Fragment {

    private RecyclerView recyclerView;
    private OwnerVehicleAdapter adapter;
    private ArrayList<Vehicle> vehicles;
    private FirebaseFirestore dtbVehicle;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Button btnAdd;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.owner_fragment_vehicle, container, false);

        // Initialize views
        btnAdd = view.findViewById(R.id.btn_add);
        recyclerView = view.findViewById(R.id.vehicle_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firebase and ProgressDialog
        firebaseAuth = FirebaseAuth.getInstance();
        dtbVehicle = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Đang lấy dữ liệu...");

        // Initialize adapter
        vehicles = new ArrayList<>();
        adapter = new OwnerVehicleAdapter(this, vehicles, position -> {
            // Click handled in adapter
        });
        recyclerView.setAdapter(adapter);

        // Set button listener
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddVehicleActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(0, 0);
            }
        });

        // Load data
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            progressDialog.show();
            loadVehicles(firebaseUser.getUid());
        } else {
            Toast.makeText(getContext(), "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadVehicles(String userId) {
        dtbVehicle.collection("Vehicles")
                .whereEqualTo("provider_id", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            vehicles.clear(); // Clear old data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Vehicle temp = new Vehicle();
                                temp.setVehicle_id(document.getId());
                                temp.setVehicle_name(document.getString("vehicle_name"));
                                temp.setVehicle_price(document.getString("vehicle_price"));
                                temp.setProvider_name(document.getString("provider_name"));
                                temp.setVehicle_imageURL(document.getString("vehicle_imageURL"));
                                temp.setVehicle_rating(document.getString("vehicle_rating"));
                                vehicles.add(temp);
                            }
                            adapter.notifyDataSetChanged();
                            if (vehicles.isEmpty()) {
                                Toast.makeText(getContext(), "Không có xe nào", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Không thể lấy danh sách xe", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}