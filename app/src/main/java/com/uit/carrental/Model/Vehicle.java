package com.uit.carrental.Model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class Vehicle implements Parcelable {

    private String vehicleId;
    private String ownerId;
    private String vehicleName;
    private String vehiclePrice;
    private String vehicleSeats;
    private String vehicleNumber;
    private String vehicleAvailability;
    private String vehicleImageUrl;
    private String fuelType;
    private String maxSpeed;
    private String transmission;
    private String doorsAndSeats;
    private String vehicleBrand;
    private Float vehicleRating;
    private String verificationStatus;
    private Timestamp createdAt;
    private String documentImageUrl;

    public Vehicle() {
        this.vehicleId = "";
        this.ownerId = "";
        this.vehicleName = "";
        this.vehiclePrice = "";
        this.vehicleSeats = "";
        this.vehicleNumber = "";
        this.vehicleAvailability = "available";
        this.vehicleImageUrl = "";
        this.fuelType = "";
        this.maxSpeed = "";
        this.transmission = "";
        this.doorsAndSeats = "";
        this.vehicleBrand = "";
        this.vehicleRating = 4.0f;
        this.verificationStatus = "pending";
        this.createdAt = Timestamp.now();
        this.documentImageUrl = "";
    }

    protected Vehicle(Parcel in) {
        vehicleId = in.readString();
        ownerId = in.readString();
        vehicleName = in.readString();
        vehiclePrice = in.readString();
        vehicleSeats = in.readString();
        vehicleNumber = in.readString();
        vehicleAvailability = in.readString();
        vehicleImageUrl = in.readString();
        fuelType = in.readString();
        maxSpeed = in.readString();
        transmission = in.readString();
        doorsAndSeats = in.readString();
        vehicleBrand = in.readString();
        vehicleRating = in.readFloat();
        verificationStatus = in.readString();
        createdAt = in.readParcelable(Timestamp.class.getClassLoader());
        documentImageUrl = in.readString();
    }

    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    // Getters and Setters
    @NonNull
    @PropertyName("vehicleId")
    public String getVehicleId() { return vehicleId != null ? vehicleId : ""; }
    public void setVehicleId(@Nullable String vehicleId) { this.vehicleId = vehicleId != null ? vehicleId : ""; }

    @NonNull
    @PropertyName("ownerId")
    public String getOwnerId() { return ownerId != null ? ownerId : ""; }
    public void setOwnerId(@Nullable String ownerId) { this.ownerId = ownerId != null ? ownerId : ""; }

    @NonNull
    @PropertyName("vehicleName")
    public String getVehicleName() { return vehicleName != null ? vehicleName : ""; }
    public void setVehicleName(@Nullable String vehicleName) { this.vehicleName = vehicleName != null ? vehicleName : ""; }

    @NonNull
    @PropertyName("vehiclePrice")
    public String getVehiclePrice() { return vehiclePrice != null ? vehiclePrice : ""; }
    public void setVehiclePrice(@Nullable String vehiclePrice) { this.vehiclePrice = vehiclePrice != null ? vehiclePrice : ""; }

    @NonNull
    @PropertyName("vehicleSeats")
    public String getVehicleSeats() { return vehicleSeats != null ? vehicleSeats : ""; }
    public void setVehicleSeats(@Nullable String vehicleSeats) { this.vehicleSeats = vehicleSeats != null ? vehicleSeats : ""; }

    @NonNull
    @PropertyName("vehicleNumber")
    public String getVehicleNumber() { return vehicleNumber != null ? vehicleNumber : ""; }
    public void setVehicleNumber(@Nullable String vehicleNumber) { this.vehicleNumber = vehicleNumber != null ? vehicleNumber : ""; }

    @NonNull
    @PropertyName("vehicleAvailability")
    public String getVehicleAvailability() { return vehicleAvailability != null ? vehicleAvailability : "available"; }
    public void setVehicleAvailability(@Nullable String vehicleAvailability) {
        this.vehicleAvailability = vehicleAvailability != null ? vehicleAvailability : "available";
    }

    @NonNull
    @PropertyName("vehicleImageUrl")
    public String getVehicleImageUrl() { return vehicleImageUrl != null ? vehicleImageUrl : ""; }
    public void setVehicleImageUrl(@Nullable String vehicleImageUrl) {
        this.vehicleImageUrl = vehicleImageUrl != null ? vehicleImageUrl : "";
    }

    @NonNull
    @PropertyName("fuelType")
    public String getFuelType() { return fuelType != null ? fuelType : ""; }
    public void setFuelType(@Nullable String fuelType) { this.fuelType = fuelType != null ? fuelType : ""; }

    @NonNull
    @PropertyName("maxSpeed")
    public String getMaxSpeed() { return maxSpeed != null ? maxSpeed : ""; }
    public void setMaxSpeed(@Nullable String maxSpeed) { this.maxSpeed = maxSpeed != null ? maxSpeed : ""; }

    @NonNull
    @PropertyName("transmission")
    public String getTransmission() { return transmission != null ? transmission : ""; }
    public void setTransmission(@Nullable String transmission) { this.transmission = transmission != null ? transmission : ""; }

    @NonNull
    @PropertyName("doorsAndSeats")
    public String getDoorsAndSeats() { return doorsAndSeats != null ? doorsAndSeats : ""; }
    public void setDoorsAndSeats(@Nullable String doorsAndSeats) { this.doorsAndSeats = doorsAndSeats != null ? doorsAndSeats : ""; }

    @NonNull
    @PropertyName("vehicleBrand")
    public String getVehicleBrand() { return vehicleBrand != null ? vehicleBrand : ""; }
    public void setVehicleBrand(@Nullable String vehicleBrand) { this.vehicleBrand = vehicleBrand != null ? vehicleBrand : ""; }

    @Nullable
    @PropertyName("vehicleRating")
    public Float getVehicleRating() { return vehicleRating; }
    public void setVehicleRating(@Nullable Float vehicleRating) {
        this.vehicleRating = vehicleRating != null && vehicleRating >= 0 ? vehicleRating : 4.0f;
    }

    @NonNull
    @PropertyName("verificationStatus")
    public String getVerificationStatus() { return verificationStatus != null ? verificationStatus : "pending"; }
    public void setVerificationStatus(@Nullable String verificationStatus) {
        this.verificationStatus = verificationStatus != null ? verificationStatus : "pending";
    }

    @Nullable
    @PropertyName("createdAt")
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(@Nullable Timestamp createdAt) {
        this.createdAt = createdAt != null ? createdAt : Timestamp.now();
    }

    @NonNull
    @PropertyName("documentImageUrl")
    public String getDocumentImageUrl() { return documentImageUrl != null ? documentImageUrl : ""; }
    public void setDocumentImageUrl(@Nullable String documentImageUrl) {
        this.documentImageUrl = documentImageUrl != null ? documentImageUrl : "";
    }

    // Parcelable
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(vehicleId);
        dest.writeString(ownerId);
        dest.writeString(vehicleName);
        dest.writeString(vehiclePrice);
        dest.writeString(vehicleSeats);
        dest.writeString(vehicleNumber);
        dest.writeString(vehicleAvailability);
        dest.writeString(vehicleImageUrl);
        dest.writeString(fuelType);
        dest.writeString(maxSpeed);
        dest.writeString(transmission);
        dest.writeString(doorsAndSeats);
        dest.writeString(vehicleBrand);
        dest.writeFloat(vehicleRating != null ? vehicleRating : 4.0f);
        dest.writeString(verificationStatus);
        dest.writeParcelable(createdAt, flags);
        dest.writeString(documentImageUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "Vehicle{vehicleId='" + vehicleId + "', vehicleName='" + vehicleName + "', brand='" + vehicleBrand +
                "', rating=" + vehicleRating + ", verificationStatus='" + verificationStatus + "'}";
    }
}