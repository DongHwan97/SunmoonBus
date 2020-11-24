package com.example.sunmoonbus;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


public class MapActivity extends AppCompatActivity {
    Button refreshButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        refreshButton=(Button)findViewById(R.id.refreshButton);

        openMapView();
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //새로고침
            }
        });
    }


    public void openMapView(){//지도생성
        MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        MapPoint centerMapPoint = MapPoint.mapPointWithGeoCoord( 36.815291,127.113840);//천안시청이 중심
        mapView.setMapCenterPoint(centerMapPoint,true);
        mapView.zoomIn(true);
        mapView.zoomOut(true);
        mapView.setZoomLevel(6,true);
        mapViewContainer.addView(mapView);

        for (BusInfo businfo : ShuttleDBConnect.busInfo.values()) {
            openMarker(businfo.latitude, businfo.longitude, businfo.Destination,businfo.userCount, mapView);
        }
    }

    public void openMarker(double latitude, double longitude, String Destination, int userCount, MapView mapView){//마커생성
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("천안터미널행" + userCount+"/45");
        marker.setTag(1);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude,longitude);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomImageResourceId(R.drawable.bus_marker);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);
    }

}