package com.app.soonitsoon.interest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.soonitsoon.R;
import com.google.android.material.textfield.TextInputLayout;

public class InterestAddActivity extends AppCompatActivity {
    private final static int MAX_SIZE_OF_DISASTER_KEYWORD = 7;

    private Activity activity = this;
    private Context context;

    // 지역 Array
    private String[] location1;
    private String[] location2;
    private int location1Index;
    private int location2Index;

    // 재난 종류
    private int disasterIndex;

    // 재난 키워드
    private String[] disasterKeyword;
    private LinearLayout keywordLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrest_add);
        context = this;

        // 저장된 관심분야 수 확인
        SharedPreferences spref = context.getSharedPreferences("InterestData", Context.MODE_PRIVATE);
        int interestSize = spref.getInt("Size", -1);

        // 뒤로가기 버튼
        Button backBtn = findViewById(R.id.btn_interest_add_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 별명 설정 부분
        final TextInputLayout nicknameInputLayout = findViewById(R.id.inputLayout_interest_add_nickname);
        EditText nicknameText = nicknameInputLayout.getEditText();
        nicknameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("김")) {
                    nicknameInputLayout.setError("이미 설정되어 있는 별명입니다.");
                } else {
                    nicknameInputLayout.setError(null);
                }
            }
        });

        // 지역 선택 부분
        final Button btnLocation1 = findViewById(R.id.btn_interest_add_location_1);
        final Button btnLocation2 = findViewById(R.id.btn_interest_add_location_2);
        location1 = getResources().getStringArray(R.array.location);
        btnLocation1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("시/도").setItems(location1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnLocation1.setText(location1[which]);
                        location1Index = which;
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

        // 재난 키워드 레이아웃 설정
        keywordLayout = findViewById(R.id.check_keyword_layout);
        keywordLayout.setVisibility(View.INVISIBLE);

        // 재난 종류 선택 부분
        RadioGroup disasterGroup = findViewById(R.id.radioGroup_disaster);
        disasterGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                keywordLayout.setVisibility(View.VISIBLE);    // 키워드 레이아웃 숨김
                int disasterID;
                if(checkedId == R.id.radio_disaster_0) {
                    disasterIndex = 0;
                    disasterID = getResources().getIdentifier("disaster_" + disasterIndex, "array", getPackageName());
                    disasterKeyword = getResources().getStringArray(disasterID);
                    setDisasterKeyword();
                } else if(checkedId == R.id.radio_disaster_1) {
                    disasterIndex = 1;
                    disasterID = getResources().getIdentifier("disaster_" + disasterIndex, "array", getPackageName());
                    disasterKeyword = getResources().getStringArray(disasterID);
                    setDisasterKeyword();
                } else if(checkedId == R.id.radio_disaster_2) {
                    disasterIndex = 2;
                    disasterID = getResources().getIdentifier("disaster_" + disasterIndex, "array", getPackageName());
                    disasterKeyword = getResources().getStringArray(disasterID);
                    setDisasterKeyword();
                } else if (checkedId == R.id.radio_disaster_3) {
                    disasterIndex = 3;
                    disasterID = getResources().getIdentifier("disaster_" + disasterIndex, "array", getPackageName());
                    disasterKeyword = getResources().getStringArray(disasterID);
                    setDisasterKeyword();
                }
            }
        });
    }

    private void setDisasterKeyword() {
        // 재난 알림 키워드 세팅
        int keywordIndex = 0;
        while (keywordIndex < disasterKeyword.length) {
            int checkBoxID = getResources().getIdentifier("check_keyword_" + keywordIndex, "id", getPackageName());
            CheckBox checkBox = findViewById(checkBoxID);
            checkBox.setText(disasterKeyword[keywordIndex]);
            checkBox.setVisibility(View.VISIBLE);
            keywordIndex++;
        }
        // 필요없는 키워드 체크박스는 숨김
        while (keywordIndex < MAX_SIZE_OF_DISASTER_KEYWORD) {
            int checkBoxID = getResources().getIdentifier("check_keyword_" + keywordIndex, "id", getPackageName());
            CheckBox checkBox = findViewById(checkBoxID);
            checkBox.setVisibility(View.INVISIBLE);
            keywordIndex++;
        }
    }
}
