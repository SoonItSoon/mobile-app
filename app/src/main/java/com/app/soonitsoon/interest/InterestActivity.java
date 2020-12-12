package com.app.soonitsoon.interest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.soonitsoon.datetime.CalDate;
import com.app.soonitsoon.R;
import com.app.soonitsoon.server.GetServerInfo;
import com.app.soonitsoon.datetime.DateNTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class InterestActivity extends AppCompatActivity {
    private final static int NUM_OF_DISASTER_LEVELS = 9;
    public static Activity activity;
    private Context context = this;

    private SharedPreferences spref;
    private int interestSize;

    // Arrays
    private String[] disasterArray; // 재난 종류 Array
    private ArrayList<String[]> disasterLevelArray; // 재난별 등급 Array
    private String[] nicknames; // 별명 Array

    // 관심분야 레이아웃
    public LinearLayout interestLayout;

    // 관심분야 선택 버튼
    private Button selectBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        activity = this;

        // SP 값 불러오기
        spref = context.getSharedPreferences("InterestData", Context.MODE_PRIVATE);
        interestSize = spref.getInt("size", 0);
        initNicknameArray();

        interestLayout = findViewById(R.id.layout_interest_content);

        // Array 초기화
        disasterArray = getResources().getStringArray(R.array.disaster);
        disasterLevelArray = new ArrayList<>();
        disasterLevelArray.add(new String[0]);
        for (int i = 1; i <= NUM_OF_DISASTER_LEVELS; i++) {
            int levelArrayID = getResources().getIdentifier("disaster_" + i, "array", getPackageName());
            String[] levelArray = getResources().getStringArray(levelArrayID);
            disasterLevelArray.add(levelArray);
        }

        // 홈 버튼
        Button homeBtn = findViewById(R.id.btn_interest_goHome);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 관심분야 편집 버튼
        Button settingBtn = findViewById(R.id.btn_interest_setting);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent settingIntent = new Intent(getApplicationContext(), InterestSettingActivity.class);
                startActivity(settingIntent);
            }
        });

        // 관심분야 선택 버튼
        selectBtn = findViewById(R.id.btn_interest_select_view);
        // 알림을 통해서 들어온 경우
        String alertValue = getIntent().getStringExtra("nickname");
        if (alertValue != null) {
            selectBtn.setText(alertValue);
            showInterestContents(alertValue);
        }
        // 기본
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interestSize > 0) {
                    new AlertDialog.Builder(context).setTitle("관심분야 선택").setItems(nicknames, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectBtn.setText(nicknames[which]);
                            clearInterestContents();
                            showInterestContents(nicknames[which]);
                            Toast.makeText(context, "최근 일주일 동안의 재난문자 내용입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
                } else {
                    Toast.makeText(context, "먼저 관심분야를 설정해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearInterestContents();
        TextView resultText = findViewById(R.id.text_interest_result);
        resultText.setVisibility(View.VISIBLE);
        selectBtn.setText("관심분야 선택");

        // 알림을 통해서 들어온 경우
        String alertValue = getIntent().getStringExtra("nickname");
        if (alertValue != null) {
            selectBtn.setText(alertValue);
            showInterestContents(alertValue);
            Toast.makeText(context, "최근 일주일 동안의 재난문자 내용입니다.", Toast.LENGTH_SHORT).show();
        }

        // SP 및 별명 array 불러오기
        spref = context.getSharedPreferences("InterestData", Context.MODE_PRIVATE);
        interestSize = spref.getInt("size", 0);
        initNicknameArray();

    }

    public void showInterestContents(String nickname) {

        // 불러온 검색 조건들
        String startDateTime = CalDate.addDay(DateNTime.getDate(), -7) + " " + DateNTime.getTime();    // 검색 시작 날짜
        String mainLocation;    // 시/도
        String subLocation;     // 시/군/구
        int disasterIndex;
        // 재난 하위 레이아웃 선택 내용
        String disasterSubName; // 전염병 종류, 태풍 이름
        boolean[] disasterSubLevel = new boolean[NUM_OF_DISASTER_LEVELS + 1]; // 알림 등급
        double scale_min;   // 지진 규모 최솟값
        double scale_max;   // 지진 규모 최댓값
        String eq_mainLocation;   // 지진 관측 지역
        String eq_subLocation;

        // Interest
        String strInterest = spref.getString(nickname, "");
        if (!strInterest.isEmpty()) {
            JSONObject jsonInterest = new JSONObject();
            try {
                jsonInterest = new JSONObject(strInterest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 관심분야 값 불러오기
            mainLocation = jsonInterest.optString("mainLocation", "");
            subLocation = jsonInterest.optString("subLocation", "");
            disasterIndex =  jsonInterest.optInt("disasterIndex", 0);
            disasterSubName = jsonInterest.optString("disasterSubName", "");
            for (int i = 1; i <= NUM_OF_DISASTER_LEVELS; i++) {
                if (jsonInterest.optBoolean("disasterSubLevel" + i, false)){
                    disasterSubLevel[i] = true;
                } else {
                    disasterSubLevel[i] = false;
                }
            }
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
            scale_min = jsonInterest.optDouble("scale_min", 0);
            scale_max = jsonInterest.optDouble("scale_max", 0);
            eq_mainLocation = jsonInterest.optString("eq_mainLocation", "");
            eq_subLocation = jsonInterest.optString("eq_subLocation", "");

            // Rest Call 이용 서버 연결
            try {
                ServerConnect serverConnect =
                        new ServerConnect(GetServerInfo.makeConnUrl(startDateTime, "", mainLocation, subLocation, disasterIndex, levels, disasterSubName, eq_mainLocation, eq_subLocation, scale_min, scale_max, ""));
                serverConnect.execute();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    // 콘텐츠 지우는 기능
    public void clearInterestContents() {
        if (interestLayout != null)
            interestLayout.removeAllViews();
    }

    // 관심분야 별명 Array 초기화
    private void initNicknameArray() {
        nicknames = new String[interestSize];
        Set<String> set = spref.getAll().keySet();
        Iterator<String> iterator = set.iterator();
        int i = 0;
        while (iterator.hasNext() && interestSize > 0) {
            String nickname = iterator.next();
            if (!nickname.equals("size")) {
                nicknames[i++] = nickname;
            }
        }
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

            showInterestResult(s);
        }
    }

    private void showInterestResult(String strResultData) {
        // 로딩중 글자
        TextView resultText = findViewById(R.id.text_interest_result);

        if (resultText != null) {
            // 서버에 연결 실패한 경우
            if (strResultData.isEmpty()) {
                resultText.setText("서버와의 연결이 실패하였습니다.");
            } else if (strResultData.equals("{}")) {    // 서버에서 빈 String을 전달 받은 경우
                resultText.setText("검색 결과가 없습니다.");
            } else {    // 서버에서 전달받은 String이 있는 경우
                resultText.setVisibility(View.GONE);
                clearInterestContents();
            }
        }

        // 검색 내용을 띄워줄 View 생성
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams unitParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        unitParams.setMargins(4, 16, 4, 16);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // 서버에서 받은 String을 Json으로 파싱
        try {
            JSONObject jsonResultData = new JSONObject(strResultData);

            String tmpDate = "";

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
                // 의심 지역
                String dagerLocation = jsonResultUnit.optString("location_name", "");
                // 전염병 링크
                final String link = jsonResultUnit.optString("link", "");
                // 지진 관측위치
                String obsLocation = jsonResultUnit.optString("obs_location", "");
                // 지진 진앙지
                String center = jsonResultUnit.optString("center", "");
                // 지진 규모
                double scale = jsonResultUnit.optDouble("scale", -1);
                // 홍수 발생위치
                String flLocation = jsonResultUnit.optString("location", "");

                // 정보 제공을 위한 String 생성

                String line1 = sendDateTime;

//                String line2 = sender + " 발송";
                // 전염병 (코로나-19) 발생안내에 대한 문자입니다.
                String line3 = disasterArray[disaster];
                if (!name.isEmpty()) {  // 전염병 또는 태풍의 이름이 있는 경우
                    if (disaster == 1)
                        line3 = name;
                    if (disaster == 4)
                        line3 += (" " + name);
                }
                line3 += "에 관한 재난문자 입니다.";
//                line3 += (" - " + disasterLevelArray.get(disaster)[level]);
                // 추가 라인이 있는 경우
                String line4 = "";
                if (confirmNum != -1 && confirmNum != 0) line4 = "지역 확진자 수는 총 " + confirmNum + "명입니다.";
                else if (level == 1 && disaster == 1 && !dagerLocation.isEmpty())
                    line4 = dagerLocation + "방문자 보건소 방문 요청 내용입니다.";
                if (!center.isEmpty() && !center.equals("null") && scale != -1) line4 = obsLocation + "에서 관측된 규모 " + scale + "의 지진입니다.";
                else if (!flLocation.isEmpty() && !flLocation.equals("null")) line4 = flLocation + "에서 발생한 홍수입니다.";
                // 추가 라인이 있는 경우
                String line5 = "";
                if (!link.isEmpty() && !link.equals("null")) line5 = "관련 링크 : " + link;

                // 문자 하나에 대한 View 생성
                // 레이아웃 생성
                LinearLayout subLayout = new LinearLayout(this);
                subLayout.setLayoutParams(unitParams);
                subLayout.setPadding(24,24, 24, 24);
                subLayout.setOrientation(LinearLayout.VERTICAL);
                subLayout.setBackground(getResources().getDrawable(R.drawable.radius));

                // Text Line 1
                TextView textView1 = new TextView(this);
                textView1.setText(line1);
                textView1.setTextSize(Dimension.DP, 48);
                textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
                // Text Line 2
//                TextView textView2 = new TextView(this);
//                textView2.setText(line2);
//                textView2.setTextSize(Dimension.DP, 36);
//                textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
                // Text Line 3
                TextView textView3 = new TextView(this);
                textView3.setText(line3);
                textView3.setTextSize(Dimension.DP, 48);
                textView3.setTextColor(getResources().getColor(R.color.colorPrimary));
                // 레이아웃에 추가
                subLayout.addView(textView1);
//                subLayout.addView(textView2);
                subLayout.addView(textView3);
                // 추가 라인이 있다면
                if (!line4.isEmpty()) {
                    TextView textView4 = new TextView(this);
                    textView4.setText(line4);
                    textView4.setTextSize(Dimension.DP, 48);
                    textView4.setTextColor(getResources().getColor(R.color.colorPrimary));
                    subLayout.addView(textView4);
                }
                if (!line5.isEmpty()) {
                    TextView textView5 = new TextView(this);
                    textView5.setText(line5);
                    textView5.setTextSize(Dimension.DP, 48);
                    textView5.setTextColor(getResources().getColor(R.color.colorPrimary));
                    textView5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent;
                            if (link.contains("http://") || link.contains("https://"))
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            else
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + link));
                            startActivity(intent);
                        }
                    });
                    subLayout.addView(textView5);
                }
                // 문자 원본 추가
                TextView textMsg = new TextView(this);
                textMsg.setText(msg);
                textMsg.setTextSize(Dimension.DP, 36);
                textMsg.setTextColor(getResources().getColor(R.color.colorWhite));
                textMsg.setPadding(0, 16, 0, 0);
                subLayout.addView(textMsg);

                // 날짜 Divider 생성
                if (tmpDate.isEmpty() || !tmpDate.equals(sendDateTimeArray[0])) {

                    LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (tmpDate.isEmpty())
                        dividerParams.setMargins(4, 0, 4, 16);
                    else
                        dividerParams.setMargins(4, 48, 4, 16);

                    tmpDate = sendDateTimeArray[0];

                    LinearLayout dividerLayout = new LinearLayout(this);
                    dividerLayout.setLayoutParams(dividerParams);
                    dividerLayout.setOrientation(LinearLayout.VERTICAL);
                    dividerLayout.setBackground(getResources().getDrawable(R.drawable.radius));

                    TextView dividerText = new TextView(this);
                    dividerText.setText(DateNTime.toKoreanDate(tmpDate));
                    dividerText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    dividerText.setTextColor(getResources().getColor(R.color.colorWhite));
                    dividerText.setTextSize(Dimension.DP, 36);
                    dividerText.setPadding(0, 16, 0, 16);

                    dividerLayout.addView(dividerText);
                    linearLayout.addView(dividerLayout);
                }

                // 생성된 레이아웃 병합
                linearLayout.addView(subLayout);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        scrollView.addView(linearLayout);
        interestLayout.addView(scrollView);

    }
}
