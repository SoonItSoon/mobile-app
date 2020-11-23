package com.app.soonitsoon;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.app.soonitsoon.message.MessageActivity;
import com.app.soonitsoon.timeline.ShowTimeline;
import com.app.soonitsoon.timeline.TimelineActivity;

import net.daum.mf.map.api.MapView;

import java.util.Calendar;

public class DatePickFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private MapView mapView;    // timeline을 그릴 mapView
    private ShowTimeline showTimeline; // timeline을 그리기 위한 객체
    private int searchFlag; // fromDate or toDate
    private String defaultDate; // DatePicker 생성 시 defaultDate 설정을 위한 값
    private Calendar maxCalendar; // fromDate MAX 값을 설정하기위한 toDate 값

    // toDate 인 경우 생성
    public DatePickFragment(String date, int flag) {
        defaultDate = date;
        this.mapView = null;
        this.showTimeline = null;
        searchFlag = flag;
        maxCalendar = Calendar.getInstance();
    }

    // fromDate 인 경우 생성
    public DatePickFragment(String date, String toDate, int flag) {
        defaultDate = date;
        this.mapView = null;
        this.showTimeline = null;
        searchFlag = flag;
        maxCalendar = Calendar.getInstance();
        String[] parsingDate = toDate.split("-");
        maxCalendar.set(Integer.parseInt(parsingDate[0]), Integer.parseInt(parsingDate[1]) - 1, Integer.parseInt(parsingDate[2]));
    }

    // timeline에서 생성
    public DatePickFragment(String date, MapView mapView, ShowTimeline showTimeline) {
        defaultDate = date;
        this.mapView = mapView;
        this.showTimeline = showTimeline;
        searchFlag = -1;
        maxCalendar = Calendar.getInstance();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year, month, dayOfMonth;
        if(defaultDate.equals("")) {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        } else {
            String[] parsingDate = defaultDate.split("-");
            year = Integer.parseInt(parsingDate[0]);
            month = Integer.parseInt(parsingDate[1]) - 1;
            dayOfMonth = Integer.parseInt(parsingDate[2]);
        }

        // DatePicker 생성
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, dayOfMonth);
        // 날짜 선택 최댓값 설정
        datePickerDialog.getDatePicker().setMaxDate(maxCalendar.getTime().getTime());

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        // Timeline 에서 Call
        if(mapView != null && showTimeline != null) {
            TimelineActivity activity = (TimelineActivity) getActivity();
            if (activity != null) {
                activity.processDatePickerResult(year, month, dayOfMonth, mapView, showTimeline);
            }
        }
        // Msg Search 에서 Call
        else {
            MessageActivity activity = (MessageActivity) getActivity();
            if (activity != null) {
                activity.processDatePickerResult(year, month, dayOfMonth, searchFlag);
            }
        }
    }
}