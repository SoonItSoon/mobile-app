package com.app.soonitsoon.safety;

import android.app.Application;
import android.content.Context;
import android.icu.text.Edits;
import android.location.Location;
import android.util.Log;

import com.app.soonitsoon.server.GetServerInfo;
import com.app.soonitsoon.timeline.CheckLocation;

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
    private  int dangerCount;

    public CheckSafetyInfo(Context context, Application application) {
        this.context = context;
        this.application = application;
        dangerCount = 0;
    }
    // TODO : getServerInfo
    // TODO : getTimeline
    public void getDangerInfo() throws JSONException {
        // TODO : getServerInfo.getSafety 로 받아온 값
//        ArrayList<ArrayList<String>> dangerList = get();
        ArrayList<ArrayList<String>> dangerList = GetServerInfo.getSafetyData(application, context);

        if (dangerList == null)
            return;

        for (ArrayList<String> dangerUnit : dangerList){
            String msg = dangerUnit.get(0);
            String sender = dangerUnit.get(1);
            String date = dangerUnit.get(2);
            String startTime = dangerUnit.get(3);
            String endTime = dangerUnit.get(4);
            String locName = dangerUnit.get(5);

            // Timeline 받아오기
            InputStream inputStreamTimeline;
            String stringTLList = "";
            try {
                inputStreamTimeline = application.openFileInput(date+".json");

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

            // 접촉 의심 시간 내 타임라인을 받아오기 위한 시간 저장
            Iterator<String> iterator = jsonTLList.keys();
            String prev = "";
            ArrayList<String> dangerTimeList = new ArrayList<>();

            while(iterator.hasNext()) {
                String time = iterator.next();
                int start = 1;
                int end = 1;

                // TODO : startTime/endTime 비교하는거 추가해야댐

                if (start == 0 && end == 0) {          // 내가 접촉 의심 시간이야 (접촉 의심 시간이 범위가 아닌 경우)
                    dangerTimeList.add(time);
                    break;
                }
                else if (start == -1 && end == 1) {    // 내가 접촉 의심 시간 안에 있어
                    if(!prev.isEmpty())
                        dangerTimeList.add(prev);
                }
                else if ((start == -1 && end == -1) || (start == -1 && end == 0)) {    // 내가 접촉 의심 시간보다 빨라 또는 엔드타임과 같아
                    if (!prev.isEmpty())
                        dangerTimeList.add(prev);
                    dangerTimeList.add(time);
                    break;
                }
                prev = time;
            }

            // TODO : 주소 -> lat/lon 바꾸고
            String[] strLocRequest = RequestHttpConnection.request(locName);
            double dangerLat = Double.parseDouble(strLocRequest[1]);
            double dangerLon = Double.parseDouble(strLocRequest[0]);

            int timelineFlag = 0;
            for (String dangerTime : dangerTimeList) {
                String strTLUnit = jsonTLList.getString(dangerTime);
                JSONObject jsonTLUnit = new JSONObject(strTLUnit);

                double timelineLat = jsonTLUnit.getDouble("latitude");
                double timelineLon = jsonTLUnit.getDouble("longitude");

                // M단위
                boolean distance = CheckLocation.check(dangerLat, timelineLat, dangerLon, timelineLon);

                // true : 거리가 멀다  / false : 거리가 가깝다 -> 위험하다 -> DANGER값 1로 바꾼다
                if (distance == false) {
                    // TODO : Danger값을 바꾸는 모듈을 만들고 넣자

                    // dangerCount 설정해주는 곳
                    if (timelineFlag == 0) {
                        dangerCount++;
                    }
                    timelineFlag++;
                }
            }
        }

        // TODO : 알림 보내기 "!!확진자 접촉 위험 알림!!" "총 @개의 장소에서 접촉 위험을 발견했습니다"
    }

//    private static ArrayList<ArrayList<String>> get () {
//        ArrayList<ArrayList<String>> dangerList= new ArrayList<>();
//        ArrayList<String> dangerUnit = new ArrayList<>();
//        dangerUnit.add("2020-11-20");
//        dangerUnit.add("22:30:00");
//        dangerUnit.add("23:59:59");
//        dangerUnit.add("부산 코아 노래연습장");
//        dangerUnit.add("금곡대로303번길 80");
//        dangerList.add(dangerUnit);
//        dangerUnit = new ArrayList<>();
//
//        dangerUnit.add("2020-11-21");
//        dangerUnit.add("00:00:00");
//        dangerUnit.add("03:00:00");
//        dangerUnit.add("부산 코아 노래연습장");
//        dangerUnit.add("금곡대로303번길 80");
//        dangerList.add(dangerUnit);
//        dangerUnit = new ArrayList<>();
//
//        dangerUnit.add("2020-11-22");
//        dangerUnit.add("12:00:00");
//        dangerUnit.add("14:00:00");
//        dangerUnit.add("해남 삼산면 매화정");
//        dangerUnit.add("-");
//        dangerList.add(dangerUnit);
//        dangerUnit = new ArrayList<>();
//
//        dangerUnit.add("2020-11-23");
//        dangerUnit.add("11:00:00");
//        dangerUnit.add("14:00:00");
//        dangerUnit.add("해남읍 정성한우촌");
//        dangerUnit.add("-");
//        dangerList.add(dangerUnit);
//
//        return dangerList;
//    }

}
