package com.lazysleep.supersmartguide.nearby;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lazysleep.supersmartguide.R;
import com.lazysleep.util.DbManager;

public class ShowSingleInfoActivity extends AppCompatActivity {
    private String id;
    ImageView iv;
    TextView title;
    TextView info;
    TextView jump_to_map;
    String name;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_single_info);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        iv = findViewById(R.id.img);
        title = findViewById(R.id.show_info_title);
        info = findViewById(R.id.info);
        jump_to_map = findViewById(R.id.jump_to_map);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String mode = intent.getStringExtra("mode");
        switch (mode) {
            case "scenic":
                initScenic();
                break;
            case "hotel":
                initHotel();
                break;
            case "food":
                initFood();
                break;
            default:
                finish();
                Toast.makeText(getApplicationContext(), "出现错误", Toast.LENGTH_SHORT).show();
                break;
        }
        jump_to_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowSingleInfoActivity.this, ShowInMapActivity.class);
                intent.putExtra("key", name);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });
    }

    private void initScenic() {
        SQLiteDatabase db = ((DbManager) getApplicationContext()).getSQLiteDatabase();
        String sql = "SELECT * FROM scenic WHERE sid = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{id});
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex("sname"));
            String introduce = cursor.getString(cursor.getColumnIndex("sintroduce"))
                    .replace("\\t", "\t");
            String address = cursor.getString(cursor.getColumnIndex("saddress"));
            latitude = cursor.getDouble(cursor.getColumnIndex("slatitude"));
            longitude = cursor.getDouble(cursor.getColumnIndex("slongitude"));
            String openTime = cursor.getString(cursor.getColumnIndex("sopentime"));
            String transit = cursor.getString(cursor.getColumnIndex("stransit"));
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex("simage"));
            Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            img = Bitmap.createScaledBitmap(img, 900, 450, true);
            iv.setImageBitmap(img);
            title.setText(name);
            String data = introduce + "\n地址：" + address + "\n开放时间：" + openTime + "\n交通方式：\n" + transit;
            info.setText(data);
        }
        cursor.close();
    }

    private void initFood() {
        SQLiteDatabase db = ((DbManager) getApplicationContext()).getSQLiteDatabase();
        String sql = "SELECT * FROM food WHERE f_id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{id});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("f_name"));
            String introduce = cursor.getString(cursor.getColumnIndex("f_introduce"))
                    .replace("\\t", "\t");
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex("f_image"));
            Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            img = Bitmap.createScaledBitmap(img, 900, 450, true);
            iv.setImageBitmap(img);
            title.setText(name);
            info.setText(introduce);
            jump_to_map.setVisibility(View.GONE);
        }
        cursor.close();
    }

    private void initHotel() {
        SQLiteDatabase db = ((DbManager) getApplicationContext()).getSQLiteDatabase();
        String sql = "SELECT * FROM hotel WHERE h_id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{id});
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex("h_name"));
            String introduce = cursor.getString(cursor.getColumnIndex("h_introduce"))
                    .replace("\\t", "\t");
            latitude = cursor.getDouble(cursor.getColumnIndex("h_latitude"));
            longitude = cursor.getDouble(cursor.getColumnIndex("h_longitude"));
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex("h_image"));
            Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            img = Bitmap.createScaledBitmap(img, 900, 450, true);
            iv.setImageBitmap(img);
            title.setText(name);
            info.setText(introduce);
        }
        cursor.close();
    }
}
