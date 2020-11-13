package com.app.soonitsoon;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private final static String TAG = BootReceiver.class.getSimpleName();

    // BroadcastReceiver를 상속
    @Override
    public void onReceive(final Context context, Intent intent) {
        // 전달 받은 Broadcast의 값을 가져오기
        // androidmanifest.xml에 정의한 인텐트 필터를 받아 옴
        String action = intent.getAction();

        // 전달된 값이 '부팅완료' 인 경우
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            Log.e(TAG, "action = " + action);

            // 3초 후에 백그라운드 서비스 동작 시작
            new Handler().postDelayed(new Runnable() {
                // 3초 후에 실행
                @Override public void run() {
                    //Toast.makeText(context, "-- BootReceiver.onReceive", Toast.LENGTH_LONG).show();

                    // BackgroundService
                    Intent serviceLauncher = new Intent(context, BackgroundService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceLauncher);
                    } else {
                        context.startService(serviceLauncher);
                    }
                }
            }, 3000);
        }
    }

    public static boolean isServiceRunning(Context context, Class serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.e(TAG,"ServiceRunning? = "+true);
                return true;
            }
        }
        Log.e(TAG,"ServiceRunning? = "+ false);
        return false;
    }
}