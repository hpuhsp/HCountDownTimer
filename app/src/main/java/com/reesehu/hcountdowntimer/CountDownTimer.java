package com.reesehu.hcountdowntimer;

/**
 * Function:
 * <br/>
 * Describe:
 * <br/>
 * Author: reese on 2018/4/16.
 * <br/>
 * Email: reese@jiuhuar.com
 */
public class CountDownTimer extends MyCountDownTimer {
    /**
     * @param millisInFuture    总倒计时时间
     * @param countDownInterval 倒计时间隔时间
     * @param name
     */
    public CountDownTimer(long millisInFuture, long countDownInterval, String name) {
        super(millisInFuture, countDownInterval, name);
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {

    }
}
