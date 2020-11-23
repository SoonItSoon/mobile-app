package com.app.soonitsoon;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.soonitsoon.interest.InterestActivity;
import com.app.soonitsoon.message.MessageActivity;
import com.app.soonitsoon.timeline.TimelineActivity;
import com.google.android.material.navigation.NavigationView;

public class Test2Activity extends AppCompatActivity {
    public static Activity activity;
    private DrawerLayout mDrawerLayout;
    private Context context = this;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        activity = this;

        MainActivity.killMainActivity();
        TimelineActivity.killTimelineActivity();
        InterestActivity.killInterestActivity();
        MessageActivity.killMessageActivity();

        // 상단 바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_icon); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = findViewById(R.id.test2_layout);

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
                    startActivity(MainActivity.interestIntent);
                }
                else if(id == R.id.nav_item_test2){
                    Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.nav_item_message){
                    startActivity(MainActivity.messageIntent);
                }

                return true;
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

    public static void killTest2Activity() {
        if(activity != null)
            activity.finish();
    }
}
