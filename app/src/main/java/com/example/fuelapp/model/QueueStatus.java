package com.example.fuelapp.model;

public class QueueStatus {
    private int car;
    private int van;
    private int threeWheel;
    private int bike;
    private int bus;
    private int truck;

    public QueueStatus(int car, int van, int threeWheel, int bike, int bus, int truck) {
        this.car = car;
        this.van = van;
        this.threeWheel = threeWheel;
        this.bike = bike;
        this.bus = bus;
        this.truck = truck;
    }

    public int getCar() {
        return car;
    }

    public int getVan() {
        return van;
    }

    public int getThreeWheel() {
        return threeWheel;
    }

    public int getBike() {
        return bike;
    }

    public int getBus() {
        return bus;
    }

    public int getTruck() {
        return truck;
    }

    @Override
    public String toString() {
        return "QueueStatus{" +
                "car=" + car +
                ", van=" + van +
                ", threeWheel=" + threeWheel +
                ", bike=" + bike +
                ", bus=" + bus +
                ", truck=" + truck +
                '}';
    }
}
