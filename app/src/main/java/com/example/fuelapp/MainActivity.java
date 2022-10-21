package com.example.fuelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fuelapp.model.Fuel;
import com.example.fuelapp.model.Queue;
import com.example.fuelapp.model.QueueStatus;
import com.example.fuelapp.model.Shed;
import com.example.fuelapp.model.User;
import com.example.fuelapp.model.Vehicle;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView lblUser;
    AutoCompleteTextView txtShedList;
    ImageView imgAdd, imgRemove;
    TextView lblDiesel, lblPetrol, lblBike,lblCar,
            lblTruck, lblBus, lblVan, lblThreeWheel, lblAvgWait, lblDPrice,lblPPrice;
    User user;
    Spinner cmbVehicles;
    Button btnJoin, btnExitBP, btnExitAP;
    String vehicleType="";
    static Queue joinedQueue = null;
    static String[] vehicleList=null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblUser = findViewById(R.id.lblMUser);
        txtShedList = findViewById(R.id.txtShedSearch);
        lblDiesel = findViewById(R.id.lblMDiesel);
        lblPetrol = findViewById(R.id.lblMPetrol);
        lblBike = findViewById(R.id.txtBike);
        lblCar = findViewById(R.id.txtCar);
        lblTruck =findViewById(R.id.txtTruck);
        lblBus = findViewById(R.id.txtBus);
        lblVan = findViewById(R.id.txtVan);
        lblThreeWheel = findViewById(R.id.txtThreeWheel);
        cmbVehicles= findViewById(R.id.cmbVehicles);
        btnJoin = findViewById(R.id.btnJoin);
        btnExitAP = findViewById(R.id.btnEAP);
        btnExitBP = findViewById(R.id.btnExitBP);
        imgAdd = findViewById(R.id.imgAdd);
        imgRemove = findViewById(R.id.imgRemove);
        lblAvgWait = findViewById(R.id.lblAvgWait);
        lblDPrice = findViewById(R.id.lblMDPrice);
        lblPPrice = findViewById(R.id.lblMPPrice);

        btnJoin.setEnabled(false);
        btnExitAP.setEnabled(false);
        btnExitBP.setEnabled(false);

        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("USER_INFO");

        initializeServices();

        lblUser.setText("Hi " +user.getName().split(" ")[0]);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        txtShedList.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (txtShedList.getRight() - txtShedList.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    getShedFuelStatus();
                    getQueueStatus();
                    getAverageWaitingTime();
                    return true;
                }
            }
            return false;
        });

        btnJoin.setOnClickListener(view -> joinQueue());
        btnExitBP.setOnClickListener(view -> exitBeforePumpQueue());
        btnExitAP.setOnClickListener(view -> exitAfterPumpQueue());
        imgAdd.setOnClickListener(view -> showAddVehicleDialog());
        imgRemove.setOnClickListener(view -> {
            if(vehicleList != null){
                showRemoveVehicleDialog();
            }else{
                Toast.makeText(getApplicationContext(), "No vehicles to remove.", Toast.LENGTH_LONG).show();
            }
        });
    }

    //Implement add vehicle dialog view
    private void showAddVehicleDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Enter Vehicle Details");
        String[] vehicles = new String[]{"Bike","Car","ThreeWheel","Van","Bus","Truck" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, vehicles);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(55, 10, 55, 10);
        final EditText input = new EditText(this);
        final Spinner spinner = new Spinner(this);
        final TextView lblText = new TextView(this);
        final TextView paddingText = new TextView(this);
        final TextView paddingText1 = new TextView(this);
        final Button button = new Button(this);
        final Button button1 = new Button(this);

        input.setGravity(android.view.Gravity.TOP|android.view.Gravity.LEFT);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint("Enter vehicle no");
        input.setPadding(24,16,16,24);
        input.setBackground(ContextCompat.getDrawable(this, R.drawable.search_background));
        input.setLines(1);
        input.setMaxLines(1);
        lblText.setText(" Select vehicle type");
        lblText.setTextColor(Color.parseColor("#000000"));
        button.setText("ADD VEHICLE");
        button1.setText("CANCEL");

        spinner.setGravity(android.view.Gravity.TOP|android.view.Gravity.LEFT);
        spinner.setBackground(ContextCompat.getDrawable(this, R.drawable.orange_color_background));
        spinner.setAdapter(adapter);
        container.addView(paddingText, lp);
        container.addView(input, lp);
        container.addView(lblText, lp);
        container.addView(spinner, lp);
        container.addView(button, lp);
        container.addView(button1, lp);
        container.addView(paddingText1, lp);
        alertDialog.setView(container);

        button.setOnClickListener(v -> addVehicle(input.getText().toString(), spinner.getSelectedItem().toString(), alertDialog));
        button1.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();

    }

    //Implement add vehicle dialog view
    private void showRemoveVehicleDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Remove Vehicle Details");
        String[] vehicles = new String[]{"Bike","Car","ThreeWheel","Van","Bus","Truck" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, vehicleList);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(55, 10, 55, 10);
        final Spinner spinner = new Spinner(this);
        final TextView lblText = new TextView(this);
        final TextView paddingText = new TextView(this);
        final TextView paddingText1 = new TextView(this);
        final Button button = new Button(this);
        final Button button1 = new Button(this);

        lblText.setText(" Select your vehicle");
        lblText.setTextColor(Color.parseColor("#000000"));
        button.setText("REMOVE VEHICLE");
        button1.setText("CANCEL");

        spinner.setGravity(android.view.Gravity.TOP|android.view.Gravity.LEFT);
        spinner.setBackground(ContextCompat.getDrawable(this, R.drawable.orange_color_background));
        spinner.setAdapter(adapter);
        container.addView(paddingText, lp);
        container.addView(lblText, lp);
        container.addView(spinner, lp);
        container.addView(button, lp);
        container.addView(button1, lp);
        container.addView(paddingText1, lp);
        alertDialog.setView(container);

        button.setOnClickListener(v -> removeVehicle(spinner.getSelectedItem().toString().split(",")[1].trim(), alertDialog));
        button1.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();

    }

    //Initialize required services
    private void initializeServices(){
        getAllSheds();
        getVehicles();
    }

    // add vehicle for the user
    private void addVehicle(String vehicleNo, String vehicleType, AlertDialog alertDialog){
        Call<Vehicle> call = RetrofitClient.getInstance().getMyApi().createVehicle(new Vehicle(user.getId(), vehicleNo, vehicleType));

        call.enqueue(new Callback<Vehicle>() {
            @Override
            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                Vehicle vehicle = response.body();
                if(vehicle != null){
                    getVehicles();
                    alertDialog.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(), "Unable to add the vehicle.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // remove vehicle from the user
    private void removeVehicle(String vehicleNo, AlertDialog alertDialog){
        Call<Vehicle> call = RetrofitClient.getInstance().getMyApi().deleteVehicle(new Vehicle(user.getId(), vehicleNo, ""));

        call.enqueue(new Callback<Vehicle>() {
            @Override
            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                Vehicle vehicle = response.body();
                if(vehicle != null){
                    getVehicles();
                    alertDialog.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(), "Unable to delete the vehicle.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //Get All shed Details
    private void getAllSheds() {
        Call <List<Shed>> call = RetrofitClient.getInstance().getMyApi().getAllSheds();

        call.enqueue(new Callback<List<Shed>>() {
            @Override
            public void onResponse(Call<List<Shed>> call, Response<List<Shed>> response) {
                List<Shed> shedList = response.body();
                String[] arr = new String[shedList.size()];

                for(int i=0; i< shedList.size(); i++){
                    arr[i] = shedList.get(i).getShedName();
                }
                setTxtShedList(arr);
            }

            @Override
            public void onFailure(Call<List<Shed>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //set values for shed list combo
    public void setTxtShedList(String[] arr){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, arr);
        txtShedList.setThreshold(2);
        txtShedList.setAdapter(adapter);
    }

    //Get Shed Fuel Status
    private void getShedFuelStatus() {
        Call <List<Fuel>> call = RetrofitClient.getInstance().getMyApi().getAllFuelsByShedName(new Shed("",txtShedList.getText().toString()));

        call.enqueue(new Callback<List<Fuel>>() {
            @Override
            public void onResponse(Call<List<Fuel>> call, Response<List<Fuel>> response) {
                List<Fuel> fuelList = response.body();
                if(fuelList.size() != 0){
                    for (Fuel fuel: fuelList) {
                        if(fuel.getFuelType().equalsIgnoreCase("Petrol")){
                            lblPPrice.setText("LKR "+fuel.getFuelPrice());
                            if(Integer.parseInt(fuel.getFuelStatus()) >= 1000){
                                lblPetrol.setTextColor(Color.parseColor("#045c23"));
                            }else{
                                lblPetrol.setTextColor(Color.parseColor("#eb4034"));
                            }
                            lblPetrol.setText(fuel.getFuelStatus());
                        }else if(fuel.getFuelType().equalsIgnoreCase("Diesel")){
                            lblDPrice.setText("LKR "+fuel.getFuelPrice());
                            if(Integer.parseInt(fuel.getFuelStatus()) >= 1000){
                                lblDiesel.setTextColor(Color.parseColor("#045c23"));
                            }else{
                                lblDiesel.setTextColor(Color.parseColor("#eb4034"));
                            }
                            lblDiesel.setText(fuel.getFuelStatus());
                        }
                        btnJoin.setEnabled(true);
                        btnExitAP.setEnabled(false);
                        btnExitBP.setEnabled(false);
                    }
                }else{
                    lblPetrol.setText("0");
                    lblDiesel.setText("0");
                    btnJoin.setEnabled(false);
                    btnExitAP.setEnabled(false);
                    btnExitBP.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Unable to find the Shed.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Fuel>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getAverageWaitingTime(){
        Call<String> call = RetrofitClient.getInstance().getMyApi().getAvgWaitingTime(new Shed("",txtShedList.getText().toString()));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String avg = response.body();
                if(avg != null){
                    lblAvgWait.setText(avg.substring(0,5)+"hrs");
                    lblAvgWait.setTextColor(Color.parseColor("#32a852"));

                }else{
                    lblAvgWait.setText("0 hrs");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //Get Queue status
    private void getQueueStatus() {
        Call<QueueStatus> call = RetrofitClient.getInstance().getMyApi().getQueueStatusByShedName(new Shed("",txtShedList.getText().toString()));

        call.enqueue(new Callback<QueueStatus>() {
            @Override
            public void onResponse(Call<QueueStatus> call, Response<QueueStatus> response) {
                QueueStatus queue = response.body();
                if(queue != null){
                    lblBike.setText(Integer.toString(queue.getBike()));
                    lblVan.setText(Integer.toString(queue.getVan()));
                    lblBus.setText(Integer.toString(queue.getBus()));
                    lblCar.setText(Integer.toString(queue.getCar()));
                    lblTruck.setText(Integer.toString(queue.getTruck()));
                    lblThreeWheel.setText(Integer.toString(queue.getThreeWheel()));
                }else{
                    lblBike.setText("0");
                    lblVan.setText("0");
                    lblThreeWheel.setText("0");
                    lblBus.setText("0");
                    lblTruck.setText("0");
                    lblCar.setText("0");
                }
            }

            @Override
            public void onFailure(Call<QueueStatus> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //join vehicle to Queue
    private void joinQueue(){
        Call<Queue> call = RetrofitClient.getInstance().getMyApi().createQueue(new Queue(
                user.getId(),
                txtShedList.getText().toString(),
                cmbVehicles.getSelectedItem().toString().split(",")[0],
                new Date(),
                null,
                true,
                false,
                false
        ));

        call.enqueue(new Callback<Queue>() {
            @Override
            public void onResponse(Call<Queue> call, Response<Queue> response) {
                Queue queue = response.body();
                if(queue != null){
                    btnJoin.setEnabled(false);
                    getQueueStatus();
                    btnExitAP.setEnabled(true);
                    btnExitBP.setEnabled(true);
                    joinedQueue = queue;
                }
            }

            @Override
            public void onFailure(Call<Queue> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //exit vehicle before pump
    private void exitBeforePumpQueue(){
        Call<Queue> call = RetrofitClient.getInstance().getMyApi().updateQueue(new Queue(
                user.getId(),
                joinedQueue.getShedName(),
                joinedQueue.getVehicleType(),
                joinedQueue.getArivalTime(),
                new Date(),
                false,
                true,
                false
        ));

        call.enqueue(new Callback<Queue>() {
            @Override
            public void onResponse(Call<Queue> call, Response<Queue> response) {
                Queue queue = response.body();
                if(queue != null){
                    btnJoin.setEnabled(true);
                    getQueueStatus();
                    btnExitAP.setEnabled(false);
                    btnExitBP.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<Queue> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //exit vehicle after pump
    private void exitAfterPumpQueue(){
        Call<Queue> call = RetrofitClient.getInstance().getMyApi().updateQueue(new Queue(
                user.getId(),
                joinedQueue.getShedName(),
                joinedQueue.getVehicleType(),
                joinedQueue.getArivalTime(),
                new Date(),
                false,
                false,
                true
        ));

        call.enqueue(new Callback<Queue>() {
            @Override
            public void onResponse(Call<Queue> call, Response<Queue> response) {
                Queue queue = response.body();
                if(queue != null){
                    btnJoin.setEnabled(true);
                    getQueueStatus();
                    btnExitAP.setEnabled(false);
                    btnExitBP.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<Queue> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //Get All Vehicle for the user
    private void getVehicles() {
        Call <List<Vehicle>> call = RetrofitClient.getInstance().getMyApi().getVehicleByUserId(new User(user.getId()));

        call.enqueue(new Callback<List<Vehicle>>() {
            @Override
            public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {
                List<Vehicle> vehicleList = response.body();
                if(vehicleList != null){
                    String[] arr = null;
                    if(vehicleList.size() != 0){
                        arr = new String[vehicleList.size()];

                        for(int i=0; i< vehicleList.size(); i++){
                            arr[i] = vehicleList.get(i).getVehicleType()+", "+vehicleList.get(i).getVehicleNo();
                        }
                    }else{
                        arr = new String[1];
                        arr[0] = "No Vehicle Found";
                    }
                    setCmbVehicleList(arr);

                }else{
                    Toast.makeText(getApplicationContext(), "Not found Vehicles", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setCmbVehicleList(String[] arr){
        vehicleList = arr;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arr);
        cmbVehicles.setAdapter(adapter);
    }
}

