package com.uit.carrental.Repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.uit.carrental.Model.Vehicle;

import java.util.HashMap;
import java.util.Map;

public class VehicleRepository {
    private final CollectionReference vehiclesRef;
    private final CollectionReference notificationsRef;

    public VehicleRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        vehiclesRef = db.collection("Vehicles");
        notificationsRef = db.collection("Notifications");
    }

    public Task<DocumentReference> addVehicle(Vehicle vehicle) {
        return vehiclesRef.add(vehicle);
    }

    public Task<Void> updateVehicleId(String vehicleId, String documentId) {
        Map<String, Object> data = new HashMap<>();
        data.put("vehicleId", vehicleId);
        return vehiclesRef.document(documentId).update(data);
    }

    public Task<DocumentReference> createAdminNotification(String title, String content, String vehicleId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("content", content);
        notification.put("timestamp", com.google.firebase.Timestamp.now());
        notification.put("status", "unread");
        notification.put("type", "vehicle_verification");
        notification.put("vehicleId", vehicleId);
        notification.put("recipient", "admin");
        return notificationsRef.add(notification);
    }

    public Query getVehiclesForOwner(String ownerId) {
        return vehiclesRef.whereEqualTo("ownerId", ownerId);
    }

    public Query getVerifiedVehicles() {
        return vehiclesRef.whereEqualTo("verificationStatus", "verified")
                .whereEqualTo("vehicleAvailability", "available");
    }
}