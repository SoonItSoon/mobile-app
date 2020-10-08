package com.app.soonitsoon.timeline;

import android.app.Activity;
import android.util.Log;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.HashMap;
import java.util.ArrayList;

// 현재 마커를 추가하는 기능
public class AddTimeline {
    MapView mapView;
    GpsTracker gpsTracker;
    CheckLocation checkLocation;

    ArrayList<TimelineData> timelineList;
    HashMap<String, ArrayList<TimelineData>> timelineMap;

    public AddTimeline(Activity mainActivity, MapView mapView) {
        gpsTracker = new GpsTracker(mainActivity);
        this.mapView = mapView;
        checkLocation = new CheckLocation();

        timelineList = new ArrayList<>();
        timelineMap = new HashMap<>();
    }

    public void add() {
        // 현재 위치
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        // 현재 시간
        DateNTime dateNTime = new DateNTime();
        String date = dateNTime.getDate();
        String time = dateNTime.getTime();

        // 화면 이동
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 5, true);

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
            TimelineData timeLineData = new TimelineData(time, latitude, longitude);
            timelineList = new ArrayList<>();
            timelineList.add(timeLineData);
            timelineMap.put(date, timelineList);

            Log.e("AddTimeline", "init 완료, 추가된 데이터 : time="+time+" latitude="+latitude+" longitude="+longitude);
        }
        // 오늘자 데이터가 있는경우
        else {
            ArrayList<TimelineData> curTimelineList = timelineMap.get(date);
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
                TimelineData timelineData = new TimelineData(time, latitude, longitude);
                timelineMap.get(date).add(timelineData);

                Log.e("AddTimeline", "추가 완료, 추가된 데이터 : time="+time+" latitude="+latitude+" longitude="+longitude);
            }
            else {
                Log.e("AddTimeline", "거리가 허용범위보다 작아 Timeline이 추가되지 않았습니다.");
            }
        }
    }

    // 테스트 버튼용
    public void add(String title, double latitude, double longitude) {
        // 화면 이동
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 5, true);

        // 마커 추가
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(title);
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker);
    }
}
