package com.example.user.bluetooth_communication.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.user.bluetooth_communication.R;
import com.example.user.bluetooth_communication.remote.AppUtils;
import com.example.user.bluetooth_communication.remote.Model.Request.LoginRequest;
import com.example.user.bluetooth_communication.remote.Model.Response.LoginResponse;
import com.example.user.bluetooth_communication.remote.SharedPref;
import com.example.user.bluetooth_communication.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public UserService mService = AppUtils.mService();
    ProgressDialog mProgressDialog;
    EditText userName;
    Button mLogin;
   SharedPref sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       sharedPreferences = new SharedPref(getApplicationContext());

        userName = findViewById(R.id.mName);
        mLogin = findViewById(R.id.btnLogin);

        mLogin.setOnClickListener(this);

    }

    public void userLogin(String userName) {
        mProgressDialog = ProgressDialog.show(this, "Log in"
                , "Please Wait...", true);
        LoginRequest.Request request = new LoginRequest.Request(userName);
        mService.login(request).enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    LoginResponse resp = response.body();

                    String userToken = response.body().getLoginData().getToken();
                    String mesage = response.body().getMessage();
                    sharedPreferences.createLoginSession(userToken);
                    Intent intent = new Intent(getApplicationContext(), SelectDeviceActivity.class);
                    startActivity(intent);
                    finish();
                    Log.i("success", "onResponse: " + resp);
                    Log.i("success", userToken);
                    Toast.makeText(getApplicationContext(),mesage, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(),response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.i("FAILURE MESSAGE", "Login failed");
            }
        });


    }

    @Override
    public void onClick(View view) {
        if (!TextUtils.isEmpty(userName.getText().toString().trim())){
            userLogin(userName.getText().toString().trim());

        }else {
            Toast.makeText(this, "Field must not be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPreferences.isLoggedIn()){
            Intent intent = new Intent(getApplicationContext(), SelectDeviceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    }

    @Override
        public void onDestroy(){
            super.onDestroy();
            if(mProgressDialog!=null)
                if(mProgressDialog.isShowing()){
                    mProgressDialog.cancel();
                }

        }

}