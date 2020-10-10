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
    private MapView mapView;
    private CheckLocation checkLocation;

    ArrayList<TimelineData> timelineList;
    HashMap<String, ArrayList<TimelineData>> timelineMap;

    public AddTimeline(MapView mapView) {
        this.mapView = mapView;
        checkLocation = new CheckLocation();

        timelineList = new ArrayList<>();
        timelineMap = new HashMap<>();
    }

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
            TimelineData timeLineData = new TimelineData(time, latitude, longitude);
            timelineList = new ArrayList<>();
            timelineList.add(timeLineData);
            timelineMap.put(date, timelineList);

            Log.e("AddTimeline", "init 완료, 추가된 데이터 : time="+time+" latitude="+latitude+" longitude="+longitude);
            return true;
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
                return true;
            }
            else {
                Log.e("AddTimeline", "거리가 허용범위보다 작아 Timeline이 추가되지 않았습니다. latitude="+latitude+" longitude="+longitude);
                return false;
            }
        }
    }
}
