package com.app.soonitsoon.timeline;

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
}
