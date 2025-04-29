package com.uit.carrental.Model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Map;

public class User implements Parcelable {

    private String email;
    private String user_id;
    private String username;
    private String avatarURL;
    private String birthday;
    private String phoneNumber;
    private String address;
    private String city;
    private String ciCardFront;
    private String ciCardBehind;
    private String role;
    private Map<String, Boolean> roles;
    private String currentRole;

    public User() {
        address = "";
        email = "";
        user_id = "";
        username = "";
        avatarURL = "";
        birthday = "";
        phoneNumber = "";
        city = "";
        ciCardFront = "";
        ciCardBehind = "";
    }

    protected User(Parcel in) {
        email = in.readString();
        user_id = in.readString();
        username = in.readString();
        avatarURL = in.readString();
        birthday = in.readString();
        phoneNumber = in.readString();
        address = in.readString();
        city = in.readString();
        ciCardFront = in.readString();
        ciCardBehind = in.readString();
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

    // --- Getter và Setter ---
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAvatarURL() { return avatarURL; }
    public void setAvatarURL(String avatarURL) { this.avatarURL = avatarURL; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCiCardFront() { return ciCardFront; }
    public void setCiCardFront(String ciCardFront) { this.ciCardFront = ciCardFront; }

    public String getCiCardBehind() { return ciCardBehind; }
    public void setCiCardBehind(String ciCardBehind) { this.ciCardBehind = ciCardBehind; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Map<String, Boolean> getRoles() { return roles; }
    public void setRoles(Map<String, Boolean> roles) { this.roles = roles; }

    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }

    // --- Parcelable ---
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(avatarURL);
        dest.writeString(birthday);
        dest.writeString(phoneNumber);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(ciCardFront);
        dest.writeString(ciCardBehind);
    }
}
