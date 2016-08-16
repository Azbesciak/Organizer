package com.example.witek.organizer;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Witek on 22.05.2016.
 */
public class FoodFormEvent extends Event {

    private String weight;

    public FoodFormEvent(Event event, String weight) {
        super(event);
        this.weight = weight;
        countGainedKcal();
    }

    public FoodFormEvent(String name, String kcalPerUnit, String date, String time,
                         String weight) {
        super(name, kcalPerUnit, date, time);
        this.weight = weight;
        countGainedKcal();
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String countGainedKcal() {
        float counted;
        try {
            counted = Float.valueOf(weight) * Float.valueOf(getKcalPerUnit()) / 100;
            String result = String.valueOf(new Formatter(Locale.US).format("%.0f", counted));
            setKcalBalance(result);
            return result;

        } catch (Exception exc) {
            setKcalBalance("");
            return "";
        }
    }
}
