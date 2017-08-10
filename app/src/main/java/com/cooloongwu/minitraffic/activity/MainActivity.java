package com.cooloongwu.minitraffic.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.cooloongwu.minitraffic.R;
import com.cooloongwu.minitraffic.adapter.MyInfoWindowAdapter;
import com.cooloongwu.minitraffic.utils.PolylineUtil;
import com.cooloongwu.minitraffic.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements RouteSearch.OnRouteSearchListener {

    private MapView mapView = null;
    private Button btn_plan, btn_start, plan_from, plan_to;
    private boolean isClickFrom = false;
    private AMap aMap = null;

    private LatLng latLngFrom;
    private LatLng latLngTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map);
        btn_plan = (Button) findViewById(R.id.btn_plan);
        btn_start = (Button) findViewById(R.id.btn_start);
        plan_from = (Button) findViewById(R.id.plan_from);
        plan_to = (Button) findViewById(R.id.plan_to);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);

        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mapView.getMap();
        }

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


        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.e("地图点击", latLng.toString());
                if (isClickFrom) {
                    latLngFrom = latLng;
                } else {
                    latLngTo = latLng;
                }
            }
        });


        btn_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latLngFrom == null || latLngTo == null) {
                    ToastUtil.showShort(MainActivity.this, "请先设置起点和终点");
                } else {
                    //路线搜索
                    RouteSearch routeSearch = new RouteSearch(MainActivity.this);
                    routeSearch.setRouteSearchListener(MainActivity.this);
                    // fromAndTo包含路径规划的起点和终点，drivingMode表示驾车模式
                    // 第三个参数表示途经点（最多支持16个），第四个参数表示避让区域（最多支持32个），第五个参数表示避让道路

                    RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                            new LatLonPoint(latLngFrom.latitude, latLngFrom.longitude),
                            new LatLonPoint(latLngTo.latitude, latLngTo.longitude));
                    RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null, null, "");
                    routeSearch.calculateDriveRouteAsyn(query);
                }

            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMove();
            }
        });

        plan_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClickFrom = true;
            }
        });

        plan_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClickFrom = false;
            }
        });
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

        Map<Integer, List<LatLng>> polylinePoints = PolylineUtil.getPolylinePoints();

        if (PolylineUtil.getPolylines().isEmpty()) {
            ToastUtil.showShort(this, "请先设置路线");
            return;
        }

        for (List<LatLng> points : polylinePoints.values()) {
            // 实例 SmoothMoveMarker 对象
            SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
            // 设置 平滑移动的 图标
            smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.icon_traffic_car));

            // 设置轨迹点
            smoothMarker.setPoints(points);
            // 设置平滑移动的总时间  单位  秒
            smoothMarker.setTotalDuration(100);

            // 设置  自定义的InfoWindow 适配器
            final MyInfoWindowAdapter infoWindowAdapter = new MyInfoWindowAdapter(this);
            aMap.setInfoWindowAdapter(infoWindowAdapter);
            // 显示 infoWindow
            smoothMarker.getMarker().showInfoWindow();

            // 设置移动的监听事件  返回 距终点的距离  单位 米
            smoothMarker.setMoveListener(new SmoothMoveMarker.MoveListener() {
                @Override
                public void move(final double distance) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (infoWindowAdapter.getTitleView() != null) {
                                infoWindowAdapter.setTitle("距离终点还有： " + (int) distance + "米");
                            }
                        }
                    });

                }
            });

            // 开始移动
            smoothMarker.startSmoothMove();
        }


        // 取轨迹点的第一个点 作为 平滑移动的启动
//        LatLng drivePoint = points.get(0);
//        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
//        points.set(pair.first, drivePoint);
//        List<LatLng> subList = points.subList(pair.first, points.size());


    }


    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
        Log.e("公交路线", "" + busRouteResult.toString());
    }


    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        Log.e("驾车路线", "" + result.getPaths().size());
        List<LatLng> points = new ArrayList<>();
        DrivePath drivePath = result.getPaths().get(0);
        List<DriveStep> driveSteps = drivePath.getSteps();
        for (DriveStep driveStep : driveSteps) {
            List<LatLonPoint> latLonPoints = driveStep.getPolyline();
            for (LatLonPoint latLonPoint : latLonPoints) {
                points.add(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()));
            }
        }

        PolylineUtil.addPolylines(aMap, points);
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

