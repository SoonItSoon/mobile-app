package com.app.soonitsoon.message;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.app.soonitsoon.R;
import com.app.soonitsoon.server.GetServerInfo;
import com.app.soonitsoon.timeline.DateNTime;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageResultActivity extends AppCompatActivity {
    private final static String TAG = "MessageResultActivity";
    private final static int NUM_OF_DISASTER_LEVELS = 9;
    private static Activity activity;
    private Context context = this;

    // 넘겨받은 검색 조건들
    private String fromDate;    // 검색 시작 날짜
    private String toDate;      // 검색 종료 날짜
    private String mainLocation;    // 시/도
    private String subLocation;     // 시/군/구
    private int disasterIndex;
    // 재난 하위 레이아웃 선택 내용
    private String disasterSubName; // 전염병 종류, 태풍 이름
    private boolean[] disasterSubLevel; // 알림 등급
    private double scale_min;   // 지진 규모 최솟값
    private double scale_max;   // 지진 규모 최댓값
    private String eq_mainLocation;   // 지진 관측 지역 시/도
    private String eq_subLocation;   // 지진 관측 지역 시/군/구

    // Arrays
    private String[] disasterArray; // 재난 종류 Array
    private ArrayList<String[]> disasterLevelArray; // 재난별 등급 Array

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_result);
        activity = this;

        // 뒤로가기 버튼
        Button homeBtn = findViewById(R.id.btn_message_result_back);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Arrays 초기화
        // 재난 종류
        int disasterArrayID = getResources().getIdentifier("disaster", "array", getPackageName());
        disasterArray = getResources().getStringArray(disasterArrayID);
        // 재난별 등급
        disasterLevelArray = new ArrayList<>();
        disasterLevelArray.add(new String[0]);
        for (int i = 1; i <= NUM_OF_DISASTER_LEVELS; i++) {
            int levelArrayID = getResources().getIdentifier("disaster_" + i, "array", getPackageName());
            String[] levelArray = getResources().getStringArray(levelArrayID);
            disasterLevelArray.add(levelArray);
        }

        LinearLayout resultLayout = findViewById(R.id.layout_message_result);

        // 유효한 값을 잘 넘겨받은 경우
        if (receiveCondition()) {

            ////////////////////////////////////////////////////////////////////////////////////// 로그 처리
            String logLine1 = "검색 시작 날짜 : " + fromDate;
            Log.e(TAG, logLine1);
            String logLine2 = "검색 종료 날짜 : " + toDate;
            Log.e(TAG, logLine2);
            String logLine3 = "검색 지역 : " + mainLocation + " " + subLocation;
            Log.e(TAG, logLine3);
            String logLine4 = "재난 종류 : " + disasterArray[disasterIndex];
            Log.e(TAG, logLine4);
            String logLine5 = "";
            String logLine6 = "";
            StringBuilder logLine7 = new StringBuilder("알림 종류 :");
            if (disasterIndex == 1) {   // 전염병
                logLine5 = "전염병 종류 : " + disasterSubName;
                Log.e(TAG, logLine5);
            } else if (disasterIndex == 2) {    // 지진
                logLine5 = "규모 범위 : " + scale_min + " ~ " + scale_max;
                Log.e(TAG, logLine5);
                logLine6 = "지진 발생 지역 : " + eq_mainLocation + " " + eq_subLocation;
                Log.e(TAG, logLine6);
            } else if (disasterIndex == 3) {    // 미세먼지
            } else if (disasterIndex == 4) {    // 태풍
                logLine5 = "태풍 이름 : " + disasterSubName;
                Log.e(TAG, logLine5);
            } else if (disasterIndex == 5) {    // 홍수
            } else if (disasterIndex == 6) {    // 폭염
            } else if (disasterIndex == 7) {    // 한파
            } else if (disasterIndex == 8) {    // 호우
            } else if (disasterIndex == 9) {    // 대설
            }
            for (int i = 1; i <= NUM_OF_DISASTER_LEVELS; i++) {
                if (disasterSubLevel[i]){
                    logLine7.append(" ").append(disasterLevelArray.get(disasterIndex)[i]);
                }
            }
            Log.e(TAG, String.valueOf(logLine7));
            TextView textView1 = new TextView(this);
            TextView textView2 = new TextView(this);
            TextView textView3 = new TextView(this);
            TextView textView4 = new TextView(this);
            TextView textView5 = new TextView(this);
            TextView textView6 = new TextView(this);
            TextView textView7 = new TextView(this);
            textView1.setText(logLine1);
            textView2.setText(logLine2);
            textView3.setText(logLine3);
            textView4.setText(logLine4);
            resultLayout.addView(textView1);
            resultLayout.addView(textView2);
            resultLayout.addView(textView3);
            resultLayout.addView(textView4);
            if (disasterIndex == 1 || disasterIndex == 2 || disasterIndex == 4) {
                textView5.setText(logLine5);
                resultLayout.addView(textView5);
            }
            if (disasterIndex == 2) {
                textView6.setText(logLine6);
                resultLayout.addView(textView6);
            }
            textView7.setText(logLine7);
            resultLayout.addView(textView7);
            ////////////////////////////////////////////////////////////////////////////////////// 로그 처리

            // getServerInfo로 전달할 생성이 필요한 값 생성
            String startDateTime = fromDate + " 00:00:00";
            String endDateTime = toDate + " 23:59:59";
            String levels = "";
            for (int i = 1; i <= NUM_OF_DISASTER_LEVELS; i++ ) {
                if (disasterSubLevel[i]) {
                    if (levels.isEmpty()) {
                        levels += i;
                    } else {
                        levels += ("," + i);
                    }
                }
            }

            // REST Call Url 생성
            URL connUrl;
            try {
                connUrl = GetServerInfo.makeConnUrl(startDateTime, endDateTime, mainLocation, subLocation, disasterIndex, levels, disasterSubName, eq_mainLocation, eq_subLocation, scale_min, scale_max);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


        }
        // 값을 받지 못한 경우
        else {
            TextView textView5 = new TextView(this);
            textView5.setText("값을 불러오지 못하였습니다.");

            resultLayout.addView(textView5);
        }
    }

    // 검색 조건을 전달받고 유효성 검사 실시
    private boolean receiveCondition() {
        final String TAG = "ReceiveCondition";
        Intent intent = getIntent();

        fromDate = intent.getStringExtra("fromDate");
        if (fromDate == null || fromDate.isEmpty()) {
            Log.e(TAG, "유효성 검사 Failed (fromDate)");
            Log.e(TAG, "fromDate : " + fromDate);
            return false;
        }

        toDate = intent.getStringExtra("toDate");
        if (toDate == null || toDate.isEmpty()) {
            Log.e(TAG, "유효성 검사 Failed (toDate)");
            Log.e(TAG, "toDate : " + toDate);
            return false;
        }

        mainLocation = intent.getStringExtra("mainLocation");
        if (mainLocation == null || mainLocation.isEmpty()) {
            Log.e(TAG, "유효성 검사 Failed (mainLocation)");
            Log.e(TAG, "mainLocation : " + mainLocation);
            return false;
        }

        subLocation = intent.getStringExtra("subLocation");
        if (subLocation == null || subLocation.isEmpty()) {
            Log.e(TAG, "유효성 검사 Failed (subLocation)");
            Log.e(TAG, "subLocation : " + subLocation);
            return false;
        }

        disasterIndex = intent.getIntExtra("disasterIndex", -1);
        if (disasterIndex == -1) {
            Log.e(TAG, "유효성 검사 Failed (disasterIndex)");
            Log.e(TAG, "disasterIndex : " + disasterIndex);
            return false;
        }

        disasterSubName = intent.getStringExtra("disasterSubName");
        if (disasterSubName == null) {
            Log.e(TAG, "유효성 검사 Failed (disasterSubName)");
            Log.e(TAG, "disasterSubName : " + disasterSubName);
            return false;
        }

        disasterSubLevel = intent.getBooleanArrayExtra("disasterSubLevel");
        boolean check = false;
        for (boolean level : disasterSubLevel) {
            check = check || level;
        }
        if (disasterSubLevel == null || !check) {
            Log.e(TAG, "유효성 검사 Failed (disasterSubLevel)");
            Log.e(TAG, "disasterSubLevel : " + Arrays.toString(disasterSubLevel));
            return false;
        }

        scale_min = intent.getDoubleExtra("scale_min", -1);
        scale_max = intent.getDoubleExtra("scale_max", -1);

        eq_mainLocation = intent.getStringExtra("eq_mainLocation");
        if (eq_mainLocation == null) {
            Log.e(TAG, "유효성 검사 Failed (eq_mainLocation)");
            Log.e(TAG, "eq_mainLocation : " + eq_mainLocation);
            return false;
        }

        eq_subLocation = intent.getStringExtra("eq_subLocation");
        if (eq_subLocation == null) {
            Log.e(TAG, "유효성 검사 Failed (eq_subLocation)");
            Log.e(TAG, "eq_subLocation : " + eq_subLocation);
            return false;
        }

        return true;
    }
}
