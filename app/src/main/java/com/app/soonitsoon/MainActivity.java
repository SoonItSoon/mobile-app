package com.app.soonitsoon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.soonitsoon.timeline.DateNTime;
import com.app.soonitsoon.timeline.GpsTracker;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MainActivity extends AppCompatActivity {
    public Activity mainActivity = this;
    private Intent mBackgroundServiceIntent;
    private BackgroundService mBackgroundService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 위치 권한 허용 받기
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        final MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        GpsTracker gpsTracker = new GpsTracker(mainActivity);
        // 화면 이동
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 2, true);

        // BackgroundService
        mBackgroundService = new BackgroundService(getApplicationContext());
        mBackgroundServiceIntent = new Intent(getApplicationContext(), mBackgroundService.getClass());
        // 서비스가 실행 중인지 확인
        if (!BootReceiver.isServiceRunning(this, mBackgroundService.getClass())) {
            // 서비스가 실행하고 있지 않는 경우 서비스 실행
            startService(mBackgroundServiceIntent);
        }

        Button gpsTimelineBtn = findViewById(R.id.showTimeline);
        gpsTimelineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateNTime dateNTime = new DateNTime();
                String date = dateNTime.getDate();

                String toastStr = date + " Timeline 입니다.";
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
                BackgroundService.timelineData.show(mapView, date);
            }
        });



        Button btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BackgroundService.timelineData.addTest(1);

                String toastStr = "겨리집 추가완료";
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
            }
        });
        Button btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BackgroundService.timelineData.addTest(2);

                String toastStr = "운수집 추가완료";
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
            }
        });
        Button btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BackgroundService.timelineData.addTest(3);

                String toastStr = "수눅집 추가완료";
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
            }
        });
        Button btn4 = findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BackgroundService.timelineData.addTest(4);

                String toastStr = "에운집 추가완료";
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
            }
        });
        Button btn5 = findViewById(R.id.btn5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BackgroundService.timelineData.addTest(5);

                String toastStr = "현수집 추가완료";
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
            }
        });
    }
}
