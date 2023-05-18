package com.github.siela1915.bootcamp;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class TimerViewModel extends ViewModel {
    private final Map<Integer, CountDownTimerWithPause> timerList = new HashMap<>();

    public void addTimer(int index, CountDownTimerWithPause timer) {
        timerList.put(index, timer);
    }

    public void deleteTimer(int index) {
        if (timerList.containsKey(index)) timerList.remove(index);
    }

    public long resumeTimer(int index) {
        if (timerList.containsKey(index)) {
            CountDownTimerWithPause timer = timerList.get(index);
            if (timer.isPaused()) return timerList.get(index).resume();
            return timer.getRemainingTime();
        }
        return 0;
    }

    public long pauseTimer(int index) {
        if (timerList.containsKey(index)) {
            CountDownTimerWithPause timer = timerList.get(index);
            if (!timer.isPaused()) return timerList.get(index).pause();
            return timer.getPauseTime();
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
