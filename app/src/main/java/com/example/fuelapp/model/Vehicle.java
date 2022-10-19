package com.example.fuelapp.model;

public class Vehicle {
    private String userId;
    private String vehicleNo;
    private String vehicleType;

    public Vehicle(String userId, String vehicleNo, String vehicleType) {
        this.userId = userId;
        vehicleNo = vehicleNo;
        this.vehicleType = vehicleType;
    }

    public String getUserId() {
        return userId;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public String getVehicleType() {
        return vehicleType;
    }
}
