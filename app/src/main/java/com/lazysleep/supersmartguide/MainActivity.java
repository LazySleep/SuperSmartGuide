package com.lazysleep.supersmartguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;

public class MainActivity extends AppCompatActivity {
    private final static int SELECT_COLOR = 0xFF3DE5D2;
    private final static int UN_SELECT_COLOR = 0xFF707070;// 第一次按下返回键的事件
    private long firstPressedTime;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }

    int selectId;
    private HomePage home_layout;
    private NearbyPage nearby_layout;
    private LinePage line_layout;
    private WeatherPage weather_layout;
    private MinePage mine_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        home_layout = findViewById(R.id.home_layout);
        nearby_layout = findViewById(R.id.nearby_layout);
        line_layout = findViewById(R.id.line_layout);
        weather_layout = findViewById(R.id.weather_layout);
        mine_layout = findViewById(R.id.mine_layout);
        LinearLayout navLayout = findViewById(R.id.nav);
        selectId = R.id.nav1;
        for (int i = 0; i < navLayout.getChildCount(); i++) {
            navLayout.getChildAt(i)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPage((LinearLayout) v);
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        home_layout.onDestroy();
        nearby_layout.onDestroy();
        line_layout.onDestroy();
        weather_layout.onDestroy();
        mine_layout.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        home_layout.onResume();
        nearby_layout.onResume();
        line_layout.onResume();
        weather_layout.onResume();
        mine_layout.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        home_layout.onPause();
        nearby_layout.onPause();
        line_layout.onPause();
        weather_layout.onPause();
        mine_layout.onPause();
    }

    void showPage(LinearLayout view) {
        LinearLayout cancel = findViewById(selectId);
        cancel.getChildAt(0).setBackgroundColor(UN_SELECT_COLOR);
        ((TextView) cancel.getChildAt(1)).setTextColor(UN_SELECT_COLOR);
        (view).getChildAt(0).setBackgroundColor(SELECT_COLOR);
        ((TextView) view.getChildAt(1)).setTextColor(SELECT_COLOR);
        selectId = view.getId();
        switch (view.getId()) {
            case R.id.nav1:
                home_layout.bringToFront();
                break;
            case R.id.nav2:
                nearby_layout.bringToFront();
                break;
            case R.id.nav3:
                line_layout.bringToFront();
                break;
            case R.id.nav4:
                weather_layout.bringToFront();
                break;
            case R.id.nav5:
                mine_layout.bringToFront();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mine_layout.onActivityResult(requestCode, resultCode, data);
    }
}
