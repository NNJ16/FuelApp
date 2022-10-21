package com.example.fuelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fuelapp.model.Fuel;
import com.example.fuelapp.model.Queue;
import com.example.fuelapp.model.Shed;
import com.example.fuelapp.model.User;
import com.example.fuelapp.model.Vehicle;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {
    Spinner cmbShedList, cmbFuelTypeList;
    User user;
    ImageView imgAdd, imgRemove;
    TextView lblPetrol, lblDiesel, lblPPrice, lblDPrice, lblEAP, lblEBP, lblIQ;
    EditText txtFuelPrice, txtFuelAmount;
    static String[] shedList=null;
    Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Intent intent = getIntent();
        user = (User)intent.getSerializableExtra("USER_INFO");

        cmbShedList =  findViewById(R.id.cmbASheds);
        imgAdd = findViewById(R.id.btnAAdd);
        imgRemove = findViewById(R.id.btnARemove);
        lblPetrol = findViewById(R.id.lblAPetrol);
        lblDiesel = findViewById(R.id.lblADiesel);
        btnUpdate = findViewById(R.id.btnUpdateFuel);
        lblDPrice= findViewById(R.id.lblADPrice);
        lblPPrice = findViewById(R.id.lblAPPrice);
        cmbFuelTypeList = findViewById(R.id.cmbAFuelType);
        txtFuelPrice = findViewById(R.id.txtAFPrice);
        txtFuelAmount = findViewById(R.id.txtAAmount);
        lblEAP = findViewById(R.id.lblAEAP);
        lblEBP = findViewById(R.id.lblAEBP);
        lblIQ = findViewById(R.id.lblAIN);

        String[] arr = new String[]{"Petrol","Diesel"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arr);
        cmbFuelTypeList.setAdapter(adapter);

        initializeServices();

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        imgAdd.setOnClickListener(v ->showAddVehicleDialog());
        imgRemove.setOnClickListener(v ->showRemoveShedDialog());
        btnUpdate.setOnClickListener(v ->updateFuel());

        cmbShedList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                getShedFuelStatus();
                getVehicleCount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void initializeServices(){
        getSheds();
    }

    //Implement add vehicle dialog view
    private void showAddVehicleDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Enter Shed Details");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(55, 10, 55, 10);
        final EditText input = new EditText(this);
        final EditText input1 = new EditText(this);
        final TextView paddingText = new TextView(this);
        final TextView paddingText1 = new TextView(this);
        final TextView paddingText3 = new TextView(this);
        final Button button = new Button(this);
        final Button button1 = new Button(this);

        input.setHint("Enter shed name");
        input.setPadding(24,18,18,24);
        input.setBackground(ContextCompat.getDrawable(this, R.drawable.search_background));

        input1.setHint("Enter shed address");
        input1.setPadding(24,18,18,24);
        input1.setBackground(ContextCompat.getDrawable(this, R.drawable.search_background));

        button.setText("ADD Shed");
        button1.setText("CANCEL");

        container.addView(paddingText, lp);
        container.addView(input, lp);
        container.addView(input1, lp);
        container.addView(paddingText3, lp);
        container.addView(button, lp);
        container.addView(button1, lp);
        container.addView(paddingText1, lp);
        alertDialog.setView(container);

        button.setOnClickListener(v -> addShed(input.getText().toString(), input1.getText().toString(), alertDialog));
        button1.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }

    //Implement add vehicle dialog view
    private void showRemoveShedDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Remove Shed Details");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, shedList);

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

        button.setOnClickListener(v -> removeShed(spinner.getSelectedItem().toString(), alertDialog));
        button1.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();

    }

    // add shed for the user
    private void addShed(String shedName, String shedAddress, AlertDialog alertDialog){
        Call<Shed> call = RetrofitClient.getInstance().getMyApi().createShed(new Shed(user.getId(), shedName, shedAddress));

        call.enqueue(new Callback<Shed>() {
            @Override
            public void onResponse(Call<Shed> call, Response<Shed> response) {
                Shed shed = response.body();
                if(shed != null){
                    getSheds();
                    addFuel(shedName, "Petrol");
                    addFuel(shedName, "Diesel");
                    alertDialog.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(), "Unable to add the shed.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Shed> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // add fuel for the shed
    private void addFuel(String shedName, String fuelType){

        Call<Fuel> call = RetrofitClient.getInstance().getMyApi().createFuel(new Fuel(shedName, fuelType,null,null,"0","0"));

        call.enqueue(new Callback<Fuel>() {
            @Override
            public void onResponse(Call<Fuel> call, Response<Fuel> response) {
                Fuel fuel = response.body();
                if(fuel != null){

                }else{
                    Toast.makeText(getApplicationContext(), "Unable to add fuel for the shed.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Fuel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // add fuel for the shed
    private void updateFuel(){
        Call<Fuel> call;
        if(Integer.parseInt(txtFuelAmount.getText().toString()) > 0){
           call = RetrofitClient.getInstance().getMyApi().updateFuel(new Fuel(cmbShedList.getSelectedItem().toString(), cmbFuelTypeList.getSelectedItem().toString(),
                   new Date(),null,txtFuelAmount.getText().toString(),txtFuelPrice.getText().toString()));
        }else{
            call = RetrofitClient.getInstance().getMyApi().updateFuel(new Fuel(cmbShedList.getSelectedItem().toString(), cmbFuelTypeList.getSelectedItem().toString(),
                    null,new Date(),txtFuelAmount.getText().toString(),txtFuelPrice.getText().toString()));
        }

        call.enqueue(new Callback<Fuel>() {
            @Override
            public void onResponse(Call<Fuel> call, Response<Fuel> response) {
                Fuel fuel = response.body();
                if(fuel != null){
                    Toast.makeText(getApplicationContext(), "Fuel status updated successfully.", Toast.LENGTH_LONG).show();
                    getShedFuelStatus();
                    txtFuelAmount.setText("");
                    txtFuelPrice.setText("");
                }else{
                    Toast.makeText(getApplicationContext(), "Unable to update fuel for the shed.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Fuel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // add fuel for the shed
    private void removeFuel(String shedName, String fuelType){
        Call<Fuel> call = RetrofitClient.getInstance().getMyApi().deleteFuel(new Fuel(shedName, fuelType,null,null,"0","0"));

        call.enqueue(new Callback<Fuel>() {
            @Override
            public void onResponse(Call<Fuel> call, Response<Fuel> response) {
                Fuel fuel = response.body();
                if(fuel != null){

                }else{
                    Toast.makeText(getApplicationContext(), "Unable to delete fuel for the shed.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Fuel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // remove shed from the user
    private void removeShed(String shedName, AlertDialog alertDialog){
        Call<Shed> call = RetrofitClient.getInstance().getMyApi().deleteShed(new Shed(user.getId(), shedName, ""));

        call.enqueue(new Callback<Shed>() {
            @Override
            public void onResponse(Call<Shed> call, Response<Shed> response) {
                Shed shed = response.body();
                if(shed != null){
                    getSheds();
                    removeFuel(shedName, "Petrol");
                    removeFuel(shedName, "Diesel");
                    alertDialog.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(), "Unable to delete the shed.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Shed> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getSheds() {
        Call<List<Shed>> call = RetrofitClient.getInstance().getMyApi().findShedsByOwner(new User(user.getId()));

        call.enqueue(new Callback<List<Shed>>() {
            @Override
            public void onResponse(Call<List<Shed>> call, Response<List<Shed>> response) {
                List<Shed> shedList = response.body();
                String[] arr = new String[shedList.size()];

                for(int i=0; i< shedList.size(); i++){
                    arr[i] = shedList.get(i).getShedName();
                }
                setCmbShedList(arr);
            }

            @Override
            public void onFailure(Call<List<Shed>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //Get Shed Fuel Status
    private void getShedFuelStatus() {
        Call <List<Fuel>> call = RetrofitClient.getInstance().getMyApi().getAllFuelsByShedName(new Shed("",cmbShedList.getSelectedItem().toString()));

        call.enqueue(new Callback<List<Fuel>>() {
            @Override
            public void onResponse(Call<List<Fuel>> call, Response<List<Fuel>> response) {
                List<Fuel> fuelList = response.body();
                if(fuelList.size() != 0){
                    for (Fuel fuel: fuelList) {
                        if(fuel.getFuelType().equalsIgnoreCase("Petrol")){
                            if(Integer.parseInt(fuel.getFuelStatus()) >= 1000){
                                lblPetrol.setTextColor(Color.parseColor("#045c23"));
                            }else{
                                lblPetrol.setTextColor(Color.parseColor("#eb4034"));
                            }
                            lblPetrol.setText(fuel.getFuelStatus());
                            lblPPrice.setText("LKR "+fuel.getFuelPrice());
                        }else if(fuel.getFuelType().equalsIgnoreCase("Diesel")){
                            if(Integer.parseInt(fuel.getFuelStatus()) >= 1000){
                                lblDiesel.setTextColor(Color.parseColor("#045c23"));
                            }else{
                                lblDiesel.setTextColor(Color.parseColor("#eb4034"));
                            }
                            lblDiesel.setText(fuel.getFuelStatus());
                            lblDPrice.setText("LKR "+fuel.getFuelPrice());
                        }
                        btnUpdate.setEnabled(true);

                    }
                }else{
                    lblPetrol.setText("0");
                    lblDiesel.setText("0");
                    btnUpdate.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Unable to find the Fuel Status.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Fuel>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getVehicleCount() {
        Call<List<Queue>> call = RetrofitClient.getInstance().getMyApi().getVehiclesByShed(new Shed("",cmbShedList.getSelectedItem().toString()));

        call.enqueue(new Callback<List<Queue>>() {
            @Override
            public void onResponse(Call<List<Queue>> call, Response<List<Queue>> response) {
                List<Queue> queueList = response.body();
                int ebp=0,eap=0, iq=0;
                if(queueList.size()>0){
                    for (Queue queue: queueList) {
                        if(queue.isInQueue()){
                            iq += 1;
                        }else  if(queue.isExitAfterPump()){
                            eap += 1;
                        }else if(queue.isExitBeforePump()){
                            ebp +=1;
                        }
                    }
                    lblEAP.setText(Integer.toString(eap));
                    lblEBP.setText(Integer.toString(ebp));
                    lblIQ.setText(Integer.toString(iq));
                }else{
                    lblEAP.setText("0");
                    lblEBP.setText("0");
                    lblIQ.setText("0");
                }
            }

            @Override
            public void onFailure(Call<List<Queue>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //set values for shed list combo
    public void setCmbShedList(String[] arr){
        shedList = arr;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arr);
        cmbShedList.setAdapter(adapter);
    }
}