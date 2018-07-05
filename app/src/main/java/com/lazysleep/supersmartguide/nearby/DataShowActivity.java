package com.lazysleep.supersmartguide.nearby;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lazysleep.supersmartguide.R;
import com.lazysleep.util.DbManager;
import com.oragee.banners.BannerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataShowActivity extends AppCompatActivity implements View.OnClickListener {
    TextView sortPriceView;
    TextView sortDistanceView;
    TextView sortRecommendView;
    ListView listview;
    BannerView banner;
    EditText input;

    boolean distanceOrder = false;
    boolean recommendOrder = false;
    boolean priceOrder = false;

    List<Map<String, Object>> dataList = new ArrayList<>();// 原始数据
    List<Map<String, Object>> showData = new ArrayList<>();
    private LatLng meLatLng;
    MySimpleAdapter adapter;

    ArrayList<View> imageViews = new ArrayList<>();

    String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_show);
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", -1);
        double longitude = intent.getDoubleExtra("longitude", -1);
        if (longitude == -1 || latitude == -1) {
            Toast.makeText(getApplicationContext(), "还未获得位置信息，请稍后再试", Toast.LENGTH_SHORT).show();
            finish();
        }
        meLatLng = new LatLng(latitude, longitude);
        banner = findViewById(R.id.banner);
        sortPriceView = findViewById(R.id.sort_price);
        sortRecommendView = findViewById(R.id.sort_recommend);
        sortDistanceView = findViewById(R.id.sort_distance);
        listview = findViewById(R.id.listview);
        input = findViewById(R.id.input);
        TextView title = findViewById(R.id.data_show_title);
        mode = intent.getStringExtra("name");
        switch (mode) {
            case "scenic":
                title.setText("景点");
                initScenic();
                break;
            case "hotel":
                title.setText("酒店");
                initHotel();
                break;
            case "food":
                title.setText("美食");
                findViewById(R.id.sort_layout).setVisibility(View.GONE);
                initFood();
                break;
            default:
                finish();
                Toast.makeText(getApplicationContext(), "出现错误", Toast.LENGTH_SHORT).show();
                break;
        }
        findViewById(R.id.back_btn).setOnClickListener(this);
        sortPriceView.setOnClickListener(this);
        sortRecommendView.setOnClickListener(this);
        sortDistanceView.setOnClickListener(this);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showData.clear();
                if (s.length() == 0) {
                    showData.addAll(dataList);
                } else {
                    for (Map<String, Object> hashMap : dataList) {
                        String name = hashMap.get("name").toString();
                        String info = hashMap.get("info").toString();
                        String recommend = hashMap.get("recommend").toString();
                        if (name != null &&
                                name.contains(s.toString()) ||
                                info.contains(s.toString()) ||
                                recommend.contains(s.toString())) {
                            showData.add(hashMap);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        banner.setViewList(imageViews);
        banner.startLoop(true);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(DataShowActivity.this, ShowSingleInfoActivity.class);
                it.putExtra("mode", mode);
                it.putExtra("id", view.getTag().toString());
                startActivity(it);
            }
        });
    }


    private void initScenic() {
        SQLiteDatabase db = ((DbManager) getApplicationContext()).getSQLiteDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM scenic", null);
        HashMap<String, Object> hashMap;
        while (cursor.moveToNext()) {
            hashMap = new HashMap<>();
            int id = cursor.getInt(cursor.getColumnIndex("sid"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("slongitude"));
            double latitude = cursor.getDouble(cursor.getColumnIndex("slatitude"));
            double distance = (int) DistanceUtil.getDistance(meLatLng, new LatLng(latitude, longitude));
            String name = cursor.getString(cursor.getColumnIndex("sname"));
            double price = cursor.getDouble(cursor.getColumnIndex("sprice"));
            String info = cursor.getString(cursor.getColumnIndex("sreason"));
            int recommend = cursor.getInt(cursor.getColumnIndex("srecommend"));
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex("simage"));
            Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            img = Bitmap.createScaledBitmap(img, 600, 450, true);
            imageViews.add(getImageView(img));
            hashMap.put("id", id);
            hashMap.put("name", name);
            hashMap.put("price", price);
            hashMap.put("info", info);
            hashMap.put("recommend", recommend);
            hashMap.put("distance", distance * 1.0 / 1000);
            hashMap.put("img", img);
            dataList.add(hashMap);
        }
        showData.addAll(dataList);
        adapter = new MySimpleAdapter(this, showData, R.layout.list_view_item_1,
                new String[]{
                        "name", "price", "info", "recommend", "distance"
                },
                new int[]{
                        R.id.name, R.id.price, R.id.info, R.id.recommend, R.id.distance
                });
        listview.setAdapter(adapter);
        cursor.close();
    }

    private void initFood() {
        SQLiteDatabase db = ((DbManager) getApplicationContext()).getSQLiteDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM food", null);
        HashMap<String, Object> hashMap;
        while (cursor.moveToNext()) {
            hashMap = new HashMap<>();
            int id = cursor.getInt(cursor.getColumnIndex("f_id"));
            String name = cursor.getString(cursor.getColumnIndex("f_name"));
            String info = cursor.getString(cursor.getColumnIndex("f_reason"));
            int recommend = cursor.getInt(cursor.getColumnIndex("f_recommend"));
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex("f_image"));
            Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            img = Bitmap.createScaledBitmap(img, 600, 450, true);
            imageViews.add(getImageView(img));
            hashMap.put("id", id);
            hashMap.put("name", name);
            hashMap.put("info", info);
            hashMap.put("recommend", recommend);
            hashMap.put("img", img);
            dataList.add(hashMap);
        }
        showData.addAll(dataList);
        adapter = new MySimpleAdapter(this, showData, R.layout.list_view_item_2,
                new String[]{
                        "name", "info", "recommend"
                },
                new int[]{
                        R.id.name, R.id.info, R.id.recommend
                });
        listview.setAdapter(adapter);
        cursor.close();
    }

    private void initHotel() {
        SQLiteDatabase db = ((DbManager) getApplicationContext()).getSQLiteDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM hotel", null);
        HashMap<String, Object> hashMap;
        while (cursor.moveToNext()) {
            hashMap = new HashMap<>();
            int id = cursor.getInt(cursor.getColumnIndex("h_id"));
            double longitude = cursor.getDouble(cursor.getColumnIndex("h_longitude"));
            double latitude = cursor.getDouble(cursor.getColumnIndex("h_latitude"));
            double distance = (int) DistanceUtil.getDistance(meLatLng, new LatLng(latitude, longitude));
            String name = cursor.getString(cursor.getColumnIndex("h_name"));
            double price = cursor.getDouble(cursor.getColumnIndex("h_price"));
            String info = cursor.getString(cursor.getColumnIndex("h_reason"));
            int recommend = cursor.getInt(cursor.getColumnIndex("h_recommend"));
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex("h_image"));
            Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            img = Bitmap.createScaledBitmap(img, 600, 450, true);
            imageViews.add(getImageView(img));
            hashMap.put("id", id);
            hashMap.put("name", name);
            hashMap.put("price", price);
            hashMap.put("info", info);
            hashMap.put("recommend", recommend);
            hashMap.put("distance", distance * 1.0 / 1000);
            hashMap.put("img", img);
            dataList.add(hashMap);
        }
        showData.addAll(dataList);
        adapter = new MySimpleAdapter(this, showData, R.layout.list_view_item_1,
                new String[]{
                        "name", "price", "info", "recommend", "distance"
                },
                new int[]{
                        R.id.name, R.id.price, R.id.info, R.id.recommend, R.id.distance
                });
        listview.setAdapter(adapter);
        cursor.close();
    }

    /**
     * 票价排序
     */
    private void sortPrice() {
        priceOrder = !priceOrder;
        if (priceOrder) {
            sortPriceView.setText("票价  ▲");
        } else {
            sortPriceView.setText("票价  ▼");
        }
        sortRecommendView.setText("推荐");
        sortDistanceView.setText("距离");
        class SortByPrice implements Comparator {

            @Override
            public int compare(Object o1, Object o2) {
                double m1 = (double) ((HashMap) o1).get("price");
                double m2 = (double) ((HashMap) o2).get("price");
                if (priceOrder) {
                    return m1 > m2 ? 1 : -1;
                } else {
                    return m1 < m2 ? 1 : -1;
                }
            }
        }
        Collections.sort(showData, new SortByPrice());
        adapter.notifyDataSetChanged();
    }

    /**
     * 推荐值排序
     */
    private void sortRecommend() {
        recommendOrder = !recommendOrder;
        if (recommendOrder) {
            sortRecommendView.setText("推荐  ▲");
        } else {
            sortRecommendView.setText("推荐  ▼");
        }
        sortDistanceView.setText("距离");
        sortPriceView.setText("票价");
        class SortByRecommend implements Comparator {

            @Override
            public int compare(Object o1, Object o2) {
                int r1 = (int) ((HashMap) o1).get("recommend");
                int r2 = (int) ((HashMap) o2).get("recommend");
                if (recommendOrder) {
                    return r1 > r2 ? 1 : -1;
                } else {
                    return r1 < r2 ? 1 : -1;
                }
            }
        }
        Collections.sort(showData, new SortByRecommend());
        adapter.notifyDataSetChanged();
    }


    /**
     * 距离排序
     */
    private void sortDistance() {
        distanceOrder = !distanceOrder;
        if (distanceOrder) {
            sortDistanceView.setText("距离  ▲");
        } else {
            sortDistanceView.setText("距离  ▼");
        }
        sortRecommendView.setText("推荐");
        sortPriceView.setText("票价");
        class SortByDistance implements Comparator {

            @Override
            public int compare(Object o1, Object o2) {
                double d1 = (double) ((HashMap) o1).get("distance");
                double d2 = (double) ((HashMap) o2).get("distance");
                if (distanceOrder) {
                    return d1 > d2 ? 1 : -1;
                } else {
                    return d1 < d2 ? 1 : -1;
                }
            }
        }
        Collections.sort(showData, new SortByDistance());
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.sort_price:
                sortPrice();
                break;
            case R.id.sort_recommend:
                sortRecommend();
                break;
            case R.id.sort_distance:
                sortDistance();
                break;
        }
    }

    /**
     * 根据bitmap返回ImageView
     *
     * @param bitmap
     * @return
     */
    private ImageView getImageView(Bitmap bitmap) {
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

}
