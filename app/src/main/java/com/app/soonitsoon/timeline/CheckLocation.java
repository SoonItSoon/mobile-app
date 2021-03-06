package com.app.soonitsoon.timeline;

public class CheckLocation {
    private static final String TAG = "CheckLocation";

    private static final double ALLOW_DISTANCE = 50;      // CheckLocation 허용 거리 (m)

    // 두 좌표사이가 허용거리 이상이면 true, 이하면 false 반환
    public static boolean check(double lat1, double lon1, double lat2, double lon2){
        return calDistance(lat1, lon1, lat2, lon2) >= ALLOW_DISTANCE;
    }
    public static boolean check(double lat1, double lon1, double lat2, double lon2, double dist){
        return calDistance(lat1, lon1, lat2, lon2) >= dist;
    }

    // 좌표 사이에 거리 계산
    // @return : m단위 거리
    private static double calDistance(double lat1, double lon1, double lat2, double lon2){

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // mile to km
        dist = dist * 1000.0;      // km to m

        return dist;
    }

    // degree to radian
    private static double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // radian to degree
    private static double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }
}
