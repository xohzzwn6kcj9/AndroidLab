package com.example.student.lbs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;


public class LocationUtil {
	
	private final String DEBUG_TAG = "kkang";

	private DBAdapter dbAdapter;

	private Context context;

	private Location preLocation = null;

	public LocationUtil(Context context) {
		this.context = context;
		setFenceList();
	}

	/*********************************************************************
	 * GEOFENCE
	 ********************************************************************/

	ArrayList<FenceVO> fenceList = null;

	// TO DO : database (if fence is changed, values must be set.
	public void setFenceList() {
		fenceList = new ArrayList<FenceVO>();
		dbAdapter = new DBAdapter(context, DBAdapter.SQL_CREATE_FENCE, "fence");
		dbAdapter.open();

		Cursor cursor = dbAdapter.selectTable(new String[] { "_id",
				"fence_name", "fence_latitude", "fence_longitude",
				"fence_radius" }, null, null, null, null, null);

        Log.d("kkang","11111111111111:"+cursor.getCount());
		if (cursor.moveToFirst()) {

			do {

				FenceVO f = new FenceVO();
				f.setFenceNo(cursor.getInt(0));
				f.setFenceName(cursor.getString(1));
				f.setFenceLatitude(cursor.getDouble(2));
				f.setFenceLongitude(cursor.getDouble(3));
				f.setFenceRadius(cursor.getInt(4));
				fenceList.add(f);
				Log.d("kkang", "fence:" + f.getFenceName());
			} while (cursor.moveToNext());
		}
		dbAdapter.close();

	}

	public void checkGeofence(Location location) {
		Log.d(DEBUG_TAG, "checkGeofence()");

		// debugLocation(location);
		if (location == null || fenceList == null) {
			return;
		}

		try {
			if (preLocation == null) {

			} else {
				Log.d("kkang", "checkGeofence in if."+fenceList.size());
				for (int i = 0; i < fenceList.size(); i++) {
					compGeoFence(fenceList.get(i), location);

				}
			}
			preLocation = location;
		} catch (Exception e) {
		}
	}

	public void compGeoFence(FenceVO fence, Location location) {
		try {

			long fenceRadius = fence.getFenceRadius();
			double fenceLat = fence.getFenceLatitude();
			double fenceLng = fence.getFenceLongitude();
			double preLat = preLocation.getLatitude();
			double preLng = preLocation.getLongitude();
			double curLat = location.getLatitude();
			double curLng = location.getLongitude();
			Log.d("kkang","pre:"+preLat+","+preLng+"... cur:"+curLat+","+curLng+".... fence:"+fence.getFenceLatitude()+","+fence.getFenceLongitude());
			boolean isCurIn = isFenceIn(fenceRadius, fenceLat, fenceLng,
					curLat, curLng);
			boolean isPreIn = isFenceIn(fenceRadius, fenceLat, fenceLng,
					preLat, preLng);
            // IN : 현재 위치가 펜스 내부이고, 이전 위치가 펜스 외부일 때
            // OUT : 현재 위치가 펜스 외부이고, 이전 위치가 펜스 내부일 때
			if (isCurIn && !isPreIn) {
				Log.d("kkang", fence.getFenceName()+" IN");
                // $$$$$$$$$$$$$$$$$$$$$$ sms 전송
				sendSMS(fence, true);// true - in
				insertLog(fence,location,true);
			} else if (!isCurIn && isPreIn) { 
				Log.d("kkang", fence.getFenceName()+" Fence OUT");
                // $$$$$$$$$$$$$$$$$$$$$$ sms 전송
				sendSMS(fence, false);// true - out
				insertLog(fence,location,false);
			} else {
				Log.d("kkang", "NOTHING");
			}
		} catch (Exception e) {
		}
	}

