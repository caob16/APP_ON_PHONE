package com.example.app_on_phone.appmaneger.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class APP {
    @SerializedName("userAppid")
    private int id;
    @SerializedName("accountId")
    private int accID; // Account ID
    @SerializedName("appName")
    private String APPName; // App Name
    @SerializedName("status")
    private int isOn; // Whether the app is on or off

    // Constructor
    public APP() {}

    public APP(int id, String APPName, int isOn) {
        this.id = id;
        this.APPName = APPName;
        this.isOn = isOn;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getAPPName() {
        return APPName;
    }

    public int getIsOn() {
        return isOn;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setAPPName(String APPName) {
        this.APPName = APPName;
    }

    public void setIsOn(int isOn) {
        this.isOn = isOn;
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        APP app = (APP) o;
        return id == app.id && isOn == app.isOn && Objects.equals(APPName, app.APPName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, APPName, isOn);
    }

    // toString method
    @Override
    public String toString() {
        return "APP{" +
                "id=" + id +
                ", APPName='" + APPName + '\'' +
                ", isOn=" + isOn +
                '}';
    }

}
