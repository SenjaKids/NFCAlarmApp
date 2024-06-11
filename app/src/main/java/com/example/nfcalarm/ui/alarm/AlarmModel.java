package com.example.nfcalarm.ui.alarm;

import android.content.Intent;

import com.example.nfcalarm.AlarmReceiver;

public class AlarmModel {
    private String alarmTime;
    private int hour, minute;
     AlarmReceiver alarmReceiver;
    private boolean isActive;

    public AlarmModel(String alarmTime, AlarmReceiver alarmReceiver, int hour, int minute) {
        this.alarmTime = alarmTime;
        this.hour = hour;
        this.minute = minute;
        this.alarmReceiver = alarmReceiver;
        this.isActive = false;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public int getHour(){
        return hour;
    }

    public int getMinute(){
        return minute;
    }

    public boolean getIsActive(){
        return isActive;
    }

    public void setIsActive(boolean value){
        isActive = value;
    }


}
