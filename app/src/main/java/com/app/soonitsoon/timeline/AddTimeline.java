package com.app.soonitsoon.timeline;

import android.app.Activity;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

// 현재 마커를 추가하는 기능
public class AddTimeline {
    MapView mapView;
    GpsTracker gpsTracker;
    CheckLocation checkLocation;

    public AddTimeline(Activity mainActivity, MapView mapView) {
        gpsTracker = new GpsTracker(mainActivity);
        this.mapView = mapView;
        checkLocation = new CheckLocation();
    }

    public void add(String title, double latitude, double longitude) {
        // 화면 이동
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 5, true);

        // 현재 위치
//        double curLatitude = gpsTracker.getLatitude();
//        double curLongitude = gpsTracker.getLongitude();

        // 마커 추가
//        if(checkLocation.check(curLatitude, curLongitude, latitude, longitude)) {
            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(title);
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            mapView.addPOIItem(marker);
//        }
    }
}
