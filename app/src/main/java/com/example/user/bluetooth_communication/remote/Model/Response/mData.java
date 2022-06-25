package com.example.user.bluetooth_communication.remote.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class mData {
    @Expose
    @SerializedName("usersInfo")
    private List<UserInfo> userInfo;

    @Expose
    @SerializedName("idsOnDevice")
    private String idDevice;

    public String  getIdDevice() {
        return idDevice;
    }

    public List<UserInfo> getUserInfo() {
        return userInfo;
    }
}
