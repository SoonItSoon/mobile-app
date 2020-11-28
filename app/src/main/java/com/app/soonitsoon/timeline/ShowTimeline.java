package com.app.soonitsoon.timeline;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapPOIItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class ShowTimeline {
    private static final String TAG = "ShowTimeline";

    private Application application;
    private MapView mapView;

    public ShowTimeline(Application application, MapView mapView) {
        this.application = application;
        this.mapView = mapView;
    }

    public void show(String date) {
        // Json 파일 읽기
        InputStream inputStream;
        String stringTLList = "";
        try {
            inputStream = application.openFileInput(date+".json");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                stringTLList = bufferedReader.readLine();
                inputStream.close();
            }
            Log.e(TAG, "stringTL = " + stringTLList);
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

        // Key Set
        Iterator<String> iterator = jsonTLList.keys();

        // PolyLine 설정
        MapPolyline polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(128, 255, 51, 0));

        // 각 위치마다 좌표 찍기 및 라인 그리기
        while (iterator.hasNext()) {
            String time = iterator.next();
            String title = DateNTime.toKoreanTime(time);
            String stringTLUnit = "";
            JSONObject jsonTLUnit = new JSONObject();
            try {
                stringTLUnit = jsonTLList.getString(time);
                jsonTLUnit = new JSONObject(stringTLUnit);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            double latitude = 0;
            double longitude = 0;
            int danger = 0;
            try {
                latitude = jsonTLUnit.getDouble("latitude");
                longitude = jsonTLUnit.getDouble("longitude");
                danger = jsonTLUnit.getInt("danger");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));

            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(title);
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
            mapView.addPOIItem(marker);
        }

        // 라인 그리기
        mapView.addPolyline(polyline);

        // 지도 크기 맞게 조절
        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100;
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
    }
}
