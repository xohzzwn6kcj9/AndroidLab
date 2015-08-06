package com.example.student.lbs;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {
    public LocationService() {
    }

    LocationUtil util;
    LocationManager manager;
    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(location!=null){
                util.checkGeofence(location);
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        util = new LocationUtil(this);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try{
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 2, listener);
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 2, listener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            manager.removeUpdates(listener);
        }catch (Exception e){

        }
    }
}
