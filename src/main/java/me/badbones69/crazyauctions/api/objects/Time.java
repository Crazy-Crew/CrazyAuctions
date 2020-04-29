package me.badbones69.crazyauctions.api.objects;

import java.util.Calendar;

public class Time {
    
    private int day = 0;
    private int hour = 0;
    private int minute = 0;
    private int second = 0;
    
    public Time(String timeString) {
        for (String time : timeString.toLowerCase().split(" ")) {
            if (time.contains("d")) {
                day = Integer.parseInt(time.replace("d", ""));
            } else if (time.contains("h")) {
                hour = Integer.parseInt(time.replace("h", ""));
            } else if (time.contains("m")) {
                minute = Integer.parseInt(time.replace("m", ""));
            } else if (time.contains("s")) {
                second = Integer.parseInt(time.replace("s", ""));
            }
        }
    }
    
    public Time(int day, int hour, int minute, int second) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }
    
    public int getDay() {
        return day;
    }
    
    public int getHour() {
        return hour;
    }
    
    public int getMinute() {
        return minute;
    }
    
    public int getSecond() {
        return second;
    }
    
    public long getTimeInMillis() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, day);
        now.add(Calendar.HOUR, hour);
        now.add(Calendar.MINUTE, minute);
        now.add(Calendar.SECOND, second);
        return now.getTimeInMillis();
    }
    
}