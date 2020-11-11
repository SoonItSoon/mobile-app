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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
//    public Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
//    public Intent timelineIntent = new Intent(getApplicationContext(), TimelineActivity.class);
//    public Intent test1Intent = new Intent(getApplicationContext(), Test1Activity.class);
//    public Intent test2Intent = new Intent(getApplicationContext(), Test2Activity.class);
//    public Intent test3Intent = new Intent(getApplicationContext(), Test3Activity.class);


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

        // 상단 바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_icon); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = findViewById(R.id.home_layout);

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
                    Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                    startActivity(intent);
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


        // BackgroundService
        mBackgroundService = new BackgroundService(getApplicationContext());
        mBackgroundServiceIntent = new Intent(getApplicationContext(), mBackgroundService.getClass());
        // 서비스가 실행 중인지 확인
        if (!BootReceiver.isServiceRunning(this, mBackgroundService.getClass())) {
            // 서비스가 실행하고 있지 않는 경우 서비스 실행
            startService(mBackgroundServiceIntent);
        }

        Button mainBtn = findViewById(R.id.mainBtn1);
        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonUnit1 = new JSONObject();
                try {
                    jsonUnit1.put("latitude", 11.111);
                    jsonUnit1.put("longitude", 111.111);
                    jsonUnit1.put("danger", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject jsonUnit2 = new JSONObject();
                try {
                    jsonUnit2.put("latitude", 22.222);
                    jsonUnit2.put("longitude", 122.222);
                    jsonUnit2.put("danger", 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject jsonList = new JSONObject();
                try {
                    jsonList.put("11:11:11", jsonUnit1);
                    jsonList.put("12:12:12", jsonUnit2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("2020/11/11", jsonList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                FileOutputStream outputStream;
                try {
                    outputStream = openFileOutput("SoonItSoon.json", Context.MODE_PRIVATE);
                    //outputStream.write(jsonObject);
                    Toast.makeText(getApplicationContext(), "성공!!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
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
