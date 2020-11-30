package com.app.soonitsoon.safety;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

public class RequestHttpConnection {
    public static String[] request(String address){
        String jsonResult = "";
        String connectionResult = "";
        BufferedReader br = null;
        StringBuffer sb = null;
        String[] result = new String[2];

        try {
            // url Connection
            URL url = new URL("https://dapi.kakao.com/v2/local/search/keyword.json?query=" + address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "KakaoAK 73d1a33c0c8c92af9e0d7215b968a64c");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            sb = new StringBuffer();

            while ((jsonResult = br.readLine()) != null)
                sb.append(jsonResult);

            connectionResult = sb.toString();                               // connection 전체 결과

            JSONObject jsonObject = new JSONObject(connectionResult);
            String strDocuments = jsonObject.get("documents").toString();   // Documents 에 있는 결과
            JSONArray jsonDocuments = new JSONArray(strDocuments);

            // 검색 결과가 없으면 (0, 0)
            if (jsonDocuments.length() == 0) {
                result[0] = "0";
                result[1] = "0";
            }
            else {
                String jsonLocX = jsonDocuments.getJSONObject(0).get("x").toString();
                String jsonLocY = jsonDocuments.getJSONObject(0).get("y").toString();

                result[0] = jsonLocX;
                result[1] = jsonLocY;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
