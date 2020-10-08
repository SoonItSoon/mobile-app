package com.app.soonitsoon.timeline;

// TimeLine 데이터 객체
public class TimelineData {
    String time;
    double latitude;
    double longitude;
    int danger;

    public TimelineData(String time, double latitude, double longitude) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        danger = 1;
    }

    // danger 값 변경 메소드
    public void danger2Zero() { danger = 0; }
    public void danger2One() { danger = 1; }
    public void danger2Two() { danger = 2; }
}
