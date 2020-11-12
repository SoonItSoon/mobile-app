package com.app.soonitsoon.timeline;

import java.text.SimpleDateFormat;
import java.util.Date;

// getDate() -> (String) yyyy/MM/dd
// getTime() -> (String) hh:mm:ss
public class DateNTime {
    private static final String DATE_FORMAT = "yyyy/MM/dd";
    private static final String TIME_FORMAT = "HH:mm:ss";


    public static String getDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

        return simpleDateFormat.format(date);
    }

    public static String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);

        return simpleDateFormat.format(date);
    }
}
