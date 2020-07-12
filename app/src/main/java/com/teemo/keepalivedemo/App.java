package com.teemo.keepalivedemo;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.teemo.keepalive.KeepAliveConfig;

public class App extends Application {
    public static final String TAG = App.class.getSimpleName();
    private static MainActivity mainActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        if (KeepAliveConfig.isMainProcess(this)) {
            Log.i(TAG, "进行主进程的初始化");
            Log.i(TAG, "启动本地服务");
            KeepAliveConfig.init(this, new KeepAliveConfig.StartMainCallBack() {
                @Override
                public void onStartMainActivity() {
                    Log.i(TAG, "执行回调方法");
                    Log.i(TAG, "App.getMainActivity(): " + App.getMainActivity());
                    if (App.getMainActivity() == null) {
                        Log.i(TAG, "启动主页");
                        Intent intent = new Intent(App.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        App.this.startActivity(intent);
                    }
                }
            });
        } else {
            Log.i(TAG, "守护进程的初始化");
            //do nothing
        }


    }


    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(MainActivity activity) {
        mainActivity = activity;
    }
}
