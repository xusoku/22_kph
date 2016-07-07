package com.davis.kangpinhui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.model.Order;
import com.davis.kangpinhui.model.OrderDetail;
import com.davis.kangpinhui.model.Recharge;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.util.DateUtils;
import com.davis.kangpinhui.util.UtilText;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;

public class PayResultActivity extends BaseActivity {

    private String code = "";

    private LinearLayout pay_result_product, pay_result_chongzhi;

    private TextView
            pay_result_product_ordernumber,
            pay_result_product_people,
            pay_result_product_phone,
            pay_result_product_address,
            pay_result_product_time,
            pay_result_product_paytype,
            pay_result_product_paystatus,
            pay_result_product_paymoney,
            pay_result_kefu;

    private TextView
            pay_result_chongzhi_ordernum,
            pay_result_chongzhi_paymoney,
            pay_result_chongzhi_paytype,
            pay_result_chongzhi_paystatus,
            pay_result_kefu1;

    private TextView pay_result_text;
    private ImageView pay_result_iv;
    private boolean flag = false;
    private boolean isYue = false;

    public static void jumpPayResultActivity(Context cot, boolean flag) {
        if (AppApplication.isLogin(cot)) {
            Intent it = new Intent(cot, PayResultActivity.class);
            it.putExtra("flag", flag);
            cot.startActivity(it);
        }
    }

    public static void jumpPayResultActivity(Context cot, boolean flag, boolean isYue) {
        if (AppApplication.isLogin(cot)) {
            Intent it = new Intent(cot, PayResultActivity.class);
            it.putExtra("flag", flag);
            it.putExtra("isyue", isYue);
            cot.startActivity(it);
        }
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_pay_result;
    }

    @Override
    protected void initVariable() {
        flag = getIntent().getBooleanExtra("flag", false);
        isYue = getIntent().getBooleanExtra("isyue", false);
    }

    @Override
    protected void findViews() {

        showTopBar();
        setTitle("支付结果");

        pay_result_product = $(R.id.pay_result_product);
        pay_result_chongzhi = $(R.id.pay_result_chongzhi);

        pay_result_product_ordernumber = $(R.id.pay_result_product_ordernumber);
        pay_result_product_people = $(R.id.pay_result_product_people);
        pay_result_product_phone = $(R.id.pay_result_product_phone);
        pay_result_product_address = $(R.id.pay_result_product_address);
        pay_result_product_time = $(R.id.pay_result_product_time);
        pay_result_product_paytype = $(R.id.pay_result_product_paytype);
        pay_result_product_paystatus = $(R.id.pay_result_product_paystatus);
        pay_result_product_paymoney = $(R.id.pay_result_product_paymoney);
        pay_result_kefu = $(R.id.pay_result_kefu);

        pay_result_chongzhi_ordernum = $(R.id.pay_result_chongzhi_ordernum);
        pay_result_chongzhi_paymoney = $(R.id.pay_result_chongzhi_paymoney);
        pay_result_chongzhi_paytype = $(R.id.pay_result_chongzhi_paytype);
        pay_result_chongzhi_paystatus = $(R.id.pay_result_chongzhi_paystatus);
        pay_result_kefu1 = $(R.id.pay_result_kefu1);

        pay_result_iv = $(R.id.pay_result_iv);
        pay_result_text = $(R.id.pay_result_text);


        if (flag) {//支付结果
            pay_result_text.setTextColor(Color.parseColor("#ff4CAF50"));
            if (isYue) {
                pay_result_text.setText("恭喜您,充值订单支付成功");
            } else {
                pay_result_text.setText("订单支付成功,请注意查收货品");
            }
            pay_result_iv.setImageResource(R.mipmap.pay_result_ok);

        } else {
            pay_result_text.setTextColor(Color.parseColor("#ffff0000"));
            if (isYue) {
                pay_result_text.setText("充值订单支付失败");
            } else {
                pay_result_text.setText("订单支付失败");
            }
            pay_result_iv.setImageResource(R.mipmap.pay_result_fail);
        }

        code=AppApplication.getApplication().numberCode;
        if (isYue) {//支付类型
            getRechageInfo();
            pay_result_chongzhi.setVisibility(View.VISIBLE);
            pay_result_product.setVisibility(View.GONE);
            pay_result_kefu.setText("查看我的充值");
            pay_result_kefu1.setText("查看我的充值");

        } else {
            getProdactOrderinfo();
            pay_result_product.setVisibility(View.VISIBLE);
            pay_result_chongzhi.setVisibility(View.GONE);
            pay_result_kefu.setText("查看我的订单");
            pay_result_kefu1.setText("查看我的订单");

        }
    }

