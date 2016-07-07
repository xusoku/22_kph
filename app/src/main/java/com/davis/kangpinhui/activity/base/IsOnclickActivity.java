package com.davis.kangpinhui.activity.base;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.davis.kangpinhui.activity.ScreenSaverActivity;
import com.davis.kangpinhui.util.LogUtils;

import java.util.Date;

public class IsOnclickActivity extends AppCompatActivity {

    private Handler mHandler01 = new Handler();
    private Handler mHandler02 = new Handler();

    /* 上一次User有动作的Time Stamp */
    private Date lastUpdateTime;
    /* 计算User有几秒没有动作的 */
    private long timePeriod;

    /* 静止超过N秒将自动进入屏保 */
    private float mHoldStillTime = 10;
    /*标识当前是否进入了屏保*/
    private boolean isRunScreenSaver;

    /*时间间隔*/
    private long intervalScreenSaver = 1000;
    private long intervalKeypadeSaver = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		/* 初始取得User可触碰屏幕的时间 */
        lastUpdateTime = new Date(System.currentTimeMillis());
    }

    /**
     * 计时线程
     */
    private Runnable mTask01 = new Runnable() {

        @Override
        public void run() {
            LogUtils.e("dispatchTouchEvent",""+timePeriod);
            Date timeNow = new Date(System.currentTimeMillis());
			/* 计算User静止不动作的时间间距 */
            /**当前的系统时间 - 上次触摸屏幕的时间 = 静止不动的时间**/
            timePeriod = (long) timeNow.getTime() - (long) lastUpdateTime.getTime();

			/*将静止时间毫秒换算成秒*/
            float timePeriodSecond = ((float) timePeriod / 1000);

            if (timePeriodSecond > mHoldStillTime) {
                if (isRunScreenSaver == false) {  //说明没有进入屏保
					/* 启动线程去显示屏保 */
                    mHandler02.postAtTime(mTask02, intervalScreenSaver);
					/*显示屏保置为true*/
                    isRunScreenSaver = true;
                } else {
					/*屏保正在显示中*/
                }
            } else {
				/*说明静止之间没有超过规定时长*/
                isRunScreenSaver = false;
            }
			/*反复调用自己进行检查*/
            mHandler01.postDelayed(mTask01, intervalKeypadeSaver);
        }
    };

    /**
     * 持续屏保显示线程
     */
    private Runnable mTask02 = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (isRunScreenSaver == true) {  //如果屏保正在显示，就计算不断持续显示
//				hideOriginalLayout();
                showScreenSaver();
                mHandler02.postDelayed(mTask02, intervalScreenSaver);
            } else {
                mHandler02.removeCallbacks(mTask02);  //如果屏保没有显示则移除线程
            }
        }
    };

    /**
     * 显示屏保
     */
    private void showScreenSaver() {
        Intent intent = new Intent(this, ScreenSaverActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        updateUserActionTime();
        LogUtils.e("dispatchTouchEvent","dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    /*用户有操作的时候不断重置静止时间和上次操作的时间*/
    public void updateUserActionTime() {
        Date timeNow = new Date(System.currentTimeMillis());
        timePeriod = timeNow.getTime() - lastUpdateTime.getTime();
        lastUpdateTime.setTime(timeNow.getTime());
    }

    @Override
    protected void onResume() {
		/*activity显示的时候启动线程*/
        mHandler01.postAtTime(mTask01, intervalKeypadeSaver);
        Date timeNow = new Date(System.currentTimeMillis());
        lastUpdateTime.setTime(timeNow.getTime());
        super.onResume();
    }

    @Override
    protected void onPause() {
		/*activity不可见的时候取消线程*/
        mHandler01.removeCallbacks(mTask01);
        mHandler02.removeCallbacks(mTask02);
        super.onPause();
    }

}