package com.app.soonitsoon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.soonitsoon.timeline.GpsTracker;

import net.daum.mf.map.api.MapView;

public class MainActivity extends AppCompatActivity {
    public Activity mainActivity = this;

    GpsTracker gpsTracker = new GpsTracker(mainActivity);

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

        MainBackground mainBackground = new MainBackground(mainActivity, mapView);
        mainBackground.run();

        Button gpsBtn = findViewById(R.id.gpsBtn);
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = gpsTracker.getLocation();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                String toastStr;
                if (MainBackground.timelineData.add(latitude, longitude)) {
                    toastStr = "latitude : " + latitude + ", longitude : " + longitude;
                } else {
                    toastStr = "거리가 허용범위보다 작아 Timeline이 추가되지 않았습니다.";
                }
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
            }
        });
    }

    public double getLat() {
        GpsTracker gpsTracker = new GpsTracker(this);
        return gpsTracker.getLatitude();
    }
    public double getLon() {
        GpsTracker gpsTracker = new GpsTracker(this);
        return gpsTracker.getLongitude();
    }
}
