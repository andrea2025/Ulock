package com.example.user.bluetooth_communication.remote.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLogin {
    @Expose
    @SerializedName("token")
    private String token ;

    public String getToken() {
        return token;
    }

}
