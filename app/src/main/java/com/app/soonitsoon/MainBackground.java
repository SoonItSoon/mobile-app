package com.app.soonitsoon;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.app.soonitsoon.timeline.AddTimeline;
import com.app.soonitsoon.timeline.GpsTracker;

import net.daum.mf.map.api.MapView;

import java.util.Timer;
import java.util.TimerTask;

public class MainBackground extends MainActivity {
    private static final int MININUTE = 5;
    private static final int PERIOD = 1000 * 60 * MININUTE;

    static int counter = 1;

    public static AddTimeline addTimeline;
    public GpsTracker gpsTracker;

    public MainBackground(Activity mainActivity, MapView mapView) {
        gpsTracker = new GpsTracker(mainActivity);
        addTimeline = new AddTimeline(mapView);
    }

    public void run() {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Log.e("테스크 카운터", String.valueOf(counter));
                counter++;

//                AddTimeline addTimeline = new AddTimeline(mainActivity, mapView);
//                MainActivity main = new MainActivity();
//                mainActivity = main.mainActivity;
                Location location = gpsTracker.getLocation();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                addTimeline.add(latitude, longitude);
            }
        };

        Timer timer = new Timer();
        timer.schedule(tt, 0, PERIOD);
    }
}
