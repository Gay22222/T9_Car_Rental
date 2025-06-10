package com.uit.carrental.FragmentPages.Customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.uit.carrental.R;
import java.util.HashMap;
import java.util.Map;

public class CustomerVehicleFilterFragment extends Fragment {

    private static final String ARG_BRAND = "brand";
    private static final String ARG_STATUS = "status";
    private static final String ARG_MIN_RATING = "minRating";
    private static final String ARG_MAX_RATING = "maxRating";

    private FilterCallback callback;
    private String selectedBrand;
    private String selectedStatus;
    private Float minRating, maxRating;
    private Map<String, View> brandViews = new HashMap<>();
    private Map<String, View> statusViews = new HashMap<>();
    private Map<String, View> ratingViews = new HashMap<>();

    public interface FilterCallback {
        void onFilterApplied(String brand, String status, Float minRating, Float maxRating);
    }

    public static CustomerVehicleFilterFragment newInstance(FilterCallback callback) {
        CustomerVehicleFilterFragment fragment = new CustomerVehicleFilterFragment();
        fragment.callback = callback;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_vehicle_filter, container, false);

        // Initialize views
        initializeBrandViews(view);
        initializeStatusViews(view);
        initializeRatingViews(view);

        // Nút đóng
        view.findViewById(R.id.rz3n8dabzscm).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Nút thiết lập lại
        view.findViewById(R.id.rvai73xq5syc).setOnClickListener(v -> resetFilters());

        // Nút xác nhận
        view.findViewById(R.id.rt94tooyri0r).setOnClickListener(v -> applyFilters());

        return view;
    }

    private void initializeBrandViews(View view) {
        String[] brands = {"Vinfast", "Honda", "Mercedes", "BMW", "Ford", "Toyota"};
        int[] brandIds = {
                R.id.re5bkjyzw71a, R.id.r29lmqq8qqka, R.id.r5wpc3jutt9d,
                R.id.r047t6es9ecg6, R.id.rwviyruuyw0d, R.id.r2lvmia82fj5
        };
        for (int i = 0; i < brands.length; i++) {
            View brandView = view.findViewById(brandIds[i]);
            String brand = brands[i];
            brandViews.put(brand, brandView);
            brandView.setOnClickListener(v -> selectBrand(brand));
        }
    }

    private void initializeStatusViews(View view) {
        statusViews.put("available", view.findViewById(R.id.rzvu0zdr3b79));
        statusViews.get("available").setOnClickListener(v -> selectStatus("available"));
    }

    private void initializeRatingViews(View view) {
        ratingViews.put("5", view.findViewById(R.id.rozw79prjrbs));
        ratingViews.put("4-5", view.findViewById(R.id.rx3ytiykf6v));
        ratingViews.put("4", view.findViewById(R.id.rsooed0x193));
        ratingViews.put("3", view.findViewById(R.id.rusqd789cwjj));
        ratingViews.put("1-2", view.findViewById(R.id.ru6lopcq6jh));

        ratingViews.get("5").setOnClickListener(v -> selectRating(5f, 5f));
        ratingViews.get("4-5").setOnClickListener(v -> selectRating(4f, 5f));
        ratingViews.get("4").setOnClickListener(v -> selectRating(4f, 4f));
        ratingViews.get("3").setOnClickListener(v -> selectRating(3f, 3f));
        ratingViews.get("1-2").setOnClickListener(v -> selectRating(1f, 2f));
    }

    private void selectBrand(String brand) {
        for (Map.Entry<String, View> entry : brandViews.entrySet()) {
            entry.getValue().setBackgroundResource(entry.getKey().equals(brand) ?
                    R.drawable.s003087sw1cr100b0030871f : R.drawable.s4d3a4dsw1cr100);
        }
        selectedBrand = brand.equals(selectedBrand) ? null : brand;
    }

    private void selectStatus(String status) {
        for (Map.Entry<String, View> entry : statusViews.entrySet()) {
            entry.getValue().setBackgroundResource(entry.getKey().equals(status) ?
                    R.drawable.s003087sw1cr100b0030871f : R.drawable.s4d3a4dsw1cr100);
        }
        selectedStatus = status.equals(selectedStatus) ? null : status;
    }

    private void selectRating(Float min, Float max) {
        for (Map.Entry<String, View> entry : ratingViews.entrySet()) {
            entry.getValue().setBackgroundResource((min.equals(minRating) && max.equals(maxRating)) ?
                    R.drawable.s4d3a4dsw1cr100 : R.drawable.s4d3a4dsw1cr100);
        }
        if (min.equals(minRating) && max.equals(maxRating)) {
            minRating = null;
            maxRating = null;
        } else {
            minRating = min;
            maxRating = max;
        }
    }

    private void resetFilters() {
        selectedBrand = null;
        selectedStatus = null;
        minRating = null;
        maxRating = null;
        for (View view : brandViews.values()) {
            view.setBackgroundResource(R.drawable.s4d3a4dsw1cr100);
        }
        for (View view : statusViews.values()) {
            view.setBackgroundResource(R.drawable.s4d3a4dsw1cr100);
        }
        for (View view : ratingViews.values()) {
            view.setBackgroundResource(R.drawable.s4d3a4dsw1cr100);
        }
    }

    private void applyFilters() {
        if (callback != null) {
            callback.onFilterApplied(selectedBrand, selectedStatus, minRating, maxRating);
        }
        getParentFragmentManager().popBackStack();
    }
}
