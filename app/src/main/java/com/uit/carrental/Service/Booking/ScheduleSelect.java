package com.uit.carrental.Service.Booking;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uit.carrental.Model.Booking;
import com.uit.carrental.Model.Notification;
import com.uit.carrental.Model.Vehicle;
import com.uit.carrental.R;
import com.google.firebase.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleSelect extends AppCompatActivity {

    private static final String TAG = "ScheduleSelect";
    private TextView tvSelectStartDate, tvSelectEndDate, tvSelectStartTime, tvSelectEndTime, tvTotalDays, tvTotalPrice;
    private Button btnConfirm;
    private ImageView backButton;
    private ScheduleSelectViewModel viewModel;
    private String vehicleId;
    private Calendar startDateTime, endDateTime;
    private List<Long> bookedDates; // Danh sách epoch millis của các ngày đã đặt
    private FirebaseFirestore db;
    private Vehicle vehicle; // Đối tượng xe để lấy giá
    private long totalDays;
    private long totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_select);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        vehicleId = getIntent().getStringExtra("vehicleId");
        if (vehicleId == null) {
            Toast.makeText(this, R.string.no_vehicle_id, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initViewModel();
        loadVehicleData();
        setupListeners();
    }

    private void initViews() {
        tvSelectStartDate = findViewById(R.id.tv_select_start_date);
        tvSelectEndDate = findViewById(R.id.tv_select_end_date);
        tvSelectStartTime = findViewById(R.id.tv_select_start_time);
        tvSelectEndTime = findViewById(R.id.tv_select_end_time);
        tvTotalDays = findViewById(R.id.tv_total_days);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnConfirm = findViewById(R.id.btn_confirm);
        backButton = findViewById(R.id.back_button);
        db = FirebaseFirestore.getInstance();
        bookedDates = new ArrayList<>();
        startDateTime = null;
        endDateTime = null;
        totalDays = 0;
        totalAmount = 0;
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ScheduleSelectViewModel.class);
    }

    private void loadVehicleData() {
        db.collection("Vehicles").document(vehicleId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        vehicle = documentSnapshot.toObject(Vehicle.class);
                        if (vehicle == null) {
                            Toast.makeText(this, R.string.vehicle_not_found, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            loadBookedDates();
                        }
                    } else {
                        Toast.makeText(this, R.string.vehicle_not_found, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải dữ liệu xe: " + e.getMessage());
                    Toast.makeText(this, R.string.load_vehicle_error, Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void loadBookedDates() {
        db.collection("Bookings")
                .whereEqualTo("vehicleId", vehicleId)
                .whereIn("status", List.of("pending", "confirmed", "paid"))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    bookedDates.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Booking booking = doc.toObject(Booking.class);
                        if (booking != null && booking.getStartTime() != null && booking.getEndTime() != null) {
                            addBookedDates(booking.getStartTime(), booking.getEndTime());
                        }
                    }
                    Log.d(TAG, "Loaded " + bookedDates.size() + " booked dates");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải lịch đặt xe: " + e.getMessage());
                    Toast.makeText(this, R.string.load_schedule_error, Toast.LENGTH_SHORT).show();
                });
    }

    private void addBookedDates(Timestamp startTime, Timestamp endTime) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startTime.toDate());
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endTime.toDate());
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        while (!startCal.after(endCal)) {
            bookedDates.add(startCal.getTimeInMillis());
            startCal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> finish());

        tvSelectStartDate.setOnClickListener(v -> showDatePicker(true));
        tvSelectEndDate.setOnClickListener(v -> showDatePicker(false));
        tvSelectStartTime.setOnClickListener(v -> showTimePicker(true));
        tvSelectEndTime.setOnClickListener(v -> showTimePicker(false));

        btnConfirm.setOnClickListener(v -> confirmBooking());
    }

    private void showDatePicker(boolean isStartDate) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(new CalendarConstraints.DateValidator() {
                    @Override
                    public boolean isValid(long date) {
                        Calendar today = Calendar.getInstance();
                        today.set(Calendar.HOUR_OF_DAY, 0);
                        today.set(Calendar.MINUTE, 0);
                        today.set(Calendar.SECOND, 0);
                        today.set(Calendar.MILLISECOND, 0);
                        return date >= today.getTimeInMillis() && !bookedDates.contains(date);
                    }

                    @Override
                    public int describeContents() { return 0; }

                    @Override
                    public void writeToParcel(android.os.Parcel dest, int flags) {}
                });

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isStartDate ? R.string.select_start_date : R.string.select_end_date)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTimeInMillis(selection);
            selectedCal.set(Calendar.HOUR_OF_DAY, 0);
            selectedCal.set(Calendar.MINUTE, 0);
            selectedCal.set(Calendar.SECOND, 0);
            selectedCal.set(Calendar.MILLISECOND, 0);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            if (isStartDate) {
                startDateTime = startDateTime != null ? startDateTime : Calendar.getInstance();
                startDateTime.setTimeInMillis(selection);
                tvSelectStartDate.setText(sdf.format(new Date(selection)));
            } else {
                endDateTime = endDateTime != null ? endDateTime : Calendar.getInstance();
                endDateTime.setTimeInMillis(selection);
                tvSelectEndDate.setText(sdf.format(new Date(selection)));
            }
            updateTotalDaysAndPrice();
        });

        datePicker.show(getSupportFragmentManager(), isStartDate ? "START_DATE_PICKER" : "END_DATE_PICKER");
    }

    private void showTimePicker(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePicker = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    if (isStartTime) {
                        startDateTime = startDateTime != null ? startDateTime : Calendar.getInstance();
                        startDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startDateTime.set(Calendar.MINUTE, minute);
                        tvSelectStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                    } else {
                        endDateTime = endDateTime != null ? endDateTime : Calendar.getInstance();
                        endDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        endDateTime.set(Calendar.MINUTE, minute);
                        tvSelectEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                    }
                    updateTotalDaysAndPrice();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePicker.show();
    }

    private void updateTotalDaysAndPrice() {
        if (startDateTime == null || endDateTime == null || vehicle == null) {
            tvTotalDays.setText(R.string.total_days);
            tvTotalPrice.setText(R.string.total_price);
            totalDays = 0;
            totalAmount = 0;
            return;
        }

        // Tính số giờ thuê
        long diffInMillis = endDateTime.getTimeInMillis() - startDateTime.getTimeInMillis();
        if (diffInMillis <= 0) {
            tvTotalDays.setText(R.string.total_days);
            tvTotalPrice.setText(R.string.total_price);
            totalDays = 0;
            totalAmount = 0;
            return;
        }

        // Làm tròn số ngày (1 ngày = 24 giờ)
        totalDays = (long) Math.ceil((double) diffInMillis / (1000 * 60 * 60 * 24));

        // Lấy giá xe từ vehiclePrice (giả định dạng "500.000 VNĐ/Ngày")
        long pricePerDay = parsePricePerDay(vehicle.getVehiclePrice());
        totalAmount = totalDays * pricePerDay;

        // Định dạng giá
        DecimalFormat formatter = new DecimalFormat("#,### VNĐ");
        tvTotalDays.setText(getString(R.string.total_days, totalDays));
        tvTotalPrice.setText(getString(R.string.total_price, formatter.format(totalAmount)));
    }

    private long parsePricePerDay(String priceString) {
        if (priceString == null || priceString.isEmpty()) {
            return 0;
        }
        try {
            // Loại bỏ " VNĐ/Ngày" và các dấu chấm
            String cleanPrice = priceString.replaceAll("[^0-9]", "");
            return Long.parseLong(cleanPrice);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Lỗi phân tích giá: " + e.getMessage());
            return 0;
        }
    }

    private void confirmBooking() {
        if (startDateTime == null || endDateTime == null) {
            Toast.makeText(this, R.string.no_date_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (endDateTime.before(startDateTime)) {
            Toast.makeText(this, R.string.invalid_date_range, Toast.LENGTH_SHORT).show();
            return;
        }
        if (tvSelectStartTime.getText().toString().equals(getString(R.string.select_start_time)) ||
                tvSelectEndTime.getText().toString().equals(getString(R.string.select_end_time))) {
            Toast.makeText(this, R.string.no_time_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (totalDays <= 0 || totalAmount <= 0) {
            Toast.makeText(this, R.string.invalid_booking, Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.checkScheduleConflict(vehicleId, new Timestamp(startDateTime.getTime()), new Timestamp(endDateTime.getTime()))
                .observe(this, isAvailable -> {
                    if (isAvailable == null) {
                        Toast.makeText(this, R.string.booking_failed, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!isAvailable) {
                        Toast.makeText(this, R.string.time_overlap_error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Booking booking = new Booking();
                    booking.setVehicleId(vehicleId);
                    booking.setCustomerId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    booking.setOwnerId(vehicle.getOwnerId());
                    booking.setStartTime(new Timestamp(startDateTime.getTime()));
                    booking.setEndTime(new Timestamp(endDateTime.getTime()));
                    booking.setStatus("pending");
                    booking.setCreatedAt(Timestamp.now());
                    booking.setUpdatedAt(Timestamp.now());
                    booking.setTotalAmount(totalAmount);
                    booking.setPickupLocation("Địa chỉ nhận xe"); // TODO: Lấy từ input
                    booking.setDropoffLocation("Địa chỉ trả xe"); // TODO: Lấy từ input

                    viewModel.createBooking(booking).observe(this, bookingId -> {
                        if (bookingId != null) {
                            Notification notification = new Notification();
                            notification.setUserId(vehicle.getOwnerId());
                            notification.setBookingId(bookingId);
                            notification.setMessage("Có booking mới: #" + bookingId);
                            notification.setType("new_booking");
                            db.collection("Notifications").add(notification);

                            Intent intent = new Intent(ScheduleSelect.this, RequestSuccessActivity.class);
                            intent.putExtra("bookingId", bookingId);
                            startActivity(intent);
                            Toast.makeText(this, R.string.booking_success, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, R.string.booking_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }
}
