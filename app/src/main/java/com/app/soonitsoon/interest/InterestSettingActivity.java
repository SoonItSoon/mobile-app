package com.app.soonitsoon.interest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.soonitsoon.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class InterestSettingActivity extends AppCompatActivity {
    private final static int NUM_OF_DISASTER_LEVELS = 9;
    private Activity activity = this;
    private Context context = this;

    // 저장된 관심분야
    private SharedPreferences spref;
    private int interestSize;

    // 관심분야 수 텍스트
    private TextView interestText;

    // 관심분야 레이아웃
    public LinearLayout interestLayout;

    // 관심분야 컨텐츠가 표시되어있는지 확인하는 값
    private boolean isShowingInterest;

    // Arrays
    private String[] disasterArray; // 재난 종류 Array
    private ArrayList<String[]> disasterLevelArray; // 재난별 등급 Array
    private String[] nicknames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrest_setting);

        // 저장된 관심분야 수 확인
        spref = context.getSharedPreferences("InterestData", Context.MODE_PRIVATE);
        interestSize = spref.getInt("size", 0);
        initNicknameArray();

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
                new AlertDialog.Builder(context).setTitle("삭제할 관심분야 선택").setItems(nicknames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (interestSize != 0) {
                            SharedPreferences.Editor editor = spref.edit();
                            editor.putInt("size", --interestSize);
                            editor.remove(nicknames[which]);
                            editor.apply();
                            initNicknameArray();
                            if (isShowingInterest)
                                clearInterest();
                            showInterest();
                        }
                    }
                }).show();
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

        // Array 초기화
        disasterArray = getResources().getStringArray(R.array.disaster);
        disasterLevelArray = new ArrayList<>();
        disasterLevelArray.add(new String[0]);
        for (int i = 1; i <= NUM_OF_DISASTER_LEVELS; i++) {
            int levelArrayID = getResources().getIdentifier("disaster_" + i, "array", getPackageName());
            String[] levelArray = getResources().getStringArray(levelArrayID);
            disasterLevelArray.add(levelArray);
        }

        isShowingInterest = false;
        interestLayout = findViewById(R.id.layout_interest_setting_content);
        interestText = findViewById(R.id.text_interest_setting_count);
        // 설정된 관심분야가 없는 경우
        if(interestSize == 0 ) {
            interestText.setText("설정된 관심분야가 없습니다.");
        }
        // 설정된 관심분야가 있는 경우
        else {
            interestText.setText("설정된 관심분야 개수 : " + interestSize);
            showInterest();
            isShowingInterest = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 저장된 관심분야 수 최신화
        spref = context.getSharedPreferences("InterestData", Context.MODE_PRIVATE);
        interestSize = spref.getInt("size", 0);
        initNicknameArray();

        // 설정된 관심분야가 없는 경우
        if(interestSize == 0 ) {
            interestText.setText("설정된 관심분야가 없습니다.");
        }
        // 설정된 관심분야가 있는 경우
        else {
            interestText.setText("설정된 관심분야 개수 : " + interestSize);
            if (isShowingInterest)
                clearInterest();
            showInterest();
        }
    }

    private void showInterest() {
        // 바깥 View 생성
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams unitParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        unitParams.setMargins(4, 16, 4, 16);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // Interests
        Set<String> set = spref.getAll().keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String nickname = iterator.next();
            if (nickname.equals("size")) continue;
            String strInterest = spref.getString(nickname, "");
            if (!strInterest.isEmpty()) {
                JSONObject jsonInterest = new JSONObject();
                try {
                    jsonInterest = new JSONObject(strInterest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 관심분야 값 불러오기
                String location = jsonInterest.optString("mainLocation", "") + " " + jsonInterest.optString("subLocation", "");
                String disaster = disasterArray[jsonInterest.optInt("disasterIndex", 0)];
                String disasterSubName = jsonInterest.optString("disasterSubName", "");
                StringBuilder disasterLevel = new StringBuilder();
                for (int j = 1; j <= NUM_OF_DISASTER_LEVELS; j++) {
                    if (jsonInterest.optBoolean("disasterSubLevel" + j, false)){
                        disasterLevel.append(disasterLevelArray.get(jsonInterest.optInt("disasterIndex", 0))[j]).append(" ");
                    }
                }
                String scale_range = jsonInterest.optDouble("scale_min", 0) + " ~ " + jsonInterest.optDouble("scale_max", 0);
                String eqLocation = jsonInterest.optString("eq_mainLocation", "") + " " + jsonInterest.optString("eq_subLocation", "");

                // 뷰 생성
                LinearLayout subLayout = new LinearLayout(this);
                subLayout.setLayoutParams(unitParams);
                subLayout.setPadding(24,24, 24, 24);
                subLayout.setOrientation(LinearLayout.VERTICAL);
                subLayout.setBackground(getResources().getDrawable(R.drawable.radius));

                TextView nicknameText = new TextView(this);
                nicknameText.setText(nickname);
                nicknameText.setTextSize(Dimension.DP, 64);
                nicknameText.setTextColor(getResources().getColor(R.color.colorPrimary));
                subLayout.addView(nicknameText);

                TextView locationText = new TextView(this);
                locationText.setText("지역 : " + location);
                locationText.setTextSize(Dimension.DP, 48);
                locationText.setTextColor(getResources().getColor(R.color.colorWhite));
                subLayout.addView(locationText);

                TextView disasterText = new TextView(this);
                if (disasterSubName.isEmpty())
                    disasterText.setText("재난 : " + disaster);
                else
                    disasterText.setText("재난 : " + disaster + " (" + disasterSubName + ")");
                disasterText.setTextSize(Dimension.DP, 48);
                disasterText.setTextColor(getResources().getColor(R.color.colorWhite));
                subLayout.addView(disasterText);

                TextView levelText = new TextView(this);
                levelText.setText("키워드 : " + disasterLevel);
                levelText.setTextSize(Dimension.DP, 48);
                levelText.setTextColor(getResources().getColor(R.color.colorWhite));
                subLayout.addView(levelText);

                if (disaster.equals("지진")) {
                    TextView eqText = new TextView(this);
                    eqText.setText(eqLocation + "에서 발생한, 규모 " + scale_range + "의 지진");
                    eqText.setTextSize(Dimension.DP, 48);
                    eqText.setTextColor(getResources().getColor(R.color.colorWhite));
                    subLayout.addView(eqText);
                }

                linearLayout.addView(subLayout);
            }
        }

        scrollView.addView(linearLayout);
        interestLayout.addView(scrollView);
    }

    // 콘텐츠 지우는 기능
    public void clearInterest() {
        if (interestLayout != null)
            interestLayout.removeAllViews();
    }

    // 관심분야 별명 Array 초기화
    private void initNicknameArray() {
        nicknames = new String[interestSize];
        Set<String> set = spref.getAll().keySet();
        Iterator<String> iterator = set.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            String nickname = iterator.next();
            if (!nickname.equals("size")) {
                nicknames[i++] = nickname;
            }
        }
    }
}