package com.example.app_on_phone.appmaneger.pojo;

import com.google.gson.annotations.SerializedName;

public class TimeSlot{
    @SerializedName("timeSlotid")
    private int id;
    @SerializedName("accountId")
    private String accID; // Account ID
    @SerializedName("start")
    private String startTime; // 开课时间
    @SerializedName("end")
    private String endTime; // 结束时间
    @SerializedName("status")
    private int status; // 是否开关
    @SerializedName("iswork")
    private int isWork; // 工作日or周末


    // constructor
    public TimeSlot() {}

    public TimeSlot(int id, String accID, String startTime, String endTime, int status, int isWork) {
        this.id = id;
        this.accID = accID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.isWork = isWork;
    }

    // getters
    public int getId() {
        return id;
    }

    public String getAccID() {
        return accID;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getStatus() {
        return status;
    }

    public int getIsWork() {
        return isWork;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setAccID(String accID) {
        this.accID = accID;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setIsWork(int isWork) {
        this.isWork = isWork;
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "id=" + id +
                ", accID=" + accID +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", status=" + status +
                ", isWork=" + isWork +
                '}';
    }

}
