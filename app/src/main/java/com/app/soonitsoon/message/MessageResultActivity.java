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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.app.soonitsoon.R;
import com.app.soonitsoon.server.GetServerInfo;
import com.app.soonitsoon.timeline.DateNTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class MessageResultActivity extends AppCompatActivity {
    private final static String TAG = "MessageResultActivity";
    private final static int NUM_OF_DISASTER_LEVELS = 9;
    private static Activity activity;
    private Context context = this;

    // 결과 화면
    LinearLayout resultLayout;
    ListView resultListView;

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
    private String innerText;   // 텍스트 검색

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

        resultLayout = findViewById(R.id.layout_message_result);
        resultListView = new ListView(this);

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
            String logLine8 = "텍스트 검색 : " + innerText;
            TextView textView1 = new TextView(this);
            TextView textView2 = new TextView(this);
            TextView textView3 = new TextView(this);
            TextView textView4 = new TextView(this);
            TextView textView5 = new TextView(this);
            TextView textView6 = new TextView(this);
            TextView textView7 = new TextView(this);
            TextView textView8 = new TextView(this);
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
            textView8.setText(logLine8);
            resultLayout.addView(textView8);
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

            // Rest Call 이용 서버 연결
            try {
                ServerConnect serverConnect = new ServerConnect(GetServerInfo.makeConnUrl(startDateTime, endDateTime, mainLocation, subLocation, disasterIndex, levels, disasterSubName, eq_mainLocation, eq_subLocation, scale_min, scale_max, innerText));
                serverConnect.execute();
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
            return false;
        }
        Log.e(TAG, "fromDate : " + fromDate);

        toDate = intent.getStringExtra("toDate");
        if (toDate == null || toDate.isEmpty()) {
            Log.e(TAG, "유효성 검사 Failed (toDate)");
            return false;
        }
        Log.e(TAG, "toDate : " + toDate);

        mainLocation = intent.getStringExtra("mainLocation");
        if (mainLocation == null || mainLocation.isEmpty()) {
            Log.e(TAG, "유효성 검사 Failed (mainLocation)");
            return false;
        }
        Log.e(TAG, "mainLocation : " + mainLocation);

        subLocation = intent.getStringExtra("subLocation");
        if (subLocation == null || subLocation.isEmpty()) {
            Log.e(TAG, "유효성 검사 Failed (subLocation)");
            return false;
        }
        Log.e(TAG, "subLocation : " + subLocation);

        disasterIndex = intent.getIntExtra("disasterIndex", -1);
        if (disasterIndex == -1) {
            Log.e(TAG, "유효성 검사 Failed (disasterIndex)");
            return false;
        }
        Log.e(TAG, "disasterIndex : " + disasterIndex);

        disasterSubName = intent.getStringExtra("disasterSubName");
        if (disasterSubName == null) {
            Log.e(TAG, "유효성 검사 Failed (disasterSubName)");
            return false;
        }
        Log.e(TAG, "disasterSubName : " + disasterSubName);

        disasterSubLevel = intent.getBooleanArrayExtra("disasterSubLevel");
        boolean check = false;
        for (boolean level : disasterSubLevel) {
            check = check || level;
        }
        if (disasterSubLevel == null || !check) {
            Log.e(TAG, "유효성 검사 Failed (disasterSubLevel)");
            return false;
        }
        Log.e(TAG, "disasterSubLevel : " + Arrays.toString(disasterSubLevel));

        scale_min = intent.getDoubleExtra("scale_min", -1);
        scale_max = intent.getDoubleExtra("scale_max", -1);

        eq_mainLocation = intent.getStringExtra("eq_mainLocation");
        if (eq_mainLocation == null) {
            Log.e(TAG, "유효성 검사 Failed (eq_mainLocation)");
            return false;
        }
        Log.e(TAG, "eq_mainLocation : " + eq_mainLocation);

        eq_subLocation = intent.getStringExtra("eq_subLocation");
        if (eq_subLocation == null) {
            Log.e(TAG, "유효성 검사 Failed (eq_subLocation)");
            return false;
        }
        Log.e(TAG, "eq_subLocation : " + eq_subLocation);

        innerText = intent.getStringExtra("innerText");
        if (eq_subLocation == null) {
            Log.e(TAG, "유효성 검사 Failed (innerText)");
            return false;
        }
        Log.e(TAG, "innerText : " + innerText);
        return true;
    }

    // 서버에서 데이터를 전달 받은 후 내용 출력
    private void showSearchResult(String strResultData) {
        // 로딩중 글자
        TextView textView = findViewById(R.id.text_loading);
        // 서버에 연결 실패한 경우
        if (strResultData.isEmpty()) {
            textView.setText("서버와의 연결이 실패하였습니다.");
        } else if (strResultData.equals("{}")) {    // 서버에서 빈 String을 전달 받은 경우
            textView.setText("검색 결과가 없습니다.");
        } else {    // 서버에서 전달받은 String이 있는 경우
            textView.setVisibility(View.GONE);
        }

        // HTML Code로 오는 특수기호 변환
        String parsedHtmlStr = strResultData.replace("&#34;", "\"");

        // 검색 내용을 띄워줄 View 생성
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams unitParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        unitParams.setMargins(4, 16, 4, 16);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // 서버에서 받은 String을 Json으로 파싱
        try {
            JSONObject jsonResultData = new JSONObject(parsedHtmlStr);

            // 데이터 하나씩 접근
            Iterator<String> iterator = jsonResultData.keys();
            while (iterator.hasNext()) {
                String strResultUnit = jsonResultData.getString(iterator.next());
                JSONObject jsonResultUnit = new JSONObject(strResultUnit);

                // 받아오는 값들
                // 발송 날짜 시간
                String[] sendDateTimeArray = jsonResultUnit.getString("send_date").split(" ");
                String sendDateTime = DateNTime.toKoreanDate(sendDateTimeArray[0]) + " " + DateNTime.toKoreanTime(sendDateTimeArray[1]);
                // 문자 내용
                String msg = jsonResultUnit.optString("msg", "");
                // 발송 지역
                String sendLocation = jsonResultUnit.optString("send_location", "");
                // 발송 주체
                String sender = jsonResultUnit.optString("sender", "");
                // 재난 구분
                int disaster = jsonResultUnit.optInt("disaster", -1);
                // 알림 등급
                int level = jsonResultUnit.optInt("level", -1);
                // 전염병 또는 태풍 이름
                String name = jsonResultUnit.optString("name", "");
                // 전염병 확진자 수
                int confirmNum = jsonResultUnit.optInt("confirm_num", -1);
                // 전염병 링크
                String link = jsonResultUnit.optString("link", "");
                // 지진 관측위치
                String obsLocation = jsonResultUnit.optString("obs_location", "");
                // 지진 진앙지
                String center = jsonResultUnit.optString("center", "");
                // 지진 규모
                double scale = jsonResultUnit.optDouble("scale", -1);
                // 홍수 발생위치
                String flLocation = jsonResultUnit.optString("location", "");

                // 정보 제공을 위한 String 생성
                // 2020년 1월 1일 기상청에서
                String line1 = sendDateTime + " " + sender + "에서";
                // 서울특별시 동작구, 경기도 수원시로 발송한 문자입니다.
                String line2 = sendLocation + "로 발송한 문자입니다.";
                // 전염병 (코로나-19) 발생안내에 대한 문자입니다.
                String line3 = disasterArray[disaster];
                if (!name.isEmpty()) {  // 전염병 또는 태풍의 이름이 있는 경우
                    line3 += (" (" + name + ")");
                }
                line3 += (" " + disasterLevelArray.get(disaster)[level] + "에 대한 문자입니다.");
                // 추가 라인이 있는 경우
                String line4 = "";
                if (confirmNum != -1) line4 = "확진자 수 : " + confirmNum;
                if (!center.isEmpty() && scale != -1) line4 = center + "에서 발생한 규모 " + scale + " 지진";
                if (!flLocation.isEmpty()) line4 = flLocation + "에서 발생";
                // 추가 라인이 있는 경우
                String line5 = "";
                if (!link.isEmpty()) line5 = link;

                // 문자 하나에 대한 View 생성
                // 레이아웃 생성
                LinearLayout subLayout = new LinearLayout(this);
                subLayout.setLayoutParams(unitParams);
                subLayout.setPadding(16,16, 16, 16);
                subLayout.setOrientation(LinearLayout.VERTICAL);
                subLayout.setBackground(getResources().getDrawable(R.drawable.radius));

                // Text Line 1
                TextView textView1 = new TextView(this);
                textView1.setText(line1);
                textView1.setTextSize(Dimension.DP, 24);
                textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                // Text Line 2
                TextView textView2 = new TextView(this);
                textView2.setText(line2);
                textView2.setTextSize(Dimension.DP, 24);
                textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                // Text Line 3
                TextView textView3 = new TextView(this);
                textView3.setText(line3);
                textView3.setTextSize(Dimension.DP, 24);
                textView3.setTextColor(getResources().getColor(R.color.colorPrimary));
                // 레이아웃에 추가
                subLayout.addView(textView1);
                subLayout.addView(textView2);
                subLayout.addView(textView3);
                // 추가 라인이 있다면
                if (!line4.isEmpty()) {
                    TextView textView4 = new TextView(this);
                    textView4.setText(line4);
                    textView4.setTextSize(Dimension.DP, 24);
                    textView4.setTextColor(getResources().getColor(R.color.colorPrimary));
                    subLayout.addView(textView4);
                }
                if (!line5.isEmpty()) {
                    TextView textView5 = new TextView(this);
                    textView5.setText(line5);
                    textView5.setTextSize(Dimension.DP, 24);
                    textView5.setTextColor(getResources().getColor(R.color.colorPrimary));
                    subLayout.addView(textView5);
                }
                // 문자 원본 추가
                TextView textMsg = new TextView(this);
                textMsg.setText(msg);
                textMsg.setTextSize(Dimension.DP, 40);
                textMsg.setTextColor(getResources().getColor(R.color.colorWhite));
                subLayout.addView(textMsg);

                // 생성된 레이아웃 병합
                linearLayout.addView(subLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        scrollView.addView(linearLayout);
        resultLayout.addView(scrollView);


//        // 받은 json 띄우는 곳
//        TextView textView1 = new TextView(this);
//        TextView textView2 = new TextView(this);
//        textView1.setText(" ");
//        textView2.setText(strResultData);
//        resultLayout.addView(textView1);
//        resultLayout.addView(textView2);
    }

    // 액티비티 쓰레드에서 url 콜을 하기 위한 AsyncTask class
    public class ServerConnect extends AsyncTask<Void, Void, String> {
        private URL url;
        public ServerConnect(URL url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try {
                result = GetServerInfo.getServerData(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            showSearchResult(s);

            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }
    }
}
