package com.app.soonitsoon.message;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.app.soonitsoon.R;
import com.app.soonitsoon.timeline.DateNTime;

import java.util.ArrayList;

public class MessageResultActivity extends AppCompatActivity {
    private static Activity activity;
    private Context context = this;

    // 넘겨받은 검색 조건들
    private String fromDate;
    private String toDate;
    private int location1Index;
    private int location2Index;
    private int disasterIndex;
    private String subCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_result);
        activity = this;

        // 뒤로가기 버튼
        Button homeBtn = findViewById(R.id.btn_message_result_back);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout resultLayout = findViewById(R.id.layout_message_result);

        // 값을 잘 넘겨받은 경우
        if (receiveCondition()) {
            TextView textView1 = new TextView(this);
            textView1.setText(DateNTime.toKoreanDate(fromDate) + "부터 " + DateNTime.toKoreanDate(toDate) + "까지");
            TextView textView2 = new TextView(this);
            textView2.setText("지역 인덱스 : " + location1Index + ", " + location2Index);
            TextView textView3 = new TextView(this);
            textView3.setText("재난 인덱스 : " + disasterIndex);
            TextView textView4 = new TextView(this);
            textView4.setText(subCategory);

            resultLayout.addView(textView1);
            resultLayout.addView(textView2);
            resultLayout.addView(textView3);
            resultLayout.addView(textView4);
        }
        // 값을 받지 못한 경우
        else {
            TextView textView5 = new TextView(this);
            textView5.setText("값을 불러오지 못하였습니다.");

            resultLayout.addView(textView5);
        }
    }

    // 검색 조건을 전달받는다.
    private boolean receiveCondition() {
        Intent intent = getIntent();
        fromDate = intent.getStringExtra("fromDate");
        if (fromDate == null || fromDate.isEmpty()) return false;
        toDate = intent.getStringExtra("toDate");
        if (toDate == null || toDate.isEmpty()) return false;
        location1Index = intent.getIntExtra("location1Index", -1);
        if (location1Index == -1) return false;
        location2Index = intent.getIntExtra("location2Index", -1);
        if (location2Index == -1) return false;
        disasterIndex = intent.getIntExtra("disasterIndex", -1);
        if (disasterIndex == -1) return false;
        subCategory = intent.getStringExtra("subCategory");
        if (subCategory == null || subCategory.isEmpty()) return false;

        return true;
    }
}
