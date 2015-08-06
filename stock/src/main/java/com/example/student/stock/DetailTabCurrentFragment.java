package com.example.student.stock;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class DetailTabCurrentFragment extends Fragment implements OnClickListener{

	WebView webView;

	ListView lv;
	Button bt1;
	Button bt2;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
            return null;
        }
		
		LinearLayout linear= (LinearLayout)inflater.inflate(R.layout.detail_price, container, false);
		lv=(ListView)linear.findViewById(R.id.detail_price_list);
	    
		bt1=(Button)linear.findViewById(R.id.detail_price_bt1);
	    bt1.setOnClickListener(this);
	    
	    bt2=(Button)linear.findViewById(R.id.detail_price_bt2);
	    bt2.setOnClickListener(this);
	    

		webView = (WebView) linear.findViewById(R.id.detail_price_webview);

	    ArrayList<HashMap<String, String>> datas=new ArrayList<HashMap<String,String>>();
	    
	    initData(datas);

	    
	    SimpleAdapter ap=new SimpleAdapter(getActivity(),datas,R.layout.detail_pricerow,
	    		new String[]{"data1","data2","data3"},
	    		new int[]{R.id.detail_price_row_text1,R.id.detail_price_row_text2,R.id.detail_price_row_text3});
	    
	    lv.setAdapter(ap);
	    lv.setSelection(ap.getCount()/2/2);
		return linear;
	}
	private void initWebView(){
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		webView.loadUrl("file:///android_asset/test.html");
	}

	private void initData(ArrayList<HashMap<String, String>> datas){
		HashMap<String,String> map=null;
		for(int i=0;i<10;i++){
			map=new HashMap<String,String>();
			map.put("data1", "10,000");
			map.put("data2", String.valueOf(165000+i*500));
			datas.add(map);
		}
		for(int i=0;i<10;i++){
			map=new HashMap<String,String>();
			map.put("data2", String.valueOf(160000+i*500));
			map.put("data3", "15,000");
			datas.add(map);
		}
		
	}
	


	@Override
	public void onClick(View v) {
		if(v==bt1){
			lv.setVisibility(View.VISIBLE);
			webView.setVisibility(View.INVISIBLE);
		}
		else if(v == bt2){
			lv.setVisibility(View.INVISIBLE);
			webView.setVisibility(View.VISIBLE);
			initWebView();
		}

	}
}