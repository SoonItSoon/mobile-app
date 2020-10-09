package com.app.soonitsoon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.soonitsoon.timeline.AddTimeline;
import com.app.soonitsoon.timeline.DateNTime;
import com.app.soonitsoon.timeline.GpsTracker;

import net.daum.mf.map.api.MapView;

public class MainActivity extends AppCompatActivity {
    private Activity mainActivity = this;

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
                GpsTracker gpsTracker = new GpsTracker(mainActivity);
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                String toastStr = "latitude : " + latitude + ", longitude : " + longitude;
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();

//                AddTimeline addTimeline = new AddTimeline(mainActivity, mapView);
//                addTimeline.add("현재 위치", latitude, longitude);

//                MainBackground.addTimeline.setGPS(mainActivity);
                MainBackground.addTimeline.add(latitude, longitude);
            }
        });

        Button markBtn1 = findViewById(R.id.markBtn1);
        markBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestData td = new TestData(1);
                String name = td.loc.name;
                double latitude = td.loc.latitude;
                double longitude = td.loc.longitude;

                String toastStr = name + " latitude : " + latitude + ", longitude : " + longitude;
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();

                AddTimeline addTimeline = new AddTimeline(mainActivity, mapView);
                addTimeline.add(name, latitude, longitude);
            }
        });

        Button markBtn2 = findViewById(R.id.markBtn2);
        markBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestData td = new TestData(2);
                String name = td.loc.name;
                double latitude = td.loc.latitude;
                double longitude = td.loc.longitude;

                String toastStr = name + " latitude : " + latitude + ", longitude : " + longitude;
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();

                AddTimeline addTimeline = new AddTimeline(mainActivity, mapView);
                addTimeline.add(name, latitude, longitude);
            }
        });

        Button markBtn3 = findViewById(R.id.markBtn3);
        markBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestData td = new TestData(3);
                String name = td.loc.name;
                double latitude = td.loc.latitude;
                double longitude = td.loc.longitude;

                String toastStr = name + " latitude : " + latitude + ", longitude : " + longitude;
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();

                AddTimeline addTimeline = new AddTimeline(mainActivity, mapView);
                addTimeline.add(name, latitude, longitude);
            }
        });

        Button markBtn4 = findViewById(R.id.markBtn4);
        markBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestData td = new TestData(4);
                String name = td.loc.name;
                double latitude = td.loc.latitude;
                double longitude = td.loc.longitude;

                String toastStr = name + " latitude : " + latitude + ", longitude : " + longitude;
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();

                AddTimeline addTimeline = new AddTimeline(mainActivity, mapView);
                addTimeline.add(name, latitude, longitude);
            }
        });

        Button markBtn5 = findViewById(R.id.markBtn5);
        markBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestData td = new TestData(5);
                String name = td.loc.name;
                double latitude = td.loc.latitude;
                double longitude = td.loc.longitude;

                String toastStr = name + " latitude : " + latitude + ", longitude : " + longitude;
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();

                AddTimeline addTimeline = new AddTimeline(mainActivity, mapView);
                addTimeline.add(name, latitude, longitude);
            }
        });

        final Button dateBtn = findViewById(R.id.dateBtn);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateNTime dateNTime = new DateNTime();
                String curDate = dateNTime.getDate();
                String curTime = dateNTime.getTime();

                String toastStr = curDate + " " + curTime;
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
            }
        });
    }

    public Activity getMainActivity() { return mainActivity; }
}