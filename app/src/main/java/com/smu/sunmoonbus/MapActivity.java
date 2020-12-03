package com.smu.sunmoonbus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


public class MapActivity extends AppCompatActivity {
    MapView mapView;
    Button refreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        (refreshBtn = findViewById(R.id.refreshButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("SeletedBusLa", 36.815291);
        double longitude = intent.getDoubleExtra("SeletedBusLo",127.113840);
        int zoomLevel = intent.getIntExtra("Zoom",6);

        mapView = new MapView(this);
        MapPoint centerMapPoint = MapPoint.mapPointWithGeoCoord( latitude,longitude);//천안시청이 중심
        mapView.setMapCenterPoint(centerMapPoint,false);
        mapView.setZoomLevel(zoomLevel,true);

        ((ViewGroup) findViewById(R.id.map_view)).addView(mapView);

        reMarker.setDaemon(true);
        reMarker.start();

        /*MapPolyline polyline = new MapPolyline();
                polyline.setLineColor(Color.argb(128,000,000,000));
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.80031814202122, 127.07105758743238));//터미널
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.820639740751496, 127.15639125486992));//천안역
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.81025564697209, 127.14669475486944));//아산역

        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.79802914358235, 127.07752490555227));//셔틀장
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.797553213132375, 127.08718500022104));//셔틀장
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.79601742165512, 127.08753371029236));//셔틀장
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.80043855626985, 127.09968044444324));//셔틀장
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.7924337884605, 127.10159834983548));//셔틀장
        mapView.addPolyline(polyline);*/


    }

    Thread reMarker = new Thread(){
        @Override
        public void run() {
            while(true) {
                refresh();
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    };

    public void refresh(){
        if (0 == refreshBtn.getRotation()) {
            new Thread() {
                @Override
                public void run() {
                    refreshBtn.setRotation(10);
                    while(refreshBtn.getRotation() != 0) {
                        try {
                            refreshBtn.setRotation((refreshBtn.getRotation() + 5) % 360);
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    refreshBtn.setRotation(0);
                }
            }.start();

            mapView.removeAllPOIItems();
            for (BusInfo businfo : ShuttleDBConnect.busInfo.values()) {
                mapView.addPOIItem(openMarker(businfo));
            }
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reMarker.interrupt();
        finish();
    }

    public MapPOIItem openMarker(BusInfo businfo){//마커생성
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(businfo.destination + " " + businfo.userCount+"/45");
        marker.setTag(1);

        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(businfo.latitude,businfo.longitude);
        marker.setMapPoint(mapPoint);

        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomImageResourceId(R.drawable.bus_marker);//기본 마커 이미지설정
        if (!businfo.moving) {
            marker.setCustomImageResourceId(R.drawable.bus_markerno);
        }

        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomSelectedImageResourceId(R.drawable.bus_markerclick);//선택했을때 마커이미지설정

        return marker;
    }

}