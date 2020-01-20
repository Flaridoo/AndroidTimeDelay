package com.zph.androiddelay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util {


    public static String HttpPosts(String route, String data) throws IOException
    {
        URL url = new URL(route);
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setConnectTimeout(10000);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.addRequestProperty("content-type", "application/x-www-form-urlencoded");
        httpURLConnection.addRequestProperty("accept", "application/json");
        httpURLConnection.connect();

        OutputStream os = httpURLConnection.getOutputStream();
        os.write(data.getBytes(), 0, data.length());
        os.close();

        InputStream is = httpURLConnection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String readLine = null;
        StringBuffer sb = new StringBuffer();
        while((readLine = br.readLine()) != null){
            sb.append(readLine);
        }
        br.close();
        is.close();
        httpURLConnection.disconnect();
        return sb.toString();
    }
}
