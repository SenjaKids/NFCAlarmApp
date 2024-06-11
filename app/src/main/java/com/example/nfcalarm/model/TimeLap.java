package com.example.nfcalarm.model;

public class TimeLap {
    private int lap;
    private String time;

    public TimeLap(int lap, String time) {
        this.lap = lap;
        this.time = time;
    }

    public int getLap() {
        return lap;
    }
    public void setLap(int lap) {
        this.lap = lap;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String s) {
        this.time = time;
    }
}
