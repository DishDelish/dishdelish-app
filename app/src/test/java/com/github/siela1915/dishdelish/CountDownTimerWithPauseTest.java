package com.github.siela1915.dishdelish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.os.Handler;

import com.github.siela1915.bootcamp.CountDownTimerWithPause;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

abstract class CountDownTimerWithPauseToBeTested extends CountDownTimerWithPause {
    public CountDownTimerWithPauseToBeTested(long millisInFuture, long countDownInterval, Handler mHandler) {
        super(millisInFuture, countDownInterval, mHandler);
    }

    @Override
    public long getRealTime() {
        return 1000;
    };
}

@RunWith(MockitoJUnitRunner.class)
public class CountDownTimerWithPauseTest {
    private CountDownTimerWithPauseToBeTested countDownTimerWithPause;
    private long timeStart = 100000;
    private long timeInterval = 1000;
    private static final int MSG = 1;

    @Mock
    private Handler mHandler = mock(Handler.class);

    @Before
    public void setUp() {
        countDownTimerWithPause = new CountDownTimerWithPauseToBeTested(timeStart, timeInterval, mHandler) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
            }
        };
    }

    @Test
    public void startTest() {
        when(mHandler.obtainMessage(MSG)).thenReturn(null);
        when(mHandler.sendMessage(null)).thenReturn(true);

        countDownTimerWithPause.start();

        assertFalse(countDownTimerWithPause.ismPaused());
        assertEquals(countDownTimerWithPause.getmStopTimeInFuture(), 1000 + timeStart);
    }

    @Test
    public void pauseTest() {
        countDownTimerWithPause.start();
        long pauseTime = countDownTimerWithPause.pause();

        assertTrue(countDownTimerWithPause.ismPaused());
        assertEquals(pauseTime, timeStart);
    }

    @Test
    public void resumeTest() {
        countDownTimerWithPause.start();
        countDownTimerWithPause.pause();
        long resumeTime = countDownTimerWithPause.resume();

        assertFalse(countDownTimerWithPause.ismPaused());
        assertEquals(resumeTime, timeStart);
    }

    @Test
    public void getRemainingTimeTest() {
        countDownTimerWithPause.start();
        long remainingTime = countDownTimerWithPause.getRemainingTime();

        assertEquals(remainingTime, timeStart);
    }

    @Test
    public void getmCountdownIntervalTest() {
        countDownTimerWithPause.start();

        assertEquals(timeInterval, countDownTimerWithPause.getmCountdownInterval());
    }

    @Test
    public void getmPauseTimeTest() {
        countDownTimerWithPause.start();
        countDownTimerWithPause.pause();

        assertEquals(timeStart, countDownTimerWithPause.getmPauseTime());
    }

    @Test
    public void getmStopTimeInFutureTest() {
        countDownTimerWithPause.start();

        assertEquals(timeStart + 1000, countDownTimerWithPause.getmStopTimeInFuture());
    }
}