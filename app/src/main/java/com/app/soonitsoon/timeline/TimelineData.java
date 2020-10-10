package com.app.soonitsoon.timeline;

import android.util.Log;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;

public class TimelineData {
    // Data
    public static ArrayList<TimelineUnit> timelineList;
    public static HashMap<String, ArrayList<TimelineUnit>> timelineMap;

    // Using Class
    private CheckLocation checkLocation;

    // For Test
    private MapView mapView;

    public TimelineData (MapView mapView) {
        // Data
        timelineList = new ArrayList<>();
        timelineMap = new HashMap<>();
        load(timelineMap);

        // Class
        checkLocation = new CheckLocation();

        // For Test
        this.mapView = mapView;
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
    public void load(HashMap<String, ArrayList<TimelineUnit>> timelineMap){};
    public void save(HashMap<String, ArrayList<TimelineUnit>> timelineMap){};

    // TimelineData 추가
    // @return 추가 완료 시 true
    public boolean add(double latitude, double longitude) {
        // 현재 시간
        DateNTime dateNTime = new DateNTime();
        String date = dateNTime.getDate();
        String time = dateNTime.getTime();

        // 화면 이동
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 2, true);

        // 오늘자 데이터가 없는경우
        if (!timelineMap.containsKey(date)) {
            // 마커 추가
            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(date + " " + time);
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
            mapView.addPOIItem(marker);
            // 데이터 추가
            TimelineUnit timeLineUnit = new TimelineUnit(time, latitude, longitude);
            timelineList = new ArrayList<>();
            timelineList.add(timeLineUnit);
            timelineMap.put(date, timelineList);

            Log.e("AddTimeline", "init 완료, 추가된 데이터 : time="+time+" latitude="+latitude+" longitude="+longitude);
            return true;
        }
        // 오늘자 데이터가 있는경우
        else {
            ArrayList<TimelineUnit> curTimelineList = timelineMap.get(date);
            double prevLatitude = curTimelineList.get(curTimelineList.size() - 1).latitude;
            double prevLongitude = curTimelineList.get(curTimelineList.size() - 1).longitude;

            // 이전값과 비교
            if(checkLocation.check(prevLatitude, prevLongitude, latitude, longitude)) {
                // 마크 추가
                MapPOIItem marker = new MapPOIItem();
                marker.setItemName(date + " " + time);
                marker.setTag(0);
                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                mapView.addPOIItem(marker);
                // 데이터 추가
                TimelineUnit timelineUnit = new TimelineUnit(time, latitude, longitude);
                timelineMap.get(date).add(timelineUnit);

                Log.e("AddTimeline", "추가 완료, 추가된 데이터 : time="+time+" latitude="+latitude+" longitude="+longitude);
                return true;
            }
            else {
                Log.e("AddTimeline", "거리가 허용범위보다 작아 Timeline이 추가되지 않았습니다. latitude="+latitude+" longitude="+longitude);
                return false;
            }
        }
    }
}
