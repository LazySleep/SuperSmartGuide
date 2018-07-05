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
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.lazysleep.supersmartguide.nearby.DataShowActivity;
import com.lazysleep.supersmartguide.nearby.NearbyMapActivity;
import com.lazysleep.supersmartguide.nearby.StrategyActivity;
import com.lazysleep.util.MyLocationListener;


public class NearbyPage extends LinearLayout {
    private Context context;
    double latitude;
    double longitude;

    public NearbyPage(Context context) {
        super(context);
        onCreate(context);
    }

    public NearbyPage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public NearbyPage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NearbyPage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onCreate(context);
    }

    private void onCreate(final Context context) {
        this.context = context;
        inflate(context, R.layout.nearby_page, this); // 绑定布局
        findViewById(R.id.scenic).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DataShowActivity.class);
                intent.putExtra("name", "scenic");
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                context.startActivity(intent);
            }
        });
        findViewById(R.id.food).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DataShowActivity.class);
                intent.putExtra("name", "food");
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                context.startActivity(intent);
            }
        });
        findViewById(R.id.hotel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DataShowActivity.class);
                intent.putExtra("name", "hotel");
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                context.startActivity(intent);
            }
        });
        findViewById(R.id.bank).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NearbyMapActivity.class);
                intent.putExtra("name", "bank");
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                context.startActivity(intent);
            }
        });
        findViewById(R.id.shopping).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NearbyMapActivity.class);
                intent.putExtra("name", "shopping");
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                context.startActivity(intent);
            }
        });
        findViewById(R.id.strategy).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StrategyActivity.class);
                context.startActivity(intent);
            }
        });
        registerMessageReceiver();
    }

    public void registerMessageReceiver() {
        MessageReceiver mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MyLocationListener.BROADCAST_LOCATION_ACTION);
        context.registerReceiver(mMessageReceiver, filter);
    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                BDLocation location = bundle.getParcelable("data");
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }
    }

    public void onDestroy() {
    }

    public void onResume() {
    }

    public void onPause() {
    }
}

