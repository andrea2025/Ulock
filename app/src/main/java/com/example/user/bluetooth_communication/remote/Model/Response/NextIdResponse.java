package com.example.user.bluetooth_communication.remote.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NextIdResponse {
    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("status")
    private String status;


    @Expose
    @SerializedName("data")
    private NextId nextid;

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public NextId getNextId() {
        return nextid;
    }
}
