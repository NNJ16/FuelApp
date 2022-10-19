package com.example.fuelapp.model;

import java.util.Date;

public class Fuel {
   private String shedName;
   private String fuelType;
   private Date arrivalTime;
   private Date finishedTime;
   private String fuelStatus;

   public Fuel(String shedName, String fuelType, Date arrivalTime, Date finishedTime, String fuelStatus) {
      this.shedName = shedName;
      this.fuelType = fuelType;
      this.arrivalTime = arrivalTime;
      this.finishedTime = finishedTime;
      this.fuelStatus = fuelStatus;
   }

   public String getShedName() {
      return shedName;
   }

   public String getFuelType() {
      return fuelType;
   }

   public Date getArrivalTime() {
      return arrivalTime;
   }

   public Date getFinishedTime() {
      return finishedTime;
   }

   public String getFuelStatus() {
      return fuelStatus;
   }
}
