package com.app.soonitsoon.interest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.soonitsoon.R;

public class InterestSettingActivity extends AppCompatActivity {
    private Activity activity = this;
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrest_setting);

        // 저장된 관심분야 수 확인
        SharedPreferences spref = context.getSharedPreferences("InterestData", Context.MODE_PRIVATE);
        int interestSize = spref.getInt("Size", -1);

        TextView interestText = findViewById(R.id.text_interest_setting_count);

        // SharedPreferences 설정이 되어 있지 않는 경우
        if(interestSize == -1) {
            interestText.setText("설정된 SP가 없습니다.");

            // SharedPreferences 초기 설정
            SharedPreferences.Editor editor = spref.edit();
            editor.putInt("Size", 0);
            editor.apply();
            interestSize = 0;
        }

        // 설정된 관심분야가 없는 경우
        if(interestSize == 0 ) {
            interestText.setText("설정된 관심분야가 없습니다.");
        }
        // 설정된 관심분야가 있는 경우
        else {
            interestText.setText("설정된 관심분야 개수 : " + interestSize);
        }

        // 관심분야 추가 버튼
        Button addBtn = findViewById(R.id.btn_interest_setting_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interestAddIntent = new Intent(getApplicationContext(), InterestAddActivity.class);
                startActivity(interestAddIntent);
            }
        });

        // 관심분야 삭제 버튼
        Button deleteBtn = findViewById(R.id.btn_interest_setting_del);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        // 뒤로가기 버튼
        Button backBtn = findViewById(R.id.btn_interest_setting_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
