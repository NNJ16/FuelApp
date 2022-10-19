package com.example.fuelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView lblUser;
    AutoCompleteTextView txtShedList;
    ImageView imgAdd;
    TextView lblDiesel, lblPetrol, lblBike,lblCar, lblTruck, lblBus, lblVan, lblThreeWheel;
    User user;
    Spinner cmbVehicles;
    Button btnJoin, btnExitBP, btnExitAP;
    String vehicleType="";
    Queue joinedQueue = null;

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
                    return true;
                }
            }
            return false;
        });

        btnJoin.setOnClickListener(view -> joinQueue());
        btnExitBP.setOnClickListener(view -> exitBeforePumpQueue());
        btnExitAP.setOnClickListener(view -> exitAfterPumpQueue());
        imgAdd.setOnClickListener(view -> addVehicle());
    }
    private  String m_Text = "";

    private void addVehicle(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Enter Date");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(32, 0, 32, 0);
        final EditText input = new EditText(this);
        input.setLayoutParams(lp);
        input.setGravity(android.view.Gravity.TOP|android.view.Gravity.LEFT);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setLines(1);
        input.setMaxLines(1);
        container.addView(input, lp);

        alertDialog.setView(container);

        alertDialog.show();

    }

    private void initializeServices(){
        getAllSheds();
        getVehicles();
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
                            lblPetrol.setText(fuel.getFuelStatus());
                        }else if(fuel.getFuelType().equalsIgnoreCase("Diesel")){
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
                txtShedList.getText().toString(),
                cmbVehicles.getSelectedItem().toString().split(",")[0],
                new Date(),
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
                    String[] arr = new String[vehicleList.size()];
                    if(vehicleList.size() != 0){
                        for(int i=0; i< vehicleList.size(); i++){
                            arr[i] = vehicleList.get(i).getVehicleType()+", "+vehicleList.get(i).getVehicleNo();
                        }
                        setCmbVehicleList(arr);
                    }else{
                        arr[0] = "No Vehicle Found";
                    }
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, arr);
        cmbVehicles.setAdapter(adapter);
    }
}

