//package com.example.carrenting.FragmentPages.Customer;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.carrenting.Adapter.VehicleAdapter;
//import com.example.carrenting.Model.Vehicle;
//import com.example.carrenting.Model.onClickInterface;
//import com.example.carrenting.R;
//import com.example.carrenting.Service.Vehicle.VehicleDetailActivity;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//
//
//public class CustomerHomeFragment extends Fragment {
//
//    ImageView imageView;
//    DocumentReference imageRef;
//    RecyclerView recyclerView;
//    ArrayList<Vehicle> vehicles;
//    VehicleAdapter adapter;
//    FirebaseFirestore dtb_vehicle;
//    ProgressDialog progressDialog;
//    private onClickInterface onclickInterface;
//    private View mView;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        mView = inflater.inflate(R.layout.customer_fragment_home, container, false);
//        onclickInterface = new onClickInterface() {
//            @Override
//            public void setClick(int position) {
//                vehicles.indexOf(position);
//                Log.d("Position: ","Position is " + position);
//                adapter.notifyDataSetChanged();
//            }
//        };
//
//       /* progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Đang lấy dữ liệu...");
//        progressDialog.show();*/
//
//        recyclerView = mView.findViewById(R.id.vehicle_list);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//
//        dtb_vehicle = FirebaseFirestore.getInstance();
//        vehicles = new ArrayList<Vehicle>();
//        adapter = new VehicleAdapter(CustomerHomeFragment.this, vehicles, onclickInterface);
//        recyclerView.setAdapter(adapter);
//
//        try {
//            EventChangeListener();
//            //Toast.makeText(getContext(),"Catching...", Toast.LENGTH_LONG).show();
//        } catch (Exception exception){
//            Toast.makeText(getContext(), "Exception", Toast.LENGTH_LONG).show();
//        }
//
//
//        return  mView;
//    }
//    private void initUI() {
//        /*btnMap =  mView.findViewById(R.id.locate_card);*/
//    }
//    private void LoadImage(String docId) {
//        if (docId == null) {
//            Log.e("LoadImage", "docId is null, returning");
//            return;
//        }
//        // Reference to the image document in Firestore
//        imageRef = FirebaseFirestore.getInstance().collection("Image").document(docId);
//
//        // Get the image document
//        imageRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot != null && documentSnapshot.exists()) {
//                    // Get the image URL from the document
//                    String imageUrl = documentSnapshot.getString("Image");
//
//                    // Load the image into the ImageView
//                    Glide.with(getView()).load(imageUrl).into(imageView);
//                }else{
//                    Log.e("LoadImage", "documentSnapshot is null or does not exists");
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("LoadImage", "Error: " + e.getMessage());
//            }
//        });
//    }
//
//    private void EventChangeListener()
//    {
//        dtb_vehicle.collection("Vehicles")
//                .orderBy("vehicle_name", Query.Direction.ASCENDING)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Vehicle temp = new Vehicle();
//                                temp.setVehicle_id(document.getId());
//                                temp.setVehicle_name(document.get("vehicle_name").toString());
//                                temp.setVehicle_price(document.get("vehicle_price").toString());
//                                temp.setVehicle_imageURL(document.get("vehicle_imageURL").toString());
//                                temp.setProvider_name(document.get("provider_name").toString());
//                                vehicles.add(temp);
//                                adapter.notifyDataSetChanged();/*
//                                progressDialog.cancel();*/
//                            }
//                        } else {
//                            Toast.makeText(getContext(), "Không thể lấy thông tin xe", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//}
package com.example.carrenting.FragmentPages.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrenting.Service.Booking.CarDetailActivity;
import com.example.carrenting.Adapter.VehicleAdapter;
import com.example.carrenting.Model.Vehicle;
import com.example.carrenting.Model.onClickInterface;
import com.example.carrenting.R;

import java.util.ArrayList;

public class CustomerHomeFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Vehicle> vehicles;
    VehicleAdapter adapter;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.customer_fragment_home, container, false);

        recyclerView = mView.findViewById(R.id.vehicle_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        vehicles = new ArrayList<>();
        adapter = new VehicleAdapter(CustomerHomeFragment.this, vehicles, position -> {
            Vehicle vehicle = vehicles.get(position);
            Intent intent = new Intent(getActivity(), CarDetailActivity.class);
            intent.putExtra("vehicle_imageURL", vehicle.getVehicle_imageURL());
            intent.putExtra("vehicle_name", vehicle.getVehicle_name());
            intent.putExtra("vehicle_price", vehicle.getVehicle_price());
            intent.putExtra("vehicle_seats", vehicle.getVehicle_seats());
            intent.putExtra("vehicle_number", vehicle.getVehicle_number());
            intent.putExtra("vehicle_owner", vehicle.getOwner_name());
            intent.putExtra("vehicle_email", vehicle.getOwner_email());
            intent.putExtra("vehicle_phone", vehicle.getOwner_phone());
            intent.putExtra("vehicle_address", vehicle.getOwner_address());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        loadFakeVehicles();

        return mView;
    }

    private void loadFakeVehicles() {
        vehicles.add(new Vehicle(
                "V001",
                "P001",
                "Khôi",
                "Trương DẢK Khôi",
                "khoitruong.dev@gmail.com",
                "+84945257865",
                "Biên Hòa, Đồng Nai",
                "Bugatti Khoa Pug",
                "550,000 VND/day",
                "5",
                "60A-123.45",
                "available",
                "data:image/jpeg;base64,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"

        ));

        vehicles.add(new Vehicle(
                "V002",
                "P002",
                "Khôi",
                "Nguyễn Văn A",
                "khoitruong.dev@gmail.com",
                "+84987654321",
                "Quận 1, TP. HCM",
                "Koenigsegg",
                "650,000 VND/day",
                "5",
                "51G-678.90",
                "available",
                "https://media.vov.vn/sites/default/files/styles/large/public/2021-01/2016-koenigsegg-regera-v1-1080-768x432.jpg"
        ));

        vehicles.add(new Vehicle(
                "V003",
                "P003",
                "Khôi",
                "Phạm Văn B",
                "khoitruong.dev@gmail.com",
                "+8488997766",
                "Thủ Đức, TP. HCM",
                "Pagani Zonda",
                "720,000 VND/day",
                "7",
                "60A-999.99",
                "unavailable",
                "data:image/jpeg;base64,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"
        ));

        adapter.notifyDataSetChanged();
    }
}

