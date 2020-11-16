package com.app.soonitsoon;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.app.soonitsoon.timeline.ShowTimeline;
import com.app.soonitsoon.timeline.TimelineActivity;

import net.daum.mf.map.api.MapView;

import java.util.Calendar;

public class DatePickFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private MapView mapView;
    private ShowTimeline showTimeline;

    public DatePickFragment(MapView mapView, ShowTimeline showTimeline) {
        this.mapView = mapView;
        this.showTimeline = showTimeline;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, dayOfMonth);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        TimelineActivity activity = (TimelineActivity) getActivity();
        if (activity != null) {
            activity.processDatePickerResult(year, month, dayOfMonth, mapView, showTimeline);
        }
    }
}