package com.davis.kangpinhui.util;

import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.activity.ShopActivity;
import com.davis.kangpinhui.model.Address;
import com.davis.kangpinhui.model.Shop;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by davis on 16/5/30.
 */
public class LocalUtil implements AMapLocationListener {

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private Boolean flag=false;
    public LocalUtil(boolean flag) {

        this.flag=flag;
        locationClient = new AMapLocationClient(AppApplication.getApplication());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为低功耗模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        // 设置定位监听
        locationClient.setLocationListener(this);
        //设置为单次定位
        locationOption.setOnceLocation(true);
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        // 设置是否开启缓存
        locationOption.setLocationCacheEnable(true);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        getLocationStr(aMapLocation);
        onDestroy();
    }

    public void startLocation(){
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
        if(flag)
       ToastUitl.showToast("开始定位");
    }
    public void onDestroy() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }


    /**
     * 根据定位结果返回定位信息的字符串
     *
     * @return
     */
    public synchronized  String getLocationStr(AMapLocation location) {
        if (null == location) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if (location.getErrorCode() == 0) {
            sb.append("定位成功" + "\n");
            sb.append("定位类型: " + location.getLocationType() + "\n");
            sb.append("经    度    : " + location.getLongitude() + "\n");
            sb.append("纬    度    : " + location.getLatitude() + "\n");
            sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
            sb.append("提供者    : " + location.getProvider() + "\n");

            if (location.getProvider().equalsIgnoreCase(
                    android.location.LocationManager.GPS_PROVIDER)) {
                // 以下信息只有提供者是GPS时才会有
                sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                sb.append("角    度    : " + location.getBearing() + "\n");
                // 获取当前提供定位服务的卫星个数
                sb.append("星    数    : "
                        + location.getSatellites() + "\n");
            } else {
                // 提供者是GPS时是没有以下信息的
                sb.append("国    家    : " + location.getCountry() + "\n");
                sb.append("省            : " + location.getProvince() + "\n");
                sb.append("市            : " + location.getCity() + "\n");
                sb.append("城市编码 : " + location.getCityCode() + "\n");
                sb.append("区            : " + location.getDistrict() + "\n");
                sb.append("区域 码   : " + location.getAdCode() + "\n");
                sb.append("地    址    : " + location.getAddress() + "\n");
                sb.append("兴趣点    : " + location.getPoiName() + "\n");
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if(LocalUtil.getShopid(latLng, AppApplication.shoplist)){
                SharePreferenceUtils.getSharedPreferences().putString("address",location.getPoiName());
                AppManager.getAppManager().finishActivity(ShopActivity.class);
                EventBus.getDefault().post(new Address());

            }else{

                ToastUitl.showToast("您所在的范围不再配送区,请选择配送区域");
                if(!flag) {
                    AppManager.getAppManager().finishActivity(ShopActivity.class);
//                    EventBus.getDefault().post(new Address());
                }
            }


        } else {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
            ToastUitl.showToast("定位失败");
        }

        return sb.toString();
    }

    // 功能：判断点是否在多边形内
    // 方法：求解通过该点的水平线与多边形各边的交点
    // 结论：单边交点为奇数，成立!
    //参数：
    // POINT p   指定的某个点
    // LPPOINT ptPolygon 多边形的各个顶点坐标（首末点可以不一致）
    public static boolean PtInPolygon(LatLng point, List<LatLng> APoints) {
        int nCross = 0;
        for (int i = 0; i < APoints.size(); i++) {
            LatLng p1 = APoints.get(i);
            LatLng p2 = APoints.get((i + 1) % APoints.size());
            // 求解 y=p.y 与 p1p2 的交点
            if (p1.longitude == p2.longitude)      // p1p2 与 y=p0.y平行
                continue;
            if (point.longitude < Math.min(p1.longitude, p2.longitude))   // 交点在p1p2延长线上
                continue;
            if (point.longitude >= Math.max(p1.longitude, p2.longitude))   // 交点在p1p2延长线上
                continue;
            // 求交点的 X 坐标 --------------------------------------------------------------
            double x = (double) (point.longitude - p1.longitude) * (double) (p2.latitude - p1.latitude) / (double) (p2.longitude - p1.longitude) + p1.latitude;
            if (x > point.latitude)
                nCross++; // 只统计单边交点
        }
        // 单边交点为偶数，点在多边形之外 ---
        return (nCross % 2 == 1);
    }

    // 功能：判断点是否在多边形内 取shopid
    public static boolean getShopid(LatLng point, List<Shop> list) {

        for (Shop shop : list) {
            if (shop.polygon!=null&&PtInPolygon(point, createDDRectangle(shop.polygon))) {
                AppApplication.shopid = shop.id;
                SharePreferenceUtils.getSharedPreferences("kph").putString("shopid", shop.id);
                return true;
            }
        }
        return false;
    }

    /**
     * 生成一个多边形
     */
    private static List<LatLng> createDDRectangle(String pot) {
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
