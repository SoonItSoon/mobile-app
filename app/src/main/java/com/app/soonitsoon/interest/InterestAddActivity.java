package com.app.soonitsoon.interest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.soonitsoon.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.apptik.widget.MultiSlider;

public class InterestAddActivity extends AppCompatActivity {
    private final static String TAG = "InterestAddActivity";
    private Activity activity = this;
    private Context context;

    // 관심분야 데이터
    private SharedPreferences spref;
    private int interestSize;

    // 조건이 잘 선택되었는지 확인 하는 값
    private final static int CHECKED_DATE = 8;
    private final static int CHECKED_LOCATION = 4;
    private final static int CHECKED_CATEGORY = 2;
    private final static int CHECKED_SUB_CATEGORY = 1;
    private int isChecked;

    // 별명 선택
    private String nickname;
    private TextInputLayout nicknameInputLayout;
    private EditText nicknameText;

    // 지역 Array
    private String[] location1Array;
    private String[] location2Array;
    private String mainLocation;
    private String subLocation;

    // 재난 종류
    private final static int NUM_OF_DISASTER = 6;
    private final static int NUM_OF_DISASTER_LEVELS = 9;
    private int disasterIndex;

    // 텍스트 검색 내용
    private String innerText;

    // 재난 종류 선택 라디오 버튼
    private ArrayList<RadioButton> disasterRadios;

    // 재난 하위 레이아웃 Array
    private ArrayList<LinearLayout> disasterLayouts;

    // 재난 하위 레이아웃 Contents
    // 전염병
    private Button disaster1_nameBtn;   // 전염병 종류 버튼
    private CheckBox disaster1_level1_chk;  // 전염병 등급 체크박스
    private CheckBox disaster1_level2_chk;
    private CheckBox disaster1_level3_chk;
    private CheckBox disaster1_level9_chk;
    // 지진
    private CheckBox disaster2_level1_chk;  // 지진 알림 종류 체크박스
    private CheckBox disaster2_level9_chk;
    private TextView disaster2_scale_text;  // 규모 선택 레이아웃 제목
    private LinearLayout disaster2_scale_layout;    // 규모 선택 레이아웃
    private TextView disaster2_scale_min_text;   // 규모 최솟값 텍스트 뷰
    private TextView disaster2_scale_max_text;   // 규모 최댓값 텍스트 뷰
    private MultiSlider disaster2_scale_slider; // 규모 선택 슬라이더
    private Button disaster2_mainLocationBtn;   // 지진 관측 지역 시/도 선택 버튼
    private Button disaster2_subLocationBtn;    // 지진 관측 지역 시/군/구 선택 버튼
    private String[] disaster2_mainLocation;    // 지진 관측 지역 시/도 Array
    private String[] disaster2_subLocation;     // 지진 관측 지역 시/군/구 Array
    // 미세먼지
    private CheckBox disaster3_level1_chk;  // 미세먼지 알림 종류 체크박스
    private CheckBox disaster3_level2_chk;
    private CheckBox disaster3_level9_chk;
    // 태풍
    private Button disaster4_namebtn;   // 태풍 이름 선택 버튼
    private CheckBox disaster4_level1_chk;  // 태풍 알림 종류 체크박스
    private CheckBox disaster4_level2_chk;
    private CheckBox disaster4_level9_chk;
    // 미세먼지
    private CheckBox disaster5_level1_chk;  // 홍수 알림 종류 체크박스
    private CheckBox disaster5_level2_chk;
    private CheckBox disaster5_level9_chk;
    // 그 외
    private RadioGroup disaster_etc_radioGroup; // 그 외 재난 종류 선택 라디오 그룹
    private RadioButton disaster_etc_1; // 폭염
    private RadioButton disaster_etc_2; // 한파
    private RadioButton disaster_etc_3; // 호우
    private RadioButton disaster_etc_4; // 대설
    private CheckBox disaster_etc_level1_chk;   // 그 외 알림 종류 체크박스
    private CheckBox disaster_etc_level2_chk;
    private CheckBox disaster_etc_level9_chk;

