package com.example.student.lbs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapActivity extends FragmentActivity implements
		OnMapLongClickListener {
    public static final int MODE_ADD = 0;
    public static final int MODE_VIEW = 1;// 반경 확인
    public static final int MODE_HISTORY = 2;// 경로 확인

    private int mode = MODE_ADD;

    LocationManager lm;

    GoogleMap mMap;

    LatLng currentLocation;

    ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.activity_map)).getMap();

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mMap.clear();
                drawFence();
                if(mode==MODE_HISTORY){
                    drawHistory();
                }
            }
        });



        if(mode==MODE_ADD){
            getCurrentLocation();
            setMapLocation();
            mMap.setOnMapLongClickListener(this);
        }
        else if(mode==MODE_VIEW){
            setMapLocation();
        }
        else if(mode == MODE_HISTORY){
            getCurrentLocation();
            setMapLocation();
        }
    }

    private void showToast(String message) {
        Toast t = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        t.show();
    }
    private void drawHistory(){
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.pointer);

        ArrayList<FenceLogVO> logDatas=new ArrayList<FenceLogVO>();
        //Log data select
        DBAdapter db = new DBAdapter(MapActivity.this, DBAdapter.SQL_CREATE_LOG, "location_log");
        db.open();

        //원래는 날짜별로 log 를 검색하거나 오래된 로그를 지우는 기능등이 필요하지만 여기서는 간단하게 모두다..
        Cursor cursor=db.selectTable(new String[]{"_id", "date", "fence_name", "inout", "lat", "lon"}, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                FenceLogVO logData=new FenceLogVO();
                logData.id=cursor.getInt(0);

                Long dbDate=cursor.getLong(1);
                Date d=new Date(dbDate);
                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logData.date=sdformat.format(d);

                logData.fenceName=cursor.getString(2);
                logData.inOut=cursor.getInt(3);
                logData.lat=cursor.getDouble(4);
                logData.lon=cursor.getDouble(5);

                logDatas.add(logData);


                MarkerOptions options = new MarkerOptions();
                options.title("history");
                options.snippet(logData.fenceName+":"+sdformat.format(d));
                options.position(new LatLng(logData.lat,logData.lon));
                options.icon(BitmapDescriptorFactory.fromBitmap(icon));

                mMap.addMarker(options);
            }while(cursor.moveToNext());
        }

        db.close();

    }
    private void getCurrentLocation(){
        //get Current Location of the user
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null){
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if(location == null){
            showToast("no location provider...");
            currentLocation = new LatLng(37.50, 127.04); //default location, around Yeoksam station
        }
        else{
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    private void setMapLocation(){
        if(mode == MODE_VIEW){
            FenceVO vo = (FenceVO) getIntent().getSerializableExtra("fence");
            LatLng voLocation = new LatLng(vo.getFenceLatitude(), vo.getFenceLongitude());

            CameraPosition position = new CameraPosition.Builder().target(voLocation).zoom(16f).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }
        else{
            CameraPosition position = new CameraPosition.Builder().target(currentLocation).zoom(16f).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }
    }



    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100: {// reverse geocoding
                    ContentValues values=(ContentValues)msg.obj;
                    DBAdapter db = new DBAdapter(MapActivity.this,
                            DBAdapter.SQL_CREATE_FENCE, "fence");
                    db.open();

                    db.insertTable(values);

                    db.close();

                    progressDialog.cancel();

                    Intent intent = new Intent(MapActivity.this,
                            LocationListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    finish();
                }
            }
        };
    };


    class MyGeocoderThread extends Thread {
        ContentValues values;
        LatLng latLng;

        public MyGeocoderThread(ContentValues values, LatLng latLng) {
            // TODO Auto-generated constructor stub
            this.values = values;
            this.latLng = latLng;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String address = getAddressFromLatLng(latLng);

            values.put("fence_address", address);

            Log.d("kkang","address get:"+address);

            Message msg = new Message();
            msg.what = 100;
            msg.obj = values;
            handler.sendMessage(msg);

        }
    };

    private String getAddressFromLatLng(LatLng latLng) {

        Geocoder geocoder = new Geocoder(MapActivity.this);

        List<Address> addresses = null;
        String addressText = "";
        try {
            addresses = geocoder.getFromLocation(latLng.latitude,
                    latLng.longitude, 1);
            Thread.sleep(500);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                addressText = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : address.getLocality() + " "
                        + address.getSubLocality() + " "
                        + address.getThoroughfare() + " "
                        + address.getSubThoroughfare();
                Log.d("kkang","addressText:"+addressText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return addressText;

    }

    // 반경 설정을 위한 지도 long click event 잡아내기- 반경정도 저장
    @Override
    public void onMapLongClick(final LatLng point) {
        // TODO Auto-generated method stub

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.emo_im_yelling);
        builder.setTitle("반경설정");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.dialog_location_input, null);

        builder.setView(v);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                EditText et = (EditText) v.findViewById(R.id.input_name);
                RadioGroup rg = (RadioGroup) v.findViewById(R.id.radiogroup);
                int id = rg.getCheckedRadioButtonId();
                int radius = 500;
                if (id == R.id.m1000) {
                    radius = 1000;
                } else if (id == R.id.m5000) {
                    radius = 5000;
                }

                ContentValues values = new ContentValues();
                values.put("fence_name", et.getText().toString());
                values.put("fence_latitude", point.latitude);
                values.put("fence_longitude", point.longitude);
                values.put("fence_radius", radius);

                LatLng latLng = new LatLng(point.latitude, point.longitude);
                Thread t = new MyGeocoderThread(values, latLng);
                t.start();

                progressDialog = new ProgressDialog(MapActivity.this);
                progressDialog.setTitle("wating..");
                progressDialog.setMessage("설정중입니다.");
                progressDialog.setCancelable(false);
                progressDialog.show();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void drawFence(){
        DBAdapter db = new DBAdapter(this, DBAdapter.SQL_CREATE_FENCE, "fence");
        db.open();
        Cursor cursor = db.selectTable(
                new String[]{"fence_latitude", "fence_longitude", "fence_radius"},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            LatLng latLng = new LatLng(cursor.getDouble(0), cursor.getDouble(1));
            int radius = cursor.getInt(2);

            Bitmap bitmap = getFenceBitmap(latLng, radius);
            MarkerOptions options = new MarkerOptions();
            options.position(getCoords(latLng.latitude, latLng.longitude));
            options.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

            mMap.addMarker(options);
        }
        db.close();
    }
    private static final double EARTH_RADIUS = 6378100.0;
    private int offset;

    private int convertMetersToPixels(double lat, double lng, double radiusInMeters) {
        Log.d("kkang",lat+","+lng+","+radiusInMeters);
        double lat1 = radiusInMeters / EARTH_RADIUS;
        double lng1 = radiusInMeters / (EARTH_RADIUS * Math.cos((Math.PI * lat / 180)));
        Log.d("kkang",lat1+","+lng1);
        double lat2 = lat + lat1 * 180 / Math.PI;
        double lng2 = lng + lng1 * 180 / Math.PI;
        Log.d("kkang",lat2+","+lng2);

        Point p1 = mMap.getProjection().toScreenLocation(new LatLng(lat, lng));
        Point p2 = mMap.getProjection().toScreenLocation(new LatLng(lat2, lng2));
        Log.d("kkang","p1.x - p2.x:"+(p1.x) + (p2.x));
        return Math.abs(p1.x - p2.x);
    }

    private LatLng getCoords(double lat, double lng) {

        LatLng latLng = new LatLng(lat, lng);

        Projection proj = mMap.getProjection();

        Point p = proj.toScreenLocation(latLng);
        p.set(p.x, p.y + offset);

        return proj.fromScreenLocation(p);
    }


    private Bitmap getFenceBitmap(LatLng latLng,int fence_radius) {

        // fill color
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(0x110000FF);
        paint1.setStyle(Paint.Style.FILL);

        // stroke color
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(0xFF0000FF);
        paint2.setStyle(Paint.Style.STROKE);

        // circle radius - 200 meters

        int radius = offset = convertMetersToPixels(latLng.latitude, latLng.longitude, fence_radius);
        Log.d("kkang","convert radius:"+radius);

        // create empty bitmap
        Bitmap b = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);


        c.drawCircle(radius, radius, radius, paint1);
        c.drawCircle(radius, radius, radius, paint2);

        return b;
    }


}
