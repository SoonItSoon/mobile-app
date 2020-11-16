package com.app.soonitsoon.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.app.soonitsoon.timeline.GetLocation;
import com.app.soonitsoon.timeline.RecordTimeline;

import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {
    private static final String TAG = "BackgoundService";

    private static final int MININUTE = 1;
    private static final int PERIOD = 1000 * 60 * MININUTE;

    private Context context;
    private static int counter=1;

    private GetLocation getLocation;
    private RecordTimeline recordTimeline;

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
        Log.e(TAG, "onCreate");
        recordTimeline = new RecordTimeline(this, getApplication());
        getLocation = new GetLocation(this);

//        Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "onStartCommand");

        final Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Log.e("테스크 카운터", String.valueOf(counter));
                counter++;

                Location location = getLocation.getLocation();
                double latitude = location.getLatitude();
//                double latitude = 35 - counter;
                double longitude = location.getLongitude();
                try {
                    recordTimeline.excute(latitude, longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        timer.schedule(tt, 0, PERIOD);

//        Toast.makeText(this, "onStartCommand", Toast.LENGTH_LONG).show();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");

        Intent broadcastIntent = new Intent("com.app.soonitsoon.RestartService");
        sendBroadcast(broadcastIntent);

//        Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e(TAG, "onTaskRemoved");

        final Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Log.e("테스크 카운터", String.valueOf(counter));
                counter++;

                Location location = getLocation.getLocation();
                double latitude = location.getLatitude();
//                double latitude = 38 - ((double)counter/10);
                double longitude = location.getLongitude();
                try {
                    recordTimeline.excute(latitude, longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        timer.schedule(tt, 0, PERIOD);

        // Intent 재시작
//        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
//        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
//        super.onTaskRemoved(rootIntent);

//        Toast.makeText(this, "onTaskRemoved", Toast.LENGTH_LONG).show();
    }
}