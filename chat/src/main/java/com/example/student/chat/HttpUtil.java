package com.example.student.chat;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by student on 2015-08-05.
 */
public final class HttpUtil {
    private static final String TAG = "chat.HttpUtil";
    private HttpUtil(){}

    public static String sendHttpPost(String serverIp, String serverPort, ArrayList<HashMap<String, String>>list) throws IOException {
        Log.d(TAG, "connecting");
        Log.d(TAG, serverIp + ":" + serverPort);
        final URL text = new URL("http://"+serverIp+":"+serverPort);
        final HttpURLConnection http = (HttpURLConnection) text.openConnection();
        http.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=euc-kr");
        http.setConnectTimeout(10000);
        http.setReadTimeout(10000);
        http.setRequestMethod("POST");
        http.setDoInput(true);
        http.setDoOutput(true);

        if(list != null && !list.isEmpty()){
            //aa=bb&cc=dd
            final StringBuilder sb = new StringBuilder();
            for(int i=0; i<list.size(); i++){
                if(i>0){
                    sb.append("&");
                }
                sb.append(list.get(i).get("key"));
                sb.append("=");
                sb.append(list.get(i).get("value"));
            }
            Log.d("HttpUtil", "write");
            Log.d("HttpUtil", sb.toString());
            final PrintWriter pw = new PrintWriter(new OutputStreamWriter(http.getOutputStream(), "euc-kr"));
            pw.write(sb.toString());
            pw.flush();
        }
        final BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream(), "euc-kr"));
        final StringBuilder lines = new StringBuilder();
        String line;
        while((line = in.readLine()) != null){
            lines.append(line);
        }
        in.close();
        Log.d("HttpUtil", "result="+lines.toString());
        return lines.toString();
    }
}
