package com.app.soonitsoon.safety;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.app.soonitsoon.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MapDialog {
    private static Activity activity;
    private Context context;

    MapView mapView;
    ViewGroup mapViewContainer;


    public MapDialog(Context context) {
        this.context = context;
    }

    public void callFunction(String locName, Double lat, Double lon) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.map_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        dialog.show();

        MapView mapView = new MapView(context);
        ViewGroup mapViewContainer = (ViewGroup) dialog.findViewById(R.id.dialog_map);
        mapViewContainer.addView(mapView);

        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat, lon), 2, true);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(locName);
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(lat, lon));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);
    }


}
