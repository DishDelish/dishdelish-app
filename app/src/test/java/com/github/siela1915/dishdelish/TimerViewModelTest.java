package com.github.siela1915.dishdelish;

import static com.google.common.base.CharMatcher.any;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.os.SystemClock;

import com.github.siela1915.bootcamp.CountDownTimerWithPause;
import com.github.siela1915.bootcamp.TimerViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TimerViewModelTest {
    private TimerViewModel timerViewModel;
    private int testIndex = 1;
    private long timeStart = 100000;
    private long mockRealTime = 10;
    private long timeInterval = 1000;

    @Mock
    private CountDownTimerWithPause testTimer = mock(CountDownTimerWithPause.class);

    @Before
    public void setUp() {
        timerViewModel = new TimerViewModel();
    }

    @Test
    public void addAndGetTimerTest() {
        timerViewModel.addTimer(testIndex, testTimer);

        Map<Integer, CountDownTimerWithPause> timerList = timerViewModel.getTimerList();

        assertTrue(timerList.containsKey(testIndex));
        assertEquals(testTimer, timerList.get(testIndex));
    }

    @Test
    public void deleteTimerTest() {
        timerViewModel.addTimer(testIndex, testTimer);
        timerViewModel.deleteTimer(testIndex);

        Map<Integer, CountDownTimerWithPause> timerList = timerViewModel.getTimerList();

        assertFalse(timerList.containsKey(testIndex));
    }

    @Test
    public void resumeTimerIfTimerNotPaused() {
        when(testTimer.getRemainingTime()).thenReturn(timeStart);

        timerViewModel.addTimer(testIndex, testTimer);

        long remainingTime = timerViewModel.resumeTimer(testIndex);

        assertEquals(timeStart, remainingTime);
    }

    @Test
    public void resumeTimerIfTimerPaused() {
        when(testTimer.resume()).thenReturn(timeStart);
        when(testTimer.ismPaused()).thenReturn(true);

        timerViewModel.addTimer(testIndex, testTimer);

        long remainingTime = timerViewModel.resumeTimer(testIndex);

        assertEquals(timeStart, remainingTime);
    }

    @Test
    public void pauseTimerIfTimerIsRunning() {
        when(testTimer.pause()).thenReturn(timeStart);
        when(testTimer.ismPaused()).thenReturn(false);

        timerViewModel.addTimer(testIndex, testTimer);

        long pauseTime = timerViewModel.pauseTimer(testIndex);

        assertEquals(timeStart, pauseTime);
    }

    @Test
    public void pauseTimerIfTimerIsNotRunning() {
        when(testTimer.ismPaused()).thenReturn(true);
        when(testTimer.getmPauseTime()).thenReturn(0L);

        timerViewModel.addTimer(testIndex, testTimer);

        long pauseTime = timerViewModel.pauseTimer(testIndex);

        assertEquals(0, pauseTime);
    }

    @Test
    public void checkDoesTimerExistWhenTimerExists() {
        timerViewModel.addTimer(testIndex, testTimer);

        assertTrue(timerViewModel.doesTimerExist(testIndex));
    }

    @Test
    public void checkDoesTimerExistWhenTimerNotExists() {
        assertFalse(timerViewModel.doesTimerExist(testIndex));
    }
}