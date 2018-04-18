package com.reesehu.hcountdowntimer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.reesehu.hcountdowntimer.R;

/**
 * Function:
 * <br/>
 * Describe: 自定义上下翻转View
 * <br/>
 * Author: reese on 2018/4/16.
 * <br/>
 * Email: reese@jiuhuar.com
 */
public class FlipLayout extends FrameLayout {
    private TextView mVisibleTextView;//可见的
    private TextView mInvisibleTextView;//不可见


    private int layoutWidth;
    private int layoutHeight;
    private Scroller mScroller;
    private String TAG = "FlipLayout";

    private Camera mCamera = new Camera();
    private Matrix mMatrix = new Matrix();
    private Rect mTopRect = new Rect();
    private Rect mBottomRect = new Rect();
    private boolean isUp = true;
    private Paint mShinePaint = new Paint();
    private Paint mShadePaint = new Paint();
    private boolean isFlipping = false;

    private int maxUnit = 12; // 默认的最大值，即数值变化在0~maxUnit

    private FlipOverListener mFlipOverListener;

    public FlipLayout(Context context) {
        this(context, null);
    }

    public FlipLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlipLayout);

        int resId = array.getResourceId(R.styleable.FlipLayout_flipTextBackground, -1);
        int color = Color.WHITE;
        if (-1 == resId) {
            color = array.getColor(R.styleable.FlipLayout_flipTextBackground, Color.WHITE);
        }
        float size = array.getDimensionPixelSize(R.styleable.FlipLayout_flipTextSize, 36);
        int textColor = array.getColor(R.styleable.FlipLayout_flipTextColor, Color.BLACK);

        array.recycle();
        init(context, resId, color, size, textColor);
    }

    /**
     * 初始化操作
     *
     * @param context
     * @param resId
     * @param color
     * @param size
     * @param textColor
     */
    private void init(Context context, int resId, int color, float size, int textColor) {
        mScroller = new Scroller(context, new DecelerateInterpolator());//减速 动画插入器

        mInvisibleTextView = new TextView(context);
        mInvisibleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        mInvisibleTextView.setText("0");
        mInvisibleTextView.setGravity(Gravity.CENTER);
        mInvisibleTextView.setIncludeFontPadding(false);
        mInvisibleTextView.setTextColor(textColor);
        if (resId == -1) {
            mInvisibleTextView.setBackgroundColor(color);
        } else {
            mInvisibleTextView.setBackgroundResource(resId);
        }
        addView(mInvisibleTextView);

        mVisibleTextView = new TextView(context);
        mVisibleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        mVisibleTextView.setText("0");
        mVisibleTextView.setGravity(Gravity.CENTER);
        mVisibleTextView.setIncludeFontPadding(false);
        mVisibleTextView.setTextColor(textColor);
        if (resId == -1) {
            mVisibleTextView.setBackgroundColor(color);
        } else {
            mVisibleTextView.setBackgroundResource(resId);
        }

        addView(mVisibleTextView);

        mShadePaint.setColor(Color.BLACK);
        mShadePaint.setStyle(Paint.Style.FILL);
        mShinePaint.setColor(Color.WHITE);
        mShinePaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 设置变化数字单位的最大值
     *
     * @param maxUnit
     */
    public void setMaxUnit(int maxUnit) {
        this.maxUnit = maxUnit;
    }

    /**
     * 单位转换
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        layoutHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(layoutWidth, layoutHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.layout(0, 0, layoutWidth, layoutHeight);
        }
        // 上半部分
        mTopRect.top = 0;
        mTopRect.left = 0;
        mTopRect.right = getWidth();
        mTopRect.bottom = getHeight() / 2;
        // 下半部分
        mBottomRect.top = getHeight() / 2;
        mBottomRect.left = 0;
        mBottomRect.right = getWidth();
        mBottomRect.bottom = getHeight();
    }

    @Override
    public void computeScroll() {
//        Log.d(TAG,"computeScroll");
//        if(!mScroller.isFinished() && mScroller.computeScrollOffset()){
//            lastX = mScroller.getCurrX();
//            lastY = mScroller.getCurrY();
//            scrollTo(lastX,lastY);
//            postInvalidate();
//        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!mScroller.isFinished() && mScroller.computeScrollOffset()) { // true说明滚动尚未完成，false说明滚动已经完成
            drawTopHalf(canvas);
            drawBottomHalf(canvas);
            drawFlipHalf(canvas);
            postInvalidate();
        } else {
            Log.d("TAG", "------dispatchDraw------>");
            if (isFlipping) {
                showViews(canvas);
            }

            if (mScroller.isFinished() && !mScroller.computeScrollOffset()) {
                isFlipping = false;
            }
            // 动画停止的操作

//            // 次数控制
//            if (timesCount < flipTimes) {
//                timesCount += 1;
//
//                initTextView();
//                isFlipping = true;
//                mScroller.startScroll(0, 0, 0, layoutHeight, 1000);
//                postInvalidate();
//            } else {
//                timesCount = 0;
//                flipTimes = 0;
//                if (null != mFlipOverListener && !isFlipping()) {
//                    mFlipOverListener.onFLipOver(FlipLayout.this);
//                }
//            }
        }

    }

    /**
     * 显示需要显示的数字
     *
     * @param canvas
     */
    private void showViews(Canvas canvas) {
        String current = mVisibleTextView.getText().toString();
        String past = mInvisibleTextView.getText().toString();
        mVisibleTextView.setText(past);
        mInvisibleTextView.setText(current);

        Log.d("TAG", "-----current---->" + current);
        Log.d("TAG", "-----past---->" + past);

        //防止切换抖动
        drawChild(canvas, mVisibleTextView, 0);
    }

    /**
     * 画下半部分
     */
    private void drawBottomHalf(Canvas canvas) {
        canvas.save();
        // 控制画布的显示区域和需要显示的子View
        canvas.clipRect(mBottomRect);
        View drawView = isUp ? mInvisibleTextView : mVisibleTextView;
        drawChild(canvas, drawView, 0);

        canvas.restore();
    }

    /**
     * 画上半部分
     */
    private void drawTopHalf(Canvas canvas) {
        canvas.save();

        canvas.clipRect(mTopRect);
        View drawView = isUp ? mVisibleTextView : mInvisibleTextView;
        drawChild(canvas, drawView, 0);

        canvas.restore();

    }

    /**
     * 画翻页部分
     */
    private void drawFlipHalf(Canvas canvas) {
        canvas.save();
        mCamera.save();

        View view = null;
        float deg = getDeg(); // 翻转的角度
        if (deg > 90) { // 大于90度，实现新View下半部分的视觉翻转
            canvas.clipRect(isUp ? mTopRect : mBottomRect); // 控制显示区域
            mCamera.rotateX(isUp ? deg - 180 : -(deg - 180));
            view = mInvisibleTextView;
        } else { // 小于等于90度,实现旧View上半部分的视觉翻转
            canvas.clipRect(isUp ? mBottomRect : mTopRect);
            mCamera.rotateX(isUp ? deg : -deg);
            view = mVisibleTextView;
        }

        mCamera.getMatrix(mMatrix);
        positionMatrix();
        canvas.concat(mMatrix);

        if (view != null) {
            drawChild(canvas, view, 0);
        }

        drawFlippingShadeShine(canvas);

        mCamera.restore();
        canvas.restore();
    }

    private float getDeg() {
        return mScroller.getCurrY() * 1.0f / layoutHeight * 180;
    }

    /**
     * 绘制翻页时的阳面和阴面
     */
    private void drawFlippingShadeShine(Canvas canvas) {
        final float degreesFlipped = getDeg();
        Log.d(TAG, "deg: " + degreesFlipped);
        if (degreesFlipped < 90) {
            final int alpha = getAlpha(degreesFlipped);
            Log.d(TAG, "小于90度时的透明度-------------------> " + alpha);
            mShinePaint.setAlpha(alpha);
            mShadePaint.setAlpha(alpha);
            canvas.drawRect(isUp ? mBottomRect : mTopRect, isUp ? mShinePaint : mShadePaint);
        } else {
            final int alpha = getAlpha(Math.abs(degreesFlipped - 180));
            Log.d(TAG, "大于90度时的透明度-------------> " + alpha);
            mShadePaint.setAlpha(alpha);
            mShinePaint.setAlpha(alpha);
            canvas.drawRect(isUp ? mTopRect : mBottomRect, isUp ? mShadePaint : mShinePaint);
        }
    }

    /**
     * 计算翻页的阴阳面的透明度
     *
     * @param degreesFlipped
     * @return
     */
    private int getAlpha(float degreesFlipped) {
        return (int) ((degreesFlipped / 90f) * 100);
    }

    private void positionMatrix() {
        mMatrix.preScale(0.25f, 0.25f);
        mMatrix.postScale(4.0f, 4.0f);
        mMatrix.preTranslate(-getWidth() / 2, -getHeight() / 2);
        mMatrix.postTranslate(getWidth() / 2, getHeight() / 2);
    }

    /**
     * 初始化隐藏textView显示的值
     */
    private void initTextView(int nextValue) {
//        int visibleValue = Integer.parseInt(mVisibleTextView.getText().toString());
//        int invisibleValue = isUp ? visibleValue - 1 : visibleValue + 1;
//
//        if (invisibleValue < 0) {
//            invisibleValue += 10;
//        }
//
//        if (invisibleValue >= 10) {
//            invisibleValue -= 10;
//        }
//
//        mInvisibleTextView.setText(String.valueOf(invisibleValue));
    }

//    /**
//     * 根据传入的次数计算动画的时间
//     * 控制翻页速度
//     */
//    private int getAnimDuration(int times) {
////        Log.e(TAG,"计算用的次数： " + times);
//        if (times <= 0) {
//            times = 1;
//        }
//        int animDuration = 500 - (500 - 100) / 9 * times;
////        Log.e(TAG, "animDuration: " + animDuration);
//        return 1000;
//    }

    public static interface FlipOverListener {
        /**
         * 翻页完成回调
         *
         * @param flipLayout 当前翻页的控件
         */
        void onFLipOver(FlipLayout flipLayout);
    }

    //----------API-------------

    /**
     * @param nextValue 下一个值
     * @param isFlipUp  方向标识 true: 往上翻转 , false: 往下翻转
     */
    public void smoothFlip(int nextValue, boolean isFlipUp) {
        this.isUp = isFlipUp;
        // 初始化默认显示
        mInvisibleTextView.setText(String.valueOf(nextValue));
        isFlipping = true;
        mScroller.startScroll(0, 0, 0, layoutHeight, 750);
        postInvalidate();
    }

    /**
     * 不带动画翻动
     *
     * @param value
     */
    public void flip(int value) {
        mVisibleTextView.setText(String.valueOf(value));
//        if(null != mFlipOverListener){
//            mFlipOverListener.onFLipOver(FlipLayout.this,timesCount);
//        }
    }

    public void addFlipOverListener(FlipOverListener flipOverListener) {
        this.mFlipOverListener = flipOverListener;
    }

    public TextView getmVisibleTextView() {
        return mVisibleTextView;
    }

    public TextView getmInvisibleTextView() {
        return mInvisibleTextView;
    }

    public boolean isUp() {
        return isUp;
    }

    /**
     * @param resId 图片资源id
     */
    public void setFlipTextBackground(int resId) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (null != child) {
                child.setBackgroundResource(resId);
            }
        }
    }

    public void setFLipTextSize(float size) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            TextView child = (TextView) getChildAt(i);
            if (null != child) {
                child.setTextSize(size);
            }
        }
    }

    public void setFLipTextColor(int color) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            TextView child = (TextView) getChildAt(i);
            if (null != child) {
                child.setTextColor(color);
            }
        }
    }


    public boolean isFlipping() {
        return isFlipping && !mScroller.isFinished() && mScroller.computeScrollOffset();
    }

    public int getCurrentValue() {
        return Integer.parseInt(mVisibleTextView.getText().toString());
    }
}