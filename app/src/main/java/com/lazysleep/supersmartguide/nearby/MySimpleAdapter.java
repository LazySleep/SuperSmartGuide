package com.lazysleep.supersmartguide.nearby;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.lazysleep.supersmartguide.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MySimpleAdapter extends SimpleAdapter {

    private List<HashMap<String, Object>> ItemData;

    public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data,
                           int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        ItemData = (List<HashMap<String, Object>>) data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ImageView imageView = view.findViewById(R.id.img);
        HashMap hashMap = ItemData.get(position);
        imageView.setImageBitmap((Bitmap) hashMap.get("img"));
        view.setTag(hashMap.get("id"));
        return view;
    }
}
