package com.lazysleep.supersmartguide;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 半自定义View
 */

public class WeatherPage extends LinearLayout {
    public static final String NOW_CITY_ACTION = "com.lazysleep.supersmartguide.nowcity";
    public static final String GET_WEATHER_ACTION = "com.lazysleep.supersmartguide.getweather";

    private Context context;
    TextView now_temperature;
    TextView city;
    TextView title_city;
    TextView today_date;
    TextView today_weather;
    ImageView today_weather_img;
    TextView today_temperature;
    TextView today_pm;
    TextView today_wind;
    LinearLayout tips;
    LinearLayout other_date;

    String city_str;
    SharedPreferences sharedPreferences;

    boolean settingNowCity = true;

    public WeatherPage(Context context) {
        super(context);
        onCreate(context);
    }

    public WeatherPage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public WeatherPage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WeatherPage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onCreate(context);
    }

    private void onCreate(final Context context) {
        this.context = context;
        inflate(context, R.layout.weather_page, this); // 绑定布局
        sharedPreferences = context.getSharedPreferences("weather", Activity.MODE_PRIVATE);
        findViewById(R.id.set_city).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(context);
                new android.support.v7.app.AlertDialog.Builder(context)
                        .setTitle("查看其他城市天气")
                        .setMessage("下次重启APP时会自动显示当前位的气象信息")
                        .setView(editText)
                        .setPositiveButton("查看", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                city_str = editText.getText().toString();
                                getWeather(city_str);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
        getWeather("北京市");
    }

    public void getWeather(String city_str) {
        Log.d("city_str",city_str);
        final String path = "http://api.map.baidu.com/telematics/v3/weather?location=" + city_str
                + "&output=json&ak=TWHvFEKPbVdXn6O1NSZcrcAPBm1qynTX";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(path)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    Intent intent = new Intent(GET_WEATHER_ACTION);
                    intent.putExtra("weather", response.body().string());
                    context.sendBroadcast(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    protected void onDestroy() {
    }

    public void onResume() {
        registerMessageReceiver();
    }

    public void onPause() {
    }

    public void registerMessageReceiver() {
        MessageReceiver mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(NOW_CITY_ACTION);
        filter.addAction(GET_WEATHER_ACTION);
        context.registerReceiver(mMessageReceiver, filter);
    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case NOW_CITY_ACTION:
                        if (settingNowCity) {
                            String data = intent.getStringExtra("data");
                            getWeather(data);
                            settingNowCity = false;
                        }
                        break;
                    case GET_WEATHER_ACTION:
                        String weatherJson = intent.getStringExtra("weather");
                        Log.v("weather", weatherJson);
                        try {
                            dealWithWeatherJson(weatherJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }

        }
    }

    public void dealWithWeatherJson(String info) throws JSONException {
        now_temperature = findViewById(R.id.now_temperature);
        city = findViewById(R.id.city);
        title_city = findViewById(R.id.title_city);
        today_date = findViewById(R.id.today_date);
        today_weather = findViewById(R.id.today_weather);
        today_weather_img = findViewById(R.id.today_weather_img);
        today_temperature = findViewById(R.id.today_temperature);
        today_pm = findViewById(R.id.today_pm);
        today_wind = findViewById(R.id.today_wind);
        tips = findViewById(R.id.tip);
        other_date = findViewById(R.id.other_date);

        JSONArray jsonArray = new JSONArray("[" + info + "]");
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        if (!"success".equals(jsonObject.getString("status"))) {
            Toast.makeText(context, "获取天气信息失败,请确认城市名称是否错误", Toast.LENGTH_LONG).show();
            Log.d("ERRROR", info);
            return;
        }
        today_date.setText(jsonObject.getString("date"));
        jsonArray = new JSONArray(jsonObject.getString("results"));
        jsonObject = jsonArray.getJSONObject(0);
        String cityName = jsonObject.getString("currentCity");
        city.setText(cityName);
        title_city.setText(cityName);
        String pm = "PM2.5  " + jsonObject.getString("pm25");
        today_pm.setText(pm);
        JSONArray indexArray = new JSONArray(jsonObject.getString("index"));
        for (int i = 0; i < tips.getChildCount(); i++) {
            TextView textView = (TextView) tips.getChildAt(i);
            String s =
                    indexArray.getJSONObject(i).getString("tipt") + ":" +
                            indexArray.getJSONObject(i).getString("zs") + "\n" +
                            indexArray.getJSONObject(i).getString("des");
            textView.setText(s);
        }
        JSONArray weatherArray = new JSONArray(jsonObject.getString("weather_data"));
        jsonObject = weatherArray.getJSONObject(0);
        today_weather.setText(jsonObject.getString("weather"));
        today_temperature.setText(jsonObject.getString("temperature"));
        today_wind.setText(jsonObject.getString("wind"));
        String temperature = jsonObject.getString("date");
        now_temperature.setText(temperature.substring(temperature.indexOf("(") + 1, temperature.lastIndexOf(")")));
        final String dayPictureUrl = jsonObject.getString("dayPictureUrl");
        new Thread(new Runnable() {
            @Override
            public void run() {
                setUrlImage(today_weather_img, dayPictureUrl);
            }
        }).start();
        for (int i = 1; i <= other_date.getChildCount(); i++) {
            jsonObject = weatherArray.getJSONObject(i);
            LinearLayout ll = (LinearLayout) other_date.getChildAt(i - 1);
            TextView tv_date = (TextView) ll.getChildAt(0);
            tv_date.setText(jsonObject.getString("date"));
            final ImageView iv_weather = (ImageView) ll.getChildAt(1);
            final String imgUrl = jsonObject.getString("dayPictureUrl");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    setUrlImage(iv_weather, imgUrl);
                }
            }).start();
            TextView tv_info = (TextView) ll.getChildAt(2);
            String s = jsonObject.getString("weather") + "\n" +
                    jsonObject.getString("wind") + "\n" +
                    jsonObject.getString("temperature");
            tv_info.setText(s);
        }
    }

    private void setUrlImage(final ImageView imageView, String url) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().get()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            byte[] bytes = response.body().bytes();
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bitmap);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
