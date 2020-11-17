package com.app.soonitsoon;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GetServerInfo {

    public static String getTestData() {
        String str = "";
        try {
            URL url = new URL("http://203.253.25.184:8080/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();

            // Get the stream
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            str = builder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;
    }
}
