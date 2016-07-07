package com.davis.kangpinhui;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.amap.api.services.core.PoiItem;
import com.davis.kangpinhui.model.Address;
import com.davis.kangpinhui.model.Category;
import com.davis.kangpinhui.model.Extendedinfo;
import com.davis.kangpinhui.model.Shop;
import com.davis.kangpinhui.model.UserInfo;
import com.davis.kangpinhui.activity.LoginActivity;
import com.davis.kangpinhui.util.SharePreferenceUtils;
import com.davis.kangpinhui.util.WeiXinUtil;
import com.facebook.stetho.Stetho;
import com.tencent.mm.sdk.openapi.IWXAPI;

import java.util.ArrayList;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by davis on 16/5/18.
 */
public class AppApplication extends Application {

    /**
     * 全局context单例
     */
    private static AppApplication instance  = null;

    public static String apptype = "android";
    public static String shopid = "";
    public static String token = "";
    public static UserInfo userInfo;
    public static Extendedinfo extendedinfo;
    public static Address address;

    public static String kefu="4001106648";

    public static PoiItem poiItem;

    public static ArrayList<Shop> shoplist=new ArrayList<>();

    public static ArrayList<Category> classiclist = new ArrayList<>();

    public static IWXAPI wxApi;

    //充值还是支付
    public  boolean  isYue=false;
    //订单号
    public String numberCode="";

    public static AppApplication getApplication() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
//        instance
        instance = (AppApplication) getApplicationContext();

        token=SharePreferenceUtils.getSharedPreferences().getString("token","");
        shopid=SharePreferenceUtils.getSharedPreferences().getString("shopid","");


        wxApi = WeiXinUtil.WXInit(getApplicationContext());

        ShareSDK.initSDK(this);
    }

    public static boolean isLogin(Context context) {
         token=SharePreferenceUtils.getSharedPreferences().getString("token","");
        if (TextUtils.isEmpty(token)) {
            LoginActivity.jumpLoginActivity(instance);
            return false;
        } else {
            return  true;
        }
    }

    public static String getCouponcount() {
        if (extendedinfo!=null) {
            return extendedinfo.couponcount;
        } else {
            return  "";
        }
    }
    public static String getCartcount() {
        if (extendedinfo!=null) {
            return extendedinfo.cartcount;
        } else {
            return  "";
        }
    }
    public static void setCartcount(String s) {
        if (extendedinfo!=null) {
            extendedinfo.cartcount=s;
        }
    }
    public static String getOrderall() {
        if (extendedinfo!=null) {
            return extendedinfo.orderall;
        } else {
            return  "";
        }
    }
    public static String getOrdersending() {
        if (extendedinfo!=null) {
            return extendedinfo.ordersending;
        } else {
            return  "";
        }
    }
    public static String getOrderunpaid() {
        if (extendedinfo!=null) {
            return extendedinfo.orderunpaid;
        } else {
            return  "";
        }
    }
    public static String getOrderwaitsend() {
        if (extendedinfo!=null) {
            return extendedinfo.orderwaitsend;
        } else {
            return  "";
        }
    }

}
