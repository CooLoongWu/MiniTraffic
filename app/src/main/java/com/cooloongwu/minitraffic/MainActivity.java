package com.cooloongwu.minitraffic;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RouteSearch.OnRouteSearchListener {

    private MapView mapView = null;
    private Button btn, plan;
    private AMap aMap = null;

    private Polyline mPolyline;

    //轨迹点
    List<LatLng> points = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map);
        btn = (Button) findViewById(R.id.btn);
        plan = (Button) findViewById(R.id.plan);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);

        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mapView.getMap();
        }


        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.e("地图点击", latLng.toString());
                points.add(new LatLng(latLng.latitude, latLng.longitude));

            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (points.isEmpty()) {
                    ToastUtil.showShort(MainActivity.this, "请先设置路线");
                } else {
                    startMove();
                }
            }
        });

        plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (points.isEmpty() || points.size() < 2) {
                    ToastUtil.showShort(MainActivity.this, "请先设置路线");
                } else {
                    //路线搜索
                    RouteSearch routeSearch = new RouteSearch(MainActivity.this);
                    routeSearch.setRouteSearchListener(MainActivity.this);
                    // fromAndTo包含路径规划的起点和终点，drivingMode表示驾车模式
                    // 第三个参数表示途经点（最多支持16个），第四个参数表示避让区域（最多支持32个），第五个参数表示避让道路

                    RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                            new LatLonPoint(points.get(0).latitude, points.get(0).longitude),
                            new LatLonPoint(points.get(1).latitude, points.get(1).longitude));
                    RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null, null, "");
                    routeSearch.calculateDriveRouteAsyn(query);
                }


            }
        });

        aMap.showIndoorMap(false);  //不显示室内地图
        aMap.setMaxZoomLevel(14);
        aMap.setMinZoomLevel(12);
        UiSettings uiSettings = aMap.getUiSettings();
        //缩放控件
        uiSettings.setZoomControlsEnabled(false);

        //各种手势
        //uiSettings.setAllGesturesEnabled(false);
        uiSettings.setZoomGesturesEnabled(true);//缩放手势
        uiSettings.setScrollGesturesEnabled(true);//滑动手势


    }

    @Override
    protected void onResume() {
        super.onResume();
        //重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁地图
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 开始移动
     */
    public void startMove() {

        if (mPolyline == null) {
            ToastUtil.showShort(this, "请先设置路线");
            return;
        }
        // 构建 轨迹的显示区域
        LatLngBounds bounds = new LatLngBounds(points.get(0), points.get(points.size() - 2));
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

        // 实例 SmoothMoveMarker 对象
        SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
        // 设置 平滑移动的 图标
        smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.icon_traffic_car));

        // 取轨迹点的第一个点 作为 平滑移动的启动
        LatLng drivePoint = points.get(0);
        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());

        // 设置轨迹点
        smoothMarker.setPoints(subList);
        // 设置平滑移动的总时间  单位  秒
        smoothMarker.setTotalDuration(100);

        // 设置  自定义的InfoWindow 适配器
        aMap.setInfoWindowAdapter(infoWindowAdapter);
        // 显示 infowindow
        smoothMarker.getMarker().showInfoWindow();

        // 设置移动的监听事件  返回 距终点的距离  单位 米
        smoothMarker.setMoveListener(new SmoothMoveMarker.MoveListener() {
            @Override
            public void move(final double distance) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (infoWindowLayout != null && title != null) {

                            title.setText("距离终点还有： " + (int) distance + "米");
                        }
                    }
                });

            }
        });

        // 开始移动
        smoothMarker.startSmoothMove();

    }

    /**
     * 个性化定制的信息窗口视图的类
     * 如果要定制化渲染这个信息窗口，需要重载getInfoWindow(Marker)方法。
     * 如果只是需要替换信息窗口的内容，则需要重载getInfoContents(Marker)方法。
     */
    AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter() {

        // 个性化Marker的InfoWindow 视图
        // 如果这个方法返回null，则将会使用默认的信息窗口风格，内容将会调用getInfoContents(Marker)方法获取
        @Override
        public View getInfoWindow(Marker marker) {

            return getInfoWindowView(marker);
        }

        // 这个方法只有在getInfoWindow(Marker)返回null 时才会被调用
        // 定制化的view 做这个信息窗口的内容，如果返回null 将以默认内容渲染
        @Override
        public View getInfoContents(Marker marker) {

            return getInfoWindowView(marker);
        }
    };

    LinearLayout infoWindowLayout;
    TextView title;
    TextView snippet;

    /**
     * 自定义View并且绑定数据方法
     *
     * @param marker 点击的Marker对象
     * @return 返回自定义窗口的视图
     */
    private View getInfoWindowView(Marker marker) {
        if (infoWindowLayout == null) {
            infoWindowLayout = new LinearLayout(this);
            infoWindowLayout.setOrientation(LinearLayout.VERTICAL);
            title = new TextView(this);
            snippet = new TextView(this);
            title.setTextColor(Color.BLACK);
            snippet.setTextColor(Color.BLACK);
            infoWindowLayout.setBackgroundResource(R.drawable.bg);

            infoWindowLayout.addView(title);
            infoWindowLayout.addView(snippet);
        }

        return infoWindowLayout;
    }

    /**
     * 添加轨迹线
     */
    private void addPolyline() {
        mPolyline = aMap.addPolyline(
                new PolylineOptions()
                        .addAll(points)
                        .useGradient(true)
                        .width(8)
                        .color(R.color.test));
    }


    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
        Log.e("公交路线", "" + busRouteResult.toString());
    }


    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        Log.e("驾车路线", "" + result.getPaths().size());

        points.clear();
        DrivePath drivePath = result.getPaths().get(0);
        List<DriveStep> driveSteps = drivePath.getSteps();
        for (DriveStep driveStep : driveSteps) {
            List<LatLonPoint> latLonPoints = driveStep.getPolyline();
            for (LatLonPoint latLonPoint : latLonPoints) {
                points.add(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()));
            }
        }

        addPolyline();
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        Log.e("走路路线", "" + walkRouteResult.toString());
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
        Log.e("骑车路线", "" + rideRouteResult.toString());
    }
}

