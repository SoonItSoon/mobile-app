package com.app.soonitsoon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.app.soonitsoon.timeline.DateNTime;
import com.app.soonitsoon.timeline.GpsTracker;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class TimelineActivity extends AppCompatActivity {
    private Activity activity = this;
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // 상단 바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_icon); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = findViewById(R.id.timeline_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.nav_item_home){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else if(id == R.id.nav_item_timeline){
                    Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.nav_item_test1){
                    Intent intent = new Intent(getApplicationContext(), Test1Activity.class);
                    startActivity(intent);
                }
                else if(id == R.id.nav_item_test2){
                    Intent intent = new Intent(getApplicationContext(), Test2Activity.class);
                    startActivity(intent);
                }
                else if(id == R.id.nav_item_test3){
                    Intent intent = new Intent(getApplicationContext(), Test3Activity.class);
                    startActivity(intent);
                }

                return true;
            }
        });

        final MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        GpsTracker gpsTracker = new GpsTracker(activity);
        // 화면 이동
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(), gpsTracker.getLongitude()), 2, true);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
