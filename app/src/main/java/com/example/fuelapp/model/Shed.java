package com.example.fuelapp.model;

public class Shed {
    String ownerId;
    String shedName;

    public Shed(String ownerId, String shedName) {
        this.ownerId = ownerId;
        this.shedName = shedName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getShedName() {
        return shedName;
    }
}
