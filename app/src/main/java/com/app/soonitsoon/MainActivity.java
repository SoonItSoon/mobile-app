package com.app.soonitsoon;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.soonitsoon.background.BackgroundService;
import com.app.soonitsoon.background.BootReceiver;
import com.app.soonitsoon.timeline.TimelineActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    public static Intent mainIntent;
    public static Intent timelineIntent;
    public static Intent test1Intent;
    public static Intent safetyIntent;
    public static Intent test3Intent;


    public Activity mainActivity = this;
    private Intent mBackgroundServiceIntent;
    private BackgroundService mBackgroundService;

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 위치 권한 허용 받기
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        timelineIntent = new Intent(getApplicationContext(), TimelineActivity.class);
        test1Intent = new Intent(getApplicationContext(), Test1Activity.class);
        safetyIntent = new Intent(getApplicationContext(), SafetyActivity.class);
        test3Intent = new Intent(getApplicationContext(), Test3Activity.class);

        // 상단 바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_icon); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = findViewById(R.id.home_layout);

        // 네이게이션 바
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.nav_item_home){
                    Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.nav_item_timeline){
                    startActivity(timelineIntent);
                }
                else if(id == R.id.nav_item_test1){
                    startActivity(test1Intent);
                }
                else if(id == R.id.nav_item_safety){
                    startActivity(safetyIntent);
                }
                else if(id == R.id.nav_item_test3){
                    startActivity(test3Intent);
                }

                return true;
            }
        });


        // BackgroundService
        mBackgroundService = new BackgroundService(getApplicationContext());
        mBackgroundServiceIntent = new Intent(getApplicationContext(), mBackgroundService.getClass());
        // 서비스가 실행 중인지 확인
        if (!BootReceiver.isServiceRunning(this, mBackgroundService.getClass())) {
            // 서비스가 실행하고 있지 않는 경우 서비스 실행
            startService(mBackgroundServiceIntent);
        }

        Button mainBtn1 = findViewById(R.id.mainBtn1);
        mainBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button mainBtn2 = findViewById(R.id.mainBtn2);
        mainBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        Button mainBtn3 = findViewById(R.id.mainBtn3);
        mainBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
