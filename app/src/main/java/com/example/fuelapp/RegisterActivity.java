package com.example.fuelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fuelapp.dbhelper.DatabaseHelper;
import com.example.fuelapp.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    TextView lblLogin;
    Button btnRegister;
    EditText txtEmail, txtPassword, txtName;
    Spinner cmbType;
    SQLiteDatabase dbObject;
    String queryHolder ;
    DatabaseHelper dbHelper;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        lblLogin = findViewById(R.id.lblLogin);
        btnRegister = (Button)findViewById(R.id.btnSignUp);
        txtEmail = (EditText)findViewById(R.id.txtSEmail);
        txtPassword = (EditText)findViewById(R.id.txtSPassword);
        txtName = (EditText)findViewById(R.id.txtSfname);
        cmbType = (Spinner)findViewById(R.id.cmbSUserType);

        dbHelper = new DatabaseHelper(this);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        lblLogin.setOnClickListener(view -> goToLogin());
        btnRegister.setOnClickListener(view -> register());
    }

    //go to login page
    private void goToLogin() {
        Intent RegisterIntent = new Intent(this, LoginActivity.class);
        startActivity(RegisterIntent);
    }

    //register user
    private void register(){
        initializeSQLiteDatabase();

        if(checkAllFields()){
            if(!isEmailAlreadyExist()){
                insertUserRecord();
                createUsers();
                goToLogin();
            }else{
                Toast.makeText(RegisterActivity.this,"Email Already Exists",Toast.LENGTH_LONG).show();
            }
        }
    }

    // Initialize SQLite database.
    public void initializeSQLiteDatabase(){
        dbObject = openOrCreateDatabase(DatabaseHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbObject.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseHelper.TABLE_NAME + "(" + DatabaseHelper.Table_Column_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DatabaseHelper.Table_Column_1_Name + " VARCHAR, " + DatabaseHelper.Table_Column_2_Email + " VARCHAR, " + DatabaseHelper.Table_Column_3_Password + " VARCHAR, " + DatabaseHelper.Table_Column_4_Type + " VARCHAR);");
    }

    // Check EditText are empty or Not.
    public boolean checkAllFields(){
        if (txtName.length() == 0) {
            txtName.setError("Full name is required");
            return false;
        }
        if (txtEmail.length() == 0) {
            txtEmail.setError("Email is required");
            return false;
        }
        if (txtPassword.length() == 0) {
            txtPassword.setError("Password is required");
            return false;
        }else if (txtPassword.length() < 8) {
            txtPassword.setError("Password must be minimum 8 characters");
            return false;
        }
        return true;
    }

    // Checking Email is already exists or not.
    public boolean isEmailAlreadyExist(){
        dbObject = dbHelper.getWritableDatabase();
        cursor = dbObject.query(dbHelper.TABLE_NAME, null, " " + dbHelper.Table_Column_2_Email + "=?", new String[]{txtEmail.getText().toString()}, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.isFirst()) {
                cursor.moveToFirst();
                cursor.close();
                return  true;
            }
        }
        return false;
    }

    //Add user record to database
    public void insertUserRecord(){
        queryHolder = "INSERT INTO "+ dbHelper.TABLE_NAME+" (name,email,password,type) VALUES('"+txtName.getText().toString()+"', '"+txtEmail.getText().toString()+"', '"+txtPassword.getText().toString()+"', '"+cmbType.getSelectedItem().toString()+"');";
        dbObject.execSQL(queryHolder);
        dbObject.close();
    }

    //create user for mongo db database with same credentials
    private void createUsers() {
        String type;

        if(cmbType.getSelectedItem().toString().equals("Vehicle Owner")){
            type = "user";
        }else{
            type = "owner";
        }

        User user = new User(txtName.getText().toString(), txtEmail.getText().toString(), type);
        Call<User> call = RetrofitClient.getInstance().getMyApi().createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user1 = response.body();
                Toast.makeText(getApplicationContext(), user1.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}