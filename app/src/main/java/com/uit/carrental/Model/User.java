package com.uit.carrental.Model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable {

    private String userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String birthday;
    private String address;
    private String city;
    private String ciCardFront;
    private String ciCardBehind;
    private String description;
    private String verificationStatus;
    private String status;
    private Map<String, Boolean> roles;
    private String currentRole;
    private String licenseUrl; // Trường mới cho ảnh bằng lái xe

    public User() {
        this.userId = "";
        this.username = "";
        this.email = "";
        this.phoneNumber = "";
        this.avatarUrl = "";
        this.birthday = "";
        this.address = "";
        this.city = "";
        this.ciCardFront = "";
        this.ciCardBehind = "";
        this.description = "";
        this.verificationStatus = null;
        this.status = "active";
        this.roles = new HashMap<>();
        this.currentRole = "customer";
        this.licenseUrl = "";
    }

    protected User(Parcel in) {
        userId = in.readString();
        username = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        avatarUrl = in.readString();
        birthday = in.readString();
        address = in.readString();
        city = in.readString();
        ciCardFront = in.readString();
        ciCardBehind = in.readString();
        description = in.readString();
        verificationStatus = in.readString();
        status = in.readString();
        roles = in.readHashMap(Boolean.class.getClassLoader());
        currentRole = in.readString();
        licenseUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Getters and Setters
    @NonNull
    public String getUserId() { return userId != null ? userId : ""; }
    public void setUserId(@Nullable String userId) { this.userId = userId != null ? userId : ""; }

    @NonNull
    public String getUsername() { return username != null ? username : ""; }
    public void setUsername(@Nullable String username) { this.username = username != null ? username : ""; }

    @NonNull
    public String getEmail() { return email != null ? email : ""; }
    public void setEmail(@Nullable String email) { this.email = email != null ? email : ""; }

    @NonNull
    public String getPhoneNumber() { return phoneNumber != null ? phoneNumber : ""; }
    public void setPhoneNumber(@Nullable String phoneNumber) { this.phoneNumber = phoneNumber != null ? phoneNumber : ""; }

    @NonNull
    public String getAvatarUrl() { return avatarUrl != null ? avatarUrl : ""; }
    public void setAvatarUrl(@Nullable String avatarUrl) { this.avatarUrl = avatarUrl != null ? avatarUrl : ""; }

    @NonNull
    public String getBirthday() { return birthday != null ? birthday : ""; }
    public void setBirthday(@Nullable String birthday) { this.birthday = birthday != null ? birthday : ""; }

    @NonNull
    public String getAddress() { return address != null ? address : ""; }
    public void setAddress(@Nullable String address) { this.address = address != null ? address : ""; }

    @NonNull
    public String getCity() { return city != null ? city : ""; }
    public void setCity(@Nullable String city) { this.city = city != null ? city : ""; }

    @NonNull
    public String getCiCardFront() { return ciCardFront != null ? ciCardFront : ""; }
    public void setCiCardFront(@Nullable String ciCardFront) { this.ciCardFront = ciCardFront != null ? ciCardFront : ""; }

    @NonNull
    public String getCiCardBehind() { return ciCardBehind != null ? ciCardBehind : ""; }
    public void setCiCardBehind(@Nullable String ciCardBehind) { this.ciCardBehind = ciCardBehind != null ? ciCardBehind : ""; }

    @NonNull
    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(@Nullable String description) { this.description = description != null ? description : ""; }

    @Nullable
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(@Nullable String verificationStatus) { this.verificationStatus = verificationStatus; }

    @NonNull
    public String getStatus() { return status != null ? status : "active"; }
    public void setStatus(@Nullable String status) { this.status = status != null ? status : "active"; }

    @NonNull
    public Map<String, Boolean> getRoles() { return roles != null ? roles : new HashMap<>(); }
    public void setRoles(@Nullable Map<String, Boolean> roles) { this.roles = roles != null ? roles : new HashMap<>(); }

    @NonNull
    public String getCurrentRole() { return currentRole != null ? currentRole : "customer"; }
    public void setCurrentRole(@Nullable String currentRole) { this.currentRole = currentRole != null ? currentRole : "customer"; }

    @NonNull
    public String getLicenseUrl() { return licenseUrl != null ? licenseUrl : ""; }
    public void setLicenseUrl(@Nullable String licenseUrl) { this.licenseUrl = licenseUrl != null ? licenseUrl : ""; }

    // Utility Methods
    public boolean hasRole(@Nullable String role) {
        return role != null && roles != null && roles.getOrDefault(role, false);
    }

    // Parcelable
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(avatarUrl);
        dest.writeString(birthday);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(ciCardFront);
        dest.writeString(ciCardBehind);
        dest.writeString(description);
        dest.writeString(verificationStatus);
        dest.writeString(status);
        dest.writeMap(roles);
        dest.writeString(currentRole);
        dest.writeString(licenseUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "User{userId='" + userId + "', username='" + username + "', verificationStatus='" + verificationStatus + "', currentRole='" + currentRole + "'}";
    }
}