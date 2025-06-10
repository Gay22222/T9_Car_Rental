package com.uit.carrental.FragmentPages.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.uit.carrental.R;

public class VehicleFilterFragment extends Fragment {

    private LinearLayout buttonVinfast, buttonHonda, buttonMercedes, buttonBMW, buttonFord, buttonToyota;
    private LinearLayout buttonRenting, buttonAvailable, buttonMaintenance, buttonPending;
    private LinearLayout buttonVerified, buttonUnverified;
    private LinearLayout buttonRating5, buttonRating5to4, buttonRating4, buttonRating3, buttonRating2to1;
    private LinearLayout buttonReset, buttonConfirm;
    private ImageView buttonClose;
    private String selectedBrand;
    private String selectedStatus;
    private String selectedVerificationStatus;
    private Float selectedMinRating, selectedMaxRating;
    private FilterCallback callback;

    public interface FilterCallback {
        void onFilterApplied(String brand, String status, String verificationStatus, Float minRating, Float maxRating);
    }

    public static VehicleFilterFragment newInstance(FilterCallback callback) {
        VehicleFilterFragment fragment = new VehicleFilterFragment();
        fragment.callback = callback;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle_filter, container, false);

        initViews(view);
        loadStaticImages(view);
        setupFilterButtons(view);

        buttonReset.setOnClickListener(v -> resetFilters());
        buttonConfirm.setOnClickListener(v -> applyFilters());
        buttonClose.setOnClickListener(v -> closeFragment());

