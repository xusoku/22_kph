package com.davis.kangpinhui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.AllOrderActivity;
import com.davis.kangpinhui.activity.CartListActivity;
import com.davis.kangpinhui.activity.CouponActivity;
import com.davis.kangpinhui.activity.FeedBackActivity;
import com.davis.kangpinhui.activity.HistroyBillActivity;
import com.davis.kangpinhui.activity.MyAddressActivity;
import com.davis.kangpinhui.activity.MyTiHuoActivity;
import com.davis.kangpinhui.activity.RechargeActivity;
import com.davis.kangpinhui.activity.RechargeListActivity;
import com.davis.kangpinhui.activity.SettingActivity;
import com.davis.kangpinhui.fragment.base.BaseFragment;
import com.davis.kangpinhui.model.Extendedinfo;
import com.davis.kangpinhui.model.UserInfo;
import com.davis.kangpinhui.util.CommonManager;
import com.davis.kangpinhui.util.GlideCircleTransform;
import com.davis.kangpinhui.util.LogUtils;
import com.davis.kangpinhui.util.SharePreferenceUtils;
import com.davis.kangpinhui.util.UtilText;
import com.davis.kangpinhui.views.CustomAlterDialog;
import com.davis.kangpinhui.views.MineCustomLayout;
import com.davis.kangpinhui.views.MySwipeRefreshLayout;

import de.greenrobot.event.EventBus;

