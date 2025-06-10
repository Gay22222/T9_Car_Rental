package com.uit.carrental.FragmentPages.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.carrental.Adapter.VehicleAdapter;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminVehicleListFragment extends Fragment {

    private static final String TAG = "AdminVehicleList";
    private RecyclerView recyclerViewVehicles;
    private TextView textViewVehicleCount;
    private LinearLayout buttonSearch;
    private ImageView imageViewFilter, imageView_search;
    private EditText editTextSearch;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;
    private MutableLiveData<List<Vehicle>> filteredVehicleList;
    private FirebaseFirestore dbVehicle;
    private MutableLiveData<String> currentQuery;
    private String currentBrand = null;
    private String currentStatus = null;
    private String currentVerificationStatus = null;
    private Float currentMinRating = 0.0f;
    private Float currentMaxRating = 5.0f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_vehicle_list, container, false);

        // Initialize Firebase
        dbVehicle = FirebaseFirestore.getInstance();

        // Initialize LiveData
        vehicleList = new ArrayList<>();
        filteredVehicleList = new MutableLiveData<>(new ArrayList<>());
        currentQuery = new MutableLiveData<>("");

        // Initialize views
        recyclerViewVehicles = view.findViewById(R.id.recycler_view_vehicles);
        textViewVehicleCount = view.findViewById(R.id.tv_vehicle_count);
        buttonSearch = view.findViewById(R.id.rmhryoy6hglh);
        imageViewFilter = view.findViewById(R.id.filter_button);
        imageView_search = view.findViewById(R.id.search_button);
        editTextSearch = view.findViewById(R.id.search_input);

        // Setup RecyclerView
        recyclerViewVehicles.setLayoutManager(new LinearLayoutManager(getContext()));
        vehicleAdapter = new VehicleAdapter(this, new ArrayList<>(), position -> {});
        recyclerViewVehicles.setAdapter(vehicleAdapter);

        // Observe filtered list
        filteredVehicleList.observe(getViewLifecycleOwner(), vehicles -> {
            if (vehicles == null || vehicles.isEmpty()) {
                Log.d(TAG, "Danh sách xe rỗng sau khi lọc");
                vehicleAdapter.updateData(new ArrayList<>());
                textViewVehicleCount.setText(getString(R.string.total_vehicles, 0));

            } else {
                vehicleAdapter.updateData(new ArrayList<>(vehicles));
                textViewVehicleCount.setText(getString(R.string.total_vehicles, vehicles.size()));
            }
            Log.d(TAG, "Cập nhật RecyclerView với " + (vehicles != null ? vehicles.size() : 0) + " xe");
        });

        // Setup search
        setupSearch();

        // Setup filter
        setupFilter();

        // Load vehicles
        loadVehicles();

        return view;
    }

    private void setupSearch() {
        buttonSearch.setOnClickListener(v -> performSearch());
        imageView_search.setOnClickListener(v -> performSearch());

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentQuery.setValue(s.toString().trim().toLowerCase());
            }
        });

        currentQuery.observe(getViewLifecycleOwner(), query -> {
            if (!vehicleList.isEmpty()) {
                applyFilter();
            }
        });
    }

    private void setupFilter() {
        imageViewFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void loadVehicles() {
        dbVehicle.collection("Vehicles")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    vehicleList.clear();
                    for (DocumentSnapshot document : querySnapshot) {
                        try {
                            Vehicle vehicle = document.toObject(Vehicle.class);
                            if (vehicle != null && vehicle.getVehicleId() != null) {
                                vehicleList.add(vehicle);

                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi ánh xạ tài liệu: " + document.getId(), e);
                        }
                    }
                    applyFilter();
                    Log.d(TAG, "Tải " + vehicleList.size() + " xe từ Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải dữ liệu: " + e.getMessage());
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadMockData();
                });
    }

    private void loadMockData() {
        vehicleList.clear();
        String[] brands = {"Vinfast", "Honda", "Mercedes", "BMW", "Ford", "Toyota"};
        String[] statuses = {"renting", "available", "maintenance", "pending"};
        String[] verificationStatuses = {"verified", "pending"};
        for (int i = 1; i <= 15; i++) {
            Vehicle mockVehicle = new Vehicle();
            mockVehicle.setVehicleId("mock_vehicle_" + i);
            mockVehicle.setVehicleName("Xe mẫu " + i);
            mockVehicle.setVehiclePrice("500.000 VNĐ/Ngày");
            mockVehicle.setVehicleImageUrl("https://example.com/car.jpg");
            mockVehicle.setOwnerId("mock_user_" + i);
            mockVehicle.setVehicleNumber("ABC" + (1000 + i));
            mockVehicle.setVehicleAvailability(statuses[i % 4]);
            mockVehicle.setVehicleBrand(brands[i % 6]);
            mockVehicle.setVehicleRating((float) (3 + (i % 3)));
            mockVehicle.setVerificationStatus(verificationStatuses[i % 2]);

            vehicleList.add(mockVehicle);
        }
        applyFilter();
        Log.d(TAG, "Tải dữ liệu giả: " + vehicleList.size() + " xe");
    }

    private void performSearch() {
        currentQuery.setValue(editTextSearch.getText().toString().trim().toLowerCase());
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_vehicle_filter, null);
        builder.setView(dialogView);

        // Initialize dialog views
        Spinner spinnerBrand = dialogView.findViewById(R.id.spinner_brand);
        Spinner spinnerAvailability = dialogView.findViewById(R.id.spinner_availability);
        Spinner spinnerVerification = dialogView.findViewById(R.id.spinner_verification);
        TextView tvFuelTypeLabel = dialogView.findViewById(R.id.tv_fuel_type_label);
        Spinner spinnerFuelType = dialogView.findViewById(R.id.spinner_fuel_type);
        TextView tvPriceLabel = dialogView.findViewById(R.id.tv_price_label);
        RangeSlider sliderPrice = dialogView.findViewById(R.id.slider_price);
        RangeSlider sliderRating = dialogView.findViewById(R.id.slider_rating);
        Button btnApply = dialogView.findViewById(R.id.btn_apply_filter);
        Button btnClear = dialogView.findViewById(R.id.btn_clear_filter);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        // Hide Customer-specific fields
        tvFuelTypeLabel.setVisibility(View.GONE);
        spinnerFuelType.setVisibility(View.GONE);
        tvPriceLabel.setVisibility(View.GONE);
        sliderPrice.setVisibility(View.GONE);

        // Setup spinners
        List<String> brands = new ArrayList<>(Arrays.asList(getString(R.string.all_brands), "Vinfast", "Honda", "Mercedes", "BMW", "Ford", "Toyota"));
        List<String> availabilities = new ArrayList<>(Arrays.asList(getString(R.string.all_availability), "renting", "available", "maintenance", "pending"));
        List<String> verifications = new ArrayList<>(Arrays.asList(getString(R.string.all_verification), "verified", "pending"));
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, brands);
        ArrayAdapter<String> availabilityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, availabilities);
        ArrayAdapter<String> verificationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, verifications);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        verificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(brandAdapter);
        spinnerAvailability.setAdapter(availabilityAdapter);
        spinnerVerification.setAdapter(verificationAdapter);

        // Set default filter values
        spinnerBrand.setSelection(currentBrand != null ? brands.indexOf(currentBrand) : 0);
        spinnerAvailability.setSelection(currentStatus != null ? availabilities.indexOf(currentStatus) : 0);
        spinnerVerification.setSelection(currentVerificationStatus != null ? verifications.indexOf(currentVerificationStatus) : 0);
        sliderRating.setValues(
                currentMinRating != null ? currentMinRating : 0.0f,
                currentMaxRating != null ? currentMaxRating : 5.0f
        );

        AlertDialog dialog = builder.create();

        btnApply.setOnClickListener(v -> {
            currentBrand = spinnerBrand.getSelectedItem().toString().equals(getString(R.string.all_brands)) ? null : spinnerBrand.getSelectedItem().toString();
            currentStatus = spinnerAvailability.getSelectedItem().toString().equals(getString(R.string.all_availability)) ? null : spinnerAvailability.getSelectedItem().toString();
            currentVerificationStatus = spinnerVerification.getSelectedItem().toString().equals(getString(R.string.all_verification)) ? null : spinnerVerification.getSelectedItem().toString();
            List<Float> ratingValues = sliderRating.getValues();
            currentMinRating = ratingValues.isEmpty() ? 0.0f : ratingValues.get(0);
            currentMaxRating = ratingValues.size() < 2 ? 5.0f : ratingValues.get(1);
            applyFilter();
            dialog.dismiss();
        });

        btnClear.setOnClickListener(v -> {
            currentBrand = null;
            currentStatus = null;
            currentVerificationStatus = null;
            currentMinRating = 0.0f;
            currentMaxRating = 5.0f;
            spinnerBrand.setSelection(0);
            spinnerAvailability.setSelection(0);
            spinnerVerification.setSelection(0);
            sliderRating.setValues(0.0f, 5.0f);
            applyFilter();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void applyFilter() {
        String query = currentQuery.getValue() != null ? currentQuery.getValue() : "";
        List<String> keywords = Arrays.asList(query.split("\\s+"));
        List<Vehicle> filteredList = new ArrayList<>();

        for (Vehicle vehicle : vehicleList) {
            boolean matches = true;

            // Tìm kiếm theo từ khóa
            if (!query.isEmpty()) {
                boolean queryMatch = false;
                String name = vehicle.getVehicleName() != null ? vehicle.getVehicleName().toLowerCase() : "";
                String number = vehicle.getVehicleNumber() != null ? vehicle.getVehicleNumber().toLowerCase() : "";
                for (String keyword : keywords) {
                    if (!keyword.isEmpty()) {
                        queryMatch |= name.contains(keyword) || number.contains(keyword);
                    }
                }
                matches &= queryMatch;
            }

            // Lọc theo thương hiệu
            if (currentBrand != null) {
                matches &= vehicle.getVehicleBrand() != null && vehicle.getVehicleBrand().equalsIgnoreCase(currentBrand);
            }

            // Lọc theo trạng thái sẵn sàng
            if (currentStatus != null) {
                matches &= vehicle.getVehicleAvailability() != null && vehicle.getVehicleAvailability().equalsIgnoreCase(currentStatus);
            }

            // Lọc theo trạng thái xác minh
            if (currentVerificationStatus != null) {
                matches &= vehicle.getVerificationStatus() != null && vehicle.getVerificationStatus().equalsIgnoreCase(currentVerificationStatus);
            }

            // Lọc theo điểm đánh giá
            Float rating = vehicle.getVehicleRating();
            float finalRating = rating != null ? rating : 0.0f;
            matches &= finalRating >= currentMinRating && finalRating <= currentMaxRating;

            if (matches) {
                filteredList.add(vehicle);
            }
        }

        filteredVehicleList.setValue(filteredList);
        Log.d(TAG, "Áp dụng bộ lọc, kết quả: " + filteredList.size() + " xe");
    }
}