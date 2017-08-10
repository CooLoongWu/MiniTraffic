package com.cooloongwu.minitraffic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.cooloongwu.minitraffic.R;

/**
 * 窗口视图
 * Created by CooLoongWu on 2017-8-10 10:28.
 */

public class MyInfoWindowAdapter implements AMap.InfoWindowAdapter {

    private Context context;
    private LinearLayout infoWindowLayout;
    private TextView tv_title;
    private TextView tv_snippet;

    public TextView getTitleView() {
        return tv_title;
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public MyInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return getInfoWindowView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return getInfoWindowView(marker);
    }


    /**
     * 自定义InfoWindowView并且绑定数据方法
     *
     * @param marker 点击的Marker对象
     * @return 返回自定义窗口的视图
     */
    private View getInfoWindowView(Marker marker) {
        if (infoWindowLayout == null) {
            infoWindowLayout = new LinearLayout(context);
            infoWindowLayout.setOrientation(LinearLayout.VERTICAL);
            tv_title = new TextView(context);
            tv_snippet = new TextView(context);
            tv_title.setTextColor(Color.BLACK);
            tv_snippet.setTextColor(Color.BLACK);
            infoWindowLayout.setBackgroundResource(R.drawable.bg);

            infoWindowLayout.addView(tv_title);
            infoWindowLayout.addView(tv_snippet);
        }

        return infoWindowLayout;
    }
}
