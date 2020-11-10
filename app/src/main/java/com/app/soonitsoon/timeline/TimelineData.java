package com.app.soonitsoon.timeline;

import android.util.Log;
import android.widget.Toast;

import com.app.soonitsoon.TestData;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONArray;

public class TimelineData {
    private final static String TAG = "TimelineData";
    // Data
    public static ArrayList<TimelineUnit> timelineList;
    public static HashMap<String, String> timelineMap;

    // Using Class
    private CheckLocation checkLocation;

    public TimelineData () {
        // Data
        timelineList = new ArrayList<>();
        timelineMap = new HashMap<>();
        load();

        // Class
        checkLocation = new CheckLocation();
    }

    // Unit Data
    public class TimelineUnit {
        String time;
        double latitude;
        double longitude;
        int danger;

        public TimelineUnit(String time, double latitude, double longitude) {
            this.time = time;
            this.latitude = latitude;
            this.longitude = longitude;
            danger = 1;
        }
    }

    // danger 값 변경 메소드
    public void danger2Zero(TimelineUnit unit) { unit.danger = 0; }
    public void danger2One(TimelineUnit unit) { unit.danger = 1; }
    public void danger2Two(TimelineUnit unit) { unit.danger = 2; }

    // TimelineFile 저장 및 불러오기
    //TODO
    public void load(){};
    public String save(){
        JSONObject jsonObject = new JSONObject(timelineMap);
        String jsonString = jsonObject.toString();

        return jsonString;
    };

    // TimelineData 추가
    // @return 추가 완료 시 true
    public boolean add(double latitude, double longitude) {
        // 현재 시간
        DateNTime dateNTime = new DateNTime();
        String date = dateNTime.getDate();
        String time = dateNTime.getTime();

        // 오늘자 데이터가 없는경우
        if (!timelineMap.containsKey(date)) {
            // 데이터 추가
            TimelineUnit timeLineUnit = new TimelineUnit(time, latitude, longitude);
            timelineList = new ArrayList<>();
            timelineList.add(timeLineUnit);

            JSONArray jsonArray = new JSONArray(timelineList);
            ArrayList<String> testarr = new ArrayList<>();
            testarr.add("aetasdfasd");
            testarr.add("aetasdfasasdfd");
            testarr.add("aetasdfasasdfd");
            JSONArray jsonArray2 = new JSONArray(testarr);
            String str = jsonArray2.toString();

//            timelineMap.put(date, timelineList);
            timelineMap.put(date, str);

            Log.e(TAG, "init 완료, 추가된 데이터 : time="+time+" latitude="+latitude+" longitude="+longitude);
            return true;
        }
        // 오늘자 데이터가 있는경우
        else {
//            ArrayList<TimelineUnit> curTimelineList = timelineMap.get(date);
//            double prevLatitude = curTimelineList.get(curTimelineList.size() - 1).latitude;
//            double prevLongitude = curTimelineList.get(curTimelineList.size() - 1).longitude;
//
//            // 이전값과 비교
//            if(checkLocation.check(prevLatitude, prevLongitude, latitude, longitude)) {
//                // 데이터 추가
//                TimelineUnit timelineUnit = new TimelineUnit(time, latitude, longitude);
//                timelineMap.get(date).add(timelineUnit);
//
//                Log.e(TAG, "추가 완료, 추가된 데이터 : time="+time+" latitude="+latitude+" longitude="+longitude);
//                return true;
//            }
//            else {
//                Log.e(TAG, "거리가 허용범위보다 작아 Timeline이 추가되지 않았습니다. latitude="+latitude+" longitude="+longitude);
//                return false;
//            }
            return false;
        }
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

    public void addTest(int num) {
        TestData td = new TestData(num);
        add(td.loc.latitude, td.loc.longitude);
    }
}
