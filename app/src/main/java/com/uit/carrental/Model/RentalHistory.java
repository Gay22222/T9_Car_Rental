package com.uit.carrental.Model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.Timestamp;

public class RentalHistory {

    private String rentalId; // ID của lần thuê
    private String vehicleId; // ID xe
    private String customerId; // ID khách hàng
    private Timestamp startDate; // Ngày bắt đầu thuê
    private Timestamp endDate; // Ngày kết thúc thuê
    private double totalCost; // Tổng chi phí
    private String status; // Trạng thái (completed, ongoing, cancelled)
    private String orderId; // Tham chiếu đến Orders (nếu có)

    public RentalHistory() {
        this.rentalId = "";
        this.vehicleId = "";
        this.customerId = "";
        this.startDate = Timestamp.now();
        this.endDate = Timestamp.now();
        this.totalCost = 0.0;
        this.status = "ongoing";
        this.orderId = "";
    }

    // Getters and Setters
    @NonNull
    public String getRentalId() { return rentalId != null ? rentalId : ""; }
    public void setRentalId(@Nullable String rentalId) { this.rentalId = rentalId != null ? rentalId : ""; }

    @NonNull
    public String getVehicleId() { return vehicleId != null ? vehicleId : ""; }
    public void setVehicleId(@Nullable String vehicleId) { this.vehicleId = vehicleId != null ? vehicleId : ""; }

    @NonNull
    public String getCustomerId() { return customerId != null ? customerId : ""; }
    public void setCustomerId(@Nullable String customerId) { this.customerId = customerId != null ? customerId : ""; }

    @Nullable
    public Timestamp getStartDate() { return startDate; }
    public void setStartDate(@Nullable Timestamp startDate) { this.startDate = startDate != null ? startDate : Timestamp.now(); }

    @Nullable
    public Timestamp getEndDate() { return endDate; }
    public void setEndDate(@Nullable Timestamp endDate) { this.endDate = endDate != null ? endDate : Timestamp.now(); }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost >= 0 ? totalCost : 0.0; }

    @NonNull
    public String getStatus() { return status != null ? status : "ongoing"; }
    public void setStatus(@Nullable String status) { this.status = status != null ? status : "ongoing"; }

    @NonNull
    public String getOrderId() { return orderId != null ? orderId : ""; }
    public void setOrderId(@Nullable String orderId) { this.orderId = orderId != null ? orderId : ""; }

    @NonNull
    @Override
    public String toString() {
        return "RentalHistory{rentalId='" + rentalId + "', vehicleId='" + vehicleId + "', customerId='" + customerId +
                "', totalCost=" + totalCost + ", status='" + status + "'}";
    }
}
