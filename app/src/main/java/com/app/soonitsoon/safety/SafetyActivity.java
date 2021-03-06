package com.app.soonitsoon.safety;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.soonitsoon.R;
import com.app.soonitsoon.datetime.DateNTime;
import com.app.soonitsoon.timeline.GetTimeline;
import com.app.soonitsoon.timeline.UpdateTimeline;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SafetyActivity extends AppCompatActivity {
    private static Activity activity;
    private Context context = this;

    LinearLayout resultLayout;
    View textView;

    SharedPreferences spref;
    String strUpdateList;

//    MapView mapView;
//    ViewGroup mapViewContainer;

    public SafetyActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);
        activity = this;

        resultLayout = findViewById(R.id.layout_safety_content);
        textView = findViewById(R.id.text_safety);
        // TODO :
        textView.setVisibility(View.VISIBLE);

        // 홈 버튼
        Button homeBtn = findViewById(R.id.btn_safety_goHome);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GetTimeline getTimeline = new GetTimeline(getApplication());

        // SharedPreference에서 updateList를 받아오자
        spref = context.getSharedPreferences("PrevData", Context.MODE_PRIVATE);
        strUpdateList = spref.getString("UpdateList", "");

        // 위험 지역 리스트 보여주기
        showDangerList(strUpdateList);
    }

    private void showDangerList (String strUpdateList) {
        // 위험지역 방문한 기록이 없으면
        if (strUpdateList.isEmpty() || strUpdateList.equals("{}")) {
            textView.setVisibility(View.VISIBLE);
            return;
        }
        try {
            textView.setVisibility(View.GONE);
            // 체크해야할 timeline List를 보여줄 scrollView
            ScrollView scrollView = new ScrollView(this);
            LinearLayout.LayoutParams unitParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            unitParams.setMargins(4, 16, 4, 16);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            // date, time, locName 불러오기
            JSONObject jsonUpdateList = new JSONObject(strUpdateList);
            Iterator<String> iteratorDate = jsonUpdateList.keys();

            while (iteratorDate.hasNext()) {
                String date = iteratorDate.next();
                String strTimeList = jsonUpdateList.getString(date);
                JSONObject jsonTimeList = new JSONObject(strTimeList);

                Iterator<String> iteratorIndex = jsonTimeList.keys();
                while (iteratorIndex.hasNext()) {
                    String index = iteratorIndex.next();
                    String strTimeUnit = jsonTimeList.getString(index);
                    JSONObject jsonTimeUnit = new JSONObject(strTimeUnit);

                    LinearLayout subLayout =  makeCheckUnit(scrollView, linearLayout ,unitParams, date, index, jsonTimeUnit);
                    linearLayout.addView(subLayout);
                }
            }

            scrollView.addView(linearLayout);
            resultLayout.addView(scrollView);
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private LinearLayout makeCheckUnit(ScrollView scrollView, final LinearLayout linearLayout, LinearLayout.LayoutParams unitParams, final String date, final String index, final JSONObject jsonTimeUnit) throws JSONException {
        final String time = jsonTimeUnit.getString("time");
        final String locName = jsonTimeUnit.getString("locName");

        // 위험 알림 하나의 layout
        final LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setLayoutParams(unitParams);
        mainLayout.setPadding(24,24, 24, 24);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackground(getResources().getDrawable(R.drawable.radio_button_unselected));

        // 시간, 장소 layout
        LinearLayout timeLayout = new LinearLayout(this);
        timeLayout.setLayoutParams(unitParams);
        timeLayout.setPadding(24,24, 24, 24);
        timeLayout.setOrientation(LinearLayout.VERTICAL);
        timeLayout.setBackground(getResources().getDrawable(R.drawable.radius));

        // Text를 띄워보자
        TextView textView_date = new TextView(this);
        textView_date.setText("날짜 : " + DateNTime.toKoreanDate(date));
        textView_date.setTextSize(Dimension.DP, 64);
        textView_date.setTextColor(getResources().getColor(R.color.colorPrimary));

        TextView textView_time = new TextView(this);
        textView_time.setText("시간 : " + DateNTime.toKoreanTime(time));
        textView_time.setTextSize(Dimension.DP, 64);
        textView_time.setTextColor(getResources().getColor(R.color.colorPrimary));

        TextView textView_loc = new TextView(this);
        textView_loc.setText("장소 : " + locName);
        textView_loc.setTextSize(Dimension.DP, 64);
        textView_loc.setTextColor(getResources().getColor(R.color.colorPrimary));

        // time Layout에 추가하자
        timeLayout.addView(textView_date);
        timeLayout.addView(textView_time);
        timeLayout.addView(textView_loc);

        // Button layout
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setLayoutParams(unitParams);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setWeightSum(3);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(16,16,16,8);
        btnParams.weight = 1;

        final UpdateTimeline updateTimeline = new UpdateTimeline(context, getApplication());
        // Yes, No, 지도 버튼
        Button button_Y = new Button(this);
        button_Y.setText("갔어요!");
        button_Y.setTextSize(Dimension.DP, 40);
        button_Y.setLayoutParams(btnParams);
        button_Y.setTextColor(getResources().getColor(R.color.colorPrimary));
        button_Y.setBackground(getResources().getDrawable(R.drawable.radius));
        button_Y.setHeight(60);
        button_Y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTimeline.excute(date, time, 2);

                // SharedPreference 수정
                JSONObject jsonUpdateList = null;
                try {
                    jsonUpdateList = new JSONObject(strUpdateList);
                    String strUpdateUnit = jsonUpdateList.getString(date);
                    JSONObject jsonUpdateUnit = new JSONObject(strUpdateUnit);
                    jsonUpdateUnit.remove(index);
                    jsonUpdateList.remove(date);
                    if (jsonUpdateUnit.length() != 0) {
                        strUpdateUnit = jsonUpdateUnit.toString();
                        jsonUpdateList.put(date, strUpdateUnit);
                    }
                    strUpdateList = jsonUpdateList.toString();

                    SharedPreferences.Editor editor = spref.edit();
                    editor.putString("UpdateList", strUpdateList);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                linearLayout.removeView(mainLayout);

                if (linearLayout.getChildCount() == 0)
                    textView.setVisibility(View.VISIBLE);
            }
        });

        Button button_N = new Button(this);
        button_N.setText("안갔어요!");
        button_N.setTextSize(Dimension.DP, 40);
        button_N.setLayoutParams(btnParams);
        button_N.setTextColor(getResources().getColor(R.color.colorPrimary));
        button_N.setBackground(getResources().getDrawable(R.drawable.radius));
        button_N.setHeight(60);
        button_N.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTimeline.excute(date, time, 0);

                JSONObject jsonUpdateList = null;
                try {
                    jsonUpdateList = new JSONObject(strUpdateList);
                    String strUpdateUnit = jsonUpdateList.getString(date);
                    JSONObject jsonUpdateUnit = new JSONObject(strUpdateUnit);
                    jsonUpdateUnit.remove(index);
                    jsonUpdateList.remove(date);
                    if (jsonUpdateUnit.length() != 0) {
                        strUpdateUnit = jsonUpdateUnit.toString();
                        jsonUpdateList.put(date, strUpdateUnit);
                    }
                    strUpdateList = jsonUpdateList.toString();

                    SharedPreferences.Editor editor = spref.edit();
                    editor.putString("UpdateList", strUpdateList);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                linearLayout.removeView(mainLayout);

                if (linearLayout.getChildCount() == 0)
                    textView.setVisibility(View.VISIBLE);
            }
        });


        Button button_M = new Button(this);
        button_M.setText("어디에요?");
        button_M.setTextSize(Dimension.DP, 40);
        button_M.setLayoutParams(btnParams);
        button_M.setTextColor(getResources().getColor(R.color.colorPrimary));
        button_M.setBackground(getResources().getDrawable(R.drawable.radius));
        button_M.setHeight(60);
        button_M.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = null;
                String longitude = null;
                try {
                    latitude = jsonTimeUnit.getString("lat");
                    longitude = jsonTimeUnit.getString("lon");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SafetyMapDialog mapDialog = new SafetyMapDialog(context);
                mapDialog.callFunction(locName, Double.parseDouble(latitude), Double.parseDouble(longitude));
            }
        });

        // button Layout에 추가하자
        buttonLayout.addView(button_Y);
        buttonLayout.addView(button_N);
        buttonLayout.addView(button_M);

        // main Layout에 추가하자
        mainLayout.addView(timeLayout);
        mainLayout.addView(buttonLayout);

        return mainLayout;
    }
}
