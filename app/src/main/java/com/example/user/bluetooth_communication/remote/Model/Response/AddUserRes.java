package com.example.user.bluetooth_communication.remote.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddUserRes {
    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("status")
    private String status;

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
