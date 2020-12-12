package com.app.soonitsoon.briefing;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.soonitsoon.alert.SendAlert;

public class CheckBriefingTime {
    private static Context context;
    private static Application application;

    public CheckBriefingTime(Context context, Application application) {
        this.context = context;
        this.application = application;
    }

    // 브리핑 알림
    public void sendBriefing(String date) {
        SharedPreferences spref = context.getSharedPreferences("PrevData", Context.MODE_PRIVATE);
        String briefingDate = spref.getString("BriefingDate", "");

//        if (!briefingDate.equals(date)) {
//            SharedPreferences.Editor editor = spref.edit();
//            editor.putString("BriefingDate", date);
//            editor.apply();

            SendAlert briefingAlert = new SendAlert(context, application);
            briefingAlert.sendBriefingAlert();
//        }
    }
}
