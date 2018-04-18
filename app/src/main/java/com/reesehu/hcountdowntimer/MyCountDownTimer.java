package com.reesehu.hcountdowntimer;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Function:
 * <br/>
 * Describe: 改写倒计时时间组件
 * <br/>
 * Author: reese on 2018/4/16.
 * <br/>
 * Email: reese@jiuhuar.com
 */
public abstract class MyCountDownTimer {

    private static final int MSG = 1;
    private final long mMillisInFuture;
    private final long mCountdownInterval;
    private long mStopTimeInFuture;
    private long mPauseTimeInFuture;
    private boolean isStop = false;
    private boolean isPause = false;

    private String taskName;

    /**
     * @param millisInFuture    总倒计时时间
     * @param countDownInterval 倒计时间隔时间
     */
    public MyCountDownTimer(long millisInFuture, long countDownInterval, String name) {
        // 解决秒数有时会一开始就减去了2秒问题（如10秒总数的，刚开始就8999，然后没有不会显示9秒，直接到8秒）
        if (countDownInterval > 1000) millisInFuture += 15;
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
        taskName = name;
    }

    private synchronized MyCountDownTimer start(long millisInFuture) {
        isStop = false;
        if (millisInFuture <= 0) {
            onFinish();
            return this;
        }
        // SystemClock.elapsedRealtime()特别指的是某个时间段,以此为基点实现时间段的计算
        mStopTimeInFuture = SystemClock.elapsedRealtime() + millisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    /**
     * 开始倒计时
     */
    public synchronized final void start() {
        start(mMillisInFuture);
    }

    /**
     * 停止倒计时
     */
    public synchronized final void stop() {
        isStop = true;
//        mHandler.removeMessages(MSG);
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 暂时倒计时
     * 调用{@link #restart()}方法重新开始
     */
    public synchronized final void pause() {
        if (isStop) return;

        isPause = true;
        mPauseTimeInFuture = mStopTimeInFuture - SystemClock.elapsedRealtime();
        mHandler.removeMessages(MSG);
    }

    /**
     * 重新开始
     */
    public synchronized final void restart() {
        if (isStop || !isPause) return;

        isPause = false;
        start(mPauseTimeInFuture);
    }

    /**
     * 倒计时间隔回调
     *
     * @param millisUntilFinished 剩余毫秒数
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * 倒计时结束回调
     */
    public abstract void onFinish();


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (MyCountDownTimer.this) {
                if (isStop || isPause) {
                    return;
                }

                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();
                if (millisLeft <= 0) {
                    onFinish();
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(millisLeft); // 剩余的时间长度(时间段),毫秒计
                    // take into account user's onTick taking time to execute
                    long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();

                    // special case: user's onTick took more than interval to
                    // complete, skip to next interval
                    while (delay < 0) {
                        delay += mCountdownInterval;
                    }
                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };

    public String getTaskName() {
        return taskName;
    }

    public boolean isStop() {
        return isStop;
    }
}
