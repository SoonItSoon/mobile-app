package com.app.soonitsoon.timeline;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.app.soonitsoon.DatePickFragment;
import com.app.soonitsoon.MainActivity;
import com.app.soonitsoon.R;
import com.app.soonitsoon.SafetyActivity;
import com.app.soonitsoon.message.MessageActivity;
import com.app.soonitsoon.interest.InterestActivity;
import com.google.android.material.navigation.NavigationView;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.File;

public class TimelineActivity extends AppCompatActivity {
    public static Activity activity;
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    Toolbar toolbar;
    private String selectedDate;   // DatePicker를 통해 선택된 날짜

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        activity = this;

        MainActivity.killMainActivity();
        InterestActivity.killInterestActivity();
        SafetyActivity.killSafetyActivity();
        MessageActivity.killMessageActivity();

        // 상단 바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_icon); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = findViewById(R.id.layout_timeline);

        // 네비게이션 바
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.nav_item_home){
                    startActivity(MainActivity.mainIntent);
                }
                else if(id == R.id.nav_item_timeline){
                    Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.nav_item_interest){
                    startActivity(MainActivity.interestIntent);
                }
                else if(id == R.id.nav_item_safety){
                    startActivity(MainActivity.safetyIntent);
                }
                else if(id == R.id.nav_item_message){
                    startActivity(MainActivity.messageIntent);
                }
                return true;
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

    // 네비게이션 바 열기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // 왼쪽 상단 버튼 눌렀을 때
            case android.R.id.home:{
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String date_msg = year_string+"-"+month_string+"-"+day_string;

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
    }

    // 타임라인 Activity 종료 (Activity 전환 시)
    public static void killTimelineActivity() {
        if(activity != null)
            activity.finish();
    }
}
