package com.app.soonitsoon.interest;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.soonitsoon.Alert;
import com.app.soonitsoon.CalDate;
import com.app.soonitsoon.server.GetServerInfo;
import com.app.soonitsoon.timeline.DateNTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

public class CheckInterestInfo {
    private static final String TAG = "CheckInterestInfo";
    private static final int NUM_OF_DISASTER_LEVELS = 9;
    private Context context;
    private Application application;

    private SharedPreferences spref;
    private int interestSize;
    private String[] nicknames;

    public CheckInterestInfo(Context context, Application application) {
        this.context = context;
        this.application = application;
    }

    public void checkInterest() {
        // SP 값 불러오기
        spref = context.getSharedPreferences("InterestData", Context.MODE_PRIVATE);
        interestSize = spref.getInt("size", 0);

        if (interestSize == 0) {
            Log.e(TAG, "저장된 관심분야가 없습니다.");
            return;
        }
        initNicknameArray();

        for (String nickname : nicknames) {
            // URL 생성
            URL url = showInterestContents(nickname);
            // 서버 데이터 받기
            String serverResult = "";
            try {
                serverResult = GetServerInfo.getServerData(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (serverResult.isEmpty()) {
                Log.e(TAG, "서버연결 실패");
            } else if (serverResult.equals("{}")) {
                Log.e(TAG, "새로운 데이터 없음");
            } else {
                Alert alert = new Alert(context, application);
                alert.sendInterestAlert(nickname);
            }
        }
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

    // 관심분야 URL 생성
    private URL showInterestContents(String nickname) {
        URL retUrl = null;
        // 불러온 검색 조건들
        String startDateTime = CalDate.addDay(DateNTime.getDate(), -7) + " " + DateNTime.getTime();    // 검색 시작 날짜
        String endDateTime = "";
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
                retUrl = GetServerInfo.makeConnUrl(startDateTime, endDateTime, mainLocation, subLocation, disasterIndex, levels, disasterSubName, eq_mainLocation, eq_subLocation, scale_min, scale_max, "");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return retUrl;
    }
}