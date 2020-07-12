package com.teemo.keepalive;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class RemoteService extends Service {
    private static final String TAG = RemoteService.class.getName();
    private MyBinder mBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyAidlInterface iMyAidlInterface = MyAidlInterface.Stub.asInterface(service);
            try {
                Log.e(TAG, "connected with " + iMyAidlInterface.getServiceName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected: 链接断开，重新启动 LocalService");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(RemoteService.this, LocalService.class));
            } else {
                startService(new Intent(RemoteService.this, LocalService.class));
            }
            bindService(new Intent(RemoteService.this, LocalService.class), connection, Context.BIND_IMPORTANT);
        }
    };

    public RemoteService() {
    }

    public static final String CHANNEL_ID_STRING = RemoteService.class.getSimpleName();
    public static final int NOTIFICATION_ID = 2;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "flags: " + flags + "*** startId: " + startId);
        Log.e(TAG, "onStartCommand: RemoteService 启动");
        Log.e(TAG, "启动通知栏");

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, RemoteService.class.getSimpleName());
            builder.setTicker("远端服务正在运行")
                    .setContentIntent(contentIntent)
                    .setContentTitle("远端服务正在运行")
                    .setAutoCancel(true)
                    .setContentText("远端服务正在运行")
                    .setWhen(System.currentTimeMillis());

            //把service设置为前台运行，避免手机系统自动杀掉改服务。
            startForeground(startId, builder.build());
        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(CHANNEL_ID_STRING, "远端服务", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING)
                    .setTicker("远端服务正在运行")
                    .setContentTitle("远端服务正在运行")
                    .setAutoCancel(true)
//                    .setContentText("哈哈")
                    .build();
            startForeground(NOTIFICATION_ID, notification);
        }


        bindService(new Intent(this, LocalService.class), connection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new MyBinder();
        return mBinder;
    }

    private class MyBinder extends MyAidlInterface.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return RemoteService.class.getName();
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }
}
