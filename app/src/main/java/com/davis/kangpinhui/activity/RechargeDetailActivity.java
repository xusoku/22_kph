package com.davis.kangpinhui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.model.Recharge;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.util.UtilText;

import retrofit2.Call;

public class RechargeDetailActivity extends BaseActivity {

    private TextView
            recharge_detail_orderType,
            recharge_detail_ordertime,
            recharge_detail_orderprice,
            recharge_detail_orderPayType,
            recharge_detail_orderCode,
            recharge_detail_letterhead,
            recharge_detail_ordername,
            recharge_detail_orderphone;
private LinearLayout recharge_detail_letterlinear;

    private String id = "";

    public static void jumpRechargeDetailActivity(Context cot, String id) {
        if (AppApplication.isLogin(cot)) {
            Intent it = new Intent(cot, RechargeDetailActivity.class);
            it.putExtra("id", id);
            cot.startActivity(it);
        }
    }


    @Override
    protected int setLayoutView() {
        return R.layout.activity_recharge_detail;
    }

    @Override
    protected void initVariable() {

        id = getIntent().getStringExtra("id");
    }

    @Override
    protected void findViews() {
        showTopBar();
        setTitle("订单详情");
        recharge_detail_orderType = $(R.id.recharge_detail_orderType);
        recharge_detail_ordertime = $(R.id.recharge_detail_ordertime);
        recharge_detail_orderprice = $(R.id.recharge_detail_orderprice);
        recharge_detail_orderPayType = $(R.id.recharge_detail_orderPayType);
        recharge_detail_orderCode = $(R.id.recharge_detail_orderCode);
        recharge_detail_letterhead = $(R.id.recharge_detail_letterhead);
        recharge_detail_ordername = $(R.id.recharge_detail_ordername);
        recharge_detail_orderphone = $(R.id.recharge_detail_orderphone);
        recharge_detail_letterlinear = $(R.id.recharge_detail_letterlinear);
    }

    @Override
    protected void initData() {
        startActivityLoading();
    }

    @Override
    public void startActivityLoading() {
        super.startActivityLoading();

        Call<BaseModel<Recharge>> call = ApiInstant.getInstant().getRecharge(AppApplication.apptype,
                AppApplication.shopid, id, AppApplication.token);

        call.enqueue(new ApiCallback<BaseModel<Recharge>>() {
            @Override
            public void onSucssce(BaseModel<Recharge> rechargeBaseModel) {
                onActivityLoadingSuccess();

                bindView(rechargeBaseModel.object);
            }

            @Override
            public void onFailure() {

                onActivityLoadingFailed();
            }
        });
    }

    private void bindView(Recharge recharge) {

//                srechargetype ：//付款方式  0:'现金' 4: '微信'   2:'支付宝'
//
//                Stype：订单状态  0：未付款成功， 3：已经付款成功，6已取消订单
//
        String srechargetype = "";
        if (recharge.srechargetype.equals("0")) {
            srechargetype = "现金";
        } else if (recharge.srechargetype.equals("4")) {
            srechargetype = "微信支付";
        } else if (recharge.srechargetype.equals("2")) {
            srechargetype = "支付宝支付";
        }
        String stype = "";
        if (recharge.stype.equals("0")) {
            stype = "未付款";
        } else if (recharge.stype.equals("3")) {
            stype = "已付款";
        } else if (recharge.stype.equals("6")) {
            stype = "已取消";
        }
        recharge_detail_orderType.setText(stype);
        recharge_detail_ordertime.setText(recharge.daddtime);
        recharge_detail_orderprice.setText("¥"+ UtilText.getRechargePrice(recharge.fmoney));
        recharge_detail_orderPayType.setText(srechargetype);
        recharge_detail_orderCode.setText(recharge.schargenumber);
        if(TextUtils.isEmpty(recharge.sinvoice)){
            recharge_detail_letterlinear.setVisibility(View.GONE);
        }else{
            recharge_detail_letterlinear.setVisibility(View.VISIBLE);
            recharge_detail_letterhead.setText(recharge.sinvoice);
            recharge_detail_ordername.setText(recharge.sconsignee);
            recharge_detail_orderphone.setText(recharge.smobile);
        }


    }

    @Override
    protected void setListener() {

    }

    @Override
    public void doClick(View view) {

    }
}
