package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;

import com.github.siela1915.bootcamp.Time;

import org.junit.Test;

public class TimeTest {
    private long hour = 1;
    private long min = 2;
    private long sec = 3;
    private long milliSec = hour * 60 * 60 * 1000 + min * 60 * 1000 + sec * 1000;
    private Time time = new Time(hour, min, sec);

    @Test
    public void millisecondsToTime() {
        Time expected = new Time(hour, min, sec);

        assertEquals(expected, Time.millisecondsToTime(milliSec));
    }

    @Test
    public void milliSecToString() {
        String expected = String.format("%02d:%02d:%02d", hour, min, sec);

        assertEquals(expected, Time.milliSecToString(milliSec));
    }

    @Test
    public void getHour() {
        assertEquals(hour, time.getHour());
    }

    @Test
    public void getMin() {
        assertEquals(min, time.getMin());
    }

    @Test
    public void getSec() {
        assertEquals(sec, time.getSec());
    }
}