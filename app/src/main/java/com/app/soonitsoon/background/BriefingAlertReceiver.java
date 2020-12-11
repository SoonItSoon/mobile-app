package com.app.soonitsoon.background;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.soonitsoon.Alert;

public class BriefingAlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Alert briefingAlert = new Alert(context, (Application) context.getApplicationContext());
        briefingAlert.sendBriefingAlert();
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
