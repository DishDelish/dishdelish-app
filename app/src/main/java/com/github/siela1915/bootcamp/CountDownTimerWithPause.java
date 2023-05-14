package com.github.siela1915.bootcamp;

import android.os.Handler;
import android.os.SystemClock;
import android.os.Message;

/**
 * Schedule a countdown until a time in the future, with
 * regular notifications on intervals along the way.
 *
**/
public abstract class CountDownTimerWithPause {

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long millisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long countdownInterval;

    private long stopTimeInFuture;

    private long pauseTime;

    private boolean cancelled = false;

    private boolean paused = false;
    private static final int MSG = 1;
    // handles counting down
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            synchronized (CountDownTimerWithPause.this) {
                if (!paused) {
                    final long millisLeft = stopTimeInFuture - getRealTime();

                    if (millisLeft <= 0) {
                        onFinish();
                    } else if (millisLeft < countdownInterval) {
                        // no tick, just delay until done
                        sendMessageDelayed(obtainMessage(MSG), millisLeft);
                    } else {
                        long lastTickStart = getRealTime();
                        onTick(millisLeft);

                        // take into account user's onTick taking time to execute
                        long delay = lastTickStart + countdownInterval - getRealTime();

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += countdownInterval;

                        if (!cancelled) {
                            sendMessageDelayed(obtainMessage(MSG), delay);
                        }
                    }
                }
            }
        }
    };

    /**
     * @param millisInFuture The number of millis in the future from the call
     *   to {@link #start()} until the countdown is done and {@link #onFinish()}
     *   is called.
     * @param countDownInterval The interval along the way to receive
     *   {@link #onTick(long)} callbacks.
     */
    public CountDownTimerWithPause(long millisInFuture, long countDownInterval) {
        this.millisInFuture = millisInFuture;
        countdownInterval = countDownInterval;
    }

    public CountDownTimerWithPause(long millisInFuture, long countDownInterval, Handler handler) {
        this.millisInFuture = millisInFuture;
        countdownInterval = countDownInterval;
        this.handler = handler;
    }

    /**
     * Start the countdown.
     */
    public synchronized final void start() {
        if (millisInFuture <= 0) {
            onFinish();
        }
        stopTimeInFuture = getRealTime() + millisInFuture;
        handler.sendMessage(handler.obtainMessage(MSG));
        cancelled = false;
        paused = false;
    }

    /**
     * Pause the countdown.
     */
    public long pause() {
        pauseTime = stopTimeInFuture - getRealTime();
        paused = true;
        return pauseTime;
    }

    /**
     * Resume the countdown.
     */
    public long resume() {
        stopTimeInFuture = pauseTime + getRealTime();
        paused = false;
        handler.sendMessage(handler.obtainMessage(MSG));
        return pauseTime;
    }

    /**
     * Get the remaining time.
     */
    public long getRemainingTime() {
        return stopTimeInFuture - getRealTime();
    }

    /**
     * Get the countdown interval.
     */
    public long getCountdownInterval() {
        return countdownInterval;
    }

    /**
     * Get the time left when the timer is paused.
     */
    public long getPauseTime() {
        return pauseTime;
    }

    /**
     * Get the time that the timer will stop.
     */
    public long getStopTimeInFuture() {
        return stopTimeInFuture;
    }

    /**
     * Get if the timer is paused.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Callback fired on regular interval.
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();

    /**
     * Get real time.
     */
    public long getRealTime() {
        return SystemClock.elapsedRealtime();
    };
}
