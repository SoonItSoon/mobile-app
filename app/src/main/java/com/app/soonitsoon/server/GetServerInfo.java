package com.app.soonitsoon.server;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.app.soonitsoon.CalDate;
import com.app.soonitsoon.safety.RequestHttpConnection;
import com.app.soonitsoon.timeline.DateNTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class GetServerInfo {
    public static String getTestData() {
        String str = "";
        try {
            URL url = new URL("http://203.253.25.184:8080/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();

            // Get the stream
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            str = builder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;
    }

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

            // TODO : 서버 정보 받아오는거 모듈화하자
            String jsonResult = "";
            String strConnectionResult = "";
            BufferedReader br = null;
            StringBuffer sb = null;

            try {
                // url Connection
                URL url = new URL("http://203.253.25.184:8080/search?" +            // url 주소
                        "start_date=" + safetyPrevDate + " " + safetyPrevTime +           // 검색 시간 (prev ~ 현재)
                        "&disaster=1&name=COVID-19&level=1");                             // disaster = 전염병, 이름 = 코로나, level = 1(접촉안내)
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                sb = new StringBuffer();

                while ((jsonResult = br.readLine()) != null)
                    sb.append(jsonResult);

                strConnectionResult = sb.toString();                               // connection 전체 결과

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
            } finally {
                try {
                    if (br != null)
                        br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
