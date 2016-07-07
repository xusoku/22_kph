package com.davis.kangpinhui.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.davis.kangpinhui.activity.ScreenSaverActivity;
import com.davis.kangpinhui.util.AppManager;
import com.davis.kangpinhui.util.LogUtils;
import com.davis.kangpinhui.util.PowerManagerWakeLock;

public class ScreenSaverS extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onCreate() {
        // 屏蔽系统的屏保
        KeyguardManager manager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = manager
                .newKeyguardLock("KeyguardLock");
        lock.disableKeyguard();

        // 注册一个监听屏幕开启和关闭的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, filter);
    }

    BroadcastReceiver screenReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {

            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {//如果接受到关闭屏幕的广播
                if (!AppManager.getAppManager().IsAppActivity(ScreenSaverActivity.class)) {
                    //开启屏幕唤醒，常亮
                    PowerManagerWakeLock.acquire(ScreenSaverS.this);
                }
                PowerManagerWakeLock.acquire(ScreenSaverS.this);
                Intent intent2 = new Intent(ScreenSaverS.this,
                        ScreenSaverActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                PowerManagerWakeLock.release();
            }
        }
    };

    public void onDestroy() {
        PowerManagerWakeLock.release();
        unregisterReceiver(screenReceiver);

    };

}
