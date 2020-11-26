package com.app.soonitsoon.interest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.soonitsoon.R;

public class InterestActivity extends AppCompatActivity {
    public static Activity activity;
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        activity = this;

        // 홈 버튼
        Button homeBtn = findViewById(R.id.btn_interest_goHome);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 관심분야 편집 버튼
        Button settingBtn = findViewById(R.id.btn_interest_setting);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent settingIntent = new Intent(getApplicationContext(), InterestSettingActivity.class);
                startActivity(settingIntent);
            }
        });

    }
}
