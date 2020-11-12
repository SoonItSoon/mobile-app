package com.app.soonitsoon.timeline;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import net.daum.mf.map.api.MapView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;

public class RecordTimeline {
    private final static String TAG = "RecordTimeline";
    private Context context;
    private Context applicationContext;

    public RecordTimeline(Context context, Context applicationContext) {
        this.context = context;
        this.applicationContext = applicationContext;
    }

    // 주기적으로 BackgroundService에서 동작
    public void excute(double latitude, double longitude) throws JSONException {
        // 현재 날짜와 시간
        String date = DateNTime.getDate();
        String time = DateNTime.getTime();

        // 이전 데이터의 날짜 값
        SharedPreferences spref = context.getSharedPreferences("PrevData", Context.MODE_PRIVATE);
        String prevDate = spref.getString("Date", "");

        // 이전 날짜와 현재 날짜가 같은 경우
        if (date.equals(prevDate)) {
            Log.e(TAG, "오늘자 데이터 있음");
            // 이전 좌표값
            double prevLat = spref.getFloat("Latitude", 0);
            double prevLon = spref.getFloat("Longitude", 0);

            // 데이터를 추가할 만한 거리인지 확인
            if (CheckLocation.check(latitude, longitude, prevLat, prevLon)) {
                Log.e(TAG, "이전 좌표와 거리가 멀어 데이터가 추가됩니다.");

                // 타임라인 Unit
                JSONObject jsonTLUnit = new JSONObject();
                jsonTLUnit.put("latitude", latitude);
                jsonTLUnit.put("longitude", longitude);
                jsonTLUnit.put("danger", 1);
                String stringTLUnit = jsonTLUnit.toString();

                // 타임라인 List
                String prevTLList = spref.getString("TLList", "");
                JSONObject jsonTLList = new JSONObject(prevTLList);
                jsonTLUnit.put(time, stringTLUnit);
                String stringTLList = jsonTLList.toString();

                // 데이터 파일로 저장
                saveTimeline(date, stringTLList);

                // 현재 값을 이전 값으로 세팅
                SharedPreferences.Editor editor = spref.edit();
                editor.putString("Date", date);
                editor.putFloat("Latitude", (float)latitude);
                editor.putFloat("Longitude", (float)longitude);
                editor.putString("TLList", stringTLList);
                editor.apply();
            }
            else {
                Log.e(TAG, "이전 좌표와 거리가 가까워 데이터가 추가되지 않습니다.");
            }
        }
        // 이전 날짜와 현재 날짜가 다른 경우
        else {
            Log.e(TAG, "오늘자 데이터 없음");

            // 타임라인 Unit
            JSONObject jsonTLUnit = new JSONObject();
            jsonTLUnit.put("latitude", latitude);
            jsonTLUnit.put("longitude", longitude);
            jsonTLUnit.put("danger", 1);
            String stringTLUnit = jsonTLUnit.toString();

            // 타임라인 List
            JSONObject jsonTLList = new JSONObject();
            jsonTLList.put(time, stringTLUnit);
            String stringTLList = jsonTLList.toString();

            // 데이터 파일로 저장
            saveTimeline(date, stringTLList);

            // 현재 값을 이전 값으로 세팅
            SharedPreferences.Editor editor = spref.edit();
            editor.putString("Date", date);
            editor.putFloat("Latitude", (float)latitude);
            editor.putFloat("Longitude", (float)longitude);
            editor.putString("TLList", stringTLList);
            editor.apply();
        }

    }

    // String으로 된 타임라인 List를 날짜.json 파일로 저장
    private void saveTimeline(String date, String stringTLList) {
//        FileOutputStream outputStream;
        Log.e(TAG, "saveTimeline");
//        try {
//            outputStream = applicationContext.openFileOutput(date+".json", Context.MODE_PRIVATE);
//            outputStream.write(stringTLList.getBytes());
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    // TimelineData 보기
    // @param Mapview, "yyyy/MM/dd"
    public void show(MapView mapView, String date) {
//        ArrayList<TimelineUnit> timelineList = timelineMap.get(date);
//
//        assert timelineList != null;
//        for(TimelineUnit timelineUnit : timelineList) {
//            // 마커 추가
//            MapPOIItem marker = new MapPOIItem();
//            marker.setItemName(date + " " + timelineUnit.time);
//            marker.setTag(0);
//            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(timelineUnit.latitude, timelineUnit.longitude));
//            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
//            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
//            mapView.addPOIItem(marker);
//        }
    }

}
