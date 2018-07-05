package com.baidu.mapapi.overlayutil;

import android.content.Context;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class SinglePoiOverlay extends OverlayManager{
    private LatLng latLng = null;

    /**
     * 构造函数
     *
     * @param context
     * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
     */
    public SinglePoiOverlay(Context context, BaiduMap baiduMap) {
        super(baiduMap);
    }

    public void setData(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {
        if (latLng == null) {
            return null;
        }
        List<OverlayOptions> markerList = new ArrayList<OverlayOptions>();
            Bundle bundle = new Bundle();
            bundle.putInt("index", 0);
            markerList.add(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_mark1.png")).extraInfo(bundle)
                    .position(latLng));
        return markerList;
    }

    /**
     * 覆写此方法以改变默认点击行为
     *
     * @param i 被点击的poi在
     *          {@link com.baidu.mapapi.search.poi.PoiResult#getAllPoi()} 中的索引
     * @return
     */
    public boolean onPoiClick(int i) {

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
