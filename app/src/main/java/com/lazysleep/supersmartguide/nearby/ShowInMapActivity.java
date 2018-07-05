package com.lazysleep.supersmartguide.nearby;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.SinglePoiOverlay;
import com.lazysleep.supersmartguide.R;

public class ShowInMapActivity extends AppCompatActivity {
    MapView mapView;
    String key;
    BaiduMap baiduMap;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_in_map);
        mapView = findViewById(R.id.map);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        double latitude = intent.getDoubleExtra("latitude", -1);
        double longitude = intent.getDoubleExtra("longitude", -1);
        if (longitude == -1 || latitude == -1) {
            Toast.makeText(getApplicationContext(), "还未获得位置信息，请稍后再试", Toast.LENGTH_SHORT).show();
            finish();
        }
        latLng = new LatLng(latitude, longitude);
        baiduMap = mapView.getMap();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
        SinglePoiOverlay overlay = new SinglePoiOverlay(this,baiduMap);
        baiduMap.setOnMarkerClickListener(overlay);
        overlay.setData(latLng);
        overlay.addToMap();
        overlay.zoomToSpan();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(16f));
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

}
