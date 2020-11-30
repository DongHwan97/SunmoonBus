package com.example.sunmoonbus;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class Gps extends Service implements LocationListener {
    private final Context mContext;

    boolean isGPSEnabled = false;//gps사용가능
    boolean isNetworkEnabled = false;//네트워크사용가능
    boolean isGetLocation = false;//위치정보를얻어올수있는지

    Location location;//위치
    double lat, lon;//경도,위도

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES=10;//10미터
    private static final long MIN_TIME_BW_UPDATES=1000*10*1;//10초
    private LocationManager locationManager;

        public Gps(Context context){
            this.mContext=context;
            getLocation();
        }

        @TargetApi(23)
        public Location getLocation(){
        if(Build.VERSION.SDK_INT>=23&& ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED&& ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                return null;
        }try{
            locationManager=(LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!isGPSEnabled&&!isNetworkEnabled){
                //사용불가
                return null;
            }else{
                this.isGetLocation=true;
                if(isNetworkEnabled){//네트워크사용
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    if(locationManager!=null){
                        location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location!=null){
                            lat=location.getLatitude();
                            lon=location.getLongitude();
                        }
                    }
                }
                if(isGPSEnabled){//GPS사용
                    if(location==null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                        if(locationManager!=null){
                            location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(location!=null){
                                lat=location.getLatitude();
                                lon=location.getLongitude();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }//gps end

    public void stopUsingGPS(){//GPS사용중지
        if(locationManager!=null){
            locationManager.removeUpdates(Gps.this);
        }
    }

    public double getLat(){//위도
        if(location!=null){
            lat=location.getLatitude();
        }
        return lat;
    }

    public double getLon(){//경도
        if(location!=null){
            lon=location.getLongitude();
        }
        return lon;
    }

    public boolean isGetLocation(){
    return this.isGetLocation;
    }

    public void showSettingAlert(){//GPS설정 안 했을때
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("gps사용유무셋팅");
        alertDialog.setMessage("gps설정이 필요합니다. 설정창으로 가시겠습니까?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public IBinder onBind(Intent arg0){
    return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }
}
