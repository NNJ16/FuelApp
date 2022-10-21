package com.example.fuelapp.model;

public class Shed {
    String ownerId;
    String shedName;
    String address;

    public Shed(String ownerId, String shedName) {
        this.ownerId = ownerId;
        this.shedName = shedName;
    }

    public Shed(String ownerId, String shedName, String address) {
        this.ownerId = ownerId;
        this.shedName = shedName;
        this.address = address;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getShedName() {
        return shedName;
    }

    public String getAddress() {
        return address;
    }
}
