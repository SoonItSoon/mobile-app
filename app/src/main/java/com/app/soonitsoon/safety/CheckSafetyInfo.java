package com.app.soonitsoon.safety;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CheckSafetyInfo {
    // TODO : getServerInfo
    // TODO : getTimeline
    public static void getDangerInfo() {
        // TODO : getServerInfo.getSafety 로 받아온 값
        // {"date" : {"startTime", "endTime", "place"}, ...}
        // 밑의 예시는 2020/11/26 15:11:58
        // [진도군청]나주시 확진자 동선안내▶11.22. 해남 삼산면 매화정 12시~14시▶11.23. 해남읍 정성한우촌 11시~14시 방문하신분은 보건소에서 검사바랍니다.

        // Json 파일 읽기
        InputStream inputStream;
        String stringDangerList = "";
        try {
            inputStream = new FileInputStream("test.json");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                stringDangerList = bufferedReader.readLine();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Json 파일(String) -> JsonObject
        JSONObject jsonDangerList = new JSONObject();
        try {
            jsonDangerList = new JSONObject(stringDangerList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
