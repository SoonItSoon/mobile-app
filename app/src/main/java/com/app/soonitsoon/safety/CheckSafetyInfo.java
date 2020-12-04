package com.app.soonitsoon.safety;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.Edits;
import android.location.Location;
import android.util.Log;

import com.app.soonitsoon.Alert;
import com.app.soonitsoon.CalDate;
import com.app.soonitsoon.server.GetServerInfo;
import com.app.soonitsoon.timeline.CheckLocation;
import com.app.soonitsoon.timeline.GetTimeline;
import com.app.soonitsoon.timeline.UpdateTimeline;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


public class CheckSafetyInfo {
    private Context context;
    private static Application application;
    private UpdateTimeline updateTimeline;
    private int dangerCount;
    private int alertNum;


    public CheckSafetyInfo(Context context, Application application) {
        this.context = context;
        this.application = application;
        updateTimeline = new UpdateTimeline(context, application);
        dangerCount = 0;
        alertNum = 0;
    }

    // 접촉 위험 지역과 timeline을 비교해 danger값을 바꾸는 모듈
    public void getDangerInfo() throws JSONException {
        // getServerInfo.getSafety 로 받아온 값 (추가된 접촉 안내 data)
        ArrayList<ArrayList<String>> dangerList = GetServerInfo.getSafetyData(application, context);

        if (dangerList == null)
            return;

        for (ArrayList<String> dangerUnit : dangerList) {
            String msg = dangerUnit.get(0);
            String sender = dangerUnit.get(1);
            String date = dangerUnit.get(2);
            String startTime = dangerUnit.get(3);
            String endTime = dangerUnit.get(4);
            String locName = dangerUnit.get(5);

            // Timeline 받아오기
            GetTimeline getTimeline = new GetTimeline(application);
            JSONObject jsonTLList = getTimeline.excute(date);

            // 접촉 의심 시간 내 타임라인을 받아오기 위한 시간 저장
            Iterator<String> iterator = jsonTLList.keys();
            String prev = "";
            ArrayList<String> dangerTimeList = new ArrayList<>();

            while (iterator.hasNext()) {
                String time = iterator.next();
                int start = CalDate.isFast(startTime, time);
                int end = CalDate.isFast(endTime, time);

                if (start == 0 && end == 0) {          // 내가 접촉 의심 시간이야 (접촉 의심 시간이 범위가 아닌 경우)
                    dangerTimeList.add(time);
                    break;
                } else if (start == -1 && end == 1) {    // 내가 접촉 의심 시간 안에 있어
                    if (!iterator.hasNext())
                        dangerTimeList.add(time);
                    else if (!prev.isEmpty())
                        dangerTimeList.add(prev);
                } else if ((start == -1 && end == -1) || (start == -1 && end == 0)) {    // 내가 접촉 의심 시간보다 빨라 또는 엔드타임과 같아
                    if (!prev.isEmpty())
                        dangerTimeList.add(prev);
                    dangerTimeList.add(time);
                    break;
                }
                prev = time;
            }

            // kakao map rest api를 사용해 상호명에서 좌표값 받아오기
            // 1. sender가 "중대본" 이면 locName으로만 검색
            // 2. sender가 "중대본"이 아니면 sender-(마지막글자)+locName으로 검색
            String[] strLocRequest = {""};
            if (sender.equals("중대본"))
                strLocRequest = RequestHttpConnection.request(locName);
            else {
                String senderLoc = sender.substring(0, sender.length() - 1);
                String searchLoc = senderLoc + " " + locName;
                strLocRequest = RequestHttpConnection.request(searchLoc);
            }

            double dangerLat = Double.parseDouble(strLocRequest[1]);
            double dangerLon = Double.parseDouble(strLocRequest[0]);

            // 접촉 의심 장소와 내 timeline을 비교해서 danger값 바꾸기
            // sharedpreference의 updatelist에 추가
            // updateList = {date : {index : {time : "time", locName : "locName"}}}
            SharedPreferences spref = context.getSharedPreferences("PrevData", Context.MODE_PRIVATE);
            String strUpdateObject = spref.getString("UpdateList", "");
            JSONObject jsonUpdateObject;
            if (strUpdateObject.isEmpty()) {
                jsonUpdateObject = new JSONObject();
            }
            else {
                jsonUpdateObject = new JSONObject(strUpdateObject);
            }

            int timelineFlag = 0;

            for (String dangerTime : dangerTimeList) {
                String strTLUnit = jsonTLList.getString(dangerTime);
                JSONObject jsonTLUnit = new JSONObject(strTLUnit);

                double timelineLat = jsonTLUnit.getDouble("latitude");
                double timelineLon = jsonTLUnit.getDouble("longitude");

                // M단위
                boolean distance = CheckLocation.check(dangerLat, dangerLon, timelineLat, timelineLon);

                // true : 거리가 멀다  / false : 거리가 가깝다 -> 위험하다 -> DANGER값 2로 바꾼다 + UpdateList에 추가한다
                if (distance == false) {
                    // Danger값을 1로 바꾼다
                    updateTimeline.excute(date, dangerTime, 1);

                    // updateList에 update할 시간이랑 위험한 장소를 넣자
                    JSONObject jsonUpdateUnit = new JSONObject();
                    jsonUpdateUnit.put("time", dangerTime);
                    jsonUpdateUnit.put("locName", locName);         // UpdateUnit = {time : "time", locName : "locName"}
                    jsonUpdateUnit.put("lat", dangerLat);
                    jsonUpdateUnit.put("lon", dangerLon);
                    String strUpdateUnit = jsonUpdateUnit.toString();

                    if (jsonUpdateObject.has(date)) {
                        String strUpdateList = jsonUpdateObject.getString(date);
                        JSONObject jsonUpdateList = new JSONObject(strUpdateList);  //UpdateList = {index : {updateUnit}}

                        // 비어있는 index에 updateUnit 추가
                        int index = 0;
                        while (jsonUpdateList.has(Integer.toString(index))) {
                            index++;
                        }
                        jsonUpdateList.put(Integer.toString(index), strUpdateUnit);
                        strUpdateList = jsonUpdateList.toString();

                        //updateObject의 date에 대한 value 수정
                        jsonUpdateObject.remove(date);
                        jsonUpdateObject.put(date, strUpdateList);
                    }
                    else {
                        JSONObject jsonUpdateList = new JSONObject();
                        jsonUpdateList.put("1", strUpdateUnit);
                        String strUpdateList = jsonUpdateList.toString();

                        jsonUpdateObject.put(date, strUpdateList);
                    }
                    strUpdateObject = jsonUpdateObject.toString();

                    // SharedPreference에 수정된 updateList 넣기
                    SharedPreferences.Editor editor = spref.edit();
                    editor.putString("UpdateList", strUpdateObject);
                    editor.apply();

                    // dangerCount 설정해주는 곳
                    if (timelineFlag == 0) {
                        dangerCount++;
                    }
                    timelineFlag++;
                }
            }
        }
        if (dangerCount != 0) {
            Alert alert = new Alert(context, application);
            alert.sendSafetyAlert(dangerCount, alertNum);
            alertNum++;
            if (alertNum == 5)
                alertNum = 0;
            dangerCount = 0;
        }
    }
}
