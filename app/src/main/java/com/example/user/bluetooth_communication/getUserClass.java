package com.example.user.bluetooth_communication;

public class getUserClass {
    private String FirstName;
    private String lastName;
    private int id;

    public getUserClass(String firstName, String lastName) {
        this.FirstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
