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
    private final long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    private long mPauseTime;

    private boolean mCancelled = false;

    private boolean mPaused = false;
    private static final int MSG = 1;
    // handles counting down
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            synchronized (CountDownTimerWithPause.this) {
                if (!mPaused) {
                    final long millisLeft = mStopTimeInFuture - getRealTime();

                    if (millisLeft <= 0) {
                        onFinish();
                    } else if (millisLeft < mCountdownInterval) {
                        // no tick, just delay until done
                        sendMessageDelayed(obtainMessage(MSG), millisLeft);
                    } else {
                        long lastTickStart = getRealTime();
                        onTick(millisLeft);

                        // take into account user's onTick taking time to execute
                        long delay = lastTickStart + mCountdownInterval - getRealTime();

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += mCountdownInterval;

                        if (!mCancelled) {
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
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    public CountDownTimerWithPause(long millisInFuture, long countDownInterval, Handler handler) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
        mHandler = handler;
    }

    /**
     * Start the countdown.
     */
    public synchronized final void start() {
        if (mMillisInFuture <= 0) {
            onFinish();
        }
        mStopTimeInFuture = getRealTime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        mCancelled = false;
        mPaused = false;
    }

    /**
     * Pause the countdown.
     */
    public long pause() {
        mPauseTime = mStopTimeInFuture - getRealTime();
        mPaused = true;
        return mPauseTime;
    }

    /**
     * Resume the countdown.
     */
    public long resume() {
        mStopTimeInFuture = mPauseTime + getRealTime();
        mPaused = false;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return mPauseTime;
    }

    /**
     * Get the remaining time.
     */
    public long getRemainingTime() {
        return mStopTimeInFuture - getRealTime();
    }

    /**
     * Get the countdown interval.
     */
    public long getmCountdownInterval() {
        return mCountdownInterval;
    }

    /**
     * Get the time left when the timer is paused.
     */
    public long getmPauseTime() {
        return mPauseTime;
    }

    /**
     * Get the time that the timer will stop.
     */
    public long getmStopTimeInFuture() {
        return mStopTimeInFuture;
    }

    /**
     * Get if the timer is paused.
     */
    public boolean ismPaused() {
        return mPaused;
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
