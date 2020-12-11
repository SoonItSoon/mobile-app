package com.app.soonitsoon.timeline;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.app.soonitsoon.DatePickFragment;
import com.app.soonitsoon.R;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.File;

public class TimelineActivity extends AppCompatActivity {
    public static Activity activity;
    private Context context = this;

    private String selectedDate;   // DatePicker를 통해 선택된 날짜

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        activity = this;

        // 홈 버튼
        Button homeBtn = findViewById(R.id.btn_timeline_goHome);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 맵 그리기
        final MapView mapView = new MapView(this);
        final ViewGroup mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        GetLocation getLocation = new GetLocation(activity);
        // 화면 이동
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(getLocation.getLatitude(), getLocation.getLongitude()), 2, true);

        // 선택된 날짜를 오늘 날짜로 초기화
        selectedDate = DateNTime.getDate();

        final ShowTimeline showTimeline = new ShowTimeline(getApplication(), mapView);
        showTimeline.show(selectedDate);

        // 날짜 선택 버튼
        Button datePickBtn = findViewById(R.id.btn_timeline_datepick);
        datePickBtn.setText(DateNTime.toKoreanDate(selectedDate));
        datePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(mapView, showTimeline);
            }
        });

        // DeleteTimeline 버튼
        Button deleteTLBtn = findViewById(R.id.btn_timeline_delete);
        deleteTLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedDate.equals(DateNTime.getDate())) {
                    SharedPreferences spref = getSharedPreferences("PrevData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = spref.edit();
                    editor.clear();
                    editor.apply();
                }

                File file = new File(getFilesDir(), selectedDate + ".json");
                boolean delete = file.delete();

                String toastStr = "";
                if(delete) {
                    toastStr = "Timeline 파일 제거완료.";
                    // Clear MapView
                    mapView.removeAllPOIItems();
                    mapView.removeAllPolylines();
                }
                else toastStr = "Timeline 파일 제거실패.";

//                Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
            }
        });
    }

    // DatePick 화면 출력
    // Timeline 에서 Call
    public void showDatePicker(MapView mapView, ShowTimeline showTimeline) {
        DialogFragment datePickFragment = new DatePickFragment(selectedDate, mapView, showTimeline);
        datePickFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // DatePick을 통해 선택된 날짜 처리
    public void processDatePickerResult(int year, int month, int day, MapView mapView, ShowTimeline showTimeline) {
        String year_string = Integer.toString(year);
//        String month_string = Integer.toString(month+1);
        String month_string = String.format("%02d", month+1);
//        String day_string = Integer.toString(day);
        String day_string = String.format("%02d", day);
        String date_msg = year_string+"-"+month_string+"-"+day_string;

        if (isContainsTimeline(date_msg)) {

            Button datePickBtn = findViewById(R.id.btn_timeline_datepick);
            selectedDate = date_msg;
            datePickBtn.setText(DateNTime.toKoreanDate(selectedDate));


//        String toastStr = selectedDate + " Timeline 입니다.";
//        Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();

            // Clear MapView
            mapView.removeAllPOIItems();
            mapView.removeAllPolylines();
            // Show Timeline
            showTimeline.show(selectedDate);
        } else {
            Toast.makeText(context, "해당 날짜에 저장된 타임라인이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isContainsTimeline(String date) {
        String path = context.getFilesDir() + "/" + date + ".json";
        File file = new File(path);
        if (file.exists()) return true;
        else return false;
    }
}
