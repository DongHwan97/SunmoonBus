package com.example.sunmoonbus;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GpsActivity extends AppCompatActivity {

    private TextView txtLat;
    private TextView txtLon;
    private Gps gps;
    AccountDBConnect driverDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        txtLat=(TextView)findViewById(R.id.latitude);
        txtLon=(TextView)findViewById(R.id.longitude);
        driverDB = new AccountDBConnect("User");
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
}