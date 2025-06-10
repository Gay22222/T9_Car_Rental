package com.uit.carrental.FragmentPages.Customer;

import android.app.AlertDialog;
import android.content.Intent;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.carrental.Adapter.VehicleAdapter;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.uit.carrental.Service.Vehicle.VehicleDetailActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerHomeFragment extends Fragment {

    private static final String TAG = "CustomerHomeFragment";
    private RecyclerView recyclerViewVehicles;
    private LinearLayout buttonSearch;
    private ImageView imageViewFilter, imageViewSearch;
    private EditText editTextSearch;
    private FloatingActionButton fabChat;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;
    private List<Vehicle> filteredVehicleList;
    private FirebaseFirestore db;
    private String currentQuery = "";
    private String currentBrand;
    private String currentFuelType;
    private Float currentMinRating, currentMaxRating;
    private Long currentMinPrice, currentMaxPrice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_home, container, false);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        recyclerViewVehicles = view.findViewById(R.id.recycler_view_vehicles);
        buttonSearch = view.findViewById(R.id.search_container);
        imageViewFilter = view.findViewById(R.id.filter_button);
        imageViewSearch = view.findViewById(R.id.search_button);
        editTextSearch = view.findViewById(R.id.search_input);
        fabChat = view.findViewById(R.id.fab_chat);

        // Setup RecyclerView
        recyclerViewVehicles.setLayoutManager(new LinearLayoutManager(getContext()));
        vehicleList = new ArrayList<>();
        filteredVehicleList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(this, new ArrayList<>(filteredVehicleList), position -> {
            Vehicle clickedVehicle = filteredVehicleList.get(position);
            Intent intent = new Intent(getContext(), VehicleDetailActivity.class);
            intent.putExtra("vehicleId", clickedVehicle.getVehicleId());
            startActivity(intent);
        });
        recyclerViewVehicles.setAdapter(vehicleAdapter);

        // Setup search
        setupSearch();

        // Setup filter
        setupFilter();

        // Setup FloatingActionButton
        fabChat.setOnClickListener(v -> openChatFragment());

        // Load vehicles
        loadVehicles();

        return view;
    }

    private void setupSearch() {
        buttonSearch.setOnClickListener(v -> performSearch());
        imageViewSearch.setOnClickListener(v -> performSearch());

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString().trim().toLowerCase();
                applyFilter();
            }
        });
    }

    private void setupFilter() {
        imageViewFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void loadVehicles() {
        db.collection("Vehicles")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    vehicleList.clear();
                    filteredVehicleList.clear();
                    for (DocumentSnapshot document : querySnapshot) {
                        Vehicle vehicle = document.toObject(Vehicle.class);
                        if (vehicle != null && vehicle.getVehicleId() != null &&
                                "verified".equals(vehicle.getVerificationStatus()) &&
                                "available".equals(vehicle.getVehicleAvailability())) {
                            vehicleList.add(vehicle);
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
        filteredVehicleList.clear();
        String[] brands = {"Vinfast", "Honda", "Mercedes", "BMW", "Ford", "Toyota"};
        String[] fuelTypes = {"Xăng", "Dầu", "Điện"};
        for (int i = 1; i <= 15; i++) {
            Vehicle mockVehicle = new Vehicle();
            mockVehicle.setVehicleId("mock_vehicle_" + i);
            mockVehicle.setVehicleName("Xe mẫu " + i);
            mockVehicle.setVehiclePrice(String.format("%d.000 VNĐ/Ngày", 300 + (i * 100)));
            mockVehicle.setVehicleImageUrl("https://example.com/car.jpg");
            mockVehicle.setOwnerId("mock_user_" + i);
            mockVehicle.setVehicleNumber("ABC" + (1000 + i));
            mockVehicle.setVehicleAvailability("available");
            mockVehicle.setVehicleBrand(brands[i % 6]);
            mockVehicle.setFuelType(fuelTypes[i % 3]);
            mockVehicle.setVehicleRating((float) (3 + (i % 3)));
            mockVehicle.setVerificationStatus("verified");
            vehicleList.add(mockVehicle);
        }
        applyFilter();
        Log.d(TAG, "Tải dữ liệu giả: " + vehicleList.size() + " xe");
    }

    private void performSearch() {
        currentQuery = editTextSearch.getText().toString().trim().toLowerCase();
        applyFilter();
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_vehicle_filter, null);
        builder.setView(dialogView);

        // Initialize dialog views
        Spinner spinnerBrand = dialogView.findViewById(R.id.spinner_brand);
        TextView tvAvailabilityLabel = dialogView.findViewById(R.id.tv_availability_label);
        Spinner spinnerAvailability = dialogView.findViewById(R.id.spinner_availability);
        TextView tvVerificationLabel = dialogView.findViewById(R.id.tv_verification_label);
        Spinner spinnerVerification = dialogView.findViewById(R.id.spinner_verification);
        Spinner spinnerFuelType = dialogView.findViewById(R.id.spinner_fuel_type);
        RangeSlider sliderPrice = dialogView.findViewById(R.id.slider_price);
        RangeSlider sliderRating = dialogView.findViewById(R.id.slider_rating);
        Button btnApply = dialogView.findViewById(R.id.btn_apply_filter);
        Button btnClear = dialogView.findViewById(R.id.btn_clear_filter);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        // Hide Admin-specific fields
        tvAvailabilityLabel.setVisibility(View.GONE);
        spinnerAvailability.setVisibility(View.GONE);
        tvVerificationLabel.setVisibility(View.GONE);
        spinnerVerification.setVisibility(View.GONE);

        // Setup spinners
        List<String> brands = new ArrayList<>(Arrays.asList(getString(R.string.all_brands), "Vinfast", "Honda", "Mercedes", "BMW", "Ford", "Toyota"));
        List<String> fuelTypes = new ArrayList<>(Arrays.asList(getString(R.string.all_fuel_types), "Xăng", "Dầu", "Điện"));
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, brands);
        ArrayAdapter<String> fuelTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, fuelTypes);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fuelTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBrand.setAdapter(brandAdapter);
        spinnerFuelType.setAdapter(fuelTypeAdapter);

        // Set current filter values
        if (currentBrand != null) {
            spinnerBrand.setSelection(brands.indexOf(currentBrand));
        }
        if (currentFuelType != null) {
            spinnerFuelType.setSelection(fuelTypes.indexOf(currentFuelType));
        }
        if (currentMinRating != null && currentMaxRating != null) {
            sliderRating.setValues(currentMinRating, currentMaxRating);
        } else {
            sliderRating.setValues(0.0f, 5.0f); // Giá trị mặc định
        }
        if (currentMinPrice != null && currentMaxPrice != null) {
            sliderPrice.setValues(currentMinPrice.floatValue(), currentMaxPrice.floatValue());
        } else {
            sliderPrice.setValues(0.0f, 5000000.0f); // Giá trị mặc định
        }

        // Đảm bảo RangeSlider có giá trị hợp lệ
        sliderRating.setValueFrom(0.0f);
        sliderRating.setValueTo(5.0f);
        sliderRating.setStepSize(0.1f);
        sliderPrice.setValueFrom(0.0f);
        sliderPrice.setValueTo(5000000.0f);
        sliderPrice.setStepSize(1000.0f);

        AlertDialog dialog = builder.create();

        btnApply.setOnClickListener(v -> {
            currentBrand = spinnerBrand.getSelectedItem().toString().equals(getString(R.string.all_brands)) ? null : spinnerBrand.getSelectedItem().toString();
            currentFuelType = spinnerFuelType.getSelectedItem().toString().equals(getString(R.string.all_fuel_types)) ? null : spinnerFuelType.getSelectedItem().toString();

            // Kiểm tra ratingValues
            List<Float> ratingValues = sliderRating.getValues();
            if (ratingValues.size() >= 2) {
                currentMinRating = ratingValues.get(0);
                currentMaxRating = ratingValues.get(1);
            } else {
                Log.w(TAG, "ratingValues không đủ 2 phần tử: " + ratingValues.size());
                currentMinRating = 0.0f;
                currentMaxRating = 5.0f;
            }

            // Kiểm tra priceValues
            List<Float> priceValues = sliderPrice.getValues();
            if (priceValues.size() >= 2) {
                currentMinPrice = priceValues.get(0).longValue();
                currentMaxPrice = priceValues.get(1).longValue();
            } else {
                Log.w(TAG, "priceValues không đủ 2 phần tử: " + priceValues.size());
                currentMinPrice = 0L;
                currentMaxPrice = 5000000L;
            }

            applyFilter();
            dialog.dismiss();
        });

        btnClear.setOnClickListener(v -> {
            currentBrand = null;
            currentFuelType = null;
            currentMinRating = 0.0f;
            currentMaxRating = 5.0f;
            currentMinPrice = 0L;
            currentMaxPrice = 5000000L;
            spinnerBrand.setSelection(0);
            spinnerFuelType.setSelection(0);
            sliderRating.setValues(0.0f, 5.0f);
            sliderPrice.setValues(0.0f, 5000000.0f);
            applyFilter();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void applyFilter() {
        filteredVehicleList.clear();
        for (Vehicle vehicle : vehicleList) {
            boolean matches = true;
            if (!currentQuery.isEmpty()) {
                matches &= vehicle.getVehicleName() != null && vehicle.getVehicleName().toLowerCase().contains(currentQuery);
            }
            if (currentBrand != null) {
                matches &= vehicle.getVehicleBrand() != null && vehicle.getVehicleBrand().equalsIgnoreCase(currentBrand);
            }
            if (currentFuelType != null) {
                matches &= vehicle.getFuelType() != null && vehicle.getFuelType().equalsIgnoreCase(currentFuelType);
            }
            if (currentMinRating != null && currentMaxRating != null) {
                matches &= vehicle.getVehicleRating() >= currentMinRating && vehicle.getVehicleRating() <= currentMaxRating;
            }
            if (currentMinPrice != null && currentMaxPrice != null) {
                long price = parsePrice(vehicle.getVehiclePrice());
                matches &= price >= currentMinPrice && price <= currentMaxPrice;
            }
            if (matches) {
                filteredVehicleList.add(vehicle);
            }
        }

        vehicleAdapter = new VehicleAdapter(this, new ArrayList<>(filteredVehicleList), position -> {
            Vehicle clickedVehicle = filteredVehicleList.get(position);
            Intent intent = new Intent(getContext(), VehicleDetailActivity.class);
            intent.putExtra("vehicleId", clickedVehicle.getVehicleId());
            startActivity(intent);
        });
        recyclerViewVehicles.setAdapter(vehicleAdapter);
        vehicleAdapter.notifyDataSetChanged();
        Log.d(TAG, "Cập nhật RecyclerView với " + filteredVehicleList.size() + " xe");
    }

    private long parsePrice(String priceString) {
        if (priceString == null || priceString.isEmpty()) {
            return 0;
        }
        try {
            String cleanPrice = priceString.replaceAll("[^0-9]", "");
            return Long.parseLong(cleanPrice);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Lỗi phân tích giá: " + e.getMessage());
            return 0;
        }
    }

    private void openChatFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        ChatFragment chatFragment = new ChatFragment();
        transaction.replace(R.id.frame_layout_customer, chatFragment, "CHAT_FRAGMENT");
        transaction.addToBackStack("CHAT_FRAGMENT");
        transaction.commit();
    }
}