package com.reesehu.hcountdowntimer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.reesehu.hcountdowntimer.MyCountDownTimer;
import com.reesehu.hcountdowntimer.R;

/**
 * Function:
 * <br/>
 * Describe: 自定义倒计时View
 * <br/>
 * Author: reese on 2018/4/16.
 * <br/>
 * Email: reese@jiuhuar.com
 */
public class NumberCDView extends FrameLayout {

    private FlipsView mDayFlipView;
    private FlipsView mHourFlipView;
    private FlipsView mMinuteFlipView;
    private FlipsView mSecondFlipView;
    private long mMillisLeft = 5000L;
    private MyCountDownTimer myCountDownTimer;

    public NumberCDView(Context context) {
        this(context, null);
    }

    public NumberCDView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberCDView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initChildView(context, attrs);
    }

    /**
     * 初始化倒计时--->此场景下从天数到秒数的View全部显示,可根据需要做增减
     *
     * @param context
     * @param attrs
     */
    private void initChildView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.item_flip_view_layout, this);
        // 天数
        mDayFlipView = findViewById(R.id.day_flip_view);
        // 小时
        mHourFlipView = findViewById(R.id.hour_flip_view);
        // 分钟
        mMinuteFlipView = findViewById(R.id.minute_flip_view);
        // 秒数
        mSecondFlipView = findViewById(R.id.second_flip_view);
    }

    /**
     * 设置倒计时的数值
     *
     * @param millisLeft
     */
    public void setCountDownValues(long millisLeft) {
        mMillisLeft = millisLeft; // 截止时间的间隔
    }

    private OnCountDownStateListener mOnCountDownStateListener;

    public void addOnCountDownStateListener(OnCountDownStateListener countDownStateListener) {
        this.mOnCountDownStateListener = countDownStateListener;
    }

    public interface OnCountDownStateListener {
        void onTimerFinished();

        void onFlipViewFinished();
    }

    /**
     * 开始倒计时
     */
    public void start() {
        if (null != myCountDownTimer && !myCountDownTimer.isStop()) {
            myCountDownTimer.stop();
            myCountDownTimer = null;
        }

        // 设置从天数到秒数的默认显示文案，获取日时分秒的单位值
        // 初始化显示
        int[] longs = calculateTime(mMillisLeft);

        mDayFlipView.setTimeValue(longs[0]);
        mHourFlipView.setTimeValue(longs[1]);
        mMinuteFlipView.setTimeValue(longs[2]);
        mSecondFlipView.setTimeValue(longs[3]);

        // 实现翻转和倒计时的同步
        myCountDownTimer = new MyCountDownTimer(mMillisLeft, 1000, "NumberCDView") {
            @Override
            public void onTick(long millisUntilFinished) { // 每秒的变化
                checkAndFlip(millisUntilFinished);
            }

            @Override
            public void onFinish() { // 倒计时完成
                if (!myCountDownTimer.isStop()) {
                    myCountDownTimer.stop();
                }
                if (null != mOnCountDownStateListener) {
                    mOnCountDownStateListener.onTimerFinished();
                }
            }
        };

        if (mMillisLeft > 0) { // 开始预先滚动
            // 预先减去一秒
//            checkAndFlip(mMillisLeft);
            myCountDownTimer.start();
        }
    }

    /**
     * 暂停倒计时器
     */
    public void pause() {
        if (null != myCountDownTimer) {
            myCountDownTimer.pause();
        }
    }

    /**
     * 每秒检查需要翻转的View
     *
     * @param millisUntilFinished
     */
    private void checkAndFlip(long millisUntilFinished) {
        if (millisUntilFinished > 0) {
//            if (millisUntilFinished - 1000 <= 0) {
//                millisUntilFinished = 1000;
//            }
            int[] longs = calculateTime(millisUntilFinished);

            mDayFlipView.checkAndSmoothFlip(longs[0]);
            mHourFlipView.checkAndSmoothFlip(longs[1]);
            mMinuteFlipView.checkAndSmoothFlip(longs[2]);
            mSecondFlipView.checkAndSmoothFlip(longs[3]); // 秒数
        } else { // 倒计时结束
            if (null != myCountDownTimer && !myCountDownTimer.isStop()) {
                myCountDownTimer.stop();
            }
            if (null != mOnCountDownStateListener) {
                mOnCountDownStateListener.onFlipViewFinished();
            }
        }
    }

    /**
     * 格式化剩余时间
     *
     * @param limitTime
     * @return
     */
    public static int[] calculateTime(long limitTime) {
        int[] tips = new int[4];
        int days = (int) (limitTime / (1000 * 60 * 60 * 24));
        tips[0] = days;
        int hours = (int) (limitTime / (1000 * 60 * 60) - days * 24);
        tips[1] = hours;
        int mins = (int) ((limitTime / (1000 * 60)) - days * 24 * 60 - hours * 60);
        tips[2] = mins;
        int second = (int) ((limitTime / 1000) - days * 24 * 60 * 60 - hours * 60 * 60 - mins * 60);
        tips[3] = second;
        return tips;
    }
}
