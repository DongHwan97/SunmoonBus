package com.example.sunmoonbus;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;




public class BusActivity extends AppCompatActivity {
    Button refreshButton;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        refreshButton=(Button)findViewById(R.id.refreshButton);

        mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        MapPoint centerMapPoint = MapPoint.mapPointWithGeoCoord( 36.815291,127.113840);//천안시청이 중심
        mapView.setMapCenterPoint(centerMapPoint,true);
        mapView.setZoomLevel(4,true);
        mapViewContainer.addView(mapView);

        for (BusInfo businfo : ShuttleDBConnect.busInfo.values()) {
            mapView.addPOIItem(openMarker(businfo));

        }



        mapView.setPOIItemEventListener(new MapView.POIItemEventListener() {

            @Override
            public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
                MapPolyline polyline = new MapPolyline();
                /*polyline.setLineColor(Color.argb(128,000,000,000));
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.80031814202122, 127.07105758743238));//터미널
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.820639740751496, 127.15639125486992));//천안역
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.81025564697209, 127.14669475486944));//아산역*/

                polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.79802914358235, 127.07752490555227));//셔틀장
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.797553213132375, 127.08718500022104));//셔틀장
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.79601742165512, 127.08753371029236));//셔틀장
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.80043855626985, 127.09968044444324));//셔틀장
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.7924337884605, 127.10159834983548));//셔틀장
                mapView.addPolyline(polyline);
            }

            @Override
            public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

            }

            @Override
            public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

            }

            @Override
            public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
    }

    public void refresh(){
        mapView.removeAllPOIItems();
        for (BusInfo businfo : ShuttleDBConnect.busInfo.values()) {
            mapView.addPOIItem(openMarker(businfo));
        }
    }


    public MapPOIItem openMarker(BusInfo businfo){//마커생성
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(businfo.destination + businfo.userCount+"/45");
        marker.setTag(1);
        marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomImageResourceId(R.drawable.bus_marker);//기본 마커 이미지설정
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        marker.setCustomSelectedImageResourceId(R.drawable.bus_markerclick);//선택했을때 마커이미지설정
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(businfo.latitude,businfo.longitude);
        marker.setMapPoint(mapPoint);
        return marker;
    }
}