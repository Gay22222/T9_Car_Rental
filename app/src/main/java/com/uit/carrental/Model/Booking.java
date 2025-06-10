package com.uit.carrental.Model;

import com.google.firebase.Timestamp;

public class Booking {
    private String bookingId;
    private String vehicleId;
    private String customerId;
    private String ownerId;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp maintenanceEndTime;
    private long totalAmount;
    private String status; // pending, confirmed, rejected, paid, completed, cancelled
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String paymentStatus; // unpaid, paid, failed
    private String paymentMethod; // ZaloPay, Other
    private String paymentId; // ID giao dịch từ ZaloPay
    private String note;
    private String pickupLocation;
    private String dropoffLocation;
    private String orderId; // ID đơn hàng từ ZaloPay

    public Booking() {
        this.bookingId = "";
        this.vehicleId = "";
        this.customerId = "";
        this.ownerId = "";
        this.startTime = null;
        this.endTime = null;
        this.maintenanceEndTime = null;
        this.totalAmount = 0;
        this.status = "pending";
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
        this.paymentStatus = "unpaid";
        this.paymentMethod = "";
        this.paymentId = "";
        this.note = "";
        this.pickupLocation = "";
        this.dropoffLocation = "";
        this.orderId = "";
    }

    // Getters và Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }

    public Timestamp getMaintenanceEndTime() { return maintenanceEndTime; }
    public void setMaintenanceEndTime(Timestamp maintenanceEndTime) { this.maintenanceEndTime = maintenanceEndTime; }

    public long getTotalAmount() { return totalAmount; }
    public void setTotalAmount(long totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
}