/**
 * Created by davis on 16/5/19.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    private MineCustomLayout mine_allorder;
    private MineCustomLayout mine_feedback_linear, mine_histroy_bill, mine_rechange, mine_setting, mine_kefu, mine_ti_huo, mine_myaddress, mine_mycoup, mine_cart, mine_allorder_sending,
            mine_allorder_unpay,mine_allorder_unsend;
    private TextView fragment_mine_name,fragment_mine_price,fragment_mine_price_info;

    private ImageView fragment_mine_photo;

    private MySwipeRefreshLayout mine_swipe;

    @Override
    protected void initVariable() {

    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void findViews(View view) {

        fragment_mine_name = $(R.id.fragment_mine_name);
        mine_allorder_unsend = $(R.id.mine_allorder_unsend);
        fragment_mine_photo = $(R.id.fragment_mine_photo);
        mine_allorder = $(R.id.mine_allorder);
        mine_feedback_linear = $(R.id.mine_feedback_linear);
        mine_histroy_bill = $(R.id.mine_histroy_bill);
        mine_rechange = $(R.id.mine_rechange);
        mine_setting = $(R.id.mine_setting);
        mine_kefu = $(R.id.mine_kefu);
        mine_swipe = $(R.id.mine_refresh);
        mine_ti_huo = $(R.id.mine_ti_huo);
        mine_myaddress = $(R.id.mine_myaddress);
        mine_mycoup = $(R.id.mine_mycoup);
        mine_cart = $(R.id.mine_cart);
        mine_allorder_sending = $(R.id.mine_allorder_sending);
        mine_allorder_unpay = $(R.id.mine_allorder_unpay);
        fragment_mine_price = $(R.id.fragment_mine_price);
        fragment_mine_price_info = $(R.id.fragment_mine_price_info);

//        String str=SharePreferenceUtils.getSharedPreferences().getString("pic","");
//        Glide.with(this).load("http://m.kangpinhui.com/images/person/"+str+".jpg")
//                .transform(new GlideCircleTransform(getActivity()))
//                .placeholder(R.mipmap.mine_user_defualt)
//                .error(R.mipmap.mine_user_defualt)
//                .into(fragment_mine_photo);
        CommonManager.setRefreshingState(mine_swipe, false);
        mine_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CommonManager.setRefreshingState(mine_swipe, true);
                EventBus.getDefault().post(new Extendedinfo());
                mine_allorder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CommonManager.setRefreshingState(mine_swipe, false);
                    }
                },1000);
            }
        });
    }

    @Override
    protected void initData() {

        String name= SharePreferenceUtils.getSharedPreferences("kph").getString("nickname","");
        String token= SharePreferenceUtils.getSharedPreferences("kph").getString("token","");
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(token)){
            if(TextUtils.isDigitsOnly(name)){
                name=name.substring(0,3)+"****"+name.substring(7,11);
            }
            fragment_mine_name.setText(name);
        }
        String str=SharePreferenceUtils.getSharedPreferences().getString("pic","");
        LogUtils.e(TAG, "==" + str);
        Glide.with(this).load("http://m.kangpinhui.com/images/person/"+str+".jpg")
                .transform(new GlideCircleTransform(getActivity()))
                .placeholder(R.mipmap.mine_user_defualt)
                .error(R.mipmap.mine_user_defualt)
                .into(fragment_mine_photo);
    }

    public void setUi(UserInfo userInfo){
        String name=userInfo.snickname;
        if(!TextUtils.isEmpty(name)){
            if(TextUtils.isDigitsOnly(name)){
                name=name.substring(0,3)+"****"+name.substring(7,11);
            }
            fragment_mine_name.setText(name);

            LogUtils.e(TAG, "==" + userInfo.iuserid);
            Glide.with(this).load("http://m.kangpinhui.com/images/person/"+userInfo.iuserid+".jpg")
                    .transform(new GlideCircleTransform(getActivity()))
                    .placeholder(R.mipmap.mine_user_defualt)
                    .error(R.mipmap.mine_user_defualt)
                    .into(fragment_mine_photo);
        }
    }
    public void setNumber(){
        if(mine_allorder_unpay!=null) {
            CommonManager.setRefreshingState(mine_swipe, false);
            mine_allorder_unpay.setText(UtilText.getminenumber("待付款  (" + AppApplication.getOrderunpaid() + ")"));
            mine_allorder_sending.setText(UtilText.getminenumber("配送中 (" + AppApplication.getOrdersending() + ")"));
            mine_allorder_unsend.setText(UtilText.getminenumber("待配送 (" + AppApplication.getOrderwaitsend() + ")"));
            mine_cart.setText(UtilText.getminenumber("购物车 (" + (int)Float.parseFloat(AppApplication.getCartcount()) + ")"));
            mine_mycoup.setText(("我的优惠券 (" + AppApplication.getCouponcount() + "张)"));
        }
    }

    public void setLoginout(){

        if(mine_allorder_unpay!=null) {
            mine_allorder_unpay.setText(UtilText.getminenumber("待付款"));
            mine_allorder_sending.setText(UtilText.getminenumber("配送中"));
            mine_allorder_unsend.setText(UtilText.getminenumber("待配送"));
            mine_cart.setText(UtilText.getminenumber("购物车"));
            mine_mycoup.setText(("我的优惠券"));
            fragment_mine_name.setText("注册/登录");
            Glide.with(this).load("")
                    .transform(new GlideCircleTransform(getActivity()))
                    .placeholder(R.mipmap.mine_user_defualt)
                    .error(R.mipmap.mine_user_defualt)
                    .into(fragment_mine_photo);
        }
    }



    @Override
    protected void setListener() {
        mine_allorder.setOnClickListener(this);
        mine_feedback_linear.setOnClickListener(this);
        mine_histroy_bill.setOnClickListener(this);
        mine_rechange.setOnClickListener(this);
        mine_setting.setOnClickListener(this);
        mine_kefu.setOnClickListener(this);
        mine_ti_huo.setOnClickListener(this);
        mine_myaddress.setOnClickListener(this);
        mine_mycoup.setOnClickListener(this);
        mine_cart.setOnClickListener(this);
        mine_allorder_sending.setOnClickListener(this);
        mine_allorder_unpay.setOnClickListener(this);
        mine_allorder_unsend.setOnClickListener(this);
        fragment_mine_price_info.setOnClickListener(this);
        fragment_mine_price.setOnClickListener(this);
        fragment_mine_photo.setOnClickListener(this);
        fragment_mine_name.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_allorder:
                AllOrderActivity.jumpAllOrderActivity(getActivity(),0);
                break;
            case R.id.mine_feedback_linear:
                FeedBackActivity.jumpFeedBackActivity(getActivity());
                break;
            case R.id.mine_histroy_bill:
                HistroyBillActivity.jumpHistroyBillActivity(getActivity());
                break;
            case R.id.mine_rechange:
                RechargeListActivity.jumpRechargeListActivity(getActivity());
                break;
            case R.id.mine_setting:
                SettingActivity.jumpSettingActivity(getActivity());
                break;
            case R.id.mine_kefu:
                final CustomAlterDialog dialog=new CustomAlterDialog(getActivity());
                dialog.setTitle("联系客服");
                dialog.setContent_text("客服电话：" + AppApplication.kefu);
                dialog .setConfirmButton("呼叫", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + AppApplication.kefu));
                        startActivity(intent);
                    }
                }).setCancelButton("取消");
                break;
            case R.id.mine_ti_huo:
                MyTiHuoActivity.jumpMyTiHuoActivity(getActivity());
                break;
            case R.id.mine_myaddress:
                MyAddressActivity.jumpMyAddressActivity(getActivity());
                break;
            case R.id.mine_mycoup:
                CouponActivity.jumpCouponActivity(getActivity(), 0);
                break;
            case R.id.mine_cart:
                CartListActivity.jumpCartListActivity(getActivity());
                break;
            case R.id.mine_allorder_sending:
                AllOrderActivity.jumpAllOrderActivity(getActivity(), 3);
                break;
            case R.id.mine_allorder_unpay:
                AllOrderActivity.jumpAllOrderActivity(getActivity(), 1);
                break;
            case R.id.mine_allorder_unsend:
                AllOrderActivity.jumpAllOrderActivity(getActivity(), 2);
                break;
            case R.id.fragment_mine_price:
                RechargeActivity.jumpRechangeActivity(getActivity());
                break;
            case R.id.fragment_mine_price_info:
                RechargeActivity.jumpRechangeActivity(getActivity());
                break;
            case R.id.fragment_mine_photo:
                AppApplication.isLogin(getActivity());
                break;
            case R.id.fragment_mine_name:
                AppApplication.isLogin(getActivity());
                break;
        }
    }


}
