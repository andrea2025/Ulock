package com.example.user.bluetooth_communication.remote.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NextId {
    @Expose
    @SerializedName("nextId")
    private String nextId;

    public String getNextId() {
        return nextId;
    }
}
