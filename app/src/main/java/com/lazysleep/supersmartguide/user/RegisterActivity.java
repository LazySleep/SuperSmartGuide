package com.lazysleep.supersmartguide.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lazysleep.supersmartguide.R;
import com.lazysleep.util.DbManager;

public class RegisterActivity extends AppCompatActivity {

    EditText un;
    EditText pw;
    EditText repw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        un = findViewById(R.id.username);
        pw = findViewById(R.id.password);
        repw = findViewById(R.id.repassword);
        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = un.getText().toString();
                final String pwd = pw.getText().toString();
                String repwd = repw.getText().toString();
                if (username.isEmpty() || pwd.isEmpty() || repwd.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请填写完整", Toast.LENGTH_SHORT).show();
                } else {
                    if (pwd.equals(repwd)) {
                        final SQLiteDatabase db = ((DbManager) getApplicationContext()).getSQLiteDatabase();
                        String sql = "SELECT * FROM user WHERE username = ?";
                        Cursor cursor = db.rawQuery(sql, new String[]{username});
                        if (cursor.getCount() != 0) {
                            Toast.makeText(getApplicationContext(), "账号已存在", Toast.LENGTH_SHORT).show();
                            cursor.close();
                            return;
                        }
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setMessage("确认注册")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String sql = "INSERT INTO user(username,password) VALUES(?,?)";
                                        db.execSQL(sql, new String[]{username, pwd});
                                        SharedPreferences sharedPreferences = getSharedPreferences("user", Activity.MODE_PRIVATE);
                                        sharedPreferences.edit().putString("username", username).apply();
                                        finish();
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    } else {
                        Toast.makeText(getApplicationContext(), "两次密码输入要一致", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
