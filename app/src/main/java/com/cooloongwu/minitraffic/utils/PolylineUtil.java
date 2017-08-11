package com.cooloongwu.minitraffic.utils;

import android.util.ArrayMap;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

import java.util.List;
import java.util.Map;

/**
 * 路径集合
 * Created by CooLoongWu on 2017-8-10 10:53.
 */

public class PolylineUtil {

    private static Map<Integer, Polyline> polylines = new ArrayMap<>();
    private static Map<Integer, List<LatLng>> polylinePoints = new ArrayMap<>();


    public static void addPolylines(AMap aMap, List<LatLng> points) {
        Polyline polyline = aMap.addPolyline(
                new PolylineOptions()
                        .addAll(points)
                        .useGradient(false) // 速度变化
                        .width(8)
                        .color(ColorUtil.getRandomColor()));

        polylines.put(polylines.size(), polyline);
        polylinePoints.put(polylines.size(), points);
    }

    public static void addPointsToPolyline(AMap aMap) {

    }

    public static Map<Integer, Polyline> getPolylines() {
        return polylines;
    }

    public static Map<Integer, List<LatLng>> getPolylinePoints() {
        return polylinePoints;
    }
}
