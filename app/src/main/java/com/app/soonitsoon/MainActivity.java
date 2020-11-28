package com.app.soonitsoon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.app.soonitsoon.background.BackgroundService;
import com.app.soonitsoon.background.BootReceiver;
import com.app.soonitsoon.interest.InterestActivity;
import com.app.soonitsoon.message.MessageActivity;
import com.app.soonitsoon.safety.CheckSafetyInfo;
import com.app.soonitsoon.timeline.TimelineActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    public static Activity activity;
    private Intent mBackgroundServiceIntent;
    private BackgroundService mBackgroundService;

    private Context context = this;

    // 나중에 지우기
    private CheckSafetyInfo checkSafetyInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // 위치 권한 허용 받기
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 0);
            }
        }

        // 배터리 최적화 제외 권한 받기
        // TODO
//        if (ContextCompat.checkSelfPermission(this, android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) != PackageManager.PERMISSION_GRANTED) {
//
//        }

        // 배터리 예외 권한이 있는지 확인
        if (PackageManager.PERMISSION_GRANTED != getApplication().getPackageManager()
                .checkPermission(android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        getApplication().getPackageName())) { // 권한 체크
            Log.d(TAG, "checkBatteryOptimization: application hasn't REQUEST_IGNORE_BATTERY_OPTIMIZATIONS permission");
        }

        // 배터리 최적화 예외가 되어 있지 않으면 세팅 화면 열기
        PowerManager powerManager = (PowerManager) getApplication().getSystemService(Context.POWER_SERVICE);
        boolean ignoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(this.getPackageName());
        if (!ignoringBatteryOptimizations) {
            Log.d(TAG, "checkBatteryOptimization: Not ignored Battery Optimizations.");

            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse(String.format("package:%s", this.getPackageName())));
            startActivity(intent);
        }


        // BackgroundService
        mBackgroundService = new BackgroundService(getApplicationContext());
        mBackgroundServiceIntent = new Intent(getApplicationContext(), mBackgroundService.getClass());
        // 서비스가 실행 중인지 확인
        if (!BootReceiver.isServiceRunning(this, mBackgroundService.getClass())) {
            // 서비스가 실행하고 있지 않는 경우 서비스 실행
            startService(mBackgroundServiceIntent);
        }

        // 화면 전환 버튼들
        Button mainBtnBriefing = findViewById(R.id.btn_home_briefing);
        mainBtnBriefing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), BriefingActivity.class);
//                startActivity(intent);
/*
                // Unit
                JSONObject jsondangerUnit11 = new JSONObject();
                JSONObject jsondangerUnit12 = new JSONObject();
                JSONObject jsondangerUnit21 = new JSONObject();
                try {
                    jsondangerUnit11.put("startTime", "12:00:00");
                    jsondangerUnit11.put("endTime", "14:00:00");
                    jsondangerUnit11.put("place", "해남읍 정성한우촌");

                    jsondangerUnit12.put("startTime", "11:00:00");
                    jsondangerUnit12.put("endTime", "14:00:00");
                    jsondangerUnit12.put("place", "해남 삼산면 매화정");

                    jsondangerUnit21.put("startTime", "21:51:00");
                    jsondangerUnit21.put("endTime", "22:37:00");
                    jsondangerUnit21.put("place", "우리동네오락실");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String stringdangerUnit11 = jsondangerUnit11.toString();
                String stringdangerUnit12 = jsondangerUnit12.toString();
                String stringdangerUnit21 = jsondangerUnit21.toString();

                // List
                JSONObject jsondangerList1 = new JSONObject();
                JSONObject jsondangerList2 = new JSONObject();

                try {
                    jsondangerList1.put("1", stringdangerUnit11);
                    jsondangerList1.put("2", stringdangerUnit12);
                    jsondangerList2.put("1", stringdangerUnit21);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String stringdangerList1 = jsondangerList1.toString();
                String stringdangerList2 = jsondangerList2.toString();

                JSONObject jsonDangerObject = new JSONObject();
                try {
                    jsonDangerObject.put("2020-11-22", stringdangerList1);
                    jsonDangerObject.put("2020-11-21", stringdangerList2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String stringDangerObject = jsonDangerObject.toString();
                // File
                FileOutputStream outputStream;
                try {
                    outputStream = getApplication().openFileOutput("test.json", Context.MODE_PRIVATE);
                    outputStream.write(stringDangerObject.getBytes());
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/
            }
        });
        Button mainBtnInterest = findViewById(R.id.btn_home_interest);
        mainBtnInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InterestActivity.class);
                startActivity(intent);
            }
        });
        Button mainBtnTimeline = findViewById(R.id.btn_home_timeline);
        mainBtnTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
                startActivity(intent);
            }
        });
        Button mainBtnSafety = findViewById(R.id.btn_home_safety);
        mainBtnSafety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SafetyActivity.class);
                startActivity(intent);
            }
        });
        Button mainBtnMsg = findViewById(R.id.btn_home_message);
        mainBtnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                startActivity(intent);
            }
        });
    }
}
