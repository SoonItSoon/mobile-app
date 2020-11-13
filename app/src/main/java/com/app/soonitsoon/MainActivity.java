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

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    public static Intent mainIntent;
    public static Intent timelineIntent;
    public static Intent test1Intent;
    public static Intent test2Intent;
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
        test2Intent = new Intent(getApplicationContext(), Test2Activity.class);
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
                else if(id == R.id.nav_item_test2){
                    startActivity(test2Intent);
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

                String str = jsonUnit1.toString();
                JSONObject jso = new JSONObject();
                double ddd = 0;
                try {
                    jso = new JSONObject(str);
                    ddd = jso.getDouble("latitude");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, Float.toString((float)ddd));

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
//                    outputStream = openFileOutput("SoonItSoon.json", Context.MODE_PRIVATE);
                    outputStream = getApplication().openFileOutput("SoonItSoon.json", Context.MODE_PRIVATE);
                    outputStream.write(jsonObject.toString().getBytes());
                    outputStream.close();
                    Toast.makeText(getApplicationContext(), "성공!!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button mainBtn2 = findViewById(R.id.mainBtn2);
        mainBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileInputStream inputStream;
                try {
                    inputStream = openFileInput("SoonItSoon.json");
                    String result = inputStream.toString();
                    int i = inputStream.read();
                    Log.e(getAttributionTag(), result);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        Button mainBtn3 = findViewById(R.id.mainBtn3);
        mainBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getFilesDir().getAbsolutePath(), "SoonItSoon.json");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.e("파일 저장", getFilesDir().getAbsolutePath());
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
