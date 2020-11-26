package com.app.soonitsoon.message;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import com.app.soonitsoon.interest.InterestActivity;
import com.app.soonitsoon.timeline.DateNTime;
import com.app.soonitsoon.timeline.TimelineActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    public static Activity activity;
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    Toolbar toolbar;

    // 기간 선택
    private String fromDate;
    private String toDate;

    // 지역 Array
    private String[] location1;
    private String[] location2;
    private int location1Index;
    private int location2Index;

    // 재난 종류
    private int disasterIndex;
    private final static int NUM_OF_DISASTER = 6;

    // 재난 종류 선택 라디오 버튼
    private ArrayList<RadioButton> disasterRadios;

    // 재난 하위 레이아웃 Array
    private ArrayList<LinearLayout> disasterLayouts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        activity = this;

        MainActivity.killMainActivity();
        TimelineActivity.killTimelineActivity();
        InterestActivity.killInterestActivity();
        SafetyActivity.killSafetyActivity();

        // 상단 바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_icon); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = findViewById(R.id.layout_message);

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
                    startActivity(MainActivity.timelineIntent);
                }
                else if(id == R.id.nav_item_interest){
                    startActivity(MainActivity.interestIntent);
                }
                else if(id == R.id.nav_item_safety){
                    startActivity(MainActivity.safetyIntent);
                }
                else if(id == R.id.nav_item_message){
                    Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        // 기간 선택
        // 오늘 날짜로 초기화
        fromDate = DateNTime.getDate();
        toDate = DateNTime.getDate();
        // From 날짜 선택 버튼
        Button fromDatePickBtn = findViewById(R.id.btn_search_date_from);
        fromDatePickBtn.setText(DateNTime.toKoreanDate(fromDate));
        fromDatePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(0);
            }
        });
        // To 날짜 선택 버튼
        Button toDatePickBtn = findViewById(R.id.btn_search_date_to);
        toDatePickBtn.setText(DateNTime.toKoreanDate(toDate));
        toDatePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(1);
            }
        });

        // 지역 선택 부분
        final Button btnLocation1 = findViewById(R.id.btn_search_location_0);
        final Button btnLocation2 = findViewById(R.id.btn_search_location_1);
        location1 = getResources().getStringArray(R.array.location);
        btnLocation1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("시/도").setItems(location1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnLocation1.setText(location1[which]);
                        location1Index = which;
                        location2Index = 0;
                        btnLocation2.setText("전체");
                        int location2ID = getResources().getIdentifier("location_" + location1Index, "array", getPackageName());
                        location2 = getResources().getStringArray(location2ID);
                        btnLocation2.setEnabled(true);
                    }
                }).show();
            }
        });
        btnLocation2.setEnabled(false);
        btnLocation2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("시/군/구").setItems(location2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnLocation2.setText(location2[which]);
                        location2Index = which;
                    }
                }).show();
            }
        });

        // 재난 라디오 버튼 초기화
        disasterRadios = new ArrayList<>();
        initDisasterRadios();

        // 재난 하위 레이아웃 초기화
        disasterLayouts = new ArrayList<>();
        initDisasterLayouts();

        // 재난 구분 선택 부분
        disasterIndex = -1; // 재난 종류 초기화
        final RadioGroup disasterGroup1 = findViewById(R.id.radioGroup_search_disaster_category_1);
        final RadioGroup disasterGroup2 = findViewById(R.id.radioGroup_search_disaster_category_2);
        disasterGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) return; // clearCheck() 호출로 이벤트가 발생한 경우 처리
                disasterGroup2.clearCheck(); // 그룹2의 체크는 모두 해제
                uncheckDisasterRadios(); // 모든 라디오 버튼 해체 효과
                goneDisasterLayouts(); // 하위 레이아웃 숨김
                if(checkedId == R.id.radio_disaster_0) {
                    disasterIndex = 0;
                } else if(checkedId == R.id.radio_disaster_1) {
                    disasterIndex = 1;
                } else if(checkedId == R.id.radio_disaster_2) {
                    disasterIndex = 2;
                } else if (checkedId == R.id.radio_disaster_3) {
                    disasterIndex = 3;
                } else {
                    disasterIndex = -1;
                }
                if (disasterIndex != -1) {
                    checkDisasterRadio(disasterIndex); // 선택된 라디오 버튼 선택 효과
                    showDisasterLayout(disasterIndex); // 선택된 재난 종류 하위 레이어 표시
                }
            }
        });
        disasterGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) return; // clearCheck() 호출로 이벤트가 발생한 경우 처리
                disasterGroup1.clearCheck(); // 그룹1의 체크는 모두 해제
                uncheckDisasterRadios(); // 모든 라디오 버튼 해체 효과
                goneDisasterLayouts(); // 하위 레이아웃 숨김
                if(checkedId == R.id.radio_disaster_4) {
                    disasterIndex = 4;
                } else if (checkedId == R.id.radio_disaster_5) {
                    disasterIndex = 5;
                } else {
                    disasterIndex = -1;
                }
                if (disasterIndex != -1) {
                    checkDisasterRadio(disasterIndex); // 선택된 라디오 버튼 선택 효과
                    showDisasterLayout(disasterIndex); // 선택된 재난 종류 하위 레이어 표시
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // DatePick 화면 출력
    // Msg Search 에서 Call
    public void showDatePicker(int flag) {
        String defualtDate = "";
        DialogFragment datePickFragment = new DialogFragment();
        // fromDate 인 경우
        if(flag == 0) {
            defualtDate = fromDate;
            datePickFragment = new DatePickFragment(defualtDate, toDate, flag);
        }
        // toDate 인 경우
        else if (flag == 1) {
            defualtDate = toDate;
            datePickFragment = new DatePickFragment(defualtDate, fromDate, flag);
        }
        datePickFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // DatePick을 통해 선택된 날짜 처리
    public void processDatePickerResult(int year, int month, int day, int flag) {
        String year_string = Integer.toString(year);
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String date_msg = year_string+"-"+month_string+"-"+day_string;

        if (flag == 0) {
            Button datePickBtn = findViewById(R.id.btn_search_date_from);
            fromDate = date_msg;
            datePickBtn.setText(DateNTime.toKoreanDate(fromDate));
        }
        else if (flag == 1) {
            Button datePickBtn = findViewById(R.id.btn_search_date_to);
            toDate = date_msg;
            datePickBtn.setText(DateNTime.toKoreanDate(toDate));
        }

//        String toastStr = "기간 선택";
//        Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
    }

    // 재난 라디오 버튼 ArrayList 초기화
    private void initDisasterRadios() {
        int disasterRadioID;
        for(int i=0; i<NUM_OF_DISASTER; i++) {
            disasterRadioID = getResources().getIdentifier("radio_disaster_" + i, "id", getPackageName());
            RadioButton radioButton = findViewById(disasterRadioID);
            disasterRadios.add(radioButton);
        }
    }

    // 모든 재난 라디오 버튼 해제
    private void uncheckDisasterRadios() {
        if (disasterRadios != null) {
            for(RadioButton radioButton : disasterRadios) {
                radioButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                radioButton.setBackground(getResources().getDrawable(R.drawable.radio_button_unselected));
            }
        }
    }

    // 선택된 재난 라디오 버튼 체크
    private void checkDisasterRadio(int radioNum) {
        if (disasterRadios != null) {
            disasterRadios.get(radioNum).setTextColor(getResources().getColor(R.color.colorPrimary));
            disasterRadios.get(radioNum).setBackground(getResources().getDrawable(R.drawable.radio_button_selected));
        }
    }

    // 레이아웃 ArrayList 초기화
    private void initDisasterLayouts() {
        int disasterLayoutID;
        for(int i=0; i<NUM_OF_DISASTER; i++) {
            disasterLayoutID = getResources().getIdentifier("layout_search_disaster_" + i, "id", getPackageName());
            LinearLayout linearLayout = findViewById(disasterLayoutID);
            disasterLayouts.add(linearLayout);
        }
    }

    // 모든 재난의 하위 카테고리 숨김
    private void goneDisasterLayouts() {
        if (disasterLayouts != null) {
            for(LinearLayout linearLayout : disasterLayouts) {
                linearLayout.setVisibility(View.GONE);
            }
        }
    }

    // 선택된 재난 종류의 하위 카테고리 선택 레이아웃 표시
    private void showDisasterLayout(int layoutNum) {
        if (disasterLayouts != null) {
            disasterLayouts.get(layoutNum).setVisibility(View.VISIBLE);
        }
    }

    // 메시지 검색 Activity 종료 (Activity 전환 시)
    public static void killMessageActivity() {
        if(activity != null)
            activity.finish();
    }
}
