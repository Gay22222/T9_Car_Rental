package com.uit.carrental.Model;

import com.google.firebase.Timestamp;

public class Notification {
    private String notificationId;
    private String userId;
    private String title;
    private String message;
    private String type;
    private String vehicleId;
    private String bookingId;
    private Timestamp createdAt;
    private boolean isRead;
    private boolean read;
    private String role; // Thêm trường role

    public Notification() {}

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return isRead || read; }
    public void setRead(boolean read) {
        this.isRead = read;
        this.read = read;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
        this.read = isRead;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}