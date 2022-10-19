package com.example.fuelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fuelapp.dbhelper.DatabaseHelper;
import com.example.fuelapp.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    TextView lblSignUp;
    Button btnLogin;
    EditText txtUsername, txtPassword;
    SQLiteDatabase dbObject;
    DatabaseHelper dbHelper;
    Cursor cursor;
    String tempPass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lblSignUp = findViewById(R.id.lblLogin);
        btnLogin = findViewById(R.id.btnSignUp);
        txtUsername = findViewById(R.id.txtLUsername);
        txtPassword = findViewById(R.id.txtLPassword);

        dbHelper = new DatabaseHelper(this);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        lblSignUp.setOnClickListener(view -> GoToRegister());
        btnLogin.setOnClickListener(view -> authenticateUser());
    }
    //navigate to signup
    private void GoToRegister() {
        //getAllUsers();
        Intent RegisterIntent = new Intent(this, RegisterActivity.class);
        startActivity(RegisterIntent);
    }

    //check user is valid or not
    @SuppressLint("Range")
    private void authenticateUser(){
        if(checkAllFields()){
            dbObject = dbHelper.getWritableDatabase();
            cursor = dbObject.query(dbHelper.TABLE_NAME, null, " " + dbHelper.Table_Column_2_Email + "=?", new String[]{txtUsername.getText().toString()}, null, null, null);
            while (cursor.moveToNext()) {
                if (cursor.isFirst()) {
                    cursor.moveToFirst();
                    tempPass = cursor.getString(cursor.getColumnIndex(dbHelper.Table_Column_3_Password));
                    cursor.close();
                }
            }
            if(!tempPass.equals("") && tempPass.equals(txtPassword.getText().toString()))
            {
                //find for the user info in mongo db database
                findUser();
            }
            else {
                Toast.makeText(LoginActivity.this,"Username or Password is Wrong, Please Try Again.",Toast.LENGTH_LONG).show();
            }
        }
    }

    //check for empty fields
    public boolean checkAllFields(){
        if (txtUsername.length() == 0) {
            txtUsername.setError("Username is required");
            return false;
        }
        if (txtPassword.length() == 0) {
            txtPassword.setError("Password is required");
            return false;
        }
        return true;
    }

    private void findUser() {

        Call<User> call = RetrofitClient.getInstance().getMyApi().findUser(new User("", txtUsername.getText().toString(),""));

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();

                if(user != null){
                    goToMain(user);
                }else{
                    Toast.makeText(LoginActivity.this,"Unable to find the user.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //navigate to main page
    private void goToMain(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER_INFO", user);
        startActivity(intent);
    }
}