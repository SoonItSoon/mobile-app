package com.app.soonitsoon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


import com.app.soonitsoon.safety.SafetyActivity;

public class Alert extends AppCompatActivity {
    public void sendSafetyAlert(int dangerNum) {
        final String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";

        createNotificationChannel();

        Bitmap mLargeIconForNoti = BitmapFactory.decodeResource(getResources(), R.drawable.ic_alert_large);

        PendingIntent mPendingIntent = PendingIntent.getActivity(SafetyActivity.context, 0
                , new Intent(getApplicationContext(), SafetyActivity.class)
                , PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SafetyActivity.this, "Alert")
                .setSmallIcon(R.drawable.ic_alert)
                .setContentTitle("!!확진자 접촉 의심 지역 총 " + dangerNum + "개 방문!!")
                .setContentText("클릭하여 방문 확인을 해주세요")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setColor(Color.BLACK)
                .setLargeIcon(mLargeIconForNoti)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setGroup(GROUP_KEY_WORK_EMAIL)
                .setContentIntent(mPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        for (int j = 0; j < 5; j++) {
            final int i = j;
            mNotificationManager.notify(i, mBuilder.build());

        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Alert", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
