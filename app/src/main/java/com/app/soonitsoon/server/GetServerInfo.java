package com.app.soonitsoon.server;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.renderscript.Sampler;
import android.util.Log;
import android.widget.Toast;

import com.app.soonitsoon.CalDate;
import com.app.soonitsoon.safety.RequestHttpConnection;
import com.app.soonitsoon.timeline.DateNTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class GetServerInfo {

    // 위험 동선 확인
    public static ArrayList<ArrayList<String>> getSafetyData(Application application, Context context) {
        ArrayList<ArrayList<String>> resultList = new ArrayList<>();

        // 현재 날짜, 시간 받아오기
        String currentDate = DateNTime.getDate();
        String currentTime = DateNTime.getTime();

        // SharedPreference에 저장된 prev 날짜, 시간 받아오기
        SharedPreferences prevData = context.getSharedPreferences("PrevData", Context.MODE_PRIVATE);
        String safetyPrevDate = prevData.getString("SafetyPrevDate", "");
        String safetyPrevTime = prevData.getString("SafetyPrevTime", "");
        SharedPreferences.Editor editor = prevData.edit();

        // 이전 시간이랑 같다면 (근데 이런 경우가 있나?)
        if (currentDate.equals(safetyPrevDate) && currentTime.equals(safetyPrevTime)) {
            Log.e("getSafetyData", "이전 시간이랑 같음");
            resultList = null;

            return resultList;
        }
        else {
            // prev 값 변경
            editor.putString("SafetyPrevDate", currentDate);
            editor.putString("SafetyPrevTime", currentTime);
            editor.apply();

            String strConnectionResult = "";
            try {
                String startDateTime = safetyPrevDate + " " + safetyPrevTime;
                String endDateTime = currentDate + " " + currentTime;

                // connection 전체 결과
                strConnectionResult = getServerData(makeConnUrl(startDateTime, endDateTime, "", "", 1, "1", "COVID-19", "", "", -1, -1, ""));

                JSONObject jsonConnectionResult = new JSONObject(strConnectionResult);
                Iterator<String> iterator = jsonConnectionResult.keys();

                // resultList를 채운다
                while (iterator.hasNext()) {
                    String num = iterator.next();
                    JSONObject jsonObject = jsonConnectionResult.getJSONObject(num);

                    String[] start = jsonObject.getString("start_time").split(" ");
                    String[] end = jsonObject.getString("end_time").split(" ");

                    String startDate = start[0];
                    String endDate = end[0];
                    String startTime = start[1];
                    String endTime = end[1];

                    // 접촉 기간 계산
                    int date = CalDate.calDateBetweenAandB(startDate, endDate);

                    // 접촉 기간이 하루면 Unit 하나만 만든다
                    if (date == 0) {
                        ArrayList<String> resultUnit = new ArrayList<>();

                        resultUnit.add(jsonObject.getString("msg"));
                        resultUnit.add(jsonObject.getString("sender"));
                        resultUnit.add(startDate);
                        resultUnit.add(startTime);
                        resultUnit.add(endTime);
                        resultUnit.add(jsonObject.getString("location_name"));

                        resultList.add(resultUnit);
                    }
                    // 접촉 기간이 1일 이상이면 하루씩 Unit을 만든다
                    else if (date > 0) {
                        // 첫째날 : startTime ~ 23:59:59
                        ArrayList<String> resultUnit = new ArrayList<>();

                        resultUnit.add(jsonObject.getString("msg"));
                        resultUnit.add(jsonObject.getString("sender"));
                        resultUnit.add(startDate);
                        resultUnit.add(startTime);
                        resultUnit.add("23:59:59");
                        resultUnit.add(jsonObject.getString("location_name"));

                        resultList.add(resultUnit);

                        // 중간날 : 00:00:00 ~ 23:59:59
                        for (int i = 1; i < date - 1; i++) {
                            resultUnit = new ArrayList<>();

                            resultUnit.add(jsonObject.getString("msg"));
                            resultUnit.add(jsonObject.getString("sender"));
                            resultUnit.add(CalDate.addDay(startDate, i));
                            resultUnit.add("00:00:00");
                            resultUnit.add("23:59:59");
                            resultUnit.add(jsonObject.getString("location_name"));

                            resultList.add(resultUnit);
                        }

                        // 마지막날 : 00:00:00 ~ endTime
                        resultUnit = new ArrayList<>();

                        resultUnit.add(jsonObject.getString("msg"));
                        resultUnit.add(jsonObject.getString("sender"));
                        resultUnit.add(endDate);
                        resultUnit.add("00:00:00");
                        resultUnit.add(endTime);
                        resultUnit.add(jsonObject.getString("location_name"));

                        resultList.add(resultUnit);
                    }
                    // calDateBetweenAandB 에서 -1을 반환했을 경우(에러)
                    else
                        return null;
                }
                return resultList;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    // 문자 검색 결과 반환
    public static ArrayList<JSONObject> getSearchData(URL url) {

        return new ArrayList<>();
    }

    // URL 생성
    public static URL makeConnUrl(String startDateTime, String endDateTime, String mainLocation, String subLocation, int disasterIndex, String levels, String subName, String eqMainLocation, String eqSubLocation, double scaleMin, double scaleMax, String innerText) throws MalformedURLException {
        StringBuilder strUrl = new StringBuilder("http://203.253.25.184:8080/search");
        strUrl.append("?start_date=").append(startDateTime);
        if (!endDateTime.isEmpty())
            strUrl.append("&end_date=").append(endDateTime);
        if (!mainLocation.isEmpty() && !mainLocation.equals("전체")) {
            strUrl.append("&main_location=").append(mainLocation).append("&sub_location=").append(subLocation);
        }
        strUrl.append("&disaster=").append(disasterIndex);
        strUrl.append("&level=").append(levels);
        if (!subName.isEmpty() && !subName.equals("전체")) {
            strUrl.append("&name=").append(subName);
        }
        if (!eqMainLocation.isEmpty() && !eqMainLocation.equals("전체")) {
            strUrl.append("&obs_location=").append(eqMainLocation).append(" ").append(eqSubLocation);
        }
        if (disasterIndex == 2 && scaleMin != -1 && scaleMax != -1) {
            strUrl.append("&scale_min=").append(scaleMin);
            strUrl.append("&scale_max=").append(scaleMax);
        }
        if (!innerText.isEmpty()) {
            strUrl.append("&inner_text=").append(innerText);
        }

        Log.e("Created URL", String.valueOf(strUrl));

        return new URL(String.valueOf(strUrl));
    }

    // Url을 받아 서버 데이터를 받음
    public static String getServerData(URL url) throws IOException {
        String strConnectionResult = "";


        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();


        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
        StringBuffer sb = new StringBuffer();

        String line;
        while ((line = br.readLine()) != null)
            sb.append(line);

        strConnectionResult = sb.toString();

        br.close();


        return strConnectionResult;
    }
}
