package com.example.sunmoonbus;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        final MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        MapPoint centerMapPoint = MapPoint.mapPointWithGeoCoord( 36.815291,127.113840);//천안시청이 중심
        mapView.setMapCenterPoint(centerMapPoint,true);
        mapView.zoomIn(true);
        mapView.zoomOut(true);
        mapView.setZoomLevel(6,true);
        mapViewContainer.addView(mapView);

        for (BusInfo businfo : ShuttleDBConnect.busInfo.values()) {
            openMarker(businfo, mapView);
        }

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh(mapView);
            }
        });
    }

    public void refresh(MapView mapView){
        mapView.removeAllPOIItems();
        for (BusInfo businfo : ShuttleDBConnect.busInfo.values()) {
            openMarker(businfo, mapView);
        }
    }

    public void openMarker(BusInfo businfo, MapView mapView){//마커생성
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(businfo.Destination + businfo.userCount+"/45");
        marker.setTag(1);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomImageResourceId(R.drawable.bus_marker);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(businfo.latitude,businfo.longitude);
        marker.setMapPoint(mapPoint);
        mapView.addPOIItem(marker);
    }
}