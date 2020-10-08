package com.app.soonitsoon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.soonitsoon.timeline.CheckLocation;
import com.app.soonitsoon.timeline.GpsTracker;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private Activity mainActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 위치 권한 허용 받기
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }

        // get key hash
//        Log.e("getKeyHash", ""+getKeyHash(MainActivity.this));


        final MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);



        Button btn = findViewById(R.id.gpsBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsTracker gpsTracker = new GpsTracker(mainActivity);
                double latitute = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                String toastStr = "latitude : " + Double.toString(latitute) + ", longitude : " + Double.toString(longitude);
                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();

                mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitute, longitude), 5, true);

                MapPOIItem marker = new MapPOIItem();
                marker.setItemName("Default Marker");
                marker.setTag(0);
                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitute, longitude));
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                mapView.addPOIItem(marker);
            }
        });

        Button btn2 = findViewById(R.id.markBtn1);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsTracker gpsTracker = new GpsTracker(mainActivity);
                double latitute = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                CheckLocation checkLocation = new CheckLocation();
                boolean distance = checkLocation.check(latitute, longitude, 37.49455, 126.95979);
                Toast.makeText(getApplicationContext(), Boolean.toString(distance), Toast.LENGTH_LONG).show();



            }
        });

        Button btn3 = findViewById(R.id.markBtn2);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(v.getId());
            }
        });
    }




    // get key hash
    public static String getKeyHash(final Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (packageInfo == null)
                return null;

            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}