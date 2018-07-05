package com.lazysleep.util;


import android.content.Context;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class BDMapHelper {
    Context context;
    private LocationClient mLocationClient = null;

    public BDMapHelper(Context context) {
        this.context = context;
        onCreate();
    }

    private void onCreate() {
        mLocationClient = new LocationClient(context.getApplicationContext());
        MyLocationListener myListener = new MyLocationListener(context.getApplicationContext());
        // 声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        // 注册监听函数

        // 配置参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setEnableSimulateGps(false);
        option.setIsNeedAddress(true); // 获取地址
        option.setAddrType("all");
        option.setIsNeedLocationDescribe(true);// 获取定位描述
        mLocationClient.setLocOption(option);
    }

    public void start() {
        mLocationClient.start();
    }

    public void restart() {
        mLocationClient.restart();
    }

    public void stop() {
        mLocationClient.stop();
    }
}
