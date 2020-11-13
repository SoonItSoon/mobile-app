package com.app.soonitsoon.timeline;

import android.app.Activity;
import android.app.Application;
import android.icu.text.Edits;
import android.util.Log;

import com.app.soonitsoon.MainActivity;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapPOIItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
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

        JSONObject jsonTLList = new JSONObject();
        try {
            jsonTLList = new JSONObject(stringTLList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Iterator<String> iterator = jsonTLList.keys();

        while (iterator.hasNext()) {
            String time = iterator.next();
            String title = date + " " + time;
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

            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(title);
            marker.setTag(0);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            mapView.addPOIItem(marker);
        }
    }
}
