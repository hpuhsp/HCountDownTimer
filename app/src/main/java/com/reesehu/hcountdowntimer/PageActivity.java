package com.reesehu.hcountdowntimer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.reesehu.hcountdowntimer.view.NumberCDView;

public class PageActivity extends AppCompatActivity implements NumberCDView.OnCountDownStateListener {

    private TextView mCountDownTips;
    private TextView mFlipTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        final NumberCDView numberCDView = findViewById(R.id.num_cd_view);
        numberCDView.setCountDownValues(1000 * 10 + 200); // 传入的是剩余的时间段，毫秒级

        Button button = findViewById(R.id.btn_test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberCDView.start();
            }
        });
        numberCDView.addOnCountDownStateListener(this);
        mCountDownTips = findViewById(R.id.tv_count_down_finish);
        mFlipTips = findViewById(R.id.tv_flip_view_finish);
    }

    /**
     * 定时器完成
     */
    @Override
    public void onTimerFinished() {
        mCountDownTips.setText("倒计时完成！");
    }

    /**
     * 之前调试用，完成可以忽略
     */
    @Override
    public void onFlipViewFinished() {
        mFlipTips.setText("最终动画完成！");
    }
}
