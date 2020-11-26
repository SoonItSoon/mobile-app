package com.app.soonitsoon.interest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.soonitsoon.MainActivity;
import com.app.soonitsoon.R;
import com.app.soonitsoon.SafetyActivity;
import com.app.soonitsoon.message.MessageActivity;
import com.app.soonitsoon.timeline.TimelineActivity;
import com.google.android.material.navigation.NavigationView;

public class InterestActivity extends AppCompatActivity {
    public static Activity activity;
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        activity = this;

        MainActivity.killMainActivity();
        TimelineActivity.killTimelineActivity();
        SafetyActivity.killSafetyActivity();
        MessageActivity.killMessageActivity();

        // 상단 바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_icon); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = findViewById(R.id.layout_interest);

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
                    Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.nav_item_safety){
                    startActivity(MainActivity.safetyIntent);
                }
                else if(id == R.id.nav_item_message){
                    startActivity(MainActivity.messageIntent);
                }

                return true;
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

    // 관심분야 Activity 종료 (Activity 전환 시)
    public static void killInterestActivity() {
        if(activity != null)
            activity.finish();
    }
}
