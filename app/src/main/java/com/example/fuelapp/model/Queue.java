package com.example.fuelapp.model;

import java.util.Date;

public class Queue {
    private String userId;
    private String shedName;
    private String vehicleType;
    private Date arivalTime;
    private Date departTime;
    private boolean isInQueue;
    private boolean isExitBeforePump;
    private boolean isExitAfterPump;

    public Queue(String userId, String shedName, String vehicleType, Date arivalTime, Date departTime, boolean isInQueue, boolean isExitBeforePump, boolean isExitAfterPump) {
        this.userId = userId;
        this.shedName = shedName;
        this.vehicleType = vehicleType;
        this.arivalTime = arivalTime;
        this.departTime = departTime;
        this.isInQueue = isInQueue;
        this.isExitBeforePump = isExitBeforePump;
        this.isExitAfterPump = isExitAfterPump;
    }

    public String getUserId() {
        return userId;
    }

    public String getShedName() {
        return shedName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public Date getArivalTime() {
        return arivalTime;
    }

    public Date getDepartTime() {
        return departTime;
    }

    public boolean isInQueue() {
        return isInQueue;
    }

    public boolean isExitBeforePump() {
        return isExitBeforePump;
    }

    public boolean isExitAfterPump() {
        return isExitAfterPump;
    }
}
