package com.lazysleep.supersmartguide.user;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lazysleep.supersmartguide.R;
import com.lazysleep.util.DbManager;

public class LoginActivity extends AppCompatActivity {
    EditText un;
    EditText pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        un = findViewById(R.id.username);
        pw = findViewById(R.id.password);
        findViewById(R.id.reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = un.getText().toString();
                String pwd = pw.getText().toString();
                if (username.isEmpty() || pwd.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请填写完整", Toast.LENGTH_SHORT).show();
                } else {
                    check(username, pwd);
                }
            }
        });
    }

    private void check(String username, String pwd) {
        SQLiteDatabase db = ((DbManager) getApplicationContext()).getSQLiteDatabase();
        String sql = "SELECT * FROM user WHERE username = ? and password = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{username, pwd});
        if (cursor.getCount() == 1) {
            SharedPreferences sharedPreferences = getSharedPreferences("user", Activity.MODE_PRIVATE);
            sharedPreferences.edit().putString("username", username).apply();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "账号或密码错误", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }
}
