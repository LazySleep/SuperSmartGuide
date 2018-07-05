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

public class TravelStrategyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_strategy);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayout content = findViewById(R.id.content);
        SQLiteDatabase db = ((DbManager) getApplicationContext()).getSQLiteDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM travel", null);
        while (cursor.moveToNext()) {
            String author = cursor.getString(cursor.getColumnIndex("author"));
            String text = cursor.getString(cursor.getColumnIndex("content"))
                    .replace("\\t", "\t");
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.addView(getLeft(author));
            linearLayout.addView(getRight(text));
            linearLayout.setPadding(0,50,0,50);
            content.addView(linearLayout);
        }
        cursor.close();
    }

    private View getLeft(String author) {
        LinearLayout left = new LinearLayout(this);
        left.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1.0f));
        left.setOrientation(LinearLayout.VERTICAL);
        left.setGravity(Gravity.CENTER_HORIZONTAL);
        ImageView head = new ImageView(this);
        head.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        head.setImageResource(R.drawable.rabbit_ears_icon);
        left.addView(head);
        TextView textView = new TextView(this);
        textView.setText(author);
        textView.setTextColor(Color.BLACK);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.CENTER);
        left.addView(textView);
        return left;
    }

    private View getRight(String content) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,3.0f));
        textView.setText(content);
        return textView;
    }

}
