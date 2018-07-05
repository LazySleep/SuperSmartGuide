package com.lazysleep.supersmartguide;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lazysleep.supersmartguide.user.LoginActivity;
import com.lazysleep.util.DbManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class MinePage extends LinearLayout implements View.OnClickListener {
    private Context context;
    private final int TAKE_PHOTO = 1;
    private final int TAKE_GALLERY = 2;

    String username = "";
    SharedPreferences sharedPreferences;
    private Uri headImageUri;

    ImageView head;
    TextView login_or_reg;
    SQLiteDatabase db;

    public MinePage(Context context) {
        super(context);
        onCreate(context);
    }

    public MinePage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public MinePage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MinePage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onCreate(context);
    }

    private void onCreate(final Context context) {
        this.context = context;
        inflate(context, R.layout.mine_page, this); // 绑定布局
        head = findViewById(R.id.head_img);
        login_or_reg = findViewById(R.id.login_or_reg);
        findViewById(R.id.update).setOnClickListener(this);
        findViewById(R.id.about).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
        db = ((DbManager) context.getApplicationContext()).getSQLiteDatabase();
        sharedPreferences = context.getSharedPreferences("user", Activity.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_img:
                new AlertDialog.Builder(context)
                        .setTitle("更改你的信息：")
                        //设置两个item
                        .setItems(new String[]{"通过拍摄设置头像", "通过相册设置头像"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    openCamera();
                                } else if (which == 1) {
                                    openGallery();
                                }
                            }
                        }).show();
                break;
            case R.id.logout:
                new AlertDialog.Builder(context)
                        .setMessage("确认注销")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                username = "";
                                sharedPreferences.edit().clear().apply();
                                reset();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.login_or_reg:
                context.startActivity(new Intent(context, LoginActivity.class));
                break;
            case R.id.update:
                Toast.makeText(context, "已经是最新版本", Toast.LENGTH_SHORT).show();
                break;
            case R.id.about:
                context.startActivity(new Intent(context, AboutActivity.class));
                break;
        }

    }


    public void onResume() {
        username = sharedPreferences.getString("username", "");
        reset();
    }

    void reset() {
        if (!"".equals(username)) {
            // 登陆
            findViewById(R.id.logout).setVisibility(View.VISIBLE);
            login_or_reg.setOnClickListener(null);
            head.setOnClickListener(this);
            login_or_reg.setText(username);
            Bitmap img = readHeadImageFromDb();
            if (img != null) {
                img = Bitmap.createScaledBitmap(img, 400, 400, true);
                head.setImageBitmap(img);
            }else {
                head.setImageResource(R.drawable.ic_launcher_round);
            }

        } else {
            // 未登陆
            findViewById(R.id.logout).setVisibility(View.GONE);
            login_or_reg.setText("登陆/注册");
            login_or_reg.setOnClickListener(this);
            head.setOnClickListener(null);
        }
    }

    private Bitmap readHeadImageFromDb() {
        Bitmap img = null;
        byte[] bytes;
        String sql = "SELECT * FROM user WHERE username = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{username});
        if (cursor.moveToFirst()) {
            if ((bytes = cursor.getBlob(cursor.getColumnIndex("head_img"))) != null) {
                img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }
        cursor.close();
        return img;
    }

    public void onPause() {
    }

    public void onDestroy() {
    }

    private void openCamera() {
        File outPutImage = new File(context.getExternalCacheDir(), "head_imageView.jpg");
        if (outPutImage.exists()) {
            outPutImage.delete();
        }
        try {
            outPutImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //判断安卓版本是否低于7.0,低于7.0则调用 Uri.fromFile()方法 否则调用FileProvider.getUriForFile()
        if (Build.VERSION.SDK_INT >= 24) {
            headImageUri = FileProvider.getUriForFile(context,
                    "com.lazysleep.supersmartguide.FileProvider", outPutImage);
        } else {
            headImageUri = Uri.fromFile(outPutImage);
        }
        //启动相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, headImageUri);
        ((Activity) context).startActivityForResult(intent, TAKE_PHOTO);

    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            ((Activity) context).startActivityForResult(intent, TAKE_GALLERY);
        }
    }


    @TargetApi(19)
    private String handleImageOnKiteKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(context, uri)) {
            //如果是Document类型的Uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的ID
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是 content 类型的Uri，则使用普通方法处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        return getImagePath(uri, null);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和 selection 来获取真实的图片路径
        Cursor c = context.getContentResolver().query(uri, null, selection, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            c.close();
        }
        return path;
    }

    private void saveHeadImageToDb(Bitmap bitmap) {
        if (bitmap == null)
            return;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        ContentValues values = new ContentValues();
        values.put("head_img", os.toByteArray());
        db.update("user", values, "username = ?", new String[]{username});
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO://获取从相机中返回的数据
                Bitmap bitmap;
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(headImageUri));
                        if (bitmap.getWidth() < bitmap.getHeight()) {
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getWidth());
                        } else {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(90);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getHeight(), bitmap.getHeight(), matrix, true);
                        }
                        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
                        head.setImageBitmap(bitmap);
                        saveHeadImageToDb(bitmap);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                break;
            case TAKE_GALLERY://获取从相册中返回的数据
                if (resultCode == Activity.RESULT_OK) {
                    String imagePath;
                    if (Build.VERSION.SDK_INT >= 19) {
                        //Android 4.4 以上使用这个方法
                        imagePath = handleImageOnKiteKat(data);
                    } else {
                        //Android 4.4 以下使用这个方法
                        imagePath = handleImageBeforeKitKat(data);
                    }
                    if (imagePath != null) {
                        bitmap = BitmapFactory.decodeFile(imagePath);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
                        head.setImageBitmap(bitmap);
                        saveHeadImageToDb(bitmap);
                    } else {
                        Toast.makeText(context, "照片上传失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


}