	private void sendSMS(FenceVO fence, boolean inOut) {
		Log.d("kkang","sms 1");
		dbAdapter = new DBAdapter(context, DBAdapter.SQL_CREATE_SMS, "sms");
		dbAdapter.open();

		Cursor cursor = dbAdapter.selectTable(new String[] { "phone_number" },
				"ischeck=1", null, null, null, null);
		ArrayList<String> smsReceivers = new ArrayList<String>();
		if (cursor.moveToFirst()) {

			do {
				Log.d("kkang","sms 2..."+cursor.getString(0));
				smsReceivers.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		dbAdapter.close();

		SmsManager sms = SmsManager.getDefault();
        // sms 전송후 화면을 다른곳으로 넘기지 않아도 되기때문에 null 로 설정
        // PendingIntent pIntent=PendingIntent.getActivity(context, 0, new
        // Intent(context,SMSListActivity.class), 0);
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);
		Log.d("kkang","sms 3");
        //실제 폰에서는 주석 해제
        //String myNumber = telephony.getLine1Number();
        // 단순전송해서 Activiy 로 넘기기
		String msg = fence.getFenceName() + " 에 들어왔습니다.";
		if (!inOut)
			msg = fence.getFenceName() + " 에서 나갔습니다.";
		for (int i = 0; i < smsReceivers.size(); i++) {
			Log.d("kkang","send sms:"+smsReceivers.get(i));
            //실제 폰에서는 주석 해제
            //sms.sendTextMessage(smsReceivers.get(i), myNumber, msg, null, null);
		}
	}
	
	private void insertLog(FenceVO fence,Location location,boolean inOut){
		dbAdapter = new DBAdapter(context, DBAdapter.SQL_CREATE_LOG, "location_log");
		dbAdapter.open();

		ContentValues values=new ContentValues();
		values.put("date", System.currentTimeMillis());
		values.put("fence_name", fence.getFenceName());
		if(inOut)
			values.put("inout",0);
		else 
			values.put("inout", 1);
		values.put("lat", location.getLatitude());
		values.put("lon", location.getLongitude());
		dbAdapter.insertTable(values);
		dbAdapter.close();
		Log.d("kkang","db insert ok...");
	}
    //특정 위치가 특정 Fence 안에 있는지를 계산
	private boolean isFenceIn(long fenceRadius, double fenceLat,
			double fenceLng, double lat, double lng) throws Exception {
		
		if (getDistance(fenceLat, fenceLng, lat, lng) < fenceRadius) {
			return true;
		} else {
			return false;
		}
	}

	/*********************************************************************
	 * GPS Util
	 ********************************************************************/

    // 피타고라스의 정리 (빗변의 제곱 = 나머지 두 변의 각각의 제곱의 합)
    // ==> 거리의 제곱 = (두 거리의 위도간의 차이의 제곱) + (두 거리의 경도간의 차이의 제곱)
	private double getDistance(double lat_no1, double lon_no1, double lat_no2,
			double lon_no2) {
		double dis_lat_no = getLatDistance(lat_no1, lat_no2);
		double dis_lon_no = getLonDistance(lat_no1, lat_no2, lon_no1, lon_no2);
		Log.d("kkang","dis:"+Math.sqrt(dis_lat_no * dis_lat_no + dis_lon_no * dis_lon_no));
		return Math.sqrt(dis_lat_no * dis_lat_no + dis_lon_no * dis_lon_no);
		
	}

	private double getLatDistance(double lat_no1, double lat_no2) {
        // 각도는 60진법 사용. 지구를 360도로 등분한 것을 경도나 위도로 이름 붙이고, 1도는 다시 60분으로 나뉘며 1분은 다시
        // 60초로 나뉨
        // 1초 = 지구반지름 * 2 * pi / 360 / 3600 = ? m
		double degreeM = (6400000 * 2 * Math.PI) / 360; // distance(meter)
		double div = lat_no2 - lat_no1;
		if (div < 0) {
			div = div * -1;
		}
		return div * degreeM;
	}

	private double getLonDistance(double lat_no1, double lat_no2,
			double lon_no1, double lon_no2) {
		int degree = getLatDegree(lat_no1, lat_no2);
		double degreeM = (6400000 * 2 * Math.PI * Math.cos(degree)) / 360; // distance(meter)
		double div = lon_no2 - lon_no1;
		if (div < 0) {
			div = div * -1;
		}
		return div * degreeM;
	}

	private int getLatDegree(double lat_no1, double lat_no2) {
		double tmpDegree = (lat_no1 + lat_no2) / 2;
		return (int) Math.floor(tmpDegree);
	}

	public void saveLocationLog(Location location) {
		Log.d("kkang", "saveLocation()");
		try {
            // $$$$$$$$$$$$$$ db 에 로그 저장

		} catch (Exception e) {
			Log.d("kkang", "saveLocationLog ERROR...." + e.getMessage());
			e.printStackTrace();
		}
	}

}
