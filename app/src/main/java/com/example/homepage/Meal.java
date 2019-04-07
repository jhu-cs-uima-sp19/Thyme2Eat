package com.example.homepage;

public class Meal {
    public int id;
    private String date;
    private String time;
    private String dayName;
    private String month;
    private String dayNum;

    public Meal(String d, String t) {
        this.date = d;
        this.time = t;
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

}
