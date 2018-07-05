package com.lazysleep.supersmartguide;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.oragee.banners.BannerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 半自定义View
 */

public class HomePage extends LinearLayout {
    private Context context;
    private Spinner spinner;
    private TextView tv_poet;
    public HomePage(Context context) {
        super(context);
        onCreate(context);
    }

    public HomePage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public HomePage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HomePage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onCreate(context);
    }

    private void onCreate(Context context) {
        this.context = context;
        inflate(context, R.layout.home_page, this); // 绑定布局
        tv_poet=findViewById(R.id.tv_poet);
        setBanner(getBannerRes("福州"));
        spinner = findViewById(R.id.spinner_city);
        spinner.setSelection(0, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String city_name = (String)spinner.getItemAtPosition(position);
                setBanner(getBannerRes(city_name));
                switch (city_name){
                    case "福州":
                        tv_poet.setText(R.string.poet_fuzhou);
                        break;
                    case "广州":
                        tv_poet.setText(R.string.poet_guangzhou);
                        break;
                    case "北京":
                        tv_poet.setText(R.string.poet_beijing);
                        break;
                    case "南京":
                        tv_poet.setText(R.string.poet_nanjing);
                        break;
                    case "西安":
                        tv_poet.setText(R.string.poet_xian);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void onDestroy() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    private void setBanner(List<View> viewList) {
        BannerView bannerView = findViewById(R.id.banner);
        bannerView.setViewList(viewList);
        bannerView.startLoop(true);
    }

    /**
     * 获取Banner图片资源
     *
     * @param name 城市名
     * @return
     */
    private List<View> getBannerRes(String name) {
        ArrayList<View> list = new ArrayList<>();
        switch (name){
            case "福州":
                list.add(getImageView(R.drawable.city_fuzhou_1));
                list.add(getImageView(R.drawable.city_fuzhou_2));
                list.add(getImageView(R.drawable.city_fuzhou_3));
                break;
            case "广州":
                list.add(getImageView(R.drawable.city_guangzhou_1));
                list.add(getImageView(R.drawable.city_guangzhou_2));
                list.add(getImageView(R.drawable.city_guangzhou_3));
                break;
            case "北京":
                list.add(getImageView(R.drawable.city_beijing_1));
                list.add(getImageView(R.drawable.city_beijing_2));
                list.add(getImageView(R.drawable.city_beijing_3));
                break;
            case "南京":
                list.add(getImageView(R.drawable.city_nanjing_1));
                list.add(getImageView(R.drawable.city_nanjing_2));
                list.add(getImageView(R.drawable.city_nanjing_3));
                break;
            case "西安":
                list.add(getImageView(R.drawable.city_xian_1));
                list.add(getImageView(R.drawable.city_xian_2));
                list.add(getImageView(R.drawable.city_xian_3));
                break;
        }
        return list;
    }

    /**
     * 根据资源ID返回ImageView
     *
     * @param resId
     * @return
     */
    private ImageView getImageView(int resId) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(resId);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }
}
