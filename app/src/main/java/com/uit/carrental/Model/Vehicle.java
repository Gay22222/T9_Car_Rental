package com.uit.carrental.Model;

public class Vehicle {
    private String vehicle_id, provider_id, provider_name;
    private String owner_name, provider_gmail, provider_phone, provider_address;
    private String vehicle_name, vehicle_price, vehicle_seats, vehicle_number, vehicle_availability;
    private String vehicle_imageURL, vehicle_rating;
    private String fuel_type, max_speed, transmission, doors_seats;

    public Vehicle() {
        this.vehicle_id = "";
        this.owner_name = "";
        this.provider_id = "";
        this.provider_name = "";
        this.provider_gmail = "";
        this.provider_phone = "";
        this.provider_address = "";
        this.vehicle_name = "";
        this.vehicle_price = "";
        this.vehicle_seats = "";
        this.vehicle_number = "";
        this.vehicle_availability = "";
        this.vehicle_imageURL = "";
        this.vehicle_rating = "";
        this.fuel_type = "";
        this.max_speed = "";
        this.transmission = "";
        this.doors_seats = "";
    }

    public Vehicle(String vehicle_id, String provider_id, String provider_name, String owner_name,
                   String provider_gmail, String provider_phone, String provider_address,
                   String vehicle_name, String vehicle_price, String vehicle_seats,
                   String vehicle_number, String vehicle_availability, String vehicle_imageURL,
                   String vehicle_rating, String fuel_type, String max_speed,
                   String transmission, String doors_seats) {
        this.vehicle_id = vehicle_id;
        this.owner_name = owner_name;
        this.provider_id = provider_id;
        this.provider_name = provider_name;
        this.provider_gmail = provider_gmail;
        this.provider_phone = provider_phone;
        this.provider_address = provider_address;
        this.vehicle_name = vehicle_name;
        this.vehicle_price = vehicle_price;
        this.vehicle_seats = vehicle_seats;
        this.vehicle_number = vehicle_number;
        this.vehicle_availability = vehicle_availability;
        this.vehicle_imageURL = vehicle_imageURL;
        this.vehicle_rating = vehicle_rating;
        this.fuel_type = fuel_type;
        this.max_speed = max_speed;
        this.transmission = transmission;
        this.doors_seats = doors_seats;
    }

    // Getters and setters
    public String getVehicle_id() { return vehicle_id; }
    public void setVehicle_id(String vehicle_id) { this.vehicle_id = vehicle_id; }
    public String getProvider_id() { return provider_id; }
    public void setProvider_id(String provider_id) { this.provider_id = provider_id; }
    public String getProvider_name() { return provider_name; }
    public void setProvider_name(String provider_name) { this.provider_name = provider_name; }
    public String getOwner_name() { return owner_name; }
    public void setOwner_name(String owner_name) { this.owner_name = owner_name; }
    public String getProvider_gmail() { return provider_gmail; }
    public void setProvider_gmail(String provider_gmail) { this.provider_gmail = provider_gmail; }
    public String getProvider_phone() { return provider_phone; }
    public void setProvider_phone(String provider_phone) { this.provider_phone = provider_phone; }
    public String getProvider_address() { return provider_address; }
    public void setProvider_address(String provider_address) { this.provider_address = provider_address; }
    public String getVehicle_name() { return vehicle_name; }
    public void setVehicle_name(String vehicle_name) { this.vehicle_name = vehicle_name; }
    public String getVehicle_price() { return vehicle_price; }
    public void setVehicle_price(String vehicle_price) { this.vehicle_price = vehicle_price; }
    public String getVehicle_seats() { return vehicle_seats; }
    public void setVehicle_seats(String vehicle_seats) { this.vehicle_seats = vehicle_seats; }
    public String getVehicle_number() { return vehicle_number; }
    public void setVehicle_number(String vehicle_number) { this.vehicle_number = vehicle_number; }
    public String getVehicle_availability() { return vehicle_availability; }
    public void setVehicle_availability(String vehicle_availability) { this.vehicle_availability = vehicle_availability; }
    public String getVehicle_imageURL() { return vehicle_imageURL; }
    public void setVehicle_imageURL(String vehicle_imageURL) { this.vehicle_imageURL = vehicle_imageURL; }
    public String getVehicle_rating() { return vehicle_rating; }
    public void setVehicle_rating(String vehicle_rating) { this.vehicle_rating = vehicle_rating; }
    public String getFuel_type() { return fuel_type; }
    public void setFuel_type(String fuel_type) { this.fuel_type = fuel_type; }
    public String getMax_speed() { return max_speed; }
    public void setMax_speed(String max_speed) { this.max_speed = max_speed; }
    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    public String getDoors_seats() { return doors_seats; }
    public void setDoors_seats(String doors_seats) { this.doors_seats = doors_seats; }
}