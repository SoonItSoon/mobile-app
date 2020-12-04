package com.app.soonitsoon.timeline;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UpdateTimeline {
    private Context context;
    private Application application;

    public UpdateTimeline(Context context, Application application) {
        this.context = context;
        this.application = application;
    }

    public void excute(String date, String time, int dangerLevel) {
        GetTimeline getTimeline = new GetTimeline(application);
        JSONObject jsonTLList = getTimeline.excute(date);
        String strTLUnit = "";

        try {
            strTLUnit = jsonTLList.getString(time);             // {"latitude":37.4945484,"longitude":126.9596974,"danger":0}
            JSONObject jsonTLUnit = new JSONObject(strTLUnit);
            jsonTLUnit.put("danger", dangerLevel);
            strTLUnit = jsonTLUnit.toString();
            jsonTLList.put(time, strTLUnit);             // {"12:28:48" : {"latitude":37.4945484,"longitude":126.9596974,"danger":0}}
            String strTLList = jsonTLList.toString();

            FileOutputStream outputStream;
            outputStream = application.openFileOutput(date+".json", Context.MODE_PRIVATE);
            outputStream.write(strTLList.getBytes());
            outputStream.close();
        } catch (JSONException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
