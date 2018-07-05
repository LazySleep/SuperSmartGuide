package com.baidu.mapapi.overlayutil;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于显示poi的overly
 */
public class PoiOverlay extends OverlayManager {

    private static final int MAX_POI_SIZE = 10;
    private PoiResult mPoiResult = null;
    private BaiduMap baiduMap;
    private Context context;

    /**
     * 构造函数
     *
     * @param context
     * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
     */
    public PoiOverlay(Context context, BaiduMap baiduMap) {
        super(baiduMap);
        this.baiduMap = baiduMap;
        this.context = context;
    }

    /**
     * 设置POI数据
     *
     * @param poiResult 设置POI数据
     */
    public void setData(PoiResult poiResult) {
        this.mPoiResult = poiResult;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {
        if (mPoiResult == null || mPoiResult.getAllPoi() == null) {
            return null;
        }
        List<OverlayOptions> markerList = new ArrayList<OverlayOptions>();
        int markerSize = 0;
        for (int i = 0; i < mPoiResult.getAllPoi().size()
                && markerSize < MAX_POI_SIZE; i++) {
            if (mPoiResult.getAllPoi().get(i).location == null) {
                continue;
            }
            markerSize++;
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            markerList.add(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_mark"
                            + markerSize + ".png")).extraInfo(bundle)
                    .position(mPoiResult.getAllPoi().get(i).location));

        }
        return markerList;
    }

    /**
     * 获取该 PoiOverlay 的 poi数据
     *
     * @return
     */
    public PoiResult getPoiResult() {
        return mPoiResult;
    }

    /**
     * 覆写此方法以改变默认点击行为
     *
     * @param i 被点击的poi在
     *          {@link com.baidu.mapapi.search.poi.PoiResult#getAllPoi()} 中的索引
     * @return
     */
    public boolean onPoiClick(int i) {
        PoiInfo info;
        if (mPoiResult.getAllPoi() != null
                && (info = mPoiResult.getAllPoi().get(i)) != null) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackgroundColor(0xFFFFFFFF);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setPadding(20, 20, 20, 20);
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(800, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(info.name);
            textView.setTextColor(Color.BLACK);
            linearLayout.addView(textView);
            textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(800, ViewGroup.LayoutParams.WRAP_CONTENT));
            String str = info.province + " " + info.city + " " + info.area + "\n" + info.address;
            textView.setText(str);
            linearLayout.addView(textView);
            InfoWindow mInfoWindow = new InfoWindow(linearLayout, info.location, -80);
            //显示InfoWindow
            baiduMap.showInfoWindow(mInfoWindow);

            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(info.location);
            baiduMap.setMapStatus(update);

        }
        return false;
    }

    @Override
    public final boolean onMarkerClick(Marker marker) {
        if (!mOverlayList.contains(marker)) {
            return false;
        }
        if (marker.getExtraInfo() != null) {
            return onPoiClick(marker.getExtraInfo().getInt("index"));
        }
        return false;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        // TODO Auto-generated method stub
        return false;
    }
}