        return view;
    }

    private void initViews(View view) {
        buttonVinfast = view.findViewById(R.id.re5bkjyzw71a);
        buttonHonda = view.findViewById(R.id.r29lmqq8qqka);
        buttonMercedes = view.findViewById(R.id.r5wpc3jutt9d);
        buttonBMW = view.findViewById(R.id.r047t6es9ecg6);
        buttonFord = view.findViewById(R.id.rwviyruuyw0d);
        buttonToyota = view.findViewById(R.id.r2lvmia82fj5);
        buttonRenting = view.findViewById(R.id.rv2rbtkh118);
        buttonAvailable = view.findViewById(R.id.rzvu0zdr3b79);
        buttonMaintenance = view.findViewById(R.id.rpdbvxqwor8);
        buttonPending = view.findViewById(R.id.r14thtgj04tg);
        buttonVerified = view.findViewById(R.id.r_verified);
        buttonUnverified = view.findViewById(R.id.r_unverified);
        buttonRating5 = view.findViewById(R.id.rozw79prjrbs);
        buttonRating5to4 = view.findViewById(R.id.rx3ytiykf6v);
        buttonRating4 = view.findViewById(R.id.rsooed0x193);
        buttonRating3 = view.findViewById(R.id.rusqd789cwjj);
        buttonRating2to1 = view.findViewById(R.id.ru6lopcq6jh);
        buttonConfirm = view.findViewById(R.id.rt94tooyri0r);
        buttonReset = view.findViewById(R.id.rvai73xq5syc);
        buttonClose = view.findViewById(R.id.rz3n8dabzscm);
    }

    private void loadStaticImages(View view) {
        if (getContext() != null) {
            Glide.with(getContext())
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/7l1ioa0k_expires_30_days.png")
                    .into((ImageView) view.findViewById(R.id.rz3n8dabzscm));
            Glide.with(getContext())
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/inzw1wo3_expires_30_days.png")
                    .into((ShapeableImageView) view.findViewById(R.id.r4y8nv6c7mpc));
            Glide.with(getContext())
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/asva4ip1_expires_30_days.png")
                    .into((ShapeableImageView) view.findViewById(R.id.rb6215hqc4bl));
            Glide.with(getContext())
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/zlhcok6g_expires_30_days.png")
                    .into((ShapeableImageView) view.findViewById(R.id.rtnetjohacza));
            Glide.with(getContext())
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/s5ugz48w_expires_30_days.png")
                    .into((ShapeableImageView) view.findViewById(R.id.rw0l5x2vl8bi));
            Glide.with(getContext())
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/m0hnc1ol_expires_30_days.png")
                    .into((ShapeableImageView) view.findViewById(R.id.rwwi7sxl6nz));
            Glide.with(getContext())
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/hvjc3cad_expires_30_days.png")
                    .into((ShapeableImageView) view.findViewById(R.id.rayfaxchsqb8));
            Glide.with(getContext())
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/3ysO8Aie7W/13zcccmz_expires_30_days.png")
                    .into((ShapeableImageView) view.findViewById(R.id.r46s7tlb9ka4));
        }
    }

    private void setupFilterButtons(View view) {
        // Loại xe
        View.OnClickListener brandClickListener = v -> {
            int id = v.getId();
            if (id == R.id.re5bkjyzw71a) {
                selectedBrand = "Vinfast";
            } else if (id == R.id.r29lmqq8qqka) {
                selectedBrand = "Honda";
            } else if (id == R.id.r5wpc3jutt9d) {
                selectedBrand = "Mercedes";
            } else if (id == R.id.r047t6es9ecg6) {
                selectedBrand = "BMW";
            } else if (id == R.id.rwviyruuyw0d) {
                selectedBrand = "Ford";
            } else if (id == R.id.r2lvmia82fj5) {
                selectedBrand = "Toyota";
            } else {
                selectedBrand = null;
            }
            updateBrandButtonUI(id);
        };
        buttonVinfast.setOnClickListener(brandClickListener);
        buttonHonda.setOnClickListener(brandClickListener);
        buttonMercedes.setOnClickListener(brandClickListener);
        buttonBMW.setOnClickListener(brandClickListener);
        buttonFord.setOnClickListener(brandClickListener);
        buttonToyota.setOnClickListener(brandClickListener);

        // Trạng thái
        View.OnClickListener statusClickListener = v -> {
            int id = v.getId();
            if (id == R.id.rv2rbtkh118) {
                selectedStatus = "renting";
            } else if (id == R.id.rzvu0zdr3b79) {
                selectedStatus = "available";
            } else if (id == R.id.rpdbvxqwor8) {
                selectedStatus = "maintenance";
            } else if (id == R.id.r14thtgj04tg) {
                selectedStatus = "pending";
            } else {
                selectedStatus = null;
            }
            updateStatusButtonUI(id);
        };
        buttonRenting.setOnClickListener(statusClickListener);
        buttonAvailable.setOnClickListener(statusClickListener);
        buttonMaintenance.setOnClickListener(statusClickListener);
        buttonPending.setOnClickListener(statusClickListener);

        // Trạng thái duyệt
        View.OnClickListener verificationStatusClickListener = v -> {
            int id = v.getId();
            if (id == R.id.r_verified) {
                selectedVerificationStatus = "verified";
            } else if (id == R.id.r_unverified) {
                selectedVerificationStatus = "pending";
            } else {
                selectedVerificationStatus = null;
            }
            updateVerificationStatusButtonUI(id);
        };
        buttonVerified.setOnClickListener(verificationStatusClickListener);
        buttonUnverified.setOnClickListener(verificationStatusClickListener);

        // Đánh giá
        View.OnClickListener ratingClickListener = v -> {
            int id = v.getId();
            if (id == R.id.rozw79prjrbs) {
                selectedMinRating = 5.0f;
                selectedMaxRating = 5.0f;
            } else if (id == R.id.rx3ytiykf6v) {
                selectedMinRating = 4.0f;
                selectedMaxRating = 5.0f;
            } else if (id == R.id.rsooed0x193) {
                selectedMinRating = 4.0f;
                selectedMaxRating = 4.0f;
            } else if (id == R.id.rusqd789cwjj) {
                selectedMinRating = 3.0f;
                selectedMaxRating = 3.0f;
            } else if (id == R.id.ru6lopcq6jh) {
                selectedMinRating = 1.0f;
                selectedMaxRating = 2.0f;
            } else {
                selectedMinRating = null;
                selectedMaxRating = null;
            }
            updateRatingButtonUI(id);
        };
        buttonRating5.setOnClickListener(ratingClickListener);
        buttonRating5to4.setOnClickListener(ratingClickListener);
        buttonRating4.setOnClickListener(ratingClickListener);
        buttonRating3.setOnClickListener(ratingClickListener);
        buttonRating2to1.setOnClickListener(ratingClickListener);
    }

    private void updateBrandButtonUI(int selectedId) {
        int selectedBg = R.drawable.s003087sw1cr100b0030871f;
        int defaultBg = R.drawable.s4d3a4dsw1cr100;
        buttonVinfast.setBackgroundResource(selectedId == R.id.re5bkjyzw71a ? selectedBg : defaultBg);
        buttonHonda.setBackgroundResource(selectedId == R.id.r29lmqq8qqka ? selectedBg : defaultBg);
        buttonMercedes.setBackgroundResource(selectedId == R.id.r5wpc3jutt9d ? selectedBg : defaultBg);
        buttonBMW.setBackgroundResource(selectedId == R.id.r047t6es9ecg6 ? selectedBg : defaultBg);
        buttonFord.setBackgroundResource(selectedId == R.id.rwviyruuyw0d ? selectedBg : defaultBg);
        buttonToyota.setBackgroundResource(selectedId == R.id.r2lvmia82fj5 ? selectedBg : defaultBg);
    }

    private void updateStatusButtonUI(int selectedId) {
        buttonRenting.setBackgroundResource(selectedId == R.id.rv2rbtkh118 ? R.drawable.s003087sw1cr100b0030871f : R.drawable.s4d3a4dsw1cr100);
        buttonAvailable.setBackgroundResource(selectedId == R.id.rzvu0zdr3b79 ? R.drawable.s003087sw1cr100b0030871f : R.drawable.s4d3a4dsw1cr100);
        buttonMaintenance.setBackgroundResource(selectedId == R.id.rpdbvxqwor8 ? R.drawable.sb60101sw1cr100bb6050233 : R.drawable.s4d3a4dsw1cr100);
        buttonPending.setBackgroundResource(selectedId == R.id.r14thtgj04tg ? R.drawable.sb66e01sw1cr100bb64d0233 : R.drawable.s4d3a4dsw1cr100);
    }

    private void updateVerificationStatusButtonUI(int selectedId) {
        int selectedBg = R.drawable.s003087sw1cr100b0030871f;
        int defaultBg = R.drawable.s4d3a4dsw1cr100;
        buttonVerified.setBackgroundResource(selectedId == R.id.r_verified ? selectedBg : defaultBg);
        buttonUnverified.setBackgroundResource(selectedId == R.id.r_unverified ? selectedBg : defaultBg);
    }

    private void updateRatingButtonUI(int selectedId) {
        int defaultBg = R.drawable.s4d3a4dsw1cr100;
        buttonRating5.setBackgroundResource(selectedId == R.id.rozw79prjrbs ? R.drawable.s4d3a4dsw1cr100bffbc7d33 : defaultBg);
        buttonRating5to4.setBackgroundResource(selectedId == R.id.rx3ytiykf6v ? R.drawable.s4d3a4dsw1cr100bffbc7d33 : defaultBg);
        buttonRating4.setBackgroundResource(selectedId == R.id.rsooed0x193 ? R.drawable.s4d3a4dsw1cr100bffbc7d33 : defaultBg);
        buttonRating3.setBackgroundResource(selectedId == R.id.rusqd789cwjj ? R.drawable.s4d3a4dsw1cr100bffbc7d33 : defaultBg);
        buttonRating2to1.setBackgroundResource(selectedId == R.id.ru6lopcq6jh ? R.drawable.s4d3a4dsw1cr100bffbc7d33 : defaultBg);
    }

    private void resetFilters() {
        selectedBrand = null;
        selectedStatus = null;
        selectedVerificationStatus = null;
        selectedMinRating = null;
        selectedMaxRating = null;
        updateBrandButtonUI(0);
        updateStatusButtonUI(0);
        updateVerificationStatusButtonUI(0);
        updateRatingButtonUI(0);
    }

    private void applyFilters() {
        if (callback != null) {
            callback.onFilterApplied(selectedBrand, selectedStatus, selectedVerificationStatus, selectedMinRating, selectedMaxRating);
        }
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }

    private void closeFragment() {
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }
}
