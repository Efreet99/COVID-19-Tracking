package com.skypan.covid_19;

public class History {
    String date;
    String time;
    String location;
    String address;

    public History(){

    }

    public History(String date, String time, String location, String address) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }
}