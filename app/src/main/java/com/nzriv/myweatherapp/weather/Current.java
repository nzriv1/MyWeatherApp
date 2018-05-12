package com.nzriv.myweatherapp.weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Current {

    private String location, icon, summary, timeZone;
    private long time;
    private double temperature, humidity, precipChance;

    public Current() {
    }

    public Current(String location, String icon, String summary, String timeZone, long time, double temperature,
                   double humidity, double precipChance) {
        this.location = location;
        this.icon = icon;
        this.summary = summary;
        this.timeZone = timeZone;
        this.time = time;
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipChance = precipChance;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIconID() {
        return Forecast.getIconID(icon);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getTime() {
        return time;
    }

    public String getFormattedTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");

        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));

//        need to pass in a Date object into simpleDateFormat.format as constructor.
//        Date uses milliseconds. so have to * 1000 for seconds for our current time value of the app.
        Date dateTime = new Date(time * 1000);

        return simpleDateFormat.format(dateTime);
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPrecipChance() {
        return precipChance;
    }

    public void setPrecipChance(double precipChance) {
        this.precipChance = precipChance;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
