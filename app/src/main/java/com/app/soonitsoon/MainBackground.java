package com.app.soonitsoon;

import android.app.Activity;
import android.util.Log;

import com.app.soonitsoon.timeline.AddTimeline;
import com.app.soonitsoon.timeline.GpsTracker;

import net.daum.mf.map.api.MapView;

import java.util.Timer;
import java.util.TimerTask;

public class MainBackground {
    private static final int PERIOD = 600 * 1000;    // 타이머 동작 시간 (ms)

    static int counter = 1;

    private Activity mainActivity;
    private MapView mapView;
    public static AddTimeline addTimeline;

    public MainBackground(Activity mainActivity, MapView mapView) {
        this.mainActivity = mainActivity;
//        this.mapView = mapView;
        addTimeline = new AddTimeline(this.mainActivity, mapView);
    }

    public void run() {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Log.e("테스크 카운터", String.valueOf(counter));
                counter++;

//                AddTimeline addTimeline = new AddTimeline(mainActivity, mapView);
                GpsTracker gpsTracker = new GpsTracker(mainActivity);
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                addTimeline.add(latitude, longitude);
            }
        };

        Timer timer = new Timer();
        timer.schedule(tt, 0, PERIOD);
    }
}
