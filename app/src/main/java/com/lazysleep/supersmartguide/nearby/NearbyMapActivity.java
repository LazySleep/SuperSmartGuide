package com.lazysleep.supersmartguide.nearby;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.lazysleep.supersmartguide.R;

public class NearbyMapActivity extends AppCompatActivity {
    TextView title;
    String keyword;
    MapView mapView;
    BaiduMap baiduMap;
    LatLng latLng;
    double latitude;
    double longitude;
    PoiSearch mPoiSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_map);
        title = findViewById(R.id.nearby_map_title);
        mapView = findViewById(R.id.nearby_map_view);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", -1);
        longitude = intent.getDoubleExtra("longitude", -1);
        if (longitude == -1 || latitude == -1) {
            Toast.makeText(getApplicationContext(), "还未获得位置信息，请稍后再试", Toast.LENGTH_SHORT).show();
            finish();
        }
        latLng = new LatLng(latitude, longitude);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
        switch (intent.getStringExtra("name")) {
            case "bank":
                title.setText("附近的银行");
                keyword = "银行";
                break;
            case "shopping":
                title.setText("附近的商场");
                keyword = "商场";
                break;
            default:
                finish();
                Toast.makeText(this, "出现错误", Toast.LENGTH_SHORT).show();
                break;
        }
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(latitude);
        builder.longitude(longitude);
        baiduMap.setMyLocationData(builder.build());
        initSearch();
    }

    void initSearch() {
         mPoiSearch = PoiSearch.newInstance();
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR
                        && poiResult.getAllPoi().size() > 0) {
                    PoiOverlay overlay = new PoiOverlay(getApplicationContext(), baiduMap);
                    baiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(poiResult);
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                Log.d("GetPoiSearchRes", "onGetPoiDetailResult");
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                Log.d("GetPoiSearchRes", "onGetPoiIndoorResult");
            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .keyword(keyword)
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(latLng)
                .radius(10000)
                .pageNum(10));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
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
