package com.example.user.bluetooth_communication.remote.Model.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    private LoginRequest() {

    }

    public static class Request {

        @Expose
        @SerializedName("username")
        private String userName;

        public Request(String userName) {
            this.userName = userName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}