package com.app.soonitsoon.safety;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.app.soonitsoon.R;
import com.app.soonitsoon.timeline.GetTimeline;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class SafetyActivity extends AppCompatActivity {
    private static Activity activity;
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);
        activity = this;

        // 홈 버튼
        Button homeBtn = findViewById(R.id.btn_safety_goHome);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // TODO: Timeline을 읽어서 Danger값이 1인 애들을 view로 띄워야함니다
        // 필요 정보
        // 1. locName(timeline에 저장해둬야하나?)
        // 2. map view
        // 3. timeline이 찍힌 시간(가장 가까웠을 때의 시간을 얻어오는 방법은 없을까?)
        // 4. 방문했니? yes, no 버튼

        GetTimeline getTimeline = new GetTimeline(getApplication());

        // SharedPreference에서 updateList를 받아오자
        SharedPreferences spref = context.getSharedPreferences("PrevData", Context.MODE_PRIVATE);
        String strUpdateList = spref.getString("UpdateList", "");
        try {
            if (!strUpdateList.isEmpty()) {
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
                        String time = jsonTimeUnit.getString("time");

                        Toast.makeText(context, date + "" + time, Toast.LENGTH_LONG).show();
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Button buttonA = findViewById(R.id.alertButton);
        buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonA.setText("윤수바보");
                // TODO : YES 버튼을 누르면 Danger 값 2 (UpdateTimeline)
                // TODO : NO 버튼을 누르면 Danger 값 0 (UpdateTimeline)
            }
        });
    }
}
