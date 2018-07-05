package com.lazysleep.supersmartguide.nearby;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lazysleep.supersmartguide.R;
import com.lazysleep.supersmartguide.nearby.strategy.AboutStrategyActivity;
import com.lazysleep.supersmartguide.nearby.strategy.InfoStrategyActivity;
import com.lazysleep.supersmartguide.nearby.strategy.SpecialtyStrategyActivity;
import com.lazysleep.supersmartguide.nearby.strategy.TrafficStrategyActivity;
import com.lazysleep.supersmartguide.nearby.strategy.TravelStrategyActivity;
import com.lazysleep.supersmartguide.nearby.strategy.TripStrategyActivity;

public class StrategyActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);
        findViewById(R.id.back_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                startActivity(new Intent(this, AboutStrategyActivity.class));
                break;
            case R.id.btn2:
                startActivity(new Intent(this, TrafficStrategyActivity.class));
                break;
            case R.id.btn3:
                startActivity(new Intent(this, InfoStrategyActivity.class));
                break;
            case R.id.btn4:
                startActivity(new Intent(this, TripStrategyActivity.class));
                break;
            case R.id.btn5:
                startActivity(new Intent(this, SpecialtyStrategyActivity.class));
                break;
            case R.id.btn6:
                startActivity(new Intent(this, TravelStrategyActivity.class));
                break;
            case R.id.back_btn:
                finish();
                break;
        }
    }
}
