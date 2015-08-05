package com.example.student.chat;

import android.util.Log;

import java.io.BufferedReader;
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
public class HttpUtil {
    //list : client에서 서버로 전송할 query문자열이 있다면 전달해달라..
    public static String sendHttpPost(String serverIp, String serverPort,
                                      ArrayList<HashMap<String,String>> list)
        throws Exception{

        Log.d("kkang",serverIp+":"+serverPort);

        StringBuffer sb=new StringBuffer();

        String url="http://"+serverIp+":"+serverPort;
        URL text=new URL(url);
        HttpURLConnection http=(HttpURLConnection)text.openConnection();

        http.setRequestProperty("Content-type",
                "application/x-www-form-urlencoded;charset=euc-kr");
        http.setConnectTimeout(10000);
        http.setReadTimeout(10000);
        http.setRequestMethod("POST");
        http.setDoInput(true);
        http.setDoOutput(true);

        if(list != null){
            //서버에 넘길 데이터가 있다면..
            //web이다.. query문자열 형식으로 넘겨야 한다..
            //aa=bb&cc=dd
            for(int i=0;i<list.size();i++){
                if(i != 0){
                    sb.append("&");
                }
                sb.append(list.get(i).get("key"));
                sb.append("=");
                sb.append(list.get(i).get("value"));

            }

            PrintWriter pw=new PrintWriter(new OutputStreamWriter(
                    http.getOutputStream(), "euc-kr"));
            pw.write(sb.toString());
            pw.flush();
        }

        BufferedReader in=new BufferedReader(new InputStreamReader(
                http.getInputStream(), "euc-kr"));
        sb=new StringBuffer();
        String line;
        while((line=in.readLine()) != null){
            sb.append(line);
        }
        in.close();


        return sb.toString();

    }
}
