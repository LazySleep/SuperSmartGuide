package com.lazysleep.supersmartguide;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lazysleep.util.DbManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LoadActivity extends AppCompatActivity {
    private TextView progress_value;
    private ViewPager viewPager;
    private boolean isOnce;
    private SharedPreferences sharedPreferences;
    boolean existRequestPermission = false;// 存在请求权限事件
    String DbPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        viewPager = findViewById(R.id.load_page);
        progress_value = findViewById(R.id.progress_value);
        sharedPreferences = getPreferences(MODE_PRIVATE);
        isOnce = sharedPreferences.getBoolean("isOnce", true);
        if (isOnce) {
            initViewPage();
        }
        judgePermission();
        initDB();
    }

    private void judgePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0以上
            existRequestPermission = true;
            String[] permissions =
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    };
            List<String> mPermissionList = new ArrayList<>();
            mPermissionList.clear();
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permission);
                }
            }
            if (!mPermissionList.isEmpty()) {//请求权限方法
                permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
                ActivityCompat.requestPermissions(LoadActivity.this, permissions, 1);
            } else {
                existRequestPermission = false;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        if (showRequestPermission) {//
                            judgePermission();//重新申请权限
                            return;
                        }
                    }
                }
                if (!isOnce) {
                    startActivity(new Intent(LoadActivity.this, MainActivity.class));
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CountDownTimer cdt = new CountDownTimer(1500, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                String value = (100 - millisUntilFinished / 15) + "%";
                progress_value.setText(value);
            }

            @Override
            public void onFinish() {
                if (isOnce) {
                    FrameLayout frameLayout = findViewById(R.id.root);
                    frameLayout.bringChildToFront(viewPager);
                } else {
                    if (!existRequestPermission) {
                        startActivity(new Intent(LoadActivity.this, MainActivity.class));
                        finish();
                    }
                }
            }
        };
        cdt.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.edit().putBoolean("isOnce", false).apply();
    }

    void initViewPage() {
        ArrayList<View> aList = new ArrayList<>();
        LayoutInflater li = getLayoutInflater();
        LinearLayout load_page1 = new LinearLayout(this);
        LinearLayout load_page2 = new LinearLayout(this);
        load_page1.setBackgroundResource(R.drawable.start1);
        load_page2.setBackgroundResource(R.drawable.start2);
        View load_page3 = li.inflate(R.layout.load_page_end, null, false);
        load_page3.findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoadActivity.this, MainActivity.class));
                finish();
            }
        });
        aList.add(load_page1);
        aList.add(load_page2);
        aList.add(load_page3);
        LoadViewPageAdapter mAdapter = new LoadViewPageAdapter(aList);
        viewPager.setAdapter(mAdapter);
    }

    void initDB() {
        DbPath = getFilesDir() + "\\databases\\data.db";
        ((DbManager) getApplicationContext()).setDbPath(DbPath);
        if (!new File(DbPath).exists()) {
            writeDB();
        }
    }

    //写入基础数据库
    public void writeDB() {
        //如果是放在应用包名的目录下,自动放入“databases目录下
        FileOutputStream fout = null;
        InputStream inputStream = null;
        try {
            inputStream = getResources().openRawResource(R.raw.data);
            fout = new FileOutputStream(new File(DbPath));
            byte[] buffer = new byte[128];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fout.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
