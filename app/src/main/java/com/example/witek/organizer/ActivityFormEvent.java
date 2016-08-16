package com.example.witek.organizer;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Witek on 22.05.2016.
 */
public class ActivityFormEvent extends Event {
    private String duration;

    public ActivityFormEvent(Event event, String duration) {
        super(event);
        this.duration = duration;
        countBuriedKcal();
    }

    public ActivityFormEvent(String name, String kcalPerUnit, String date, String time,
                             String duration) {
        super(name, kcalPerUnit, date, time);
        this.duration = duration;
        countBuriedKcal();
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String countBuriedKcal() {
        float counted;
        try {
            counted = Float.valueOf(duration) * Float.valueOf(getKcalPerUnit()) / 60;
            String result = String.valueOf(new Formatter(Locale.US).format("%.0f", counted));
            setKcalBalance(result);
            return result;

        } catch (Exception exc) {
            setKcalBalance("");
            return "";
        }
    }

    @Override
    public String toString() {
        return "ActivityFormEvent{" + super.toString() +
               ", duration='" + duration + '\'' +
               '}';
    }
}
