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

import com.app.soonitsoon.server.GetServerInfo;
import com.app.soonitsoon.safety.CheckSafetyInfo;
import com.app.soonitsoon.timeline.GetLocation;
import com.app.soonitsoon.timeline.RecordTimeline;
import com.app.soonitsoon.timeline.UpdateTimeline;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
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

                // RecordTimeline 실행
                // Timeline
                Location location = getLocation.getLocation();
                double latitude = location.getLatitude();
//                double latitude = 35 - counter;
                double longitude = location.getLongitude();
                try {
                    recordTimeline.excute(latitude, longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // REST 콜 파싱 테스트
//                if(counter == 1) {
//                    String str = GetServerInfo.getTestData();
//                    Log.e(TAG, "REST 콜을 통해 읽은 Json : " + str);
//
//                    try {
//                        JSONObject jsonObject = new JSONObject(str);
//
//                        // Key Set
//                        Iterator<String> iterator = jsonObject.keys();
//                        while (iterator.hasNext()) {
//                            String time = iterator.next();
//                            String stringTLUnit = "";
//                            JSONObject jsonTLUnit = new JSONObject();
//                            try {
//                                stringTLUnit = jsonObject.getString(time);
//                                jsonTLUnit = new JSONObject(stringTLUnit);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            int lat = 0;
//                            String lon = "";
//                            String danger = "";
//                            try {
//                                lat = jsonTLUnit.getInt("id");
//                                lon = jsonTLUnit.getString("date");
//                                danger = jsonTLUnit.getString("text");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            Log.e(TAG, "파싱된 시간 : " + time);
//                            Log.e(TAG, "파싱된 lon : " + lat);
//                            Log.e(TAG, "파싱된 lat : " + lon);
//                            Log.e(TAG, "파싱된 danger : " + danger);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }

                // checkSafetyInfo 실행
                // TODO : 윤수한테 "추가된 확진자 접촉 의심 지역" 이름 지어달라고 하기
                try {
                    checkSafetyInfo.getDangerInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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