package com.uit.carrental.FragmentPages.Customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uit.carrental.Adapter.VehicleAdapter;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.Model.onClickInterface;
import com.uit.carrental.R;
import com.uit.carrental.Service.Vehicle.VehicleDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeFragment extends Fragment {

    private RecyclerView brandsList, vehicleList;
    private EditText searchInput;
    private TextView locationText;
    private ImageView avatarImage;
    private ArrayList<Vehicle> vehicles;
    private VehicleAdapter vehicleAdapter;
    private BrandAdapter brandAdapter;
    private FirebaseFirestore dtbVehicle;
    private ProgressDialog progressDialog;
    private onClickInterface clickInterface;
    private View mView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.customer_fragment_home, container, false);

        // Initialize views
        brandsList = mView.findViewById(R.id.brands_list);
        vehicleList = mView.findViewById(R.id.vehicle_list);
        searchInput = mView.findViewById(R.id.search_input);
        locationText = mView.findViewById(R.id.location_text);
        avatarImage = mView.findViewById(R.id.avatar_image);

        // Initialize Firebase and ProgressDialog
        dtbVehicle = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Đang lấy dữ liệu...");

        // Setup click interface for vehicles
        clickInterface = position -> {
            Vehicle clickedVehicle = vehicles.get(position);
            Intent intent = new Intent(getContext(), VehicleDetailActivity.class);
            intent.putExtra("vehicle_id", clickedVehicle.getVehicle_id());
            startActivity(intent);
        };

        // Setup RecyclerViews
        setupBrandsRecyclerView();
        setupVehiclesRecyclerView();

        // Load vehicle data from Firestore
        try {
            EventChangeListener();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return mView;
    }

    private void setupBrandsRecyclerView() {
        brandsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<Brand> brands = new ArrayList<>();
        brands.add(new Brand("BMW", R.drawable.bmw_svg));
        brands.add(new Brand("Vinfast", R.drawable.images_1__main));
        brands.add(new Brand("Mercedes", R.drawable.images_2__main));
        brands.add(new Brand("Honda", R.drawable.images_3__main));
        brandAdapter = new BrandAdapter(brands);
        brandsList.setAdapter(brandAdapter);
    }

    private void setupVehiclesRecyclerView() {
        vehicleList.setLayoutManager(new LinearLayoutManager(getContext()));
        vehicles = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(CustomerHomeFragment.this, vehicles, clickInterface);
        vehicleList.setAdapter(vehicleAdapter);
    }

    private void EventChangeListener() {
        progressDialog.show();
        dtbVehicle.collection("Vehicles")
                .orderBy("vehicle_name", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Vehicle temp = new Vehicle();
                            temp.setVehicle_id(document.getId());
                            temp.setVehicle_name(document.getString("vehicle_name"));
                            temp.setVehicle_price(document.getString("vehicle_price"));
                            temp.setVehicle_imageURL(document.getString("vehicle_imageURL"));
                            temp.setProvider_name(document.getString("provider_name"));
                            // Fake rating 4 sao nếu null
                            temp.setVehicle_rating(document.getString("vehicle_rating") != null ?
                                    document.getString("vehicle_rating") : "4.0 (0 Đánh giá)");
                            vehicles.add(temp);
                            vehicleAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "Không thể lấy thông tin xe", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void LoadImage(String docId, ImageView imageView) {
        if (docId == null) {
            Log.e("LoadImage", "docId is null");
            return;
        }
        DocumentReference imageRef = FirebaseFirestore.getInstance().collection("Image").document(docId);
        imageRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String imageUrl = documentSnapshot.getString("Image");
                if (imageUrl != null) {
                    Glide.with(mView).load(imageUrl).into(imageView);
                }
            }
        }).addOnFailureListener(e -> Log.e("LoadImage", "Error: " + e.getMessage()));
    }

    // Brand model class
    private static class Brand {
        String name;
        int imageResId;

        Brand(String name, int imageResId) {
            this.name = name;
            this.imageResId = imageResId;
        }
    }

    // Brand Adapter
    private static class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {
        private final List<Brand> brands;

        BrandAdapter(List<Brand> brands) {
            this.brands = brands;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Brand brand = brands.get(position);
            holder.brandImage.setImageResource(brand.imageResId);
            holder.brandName.setText(brand.name);
        }

        @Override
        public int getItemCount() {
            return brands.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView brandImage;
            TextView brandName;

            ViewHolder(View itemView) {
                super(itemView);
                brandImage = itemView.findViewById(R.id.brand_image);
                brandName = itemView.findViewById(R.id.brand_name);
            }
        }
    }
}