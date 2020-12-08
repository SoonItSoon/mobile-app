package com.app.soonitsoon.briefing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.soonitsoon.R;
import com.app.soonitsoon.safety.SafetyActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class BriefingActivity extends AppCompatActivity {
    private static Activity activity;
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefing);
        activity = this;

        // 홈 버튼
        Button homeBtn = findViewById(R.id.btn_briefing_goHome);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // safety 버튼
        showSafetyBtn();

        // 오늘 하루 관심분야에 해당하는 재난문자 리스트
        showInterestList();
    }

    private void showSafetyBtn () {
        Button safetyBtn = findViewById(R.id.btn_briefing_isSafety);
        safetyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SafetyActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences spref = context.getSharedPreferences("PrevData", Context.MODE_PRIVATE);
        String strUpdateList = spref.getString("UpdateList", "");

        // 확인안한 확진자 접촉 알림이 몇개인지 세어보자
        int dangerCount = 0;
        try {
            JSONObject jsonUpdateList = new JSONObject(strUpdateList);

            Iterator<String> iteratorDate = jsonUpdateList.keys();
            while (iteratorDate.hasNext()) {
                String date = iteratorDate.next();
                String strUpdateUnit = jsonUpdateList.getString(date);

                if (strUpdateUnit.isEmpty())
                    continue;
                else {
                    JSONObject jsonUpdateUnit = new JSONObject(strUpdateUnit);

                    Iterator<String> iteratorIndex = jsonUpdateUnit.keys();
                    while(iteratorIndex.hasNext()) {
                        String index = iteratorIndex.next();
                        String strUpdateOne = jsonUpdateUnit.getString(index);

                        if (!strUpdateOne.isEmpty())
                            dangerCount++;
                    }
                }
            }
            safetyBtn.setText(dangerCount + "개의 장소에서 확진자와 접촉했을 가능성이 있습니다.");
        }
        // 확인안한 확진자 접촉 알림이 없으면 버튼을 없애자
        catch (JSONException e) {
            e.printStackTrace();
            //safetyBtn.setVisibility(View.GONE);
            safetyBtn.setText(dangerCount + "개의 장소에서 확진자와 접촉했을 가능성이 있습니다.");
        }
    }

    private void showInterestList() {
        
    }
}
