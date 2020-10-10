package com.app.soonitsoon.timeline;

import java.text.SimpleDateFormat;
import java.util.Date;

// getDate() -> (String) yyyy/MM/dd
// getTime() -> (String) hh:mm:ss
public class DateNTime {
    private static final String DATE_FORMAT = "yyyy/MM/dd";
    private static final String TIME_FORMAT = "HH:mm:ss";

    long now;
    Date mDate;
    SimpleDateFormat simpleDate;
    SimpleDateFormat simpleTime;

    public DateNTime() {
        now = System.currentTimeMillis();
        mDate = new Date(now);
        simpleDate = new SimpleDateFormat(DATE_FORMAT);
        simpleTime = new SimpleDateFormat(TIME_FORMAT);
    }

    public String getDate() {
        return simpleDate.format(mDate);
    }

    public String getTime() {
        return simpleTime.format(mDate);
    }
}
