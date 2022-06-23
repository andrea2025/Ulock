package com.example.user.bluetooth_communication.remote.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfo {
    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("subscriber_id")
    private String SubscriberId;

    @Expose
    @SerializedName("first_name")
    private String firstName;

    @Expose
    @SerializedName("last_name")
    private String lastName;

    @Expose
    @SerializedName("id_on_device")
    private String idOnDevice;

    public int getId() {
        return id;
    }

    public String getSubscriberId() {
        return SubscriberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIdOnDevice() {
        return idOnDevice;
    }
}
