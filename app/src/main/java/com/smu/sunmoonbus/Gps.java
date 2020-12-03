package com.smu.sunmoonbus;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class Gps extends Service implements LocationListener {
    private Context mContext;
    private Activity mActivity;

    private String onBus;

    //사용가능한 위치정보제공자
    private List<String> enabledProviders;

    private static final String[] permission = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 7;//10미터
    private static final long MIN_TIME_BW_UPDATES = 3000;//5초

    private LocationManager locationManager;

    public Gps(Context context, Activity activity, String onBus) {
        this.mContext = context;
        this.mActivity = activity;
        this.onBus = onBus;
        this.init();
    }

    @TargetApi(23)
    public void init() {
        while (!SunmoonUtil.checkPermission(this.mActivity, permission)) { }
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        enabledProviders = locationManager.getProviders(true);

        if (locationManager == null) {
            //사용불가
            return;
        }

        for (String provider : enabledProviders) {

            locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }

    }//gps end

    public void onPauseSend() {
        locationManager.removeUpdates(this);
    }

    public void onResumeSend() {
        while (!SunmoonUtil.checkPermission(this.mActivity, permission)) { }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
    }

    public void stopUsingGPS(){//GPS사용중지
        if(locationManager!=null){
            locationManager.removeUpdates(Gps.this);
        }
    }

    @Override
    public IBinder onBind(Intent arg0){
    return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        ShuttleDBConnect.sendLocation(onBus, location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        while (!SunmoonUtil.checkPermission(this.mActivity, permission)) { }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }
}
