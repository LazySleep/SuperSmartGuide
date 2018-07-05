package com.lazysleep.supersmartguide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.lazysleep.util.BDMapHelper;
import com.lazysleep.util.MyLocationListener;


public class LinePage extends LinearLayout {
    private MapView mapView = null;
    private BaiduMap baiduMap;
    private Context context;
    BDMapHelper helper;
    private BDLocation location = null;
    boolean input_area_status = false;// false: 上面 true： 下面
    LinearLayout input_area;
    EditText et_from_address;
    EditText et_to_address;
    RoutePlanSearch mSearch;

    public LinePage(Context context) {
        super(context);
        onCreate(context);
    }

    public LinePage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public LinePage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LinePage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onCreate(context);
    }

    private void onCreate(final Context context) {
        this.context = context;
        inflate(context, R.layout.line_page, this); // 绑定布局
        mapView = findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        helper = new BDMapHelper(context);
        input_area = findViewById(R.id.input_area);
        et_to_address = findViewById(R.id.et_to_address);
        et_from_address = findViewById(R.id.et_from_address);
        mSearch = RoutePlanSearch.newInstance();
        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR
                        && drivingRouteResult.getRouteLines().size() > 0) {
                    baiduMap.clear();
                    DrivingRouteLine route = drivingRouteResult.getRouteLines().get(0);
                    DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
                    baiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(route);
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            }
        };
        mSearch.setOnGetRoutePlanResultListener(listener);
        findViewById(R.id.search).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!input_area_status) {
                    Animation anim_down = AnimationUtils.loadAnimation(context, R.anim.move_to_down);  //绑定动画
                    anim_down.setFillAfter(true);  //停留在最后的状态
                    input_area.clearAnimation();
                    input_area.bringToFront();
                    input_area.setAnimation(anim_down);
                    input_area_status = true;

                }
            }
        });
        findViewById(R.id.btn_to_address).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_area_status) {
                    input_area.clearAnimation();
                    Animation anim_up = AnimationUtils.loadAnimation(context, R.anim.move_to_up);  //绑定动画
                    anim_up.setFillAfter(true);  //停留在最后的状态
                    input_area.setAnimation(anim_up);
                    input_area_status = false;
                    if (location != null) {
                        String from = et_from_address.getText().toString();
                        String to = et_to_address.getText().toString();
                        PlanNode stNode;
                        if ("当前位置".equals(from)) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LatLng ll = new LatLng(latitude, longitude);
                            stNode = PlanNode.withLocation(ll);
                        } else {
                            stNode = PlanNode.withCityNameAndPlaceName(location.getCity(), from);
                        }
                        PlanNode enNode;
                        if ("当前位置".equals(to)) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            LatLng ll = new LatLng(latitude, longitude);
                            enNode = PlanNode.withLocation(ll);
                        } else {
                            enNode = PlanNode.withCityNameAndPlaceName(location.getCity(), to);
                        }
                        mSearch.drivingSearch((new DrivingRoutePlanOption())
                                .from(stNode)
                                .to(enNode));
                    } else {
                        Toast.makeText(context, "还未获取到位置信息，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                    mapView.bringToFront();
                }
            }
        });
    }


    public void onDestroy() {
        mapView.onDestroy();
    }

    public void onResume() {
        registerMessageReceiver();
        helper.start();
        mapView.onResume();
    }

    public void onPause() {
        mapView.onPause();
    }


    private void registerMessageReceiver() {
        MessageReceiver mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MyLocationListener.BROADCAST_LOCATION_ACTION);
        context.registerReceiver(mMessageReceiver, filter);
    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (location != null) {
                return;
            }
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                location = bundle.getParcelable("data");
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng ll = new LatLng(latitude, longitude);
                    MapStatusUpdate update;
                    update = MapStatusUpdateFactory.newLatLng(ll);
                    baiduMap.animateMapStatus(update);
                    update = MapStatusUpdateFactory.zoomTo(16f);
                    baiduMap.animateMapStatus(update);
                    MyLocationData.Builder builder = new MyLocationData.Builder();
                    builder.latitude(latitude);
                    builder.longitude(longitude);
                    baiduMap.setMyLocationData(builder.build());
                }
            }
        }
    }
}

