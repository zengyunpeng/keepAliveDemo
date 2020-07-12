package com.teemo.keepalive;

import android.app.Application;
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

import com.teemo.keepalive.MyAidlInterface;
import com.teemo.keepalive.RemoteService;


public class LocalService extends Service {
    private static final String TAG = LocalService.class.getName();
    private MyBinder mBinder;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyAidlInterface iMyAidlInterface = MyAidlInterface.Stub.asInterface(service);
            try {
                Log.e("LocalService", "connected with " + iMyAidlInterface.getServiceName());
                //TODO whh 本地service被拉起，检测如果mainActivity不存在则拉起
//                if (MyApplication.getMainActivity() == null) {
//                    Intent intent = new Intent(LocalService.this.getBaseContext(), MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    getApplication().startActivity(intent);
//                }
                KeepAliveConfig.callBack.onStartMainActivity();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected: 链接断开，重新启动 RemoteService");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(LocalService.this, RemoteService.class));
            } else {
                startService(new Intent(LocalService.this, RemoteService.class));
            }
            bindService(new Intent(LocalService.this, RemoteService.class), connection, Context.BIND_IMPORTANT);
        }
    };

    public LocalService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static final String CHANNEL_ID_STRING = LocalService.class.getSimpleName();
    public static final int NOTIFICATION_ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: LocalService 启动");
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_STRING);
            builder.setTicker("本地服务正在运行")
                    .setContentIntent(contentIntent)
                    .setContentTitle("本地服务正在运行")
                    .setAutoCancel(true)
                    .setContentText("哈哈")
                    .setWhen(System.currentTimeMillis());
            //把service设置为前台运行，避免手机系统自动杀掉改服务。
            startForeground(NOTIFICATION_ID, builder.build());
        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(CHANNEL_ID_STRING, "本地服务", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING)
                    .setTicker("本地服务正在运行")
                    .setContentTitle("本地服务正在运行")
                    .setAutoCancel(true)
//                    .setContentText("哈哈")
                    .build();
            startForeground(NOTIFICATION_ID, notification);
        }


        Log.e(TAG, "启动远程服务");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(LocalService.this, RemoteService.class));
        } else {
            startService(new Intent(LocalService.this, RemoteService.class));

        }
        bindService(new Intent(LocalService.this, RemoteService.class), connection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new MyBinder();
        return mBinder;
    }

    private class MyBinder extends MyAidlInterface.Stub {

        public String getServiceName() throws RemoteException {
            return LocalService.class.getName();
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }
}