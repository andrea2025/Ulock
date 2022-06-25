package com.example.user.bluetooth_communication.remote.Model.Request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdduserReq {

    public AdduserReq() {
    }

    public static class Request {

        @Expose
        @SerializedName("firstName")
        private String firstName;

        @Expose
        @SerializedName("lastName")
        private String lastName;

        @Expose
        @SerializedName("idOnDevice")
        private String idDevice;

        public Request(String firstName, String lastName, String idDevice) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.idDevice = idDevice;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getIdDevice() {
            return idDevice;
        }

        public void setIdDevice(String idDevice) {
            this.idDevice = idDevice;
        }
    }
}