    // 재난 하위 레이아웃 선택 내용
    private String disasterSubName; // 전염병 종류, 태풍 이름
    private boolean[] disasterSubLevel; // 알림 등급
    private double scale_min;   // 지진 규모 최솟값
    private double scale_max;   // 지진 규모 최댓값
    private String eq_mainLocation;   // 지진 관측 지역 시/도
    private String eq_subLocation;   // 지진 관측 지역 시/군/구



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrest_add);
        context = this;

        // 저장된 관심분야 수 확인
        spref = context.getSharedPreferences("InterestData", Context.MODE_PRIVATE);
        interestSize = spref.getInt("size", 0);

        // 뒤로가기 버튼
        Button backBtn = findViewById(R.id.btn_interest_add_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 별명 설정 부분
        nickname = "";
        nicknameInputLayout = findViewById(R.id.textInput_interest_nickname);
        nicknameText = nicknameInputLayout.getEditText();
        nicknameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean check = false;
                Set<String> set = spref.getAll().keySet();
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    if (s.toString().equals(iterator.next()))
                        check = true;
                }
                if(check) {
                    nicknameInputLayout.setError("이미 설정되어 있는 별명입니다.");
                } else {
                    nicknameInputLayout.setError(null);
                }
            }
        });

        // 체크 변수 초기화
        isChecked = 0;

        // 지역 선택 부분
        final Button btnLocation1 = findViewById(R.id.btn_interest_main_location);
        final Button btnLocation2 = findViewById(R.id.btn_interest_sub_location);
        location1Array = getResources().getStringArray(R.array.location);
        btnLocation1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("시/도").setItems(location1Array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnLocation1.setText(location1Array[which]);
                        mainLocation = location1Array[which];
                        subLocation = "전체";
                        checkCondition(CHECKED_LOCATION);   // 지역 선택 완료
                        btnLocation2.setText("전체");
                        int location2ID = getResources().getIdentifier("location_" + which, "array", getPackageName());
                        location2Array = getResources().getStringArray(location2ID);
                        btnLocation2.setEnabled(true);
                    }
                }).show();
            }
        });
        btnLocation2.setEnabled(false);
        btnLocation2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("시/군/구").setItems(location2Array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnLocation2.setText(location2Array[which]);
                        subLocation = location2Array[which];
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

        // 하위 카테고리 컨텐츠 초기화 및 이벤트 리스너 설정
        initDisaster1SubContents();
        initDisaster2SubContents();
        initDisaster3SubContents();
        initDisaster4SubContents();
        initDisaster5SubContents();
        initDisaster6SubContents();

        // 재난 구분 선택 부분
        disasterIndex = 0; // 재난 종류 초기화
        final RadioGroup disasterGroup1 = findViewById(R.id.radioGroup_interest_disaster_category_1);
        final RadioGroup disasterGroup2 = findViewById(R.id.radioGroup_interest_disaster_category_2);
        disasterGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) return;    // clearCheck() 호출로 이벤트가 발생한 경우 처리
                disasterGroup2.clearCheck();    // 그룹2의 체크는 모두 해제
                uncheckDisasterRadios();    // 모든 라디오 버튼 해체 효과
                goneDisasterLayouts();  // 하위 레이아웃 숨김
                uncheckCondition(CHECKED_CATEGORY); // 재난 종류 선택 미완료
                if(checkedId == R.id.radio_disaster_1) {
                    disasterIndex = 1;
                } else if(checkedId == R.id.radio_disaster_2) {
                    disasterIndex = 2;
                } else if(checkedId == R.id.radio_disaster_3) {
                    disasterIndex = 3;
                } else if (checkedId == R.id.radio_disaster_4) {
                    disasterIndex = 4;
                } else {
                    disasterIndex = 0;
                }
                if (disasterIndex != 0) {
                    checkDisasterRadio(disasterIndex);  // 선택된 라디오 버튼 선택 효과
                    showDisasterLayout(disasterIndex);  // 선택된 재난 종류 하위 레이어 표시
                    clearSubConditions();    // 하위 값들 초기화
                    checkCondition(CHECKED_CATEGORY);   // 재난 종류 선택 완료
                }
            }
        });
        disasterGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) return;    // clearCheck() 호출로 이벤트가 발생한 경우 처리
                disasterGroup1.clearCheck();    // 그룹1의 체크는 모두 해제
                uncheckDisasterRadios();    // 모든 라디오 버튼 해체 효과
                goneDisasterLayouts();  // 하위 레이아웃 숨김
                uncheckCondition(CHECKED_CATEGORY); // 재난 종류 선택 미완료
                if(checkedId == R.id.radio_disaster_5) {
                    disasterIndex = 5;
                } else if (checkedId == R.id.radio_disaster_6) {
                    disasterIndex = 6;
                } else {
                    disasterIndex = 0;
                }
                if (disasterIndex != 0) {
                    checkDisasterRadio(disasterIndex);  // 선택된 라디오 버튼 선택 효과
                    showDisasterLayout(disasterIndex);  // 선택된 재난 종류 하위 레이어 표시
                    clearSubConditions();    // 하위 값들 초기화
                    checkCondition(CHECKED_CATEGORY);   // 재난 종류 선택 완료
                }
            }
        });

        // 관심분야 저장 버튼
        Button saveBtn = findViewById(R.id.btn_save_interest);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 하위 카테고리 선택 확인
                if (isCheckedSubConditions()) {
                    checkCondition(CHECKED_SUB_CATEGORY);
                } else {
                    uncheckCondition(CHECKED_SUB_CATEGORY);
                }

                // 별명이 설정되지 않은 경우
                if (nicknameText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "별명이 설정되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                // 지역 선택이 되어있지 않은 경우
                else if ((isChecked & CHECKED_LOCATION) == 0) {
                    Toast.makeText(getApplicationContext(), "지역이 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                // 재난 종류 선택이 되어있지 않은 경우
                else if ((isChecked & CHECKED_CATEGORY) == 0 || disasterIndex == 0) {
                    Toast.makeText(getApplicationContext(), "재난 종류가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                // 하위 카테고리가 되어있지 않은 경우
                else if ((isChecked & CHECKED_SUB_CATEGORY) == 0) {
                    Toast.makeText(getApplicationContext(), "검색 필수 조건이 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                // 모두 다 선택이 된 경우
                else {
                    nickname = nicknameText.getText().toString();

                    // 값 Json에 저장
                    JSONObject jsonInterest = new JSONObject();
                    try {
                        jsonInterest.put("mainLocation", mainLocation); // 시/도
                        jsonInterest.put("subLocation", subLocation);   // 시/군/구
                        jsonInterest.put("disasterIndex", disasterIndex);    // 전염병 종류
                        jsonInterest.put("disasterSubName", disasterSubName);    // 전염병 또는 태풍 이름
                        for (int i = 0; i <= NUM_OF_DISASTER_LEVELS; i++) {
                            jsonInterest.put("disasterSubLevel" + i, disasterSubLevel[i]); // 선택된 알림 등급
                        }
                        jsonInterest.put("scale_min", scale_min);    // 지진 최소 규모
                        jsonInterest.put("scale_max", scale_max);    // 지진 최대 규모
                        jsonInterest.put("eq_mainLocation", eq_mainLocation);    // 지진 발생 지역 시/도
                        jsonInterest.put("eq_subLocation", eq_subLocation);      // 지진 발생 지역 시/군/구
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String strInterest = jsonInterest.toString();

                    // 값 SP에 저장
                    SharedPreferences.Editor editor = spref.edit();
                    editor.putInt("size", interestSize + 1); // 사이즈 값 갱신
                    editor.putString(nickname, strInterest); // 관심분야 json String
                    editor.apply();

                    // Firebase에 저장
                    putFirestoreInt(nickname, strInterest);

                    Toast.makeText(getApplicationContext(), "관심분야가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    // 재난 라디오 버튼 ArrayList 초기화
    private void initDisasterRadios() {
        int disasterRadioID;
        for(int i=1; i<=NUM_OF_DISASTER; i++) {
            disasterRadioID = getResources().getIdentifier("radio_disaster_" + i, "id", getPackageName());
            RadioButton radioButton = findViewById(disasterRadioID);
            disasterRadios.add(radioButton);
        }
    }

    // 모든 재난 라디오 버튼 해제
    private void uncheckDisasterRadios() {
        if (disasterRadios != null) {
            for(RadioButton radioButton : disasterRadios) {
                radioButton.setTextColor(getResources().getColor(R.color.colorWhite));
                radioButton.setBackground(getResources().getDrawable(R.drawable.radio_button_unselected));
            }
        }
    }

    // 선택된 재난 라디오 버튼 체크
    private void checkDisasterRadio(int radioNum) {
        if (disasterRadios != null) {
            disasterRadios.get(radioNum-1).setTextColor(getResources().getColor(R.color.colorPrimary));
            disasterRadios.get(radioNum-1).setBackground(getResources().getDrawable(R.drawable.radio_button_selected));
        }
    }

    // 레이아웃 ArrayList 초기화
    private void initDisasterLayouts() {
        int disasterLayoutID;
        for(int i=1; i<=NUM_OF_DISASTER; i++) {
            disasterLayoutID = getResources().getIdentifier("layout_interest_disaster_" + i, "id", getPackageName());
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
            disasterLayouts.get(layoutNum-1).setVisibility(View.VISIBLE);
        }
    }

    // 모든 재난의 하위 카테고리 컨텐츠 초기화 및 이벤트 리스너 설정
    // 전염병
    private void initDisaster1SubContents() {
        // 전염병 종류 선택 버튼
        disaster1_nameBtn = findViewById(R.id.btn_interest_disaster_1_0);
        final String[] disaster1Array = getResources().getStringArray(R.array.disaster_1_0);
        disaster1_nameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("전염병 종류").setItems(disaster1Array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disaster1_nameBtn.setText(disaster1Array[which]);
                        disasterSubName = "COVID-19";
                    }
                }).show();
            }
        });

        // 전염병 알림 등급 선택 체크박스
        disaster1_level1_chk = findViewById(R.id.chk_disaster_1_1_1);
        disaster1_level2_chk = findViewById(R.id.chk_disaster_1_1_2);
        disaster1_level3_chk = findViewById(R.id.chk_disaster_1_1_3);
        disaster1_level9_chk = findViewById(R.id.chk_disaster_1_1_9);
        disaster1_level1_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster1_level1_chk, 1);
            }
        }) ;
        disaster1_level2_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster1_level2_chk, 2);
            }
        }) ;
        disaster1_level3_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster1_level3_chk, 3);
            }
        }) ;
        disaster1_level9_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster1_level9_chk, 9);
            }
        }) ;
    }
    // 지진
    private void initDisaster2SubContents() {
        // 규모 선택 레이아웃
        disaster2_scale_text = findViewById(R.id.text_disaster_2_scale);
        disaster2_scale_layout = findViewById(R.id.layout_disaster_2_scale);
        disaster2_scale_text.setVisibility(View.GONE);
        disaster2_scale_layout.setVisibility(View.GONE);
        // 지진 알림 종류 선택 체크박스
        disaster2_level1_chk = findViewById(R.id.chk_disaster_2_1_1);
        disaster2_level9_chk = findViewById(R.id.chk_disaster_2_1_9);
        disaster2_level1_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster2_level1_chk, 1);
                if (disaster2_level1_chk.isChecked()) {
                    disaster2_scale_text.setVisibility(View.VISIBLE);
                    disaster2_scale_layout.setVisibility(View.VISIBLE);
                } else {
                    disaster2_scale_text.setVisibility(View.GONE);
                    disaster2_scale_layout.setVisibility(View.GONE);
                }
            }
        }) ;
        disaster2_level9_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster2_level9_chk, 9);
            }
        }) ;

        // 지진 규모 선택
        scale_min = 1.0;
        scale_max = 10.0;
        disaster2_scale_min_text = findViewById(R.id.text_disaster_scale_min);
        disaster2_scale_max_text = findViewById(R.id.text_disaster_scale_max);
        disaster2_scale_slider = findViewById(R.id.range_disaster_scale);
        disaster2_scale_slider.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        disaster2_scale_slider.setOnThumbValueChangeListener(new MultiSlider.SimpleChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int
                    thumbIndex, int value) {
                if (thumbIndex == 0) {
                    scale_min = (double)value/10.0;
                    disaster2_scale_min_text.setText(String.valueOf(scale_min));
                } else {
                    scale_max = (double)value/10.0;
                    disaster2_scale_max_text.setText(String.valueOf(scale_max));
                }
            }
        });

        // 지진 관측 지역 선택
        eq_mainLocation = "전체";
        eq_subLocation = "전체";
        disaster2_mainLocationBtn = findViewById(R.id.btn_interest_disaster_2_1_0_main_location);
        disaster2_subLocationBtn = findViewById(R.id.btn_interest_disaster_2_1_0_sub_location);
        disaster2_mainLocationBtn.setText("전체");
        disaster2_subLocationBtn.setText("전체");
        disaster2_subLocationBtn.setEnabled(false);
        disaster2_mainLocation = getResources().getStringArray(R.array.location_short);
        disaster2_mainLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("시/도").setItems(disaster2_mainLocation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disaster2_mainLocationBtn.setText(disaster2_mainLocation[which]);
                        eq_mainLocation = disaster2_mainLocation[which];

                        disaster2_subLocationBtn.setText("전체");
                        int disaster2_subLocationID = getResources().getIdentifier("location_" + which, "array", getPackageName());
                        disaster2_subLocation = getResources().getStringArray(disaster2_subLocationID);
                        disaster2_subLocationBtn.setEnabled(true);
                    }
                }).show();
            }
        });
        disaster2_subLocationBtn.setEnabled(false);
        disaster2_subLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("시/군/구").setItems(disaster2_subLocation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disaster2_subLocationBtn.setText(disaster2_subLocation[which]);
                        eq_subLocation = disaster2_subLocation[which];
                    }
                }).show();
            }
        });
    }
    // 미세먼지
    private void initDisaster3SubContents() {
        // 알림 종류 선택 체크박스
        disaster3_level1_chk = findViewById(R.id.chk_disaster_3_1);
        disaster3_level2_chk = findViewById(R.id.chk_disaster_3_2);
        disaster3_level9_chk = findViewById(R.id.chk_disaster_3_9);
        disaster3_level1_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster3_level1_chk, 1);
            }
        }) ;
        disaster3_level2_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster3_level2_chk, 2);
            }
        }) ;
        disaster3_level9_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster3_level9_chk, 9);
            }
        }) ;
    }
    // 태풍
    private void initDisaster4SubContents() {
        // 태풍 이름 선택
        disaster4_namebtn = findViewById(R.id.btn_interest_disaster_4_1);
        final String[] disaster4Array = getResources().getStringArray(R.array.disaster_4_0);
        disaster4_namebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("태풍 선택").setItems(disaster4Array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disaster4_namebtn.setText(disaster4Array[which]);
                        disasterSubName = disaster4Array[which];
                    }
                }).show();
            }
        });

        // 알림 종류 선택 체크박스
        disaster4_level1_chk = findViewById(R.id.chk_disaster_4_0_1);
        disaster4_level2_chk = findViewById(R.id.chk_disaster_4_0_2);
        disaster4_level9_chk = findViewById(R.id.chk_disaster_4_0_9);
        disaster4_level1_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster4_level1_chk, 1);
            }
        }) ;
        disaster4_level2_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster4_level2_chk, 2);
            }
        }) ;
        disaster4_level9_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster4_level9_chk, 9);
            }
        }) ;
    }
    // 홍수
    private void initDisaster5SubContents() {
        // 알림 종류 선택 체크박스
        disaster5_level1_chk = findViewById(R.id.chk_disaster_5_1);
        disaster5_level2_chk = findViewById(R.id.chk_disaster_5_2);
        disaster5_level9_chk = findViewById(R.id.chk_disaster_5_9);
        disaster5_level1_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster5_level1_chk, 1);
            }
        }) ;
        disaster5_level2_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster5_level2_chk, 2);
            }
        }) ;
        disaster5_level9_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster5_level9_chk, 9);
            }
        }) ;
    }
    // 그 외
    private void initDisaster6SubContents() {
        // 재난 종류 선택 라디오 버튼
        disaster_etc_radioGroup = findViewById(R.id.radioGroup_disaster_etc);
        disaster_etc_1 = findViewById(R.id.radio_disaster_etc_1);
        disaster_etc_2 = findViewById(R.id.radio_disaster_etc_2);
        disaster_etc_3 = findViewById(R.id.radio_disaster_etc_3);
        disaster_etc_4 = findViewById(R.id.radio_disaster_etc_4);
        disaster_etc_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 모든 하위 라디오 버튼 체크 해제
                disaster_etc_1.setTextColor(getResources().getColor(R.color.colorWhite));
                disaster_etc_1.setBackground(getResources().getDrawable(R.drawable.radio_button_unselected));
                disaster_etc_2.setTextColor(getResources().getColor(R.color.colorWhite));
                disaster_etc_2.setBackground(getResources().getDrawable(R.drawable.radio_button_unselected));
                disaster_etc_3.setTextColor(getResources().getColor(R.color.colorWhite));
                disaster_etc_3.setBackground(getResources().getDrawable(R.drawable.radio_button_unselected));
                disaster_etc_4.setTextColor(getResources().getColor(R.color.colorWhite));
                disaster_etc_4.setBackground(getResources().getDrawable(R.drawable.radio_button_unselected));
                if (checkedId == -1) return;    // clearCheck() 호출로 이벤트가 발생한 경우 처리
                else if(checkedId == R.id.radio_disaster_etc_1) {
                    disasterIndex = 6;
                } else if(checkedId == R.id.radio_disaster_etc_2) {
                    disasterIndex = 7;
                } else if(checkedId == R.id.radio_disaster_etc_3) {
                    disasterIndex = 8;
                } else if (checkedId == R.id.radio_disaster_etc_4) {
                    disasterIndex = 9;
                } else {
                    disasterIndex = 0;
                }
                // 선택된 라디오 버튼 활성화
                RadioButton selectedBtn = findViewById(checkedId);
                selectedBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
                selectedBtn.setBackground(getResources().getDrawable(R.drawable.radio_button_selected));
            }
        });
        // 알림 종류 선택 체크박스
        disaster_etc_level1_chk = findViewById(R.id.chk_disaster_etc_0_1);
        disaster_etc_level2_chk = findViewById(R.id.chk_disaster_etc_0_2);
        disaster_etc_level9_chk = findViewById(R.id.chk_disaster_etc_0_9);
        disaster_etc_level1_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster_etc_level1_chk, 1);
            }
        }) ;
        disaster_etc_level2_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster_etc_level2_chk, 2);
            }
        }) ;
        disaster_etc_level9_chk.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxClickListener(disaster_etc_level9_chk, 9);
            }
        }) ;
    }

    // 하위 카테고리 선택된 조건들 초기화
    private void clearSubConditions() {
        // 이름
        // 공통
        disasterSubName = "전체";
        // 전염병
        disaster1_nameBtn.setText("전체");
        // 태풍
        disaster4_namebtn.setText("전체");

        // 알림 등급
        // 공통
        disasterSubLevel = new boolean[10];
        Arrays.fill(disasterSubLevel, false);
        // 전염병
        disaster1_level1_chk.setChecked(false);
        disaster1_level1_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        disaster1_level2_chk.setChecked(false);
        disaster1_level2_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        disaster1_level3_chk.setChecked(false);
        disaster1_level3_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        disaster1_level9_chk.setChecked(false);
        disaster1_level9_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        // 지진
        disaster2_level1_chk.setChecked(false);
        disaster2_level1_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        disaster2_level9_chk.setChecked(false);
        disaster2_level9_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        // 미세먼지
        disaster3_level1_chk.setChecked(false);
        disaster3_level1_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        disaster3_level2_chk.setChecked(false);
        disaster3_level2_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        disaster3_level9_chk.setChecked(false);
        disaster3_level9_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        // 태풍
        disaster4_level1_chk.setChecked(false);
        disaster4_level1_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        disaster4_level2_chk.setChecked(false);
        disaster4_level2_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        disaster4_level9_chk.setChecked(false);
        disaster4_level9_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        // 홍수
        disaster5_level1_chk.setChecked(false);
        disaster5_level1_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        disaster5_level2_chk.setChecked(false);
        disaster5_level2_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        disaster5_level9_chk.setChecked(false);
        disaster5_level9_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        // 그 외
        disaster_etc_level1_chk.setChecked(false);
        disaster_etc_level1_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        disaster_etc_level2_chk.setChecked(false);
        disaster_etc_level2_chk.setTextColor(getResources().getColor(R.color.colorWhite));
        disaster_etc_level9_chk.setChecked(false);
        disaster_etc_level9_chk.setTextColor(getResources().getColor(R.color.colorWhite));

        // 지진 규모 선택
        scale_min = 1.0;
        scale_max = 10.0;
        disaster2_scale_min_text.setText("1.0");
        disaster2_scale_max_text.setText("10.0");
        disaster2_scale_text.setVisibility(View.GONE);
        disaster2_scale_layout.setVisibility(View.GONE);

        // 지진 관측 지역 선택
        eq_mainLocation = "전체";
        eq_subLocation = "전체";
        disaster2_mainLocationBtn.setText("전체");
        disaster2_subLocationBtn.setText("전체");
        disaster2_subLocationBtn.setEnabled(false);

        // 그 외 카테고리
        disaster_etc_radioGroup.clearCheck();
    }

    private boolean isCheckedSubConditions() {
        boolean ret = false;

        // 알림 등급이 하나라도 선택되었는지 확인
        for (boolean isCheckedLevel : disasterSubLevel) {
            ret = ret || isCheckedLevel;
        }

        return ret;
    }

    // 조건 선택되었을때 호출
    private void checkCondition(int checkedCondition) {
        isChecked |= checkedCondition;
    }
    private void uncheckCondition(int uncheckedCondition) {
        isChecked &= (~uncheckedCondition);
    }

    // 알림 등급을 선택하는 모든 체크박스의 이벤트 리스너
    private void checkBoxClickListener(CheckBox checkBox, int num) {
        if (checkBox.isChecked()) {
            checkBox.setTextColor(getResources().getColor(R.color.colorPrimary));
            disasterSubLevel[num] = true;
        } else {
            checkBox.setTextColor(getResources().getColor(R.color.colorWhite));
            disasterSubLevel[num] = false;
        }
    }

    // Firebase에 Interest 추가
    private void putFirestoreInt(String nickname, String strInterest) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Log.e(TAG, "Firebase Auth Error!");
        } else {
            Map<String, Object> interestMap = new HashMap<>();
            interestMap.put("nickname", nickname);
            interestMap.put("interest_string", strInterest);
            db.collection("userdata")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("interest")
                    .add(interestMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
    }
}
