package com.app.soonitsoon;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalDate {
    // 날짜 사이 계산
    public static int calDateBetweenAandB(String date1, String date2) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            Date firstDate = format.parse(date1);
            Date secondDate = format.parse(date2);

            // 계산 결과 = 초단위
            long calDateSec = firstDate.getTime() - secondDate.getTime();
            // 절대값을 구하자
            calDateSec = Math.abs(calDateSec);
            // 일 단위로 바꾸자
            long calDays = calDateSec / (24*60*60*1000);

            return (int)calDays;
        } catch(ParseException e) {
            Log.e("CalDate", "Parse 오류");
            return -1;
        }
    }

    // 날짜 더하기
    public static String addDay(String inputDate, int day) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            Calendar cal = Calendar.getInstance();
            Date date = format.parse(inputDate);
            cal.setTime(date);

            // day만큼 더하기
            cal.add(Calendar.DATE, day);

            String resultDate = format.format(cal.getTime());
            return resultDate;

        } catch(ParseException e) {
            Log.e("CalDate", "Parse 오류");
            return null;
        }
    }

    // 시간 비교하는 함수
    // time1이 time2보다 느리면 -1, 같으면 0, 빠르면 1
    public static int isFast(String inputTime1, String inputTime2) {
        int result = -2;

        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

            Date time1 = format.parse(inputTime1);
            Date time2 = format.parse(inputTime2);

            result = time1.compareTo(time2);
        } catch (ParseException e) {
            Log.e("isFast", "Parse 오류");
            return result;
        }

        return result;
    }
}
