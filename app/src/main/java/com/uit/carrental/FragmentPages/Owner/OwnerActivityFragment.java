package com.uit.carrental.FragmentPages.Owner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uit.carrental.Adapter.BookingAdapter;
import com.uit.carrental.Model.Booking;
import com.uit.carrental.R;
import com.uit.carrental.Service.Booking.OwnerBookingDetailActivity;
import java.util.ArrayList;
import java.util.List;

public class OwnerActivityFragment extends Fragment {

    private static final String TAG = "OwnerActivityFragment";
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private ListenerRegistration bookingListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.owner_fragment_activity, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Đang lấy dữ liệu...");
        progressDialog.show();

        recyclerView = view.findViewById(R.id.activity_list);
        tvEmpty = view.findViewById(R.id.tv_empty);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(bookingList, booking -> {
            Intent intent = new Intent(getContext(), OwnerBookingDetailActivity.class);
            intent.putExtra("bookingId", booking.getBookingId());
            startActivity(intent);
        });
        recyclerView.setAdapter(bookingAdapter);

        loadBookings();

        return view;
    }

    private void loadBookings() {
        String userId = auth.getCurrentUser().getUid();
        bookingListener = db.collection("Bookings")
                .whereEqualTo("ownerId", userId)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Lỗi tải đơn hàng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        return;
                    }
                    bookingList.clear();
                    if (querySnapshot != null) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Booking booking = document.toObject(Booking.class);
                            booking.setBookingId(document.getId());
                            bookingList.add(booking);
                        }
                    }
                    bookingAdapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(bookingList.isEmpty() ? View.VISIBLE : View.GONE);
                    recyclerView.setVisibility(bookingList.isEmpty() ? View.GONE : View.VISIBLE);
                    progressDialog.dismiss();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bookingListener != null) {
            bookingListener.remove();
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
