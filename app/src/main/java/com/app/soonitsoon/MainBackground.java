package com.app.soonitsoon;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import com.app.soonitsoon.timeline.GpsTracker;
import com.app.soonitsoon.timeline.TimelineData;

import net.daum.mf.map.api.MapView;

import java.util.Timer;
import java.util.TimerTask;

public class MainBackground {
    private static final int MININUTE = 1;
    private static final int PERIOD = 1000 * 60 * MININUTE / 60;

    static int counter = 1;

    // 테스트 버튼 때문에 public static 선언
    private GpsTracker gpsTracker;
    public static TimelineData timelineData;

    public MainBackground(Activity mainActivity, MapView mapView) {
        gpsTracker = new GpsTracker(mainActivity);
        timelineData = new TimelineData(mapView);
    }

    public void run() {
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

                if (counter > 10) {
                    timer.cancel();
                    Log.e("Timer", "Timer종료");
                }
            }
        };
        timer.schedule(tt, 0, PERIOD);
    }
}
