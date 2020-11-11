package com.app.soonitsoon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.app.soonitsoon.timeline.GpsTracker;
import com.app.soonitsoon.timeline.TimelineData;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {
    private static final int MININUTE = 2;
    private static final int PERIOD = 1000 * 60 * MININUTE;
    private final static String TAG = BackgroundService.class.getSimpleName();

    private Context context = null;
    static int counter=1;

    // 테스트 버튼 때문에 public static 선언
    private GpsTracker gpsTracker;
    public static TimelineData timelineData;

    public BackgroundService() {
    }

    public BackgroundService(Context applicationContext) {
        super();
        context = applicationContext;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "BackgroundService.onCreate");
        timelineData = new TimelineData();
        gpsTracker = new GpsTracker(this);

//        Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "BackgroundService.onStartCommand");

        final Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Log.e("테스크 카운터", String.valueOf(counter));
                counter++;

                Location location = gpsTracker.getLocation();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                timelineData.add(latitude, longitude);

            }
        };
        timer.schedule(tt, 0, PERIOD);

//        Toast.makeText(this, "onStartCommand", Toast.LENGTH_LONG).show();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "BackgroundService.onDestroy");

        Intent broadcastIntent = new Intent("com.app.soonitsoon.RestartService");
        sendBroadcast(broadcastIntent);

//        Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e(TAG, "BackgroundService.onTaskRemoved");
        //create an intent that you want to start again.
        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        super.onTaskRemoved(rootIntent);

//        Toast.makeText(this, "onTaskRemoved", Toast.LENGTH_LONG).show();
    }
}