package com.lazysleep.supersmartguide.nearby.strategy;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lazysleep.supersmartguide.R;
import com.lazysleep.util.DbManager;

public class AboutStrategyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_strategy);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayout content = findViewById(R.id.content);
        int i = 0;
        int[] colors = new int[]{0xFF7F1874, 0xFFDB9019, 0xFF5ED5D1, 0xFF1A2D27, 0xFFFF6E97, 0xFFF1AAA6};
        SQLiteDatabase db = ((DbManager) getApplicationContext()).getSQLiteDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM about", null);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("title"))
                    .replace("\\t","\t");
            View view = getTitleView(title);
            view.setBackgroundColor(colors[i%colors.length]);
            content.addView(view);
            String text = cursor.getString(cursor.getColumnIndex("content"))
                    .replace("\\t","\t");
            content.addView(getContentView(text));
            i++;
        }
        cursor.close();
    }

    public View getTitleView(String title) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(title);
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(20);
        textView.setPadding(10,10,10,20);
        textView.setSingleLine(true);
        return textView;
    }

    private View getContentView(String content) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(content);
        textView.setPadding(0,20,0,40);
        return textView;
    }

}
