package com.example.witek.organizer;

/**
 * Created by Witek on 22.05.2016.
 */
public class Event {
    private long id;
    private String name;
    private String kcalPerUnit;
    private String kcalBalance;
    private String date;
    private String time;
    public Event(Event event){
        name = event.getName();
        kcalPerUnit = event.getKcalPerUnit();
        kcalBalance = event.getKcalBalance();
        id = 0;
    }
    public Event(String name,String kcalPerUnit, String date, String time){
        this.name = name;
        this.kcalPerUnit = kcalPerUnit;
        this.date = date;
        this.time = time;
        kcalBalance = "";
        id = 0;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKcalPerUnit() {
        return kcalPerUnit;
    }

    public void setKcalPerUnit(String kcalPerUnit) {
        this.kcalPerUnit = kcalPerUnit;
    }

    public String getKcalBalance() {
        return kcalBalance;
    }

    public void setKcalBalance(String kcalBalance) {
        this.kcalBalance = kcalBalance;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", kcalPerUnit='" + kcalPerUnit + '\'' +
                ", kcalBalance='" + kcalBalance + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
