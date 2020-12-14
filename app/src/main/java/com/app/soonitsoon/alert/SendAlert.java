package com.app.soonitsoon.alert;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


import com.app.soonitsoon.R;
import com.app.soonitsoon.briefing.BriefingActivity;
import com.app.soonitsoon.interest.InterestActivity;
import com.app.soonitsoon.safety.SafetyActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SendAlert {
    private Context context;
    private Application application;

    public SendAlert(Context context, Application application) {
        this.context = context;
        this.application = application;
    }

    public void sendSafetyAlert(int dangerNum, int alertNum) {
        final String GROUP_KEY_SAFETY = "soonitsoon.safety";

        createNotificationChannel();

        Bitmap mLargeIconForNoti = BitmapFactory.decodeResource(application.getResources(), R.drawable.ic_alert_large);

        PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0
                , new Intent(application.getApplicationContext(), SafetyActivity.class)
                , PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "Alert")
                .setSmallIcon(R.drawable.ic_alert2)
                .setContentTitle("!!확진자 접촉 의심 지역 총 " + dangerNum + "개 방문!!")
                .setContentText("클릭하여 방문 확인을 해주세요")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(mLargeIconForNoti)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setGroup(GROUP_KEY_SAFETY)
                .setContentIntent(mPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);

        mNotificationManager.notify(alertNum, mBuilder.build());

    }

    public void sendInterestAlert(int index, String nickname, int numOfNew) {
        final String GROUP_KEY_INTEREST = "soonitsoon.interest";

        createNotificationChannel();

        Bitmap mLargeIconForNoti = BitmapFactory.decodeResource(application.getResources(), R.drawable.ic_alert_large);

        Intent intent = new Intent(application, InterestActivity.class);
        intent.putExtra("nickname", nickname);

        PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0
                , intent
                , PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "Alert")
                .setSmallIcon(R.drawable.ic_noti_inter)
                .setContentTitle(nickname + "에 대한 새로운 " + numOfNew + "개의 알림!!")
                .setContentText("클릭하여 확인을 해주세요")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(mLargeIconForNoti)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setGroup(GROUP_KEY_INTEREST)
                .setContentIntent(mPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);

        mNotificationManager.notify(index, mBuilder.build());
    }

    public void sendBriefingAlert() {
        final String GROUP_KEY_BRIEFING = "soonitsoon.briefing";

        createNotificationChannel();

        Bitmap mLargeIconForNoti = BitmapFactory.decodeResource(application.getResources(), R.drawable.ic_alert_large);

        PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0
                , new Intent(application.getApplicationContext(), BriefingActivity.class)
                , PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "Alert")
                .setSmallIcon(R.drawable.ic_noti_cal)
                .setContentTitle("오늘 하루 브리핑이 도착했어요!")
                .setContentText("클릭하여 확인을 해주세요 >_O")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(mLargeIconForNoti)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setGroup(GROUP_KEY_BRIEFING)
                .setContentIntent(mPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());

    }

    // API 26 이상일 시 알람 채널 생성
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SoonItSoon";
            String description = "SoonItSoon";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Alert", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = application.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
