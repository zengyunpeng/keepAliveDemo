package com.teemo.keepalive;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.lang.reflect.Method;

public class KeepAliveConfig {


    static StartMainCallBack callBack;

    public static void init(Context context, StartMainCallBack callBack) {
        KeepAliveConfig.callBack = callBack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, LocalService.class));
        } else {
            context.startService(new Intent(context, LocalService.class));
        }
    }

    public interface StartMainCallBack {
        void onStartMainActivity();
    }


    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) context.getSystemService
                (Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
                if (process.pid == pid) {
                    processName = process.processName;
                }
            }
        }
        return processName;
    }

    public static boolean isMainProcess(Context context) {
        /**
         * 是否为主进程
         */
        boolean isMainProcess;
        isMainProcess = context.getPackageName().equals
                (getCurrentProcessName(context));
        return isMainProcess;
    }


//    static Application sApplication;
//
//    public static Application getApplication() {
//        if (sApplication == null) {
//            try {
//                Method method = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication");
//                sApplication = (Application) method.invoke(null, null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return sApplication;
//    }
}
