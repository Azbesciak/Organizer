package com.example.witek.organizer;

/**
 * Created by Witek on 05.06.2016.
 */
public class DailyBalance {
    long id;
    String kcalLimit = "";
    String reachedKcal = "";
    String date;

    public DailyBalance(String date, String kcalLimit) {
        this.date = date;
        this.kcalLimit = kcalLimit;
        id = 0;
    }

    public String getKcalLimit() {
        return kcalLimit;
    }

    public void setKcalLimit(String kcalLimit) {
        this.kcalLimit = kcalLimit;
    }

    public String getReachedKcal() {
        return reachedKcal;
    }

    public void setReachedKcal(String reachedKcal) {
        this.reachedKcal = reachedKcal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DailyBalance{" +
                "id= " + id +
                ", kcalLimit= '" + kcalLimit + '\'' +
                ", reachedKcal= '" + reachedKcal + '\'' +
                ", date= " + date + '\'' +
                '}';
    }
}
