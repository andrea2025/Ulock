package com.example.user.bluetooth_communication.remote.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IdDevice {
    private List<String> deviceId;

    public List<String> getDeviceId() {
        return deviceId;
    }
}
