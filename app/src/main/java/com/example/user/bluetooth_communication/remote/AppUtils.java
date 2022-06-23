package com.example.user.bluetooth_communication.remote;

import android.content.Context;

public class AppUtils {

    UserService mService;

    public static final String BASE_URL = "https://ulock-backend.herokuapp.com/";
    public Context context;

    public static UserService mService(){
        return RetrofitBuilder.getRetrofit(BASE_URL).create(UserService.class);
    }
}
