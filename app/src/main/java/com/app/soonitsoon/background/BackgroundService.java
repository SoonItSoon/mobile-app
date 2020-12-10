package com.app.soonitsoon.background;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.soonitsoon.interest.CheckInterestInfo;
import com.app.soonitsoon.safety.CheckSafetyInfo;
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
    private CheckSafetyInfo checkSafetyInfo;
    private CheckInterestInfo checkInterestInfo;

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
        checkSafetyInfo = new CheckSafetyInfo(this, getApplication());
        checkInterestInfo = new CheckInterestInfo(this, getApplication());

//        Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "onStartCommand");

        // 지정한 시간마다 동작하는 타이머
        final Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Log.e("테스크 카운터", String.valueOf(counter));

                // 위치 권한이 허용되어 있는 경우에만
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // RecordTimeline 실행
                        // Timeline
                        Location location = getLocation.getLocation();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        try {
                            recordTimeline.excute(latitude, longitude);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "GPS OFF!");
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // RecordTimeline 실행
                        // Timeline
                        Location location = getLocation.getLocation();
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        try {
                            recordTimeline.excute(latitude, longitude);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "GPS OFF!");
                    }
                }

                // checkSafetyInfo 실행
                try {
                    checkSafetyInfo.getDangerInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // CheckInterestInfo 실행
                checkInterestInfo.checkInterest();

                counter++;
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
//
//        final Timer timer = new Timer();
//        TimerTask tt = new TimerTask() {
//            @Override
//            public void run() {
//                Log.e("테스크 카운터", String.valueOf(counter));
//                counter++;
//
//                Location location = getLocation.getLocation();
//                double latitude = location.getLatitude();
////                double latitude = 38 - ((double)counter/10);
//                double longitude = location.getLongitude();
//                try {
//                    recordTimeline.excute(latitude, longitude);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        };
//        timer.schedule(tt, 0, PERIOD);

        // Intent 재시작
        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        super.onTaskRemoved(rootIntent);

//        Toast.makeText(this, "onTaskRemoved", Toast.LENGTH_LONG).show();
    }
}