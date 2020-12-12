package com.app.soonitsoon.datetime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateNTime {
    private static final String TAG = "DateNTime";

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";

    // 현재 날짜 출력
    // @ret : (String) yyyy-MM-dd
    public static String getDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

        return simpleDateFormat.format(date);
    }

    // 현재 시간 출력
    // @ret : (String) hh:mm:ss
    public static String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);

        return simpleDateFormat.format(date);
    }

    // 받은 날짜를 한글로
    // @ret : (String) yyyy년 MM월 dd일
    public static String toKoreanDate (String date) {
        String koreanDate;
        String[] paresDate;
        paresDate = date.split("-");
        koreanDate = paresDate[0] + "년 " + paresDate[1] + "월 " + paresDate[2] + "일";

        return koreanDate;
    }

    // 받은 시간을 한글로
    // @ret : (String) hh시 mm분
    public static String toKoreanTime (String time) {
        String koreanTime;
        String[] paresTime;
        paresTime = time.split(":");
        koreanTime = paresTime[0] + "시 " + paresTime[1] + "분";

        return koreanTime;
    }
}
