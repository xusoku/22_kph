package com.davis.kangpinhui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Polygon;
import com.amap.api.maps2d.model.PolygonOptions;
import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.model.Shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AMapV1地图中简单介绍一些Polygon的用法.
 */
public class PolygonActivity extends BaseActivity {
    private AMap aMap;
    private MapView mapView;
    private Polygon polygon;

    private ArrayList<Shop> list;


    public static void jumpPolygonActivity(Context cot, ArrayList<Shop> list) {
        Intent it = new Intent(cot, PolygonActivity.class);
        it.putParcelableArrayListExtra("list", list);
        cot.startActivity(it);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();

    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_polygon;
    }

    @Override
    protected void initVariable() {
        list = getIntent().getParcelableArrayListExtra("list");
    }

    @Override
    protected void findViews() {

        showTopBar();
        setTitle("配送区域");

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    public void doClick(View view) {

    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {

        String [] s = list.get(0).center.split(",");
        LatLng latLng = new LatLng(Double.parseDouble(s[1]), Double.parseDouble(s[0]));

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));// 设置指定的可视区域地图


        for (Shop shop: list){
            if(shop.polygon!=null)
            aMap.addPolygon(new PolygonOptions().addAll(createDDRectangle(shop.polygon)).fillColor(Color.parseColor("#550192dc"))
                    .strokeColor(Color.RED).strokeWidth(1));
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    /**
     * 生成一个多边形
     */
    private List<LatLng> createDRectangle() {
        List<LatLng> list = new ArrayList<>();
        String pot = "121.501493,31.132901;121.500034,31.148475;121.560674,31.169188;121.571531,31.144949;121.567154,31.11828;121.526127,31.121035;121.516213,31.134958";
        String[] strs = pot.split(";");
        for (String str : strs) {
            String[] s = str.split(",");
            LatLng latLng = new LatLng(Double.parseDouble(s[1]), Double.parseDouble(s[0]));
            list.add(latLng);
        }
        return list;
    }

    /**
     * 生成一个多边形
     */
    private List<LatLng> createDDRectangle(String pot) {
        List<LatLng> list = new ArrayList<>();
        String[] strs = pot.split(";");
        for (String str : strs) {
            String[] s = str.split(",");
            LatLng latLng = new LatLng(Double.parseDouble(s[1]), Double.parseDouble(s[0]));
            list.add(latLng);
        }
        return list;
    }


}
