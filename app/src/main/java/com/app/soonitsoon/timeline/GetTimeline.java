package com.app.soonitsoon.timeline;

import android.app.Application;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GetTimeline {
    private Application application;

    public GetTimeline(Application application) {
        this.application = application;
    }

    public JSONObject excute(String date) {
        InputStream inputStreamTimeline;
        String stringTLList = "";
        try {
            inputStreamTimeline = application.openFileInput(date+".json");

            if (inputStreamTimeline != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStreamTimeline);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                stringTLList = bufferedReader.readLine();
                inputStreamTimeline.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Json 파일(String) -> JsonObject
        JSONObject jsonTLList = new JSONObject();
        try {
            jsonTLList = new JSONObject(stringTLList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonTLList;
    }
}
