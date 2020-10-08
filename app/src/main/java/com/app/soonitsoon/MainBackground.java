package com.app.soonitsoon;

import android.app.Activity;
import android.util.Log;

import com.app.soonitsoon.timeline.AddTimeline;

import net.daum.mf.map.api.MapView;

import java.util.Timer;
import java.util.TimerTask;

public class MainBackground {
    final int PERIOD = 600 * 1000;    // 타이머 동작 시간 (ms)

    static int counter = 1;

    AddTimeline addTimeline;

    public MainBackground(Activity mainActivity, MapView mapView) {
        addTimeline = new AddTimeline(mainActivity, mapView);
    }

    public void run() {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Log.e("테스크 카운터", String.valueOf(counter));
                counter++;
                addTimeline.add();
            }
        };

        Timer timer = new Timer();
        timer.schedule(tt, 0, PERIOD);
    }
}
