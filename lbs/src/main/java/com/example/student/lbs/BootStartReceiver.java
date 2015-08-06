package com.example.student.lbs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootStartReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	    // TODO Auto-generated method stub
		Intent i=new Intent(context,LocationService.class);
		context.startService(i);
	}

}
