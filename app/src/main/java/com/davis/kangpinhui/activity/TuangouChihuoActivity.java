package com.davis.kangpinhui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.api.ApiService;
import com.davis.kangpinhui.model.Order;
import com.davis.kangpinhui.model.OrderDetail;
import com.davis.kangpinhui.model.WeixinInfo;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.util.AppManager;
import com.davis.kangpinhui.util.DisplayMetricsUtils;
import com.davis.kangpinhui.util.NetWorkUtils;
import com.davis.kangpinhui.util.ThridPayUtil;
import com.davis.kangpinhui.util.ToastUitl;
import com.davis.kangpinhui.views.CustomTypefaceEditText;
import com.davis.kangpinhui.views.XWebView;

import java.util.ArrayList;

import retrofit2.Call;

public class TuangouChihuoActivity extends BaseActivity {

    private XWebView tuangou_web;
    private LinearLayout product_detail_back;
    private ThridPayUtil thridPayUtil;
    public static void jumpTuangouChihuoActivity(Context cot) {
            Intent it = new Intent(cot, TuangouChihuoActivity.class);
            cot.startActivity(it);
    }
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what==1){
                String s= (String) msg.obj;
                getDetailOrder(s);
            }
        }
    };
    @Override
    protected int setLayoutView() {
        return R.layout.activity_tuangou_chihuo;
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void findViews() {
        thridPayUtil = new ThridPayUtil(this);
        product_detail_back=$(R.id.product_detail_back);
        tuangou_web=$(R.id.content);

    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void initData() {

        startActivityLoading();
    }

    @Override
    protected void onActivityLoading() {
        super.onActivityLoading();
        if(NetWorkUtils.isAvailable(this)){
            onActivityLoadingSuccess();
            tuangou_web.loadUrl(ApiService.baseurl+"/weixin/tuanforapp.jsp?token="+AppApplication.token+"&apptype=android");
            tuangou_web.addJavascriptInterface(new DemoJavaScriptInterface(), "android");
        }else{
            onActivityLoadingFailed();
        }
    }

    final class DemoJavaScriptInterface {
        DemoJavaScriptInterface() {
        }
        @JavascriptInterface
        public void goAndroidPay(String s) {        // 注意这里的名称。它为clickOnAndroid(),注意，注意，严重注意
            Message message = new Message();
            message.what = 1;
            message.obj=s.toString();
            handler.sendMessage(message);

        }
    }
    @Override
    protected void setListener() {
    }

    @Override
    public void doClick(View view) {

        switch (view.getId()){
            case R.id.product_detail_back:
                finish();
                break;
        }
    }

    private void getDetailOrder(String code){
        Call<BaseModel<Order<ArrayList<OrderDetail>>>> call = ApiInstant.getInstant().myOrderDetail(AppApplication.apptype, code, AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<Order<ArrayList<OrderDetail>>>>() {
            @Override
            public void onSucssce(BaseModel<Order<ArrayList<OrderDetail>>> orderBaseModel) {

                Order<ArrayList<OrderDetail>> orderDetailOrder = orderBaseModel.object;
                getPayType(orderDetailOrder);
            }

            @Override
            public void onFailure() {
                onActivityLoadingFailed();
            }
        });
    }

    private void  getPayType(final Order<ArrayList<OrderDetail>> itemData){
        String type=itemData.spaytype;
        AppApplication.getApplication().numberCode=itemData.sordernumber;
        if (type.equals("0")) {
            thridPayUtil.alipay(itemData.fmoney, itemData.sordernumber);
        } else if (type.equals("4")) {//微信
            getWeixinPay(itemData.sordernumber);
        }else{
            ToastUitl.showToast("支付成功");
            PayResultActivity.jumpPayResultActivity(TuangouChihuoActivity.this, true, false);
            finish();
//            AlertDialog.Builder builder=new AlertDialog.Builder(this);
//            builder.setTitle("请输入密码");
//            CustomTypefaceEditText editText=new CustomTypefaceEditText(this);
//            editText.setTextColor(Color.parseColor("#000000"));
//            editText.setTextSize(DisplayMetricsUtils.dp2px(8) );
//            editText.setPadding((int) DisplayMetricsUtils.dp2px(10), (int) DisplayMetricsUtils.dp2px(10), 10, 10);
//            editText.setSingleLine();
//            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            builder.setView(editText);
//            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    getYuePay(itemData.sordernumber);
//                }
//            });
//            builder.setNegativeButton("取消", null);
//            AlertDialog dialog1=builder.create();
//            dialog1.show();
        }
    }

    public void getWeixinPay(String orderId) {

        Call<BaseModel<WeixinInfo>> call = ApiInstant.getInstant().getWeixinProductInfo(AppApplication.apptype, orderId, AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<WeixinInfo>>() {
            @Override
            public void onSucssce(BaseModel<WeixinInfo> weixinInfoBaseModel) {
                AppApplication.getApplication().isYue=false;
                thridPayUtil.wxpay(weixinInfoBaseModel.object);
            }

            @Override
            public void onFailure() {
                PayResultActivity.jumpPayResultActivity(TuangouChihuoActivity.this, false, false);
                AppManager.getAppManager().finishActivity(AllOrderActivity.class);

            }
        });
    }



}
