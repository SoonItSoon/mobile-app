package com.app.soonitsoon.safety;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
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

    public void callFunction(String locName, double lat, double lon ) {
        final Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.map_dialog);
        dialog.show();

        MapView mapView = new MapView(context);
        ViewGroup mapViewContainer = (ViewGroup) dialog.findViewById(R.id.dialog_map);
        mapViewContainer.addView(mapView);
    }


}
