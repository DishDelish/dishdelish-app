package com.github.siela1915.bootcamp;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class TimerViewModel extends ViewModel {
    private int currentTimerIndex;
    private Map<Integer, CountDownTimerWithPause> timerList = new HashMap<>();

    public void setCurrentTimerIndex(int index) {
        currentTimerIndex = index;
    }

    public int getCurrentTimerIndex() {
        return currentTimerIndex;
    }

    public void addTimer(int index, CountDownTimerWithPause timer) {
        timerList.put(index, timer);
    }

    public void deleteTimer(int index) {
        if (timerList.containsKey(index)) timerList.remove(index);
    }

    public long resumeTimer(int index) {
        if (timerList.containsKey(index)) {
            CountDownTimerWithPause timer = timerList.get(index);
            if (timer.ismPaused()) return timerList.get(index).resume();
            return timer.getRemainingTime();
        }
        return 0;
    }

    // TODO: test only
    public TimerViewModel() {
        CountDownTimerWithPause timer1 = new CountDownTimerWithPause(100000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
            }
        };
        CountDownTimerWithPause timer2 = new CountDownTimerWithPause(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
            }
        };

        CountDownTimerWithPause timer3 = new CountDownTimerWithPause(600000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
            }
        };

        timerList.put(2, timer1);
        timerList.put(3, timer2);
        timerList.put(9, timer3);

        timer1.start();
        timer2.start();
        timer3.start();
        timer3.pause();
    }

    public long pauseTimer(int index) {
        if (timerList.containsKey(index)) {
            CountDownTimerWithPause timer = timerList.get(index);
            if (!timer.ismPaused()) return timerList.get(index).pause();
            return timer.getmPauseTime();
        }
        return 0;
    }

    public Map<Integer, CountDownTimerWithPause> getTimerList() {
        return timerList;
    }

    public boolean doesTimerExist(int index) {
        return timerList.containsKey(index);
    }
}
