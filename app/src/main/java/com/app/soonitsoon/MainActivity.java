package com.app.soonitsoon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        // 배터리 최적화 제외 권한 받기
        // TODO
//        if (ContextCompat.checkSelfPermission(this, android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) != PackageManager.PERMISSION_GRANTED) {
//
//        }

        // 배터리 예외 권한이 있는지 확인
//        if (PackageManager.PERMISSION_GRANTED != getApplication().getPackageManager()
//                .checkPermission(android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
//                        getApplication().getPackageName())) { // 권한 체크
//            Log.d(TAG, "checkBatteryOptimization: application hasn't REQUEST_IGNORE_BATTERY_OPTIMIZATIONS permission");
//        }

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

                // Unit
                JSONObject jsonDangerUnit11 = new JSONObject();
                JSONObject jsonDangerUnit12 = new JSONObject();
                JSONObject jsonDangerUnit21 = new JSONObject();
                try {
                    jsonDangerUnit11.put("startTime", "12:00:00");
                    jsonDangerUnit11.put("endTime", "14:00:00");
                    jsonDangerUnit11.put("place", "해남읍 정성한우촌");

                    jsonDangerUnit12.put("startTime", "11:00:00");
                    jsonDangerUnit12.put("endTime", "14:00:00");
                    jsonDangerUnit12.put("place", "해남 삼산면 매화정");

                    jsonDangerUnit21.put("startTime", "21:51:00");
                    jsonDangerUnit21.put("endTime", "22:37:00");
                    jsonDangerUnit21.put("place", "우리동네오락실");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String stringDangerUnit11 = jsonDangerUnit11.toString();
                String stringDangerUnit12 = jsonDangerUnit12.toString();
                String stringDangerUnit21 = jsonDangerUnit21.toString();

                // List
                JSONObject jsonDangerList1 = new JSONObject();
                JSONObject jsonDangerList2 = new JSONObject();

                try {
                    jsonDangerList1.put("1", stringDangerUnit11);
                    jsonDangerList1.put("2", stringDangerUnit12);
                    jsonDangerList2.put("1", stringDangerUnit21);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String stringDangerList1 = jsonDangerList1.toString();
                String stringDangerList2 = jsonDangerList2.toString();

                JSONObject jsonDangerObject = new JSONObject();
                try {
                    jsonDangerObject.put("2020-11-22", stringDangerList1);
                    jsonDangerObject.put("2020-11-21", stringDangerList2);
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
