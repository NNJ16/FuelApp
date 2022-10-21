package com.example.fuelapp;


import com.example.fuelapp.model.Fuel;
import com.example.fuelapp.model.Queue;
import com.example.fuelapp.model.QueueStatus;
import com.example.fuelapp.model.Shed;
import com.example.fuelapp.model.User;
import com.example.fuelapp.model.Vehicle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface Api {
    // User API

    //Get All Users
    @GET("user")
    Call<List<User>> getAllUsers();

    //Create User
    @POST("user/create")
    Call<User> createUser(@Body User user);

    //Find User
    @POST("user/find")
    Call<User> findUser(@Body User user);

    //Shed API

    //Get All Sheds
    @GET("shed")
    Call<List<Shed>> getAllSheds();

    //Create Shed
    @POST("shed/create")
    Call<Shed> createShed(@Body Shed shed);

    //Create Shed
    @POST("shed/delete")
    Call<Shed> deleteShed(@Body Shed shed);

    //Find Shed
    @POST("shed/find")
    Call <List<Shed>> findShedsByOwner(@Body User user);


    //Fuel API

    //Create Shed
    @POST("fuel/create")
    Call<Fuel> createFuel(@Body Fuel fuel);

    //Delete Shed
    @POST("fuel/delete")
    Call<Fuel> deleteFuel(@Body Fuel fuel);

    //update Shed
    @PUT("fuel/update")
    Call<Fuel> updateFuel(@Body Fuel fuel);

    //Find Shed Fuel Status
    @POST("fuel/status")
    Call<List<Fuel>> getAllFuelsByShedName(@Body Shed shed);

    //Queue API

    //Get Queue Status
    @POST("queue/status")
    Call<QueueStatus> getQueueStatusByShedName(@Body Shed shed);

    //Get Queue Status
    @POST("queue/create")
    Call<Queue> createQueue(@Body Queue queue);

    //Update Queue Status
    @PUT("queue/update")
    Call<Queue> updateQueue(@Body Queue queue);

    //Update Queue Status
    @POST("queue/shed/users")
    Call<List<Queue>> getVehiclesByShed(@Body Shed shed);

    //Get queue average waiting time
    @POST("queue/wait/average")
    Call<String> getAvgWaitingTime(@Body Shed shed);

    //Vehicle API

    //Get Owners Vehicles
    @POST("vehicle/find")
    Call<List<Vehicle>> getVehicleByUserId(@Body User user);

    //Create a Vehicle
    @POST("vehicle/create")
    Call<Vehicle> createVehicle(@Body Vehicle vehicle);

    //Create a Vehicle
    @POST("vehicle/delete")
    Call<Vehicle> deleteVehicle(@Body Vehicle vehicle);

}