    public void getProdactOrderinfo() {
        Call<BaseModel<Order<ArrayList<OrderDetail>>>> call = ApiInstant.getInstant().myOrderDetail(AppApplication.apptype, code, AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<Order<ArrayList<OrderDetail>>>>() {
            @Override
            public void onSucssce(BaseModel<Order<ArrayList<OrderDetail>>> orderBaseModel) {
                onActivityLoadingSuccess();

                Order<ArrayList<OrderDetail>> orderDetailOrder = orderBaseModel.object;

                bindProdactOrderView(orderDetailOrder);
            }

            @Override
            public void onFailure() {
                onActivityLoadingFailed();
            }
        });
    }

    private void bindProdactOrderView(Order<ArrayList<OrderDetail>> itemData) {
        String payType = itemData.spaytype;
        if ((payType.equals("0") || payType.equals("4") || payType.equals("1")) && itemData.stype.equals("0")) {
            pay_result_product_paystatus.setText("待付款");
        } else if (!payType.equals("2") && itemData.stype.equals("1")) {
            pay_result_product_paystatus.setText("已支付");
        } else if (itemData.stype.equals("2") || itemData.stype.equals("1")) {
            pay_result_product_paystatus.setText("待配送");
        } else if (itemData.stype.equals("3")) {
            pay_result_product_paystatus.setText("配送中");
        } else if (itemData.stype.equals("4") || itemData.stype.equals("5")) {
            pay_result_product_paystatus.setText("已完成");
        } else if (itemData.stype.equals("6")) {
            pay_result_product_paystatus.setText("已关闭");
        } else if (payType.equals("2") && itemData.stype.equals("0")) {
            pay_result_product_paystatus.setText("待配送");
        } else {
            pay_result_product_paystatus.setText("未知");
        }
        CharSequence[] charSequences = {"余额支付", "货到付款", "支付宝支付", "微信支付"};
        String[] type = {"3", "2", "0", "4"};

        for (int i = 0; i < type.length; i++) {
            if (type[i].equals(itemData.spaytype)) {
                pay_result_product_paytype.setText(charSequences[i]);
            }
        }
        pay_result_product_people.setText(itemData.sconsignee);
        pay_result_product_phone.setText(itemData.smobile);
        pay_result_product_address.setText(itemData.saddress);
        pay_result_product_time.setText((itemData.ssendtime));
        pay_result_product_ordernumber.setText(itemData.sordernumber);
        Float f = Float.parseFloat(itemData.fvipmoney) + Float.parseFloat(itemData.fmoney) - Float.parseFloat(itemData.freturnmoney);
        pay_result_product_paymoney.setText("¥" + UtilText.getFloatToString(f + ""));
    }


    private void getRechageInfo() {
        Call<BaseModel<Recharge>> call = ApiInstant.getInstant().getRecharge(AppApplication.apptype,
                AppApplication.shopid, code, AppApplication.token);

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

        pay_result_chongzhi_paytype.setText(srechargetype);
        pay_result_chongzhi_paymoney.setText("¥" + UtilText.getRechargePrice(recharge.fmoney));
        pay_result_chongzhi_paystatus.setText(stype);
        pay_result_chongzhi_ordernum.setText(recharge.schargenumber);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    public void doClick(View view) {

        if (view.getId() == R.id.pay_result_kefu) {
            if (isYue) {
                RechargeListActivity.jumpRechargeListActivity(this);
            } else {
                AllOrderActivity.jumpAllOrderActivity(this, 0);
            }
            finish();
        }
        if (view.getId() == R.id.pay_result_kefu1) {
            if (isYue) {
                RechargeListActivity.jumpRechargeListActivity(this);
            } else {
                AllOrderActivity.jumpAllOrderActivity(this, 0);
            }
            finish();
        }
    }
}
