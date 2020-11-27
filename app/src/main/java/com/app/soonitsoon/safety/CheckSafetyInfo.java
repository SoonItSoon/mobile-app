package com.app.soonitsoon.safety;

import android.app.Application;
import android.content.Context;
import android.icu.text.Edits;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class CheckSafetyInfo {
    private Context context;
    private static Application application;

    public CheckSafetyInfo(Context context, Application application) {
        this.context = context;
        this.application = application;
    }
    // TODO : getServerInfo
    // TODO : getTimeline
    public static void getDangerInfo() throws JSONException {
        // TODO : getServerInfo.getSafety 로 받아온 값
        // {"date" : {"startTime", "endTime", "place"}, ...}
        // 밑의 예시는 2020/11/26 15:11:58
        // [진도군청]나주시 확진자 동선안내▶11.22. 해남 삼산면 매화정 12시~14시▶11.23. 해남읍 정성한우촌 11시~14시 방문하신분은 보건소에서 검사바랍니다.

        InputStream inputStreamDanger;
        String stringDangerObject = "";
        try {
            inputStreamDanger = application.openFileInput("test.json");

            if (inputStreamDanger != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStreamDanger);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                stringDangerObject = bufferedReader.readLine();
                inputStreamDanger.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Json 파일(String) -> JsonObject
        JSONObject jsonDangerObject = new JSONObject();
        try {
            jsonDangerObject = new JSONObject(stringDangerObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Iterator<String>  DangerDate = jsonDangerObject.keys();
        int numDate = 0;
        String[] stringDangerList = new String[jsonDangerObject.length()];
        while (DangerDate.hasNext()) {

            stringDangerList[numDate] = jsonDangerObject.getString(DangerDate.next());
            JSONObject jsonDangerList = new JSONObject();
            try {
                jsonDangerList = new JSONObject(stringDangerList[numDate]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Iterator<String> DangerIndex = jsonDangerList.keys();
            int numIndex = 0;
            String[] stringDangerUnit = new String[jsonDangerList.length()];
            while (DangerIndex.hasNext()) {

                stringDangerUnit[numIndex] = jsonDangerList.getString(DangerIndex.next());
                JSONObject jsonDangerUnit = new JSONObject();
                try {
                    jsonDangerUnit = new JSONObject(stringDangerUnit[numIndex]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                numIndex++;
            }
            numDate++;
        }
        // TODO : Timeline 받아오기
        /*
        InputStream inputStreamTimeline;
        String stringTLList = "";
        try {
            inputStreamTimeline = application.openFileInput(jsonDangerObject.get+".json");

            if (inputStreamTimeline != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStreamTimeline);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                stringTLList = bufferedReader.readLine();
                inputStreamTimeline.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Json 파일(String) -> JsonObject
        JSONObject jsonTLList = new JSONObject();
        try {
            jsonTLList = new JSONObject(stringTLList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/

    }

}
