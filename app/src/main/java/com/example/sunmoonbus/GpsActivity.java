package com.example.sunmoonbus;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class GpsActivity extends AppCompatActivity {

    TextView tv;
    ToggleButton tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);


        tv = (TextView) findViewById(R.id.textView2);
        tv.setText("위치정보 미수신중");

        tb = (ToggleButton)findViewById(R.id.toggle1);

        // LocationManager 객체를 얻어온다
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);// 2. LocationManager 를 통해서 원하는 제공자의 리스너 등록

        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(tb.isChecked()){
                        tv.setText("수신중..");
                        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                                100, // 통지사이의 최소 시간간격 (miliSecond)
                                1, // 통지사이의 최소 변경거리 (m)
                                mLocationListener);
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                                100, // 통지사이의 최소 시간간격 (miliSecond)
                                1, // 통지사이의 최소 변경거리 (m)
                                mLocationListener);
                    }else{
                        tv.setText("위치정보 미수신중");
                        lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
                    }
                }catch(SecurityException ex){
                }
            }
        });
    } // end of onCreate

    public void openMapView(){//버스들 위치받아올 배열필요할듯? 차례대로 버스 마커표시해줌
        double[] latitude = new double[0];
        double[] longitude = new double[0];
        MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        MapPoint centerMapPoint = MapPoint.mapPointWithGeoCoord( 36.815291,127.113840);//천안시청이 중심
        mapView.setMapCenterPoint(centerMapPoint,true);
        mapView.zoomIn(true);
        mapView.zoomOut(true);
        mapView.setZoomLevel(6,true);//6이 전체지도보기에 가장적합하다
        mapViewContainer.addView(mapView);//맵추가
        for(int i=0;i<latitude.length;i++)
        {
            openMarker(latitude[i],longitude[i],mapView);
        }
    }

    public void openMarker(double latitude, double longitude, MapView mapView){
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("현 위치");
        marker.setTag(1);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude,longitude);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomImageResourceId(R.drawable.bus_marker);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);

    }




    private final LocationListener mLocationListener = new LocationListener() {//기사꺼만있음 기사가 디비로 자기 좌표보냄
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            openMapView();
            tv.setText("위도 : " + longitude + "\n경도 : " + latitude);
        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }
        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };
} // end of class