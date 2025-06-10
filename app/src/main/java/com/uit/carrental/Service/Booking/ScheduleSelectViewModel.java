package com.uit.carrental.Service.Booking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;
import com.uit.carrental.Model.Booking;

import java.util.Date;
import java.util.List;

public class ScheduleSelectViewModel extends ViewModel {

    private final FirebaseFirestore db;
    private final MutableLiveData<Boolean> scheduleAvailability;
    private final MutableLiveData<String> bookingResult;

    public ScheduleSelectViewModel() {
        db = FirebaseFirestore.getInstance();
        scheduleAvailability = new MutableLiveData<>();
        bookingResult = new MutableLiveData<>();
    }

    public LiveData<Boolean> checkScheduleConflict(String vehicleId, Timestamp startTime, Timestamp endTime) {
        db.collection("Bookings")
                .whereEqualTo("vehicleId", vehicleId)
                .whereIn("status", List.of("pending", "confirmed", "paid"))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean isAvailable = true;
                    for (var doc : querySnapshot.getDocuments()) {
                        Booking existingBooking = doc.toObject(Booking.class);
                        if (existingBooking != null &&
                                existingBooking.getStartTime() != null &&
                                existingBooking.getEndTime() != null) {
                            Date existingStart = existingBooking.getStartTime().toDate();
                            Date existingEnd = existingBooking.getEndTime().toDate();
                            Date newStart = startTime.toDate();
                            Date newEnd = endTime.toDate();
                            if (!(newEnd.before(existingStart) || newStart.after(existingEnd))) {
                                isAvailable = false;
                                break;
                            }
                        }
                    }
                    scheduleAvailability.setValue(isAvailable);
                })
                .addOnFailureListener(e -> scheduleAvailability.setValue(null));
        return scheduleAvailability;
    }

    public LiveData<String> createBooking(Booking booking) {
        db.collection("Bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    String bookingId = documentReference.getId();
                    booking.setBookingId(bookingId);
                    documentReference.set(booking)
                            .addOnSuccessListener(aVoid -> bookingResult.setValue(bookingId))
                            .addOnFailureListener(e -> bookingResult.setValue(null));
                })
                .addOnFailureListener(e -> bookingResult.setValue(null));
        return bookingResult;
    }
}
