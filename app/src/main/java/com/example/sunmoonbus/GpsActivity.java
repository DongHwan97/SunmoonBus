package com.example.sunmoonbus;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GpsActivity extends AppCompatActivity {
    private Button btnShowLocation, stopBtn;
    private TextView txtLat;
    private TextView txtLon;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION=1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION=1001;

    private boolean isPermission=false;
    private Gps gps;
    AccountDBConnect driverDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        btnShowLocation=(Button)findViewById(R.id.button);

        txtLat=(TextView)findViewById(R.id.latitude);
        txtLon=(TextView)findViewById(R.id.longitude);
        driverDB = new AccountDBConnect("User");
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPermission){
                    callPermission();
                    return;
                }
                gps= new Gps(GpsActivity.this);
                if(gps.isGetLocation()){
                    double latitude = gps.getLat();
                    double longitude = gps.getLon();

                    txtLat.setText(String.valueOf(latitude));
                    txtLon.setText(String.valueOf(longitude));
                    //1. 탄버스의 태그에 2. 경도와 위도를 저장
                    ShuttleDBConnect.myRef1.child("49EDB800").child("latitude").setValue(gps.getLat());
                    ShuttleDBConnect.myRef1.child("49EDB800").child("longitude").setValue(gps.getLon());
//여기에 기사정보를 올림
                }else{
                    gps.showSettingAlert();
                }
            }
        });

    }

    private void callPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_ACCESS_FINE_LOCATION);
        }else if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSIONS_ACCESS_COARSE_LOCATION);
        }else{
            isPermission=true;
        }
    }

}