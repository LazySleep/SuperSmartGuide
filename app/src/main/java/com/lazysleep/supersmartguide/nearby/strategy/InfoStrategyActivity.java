package com.lazysleep.supersmartguide.nearby.strategy;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lazysleep.supersmartguide.R;
import com.lazysleep.util.DbManager;

public class InfoStrategyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_strategy);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayout content = findViewById(R.id.content);
        SQLiteDatabase db = ((DbManager) getApplicationContext()).getSQLiteDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM surrounding", null);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("name"))
                    .replace("\\t", "\t");
            View view = getTitleView(title);
            content.addView(view);
            String text = cursor.getString(cursor.getColumnIndex("content"))
                    .replace("\\t", "\t");
            content.addView(getContentView(text));
        }
        cursor.close();
    }

    public View getTitleView(String title) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(40, 40));
        imageView.setImageResource(R.drawable.foot_icon);
        linearLayout.addView(imageView);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(title);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        textView.setPadding(10, 10, 10, 10);
        textView.setSingleLine(true);
        linearLayout.addView(textView);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        return linearLayout;
    }

    private View getContentView(String content) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(content);
        textView.setPadding(0, 10, 0, 20);
        return textView;
    }

}
