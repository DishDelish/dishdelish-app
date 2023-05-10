package com.github.siela1915.bootcamp;

import java.util.concurrent.TimeUnit;

public class Time {
    private long hour;
    private long min;
    private long sec;

    public static Time millisecondsToTime(long milliSec) {
        long hour = TimeUnit.MILLISECONDS.toHours(milliSec);
        long min = TimeUnit.MILLISECONDS.toMinutes(milliSec) - hour * 60;
        long sec = TimeUnit.MILLISECONDS.toSeconds(milliSec) - min * 60 - hour * 60 * 60;

        return new Time(hour, min, sec);
    }

    public static String milliSecToString(long milliSec) {
        Time time = Time.millisecondsToTime(milliSec);
        return String.format("%02d:%02d:%02d", time.getHour(), time.getMin(), time.getSec());
    }

    public Time(long hour, long min, long sec) {
        this.hour = hour;
        this.min = min;
        this.sec = sec;
    }

    public long getHour() {
        return hour;
    }

    public long getMin() {
        return min;
    }

    public long getSec() {
        return sec;
    }
}
