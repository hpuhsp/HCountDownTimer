package com.reesehu.hcountdowntimer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.reesehu.hcountdowntimer.R;

/**
 * Function:
 * <br/>
 * Describe: 可设置单位和需要显示的背景样式
 * 1、单一线程控制所有View的变化
 * 2、绑定父View，实现根据秒数的变化进而决定父View等上级所有View的关键操作
 * 3、这样做保证根据倒计时实现变化，相对来说比较准确
 * <br/>
 * Author: reese on 2018/4/16.
 * <br/>
 * Email: reese@jiuhuar.com
 */
public class FlipsView extends LinearLayout {

    private static final int DAY_TYPE = 0x7b;
    private static final int HOUR_TYPE = 0x7c;
    private static final int MINUTE_TYPE = 0x7d;
    private static final int SECOND_TYPE = 0x7e;

    private FlipLayout mFirstFlipLayout;
    private FlipLayout mSecondFlipLayout;
    // 单位类型
    private int mUnitType = SECOND_TYPE;
    private int currentFirstValue;
    private int currentSecondValue;

    public FlipsView(Context context) {
        this(context, null);
    }

    public FlipsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        initChildView(context, attrs, defStyleAttr);
    }

    /**
     * 初始化子View,布局样式后期可以自定义
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initChildView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        // 获取指定的单位
//        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlipsView);
//        mUnitType = array.getInt(R.styleable.FlipsView_flips_view_unit, SECOND_TYPE); // 默认以秒数为单位
//        array.recycle();
        LayoutInflater.from(context).inflate(R.layout.cell_flip_layout, this);
        // 两个为一组的时间单位
        mFirstFlipLayout = findViewById(R.id.first_flip_layout);
        mSecondFlipLayout = findViewById(R.id.second_flip_layout);
        initTwoFlipView();
    }

    /**
     * 初始化两个FlipLayout组件
     */
    private void initTwoFlipView() {
        int firstUnit;
        if (mUnitType == DAY_TYPE || mUnitType == HOUR_TYPE) {
            firstUnit = 9;
        } else {
            firstUnit = 5;
        }
        // 设置变化单位的最大值
        mFirstFlipLayout.setMaxUnit(firstUnit); // 高位的单位
        mSecondFlipLayout.setMaxUnit(9); // 低位的单位
    }

    /**
     * 初始化默认显示
     * 设置倒计时的时间--需要格式为两位数字
     *
     * @param timeValue
     */
    public void setTimeValue(int timeValue) {
        currentFirstValue = timeValue / 10;
        currentSecondValue = timeValue % 10;

        mFirstFlipLayout.flip(currentFirstValue);
        mSecondFlipLayout.flip(currentSecondValue);
    }

    /**
     * 计时器每一秒的动作
     *
     * @param timeValue
     */
    public void checkAndSmoothFlip(int timeValue) {
        int aValue = (timeValue / 10);
        int bValue = (timeValue % 10);

        Log.d("TAG", "----timeValue-" + timeValue);

        Log.d("TAG", "----aValue-" + aValue);
        Log.d("TAG", "----bValue-" + bValue);

        if (currentFirstValue != aValue) {
            currentFirstValue = aValue;
            mFirstFlipLayout.smoothFlip(currentFirstValue, false);
        }
        if (currentSecondValue != bValue) {
            currentSecondValue = bValue;
            mSecondFlipLayout.smoothFlip(currentSecondValue, false);
        }
    }

    /**
     * 设置单位类型---待优化，直接在xml中指定单位类型
     *
     * @param unitType
     */
    public void setUnitType(int unitType) {
        this.mUnitType = unitType;
    }

    /**
     * 针对与秒数的情况下，开始递减
     */
    public void startFlipNextTime() {

    }

}
