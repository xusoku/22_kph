package com.davis.kangpinhui.util;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.activity.AllOrderActivity;
import com.davis.kangpinhui.activity.CartListActivity;
import com.davis.kangpinhui.activity.OrderActivity;
import com.davis.kangpinhui.activity.OrderDetailActivity;
import com.davis.kangpinhui.activity.PayResultActivity;
import com.davis.kangpinhui.activity.RechargeActivity;
import com.davis.kangpinhui.activity.RechargeListActivity;
import com.davis.kangpinhui.activity.TuangouChihuoActivity;
import com.davis.kangpinhui.model.WeixinInfo;
import com.davis.kangpinhui.util.alipay.PayResult;
import com.davis.kangpinhui.util.alipay.ZhifubaoPayUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by davis on 16/6/1.
 */
public class ThridPayUtil {

    private Activity context;
    private boolean isYue=false;
    public ThridPayUtil(Activity context){
        this.context=context;
    }



    /**
     * 微信支付
     */
    public void wxpay(WeixinInfo payData){
//        IWXAPI wxApi= WXAPIFactory.createWXAPI(context, "wx805154ce4d985929");
        IWXAPI wxApi= AppApplication.getApplication().wxApi;
        if(wxApi!=null){
            if(null != payData  ){
                PayReq req = new PayReq();
                req.appId			= payData.appid;
                req.partnerId		= payData.partnerid;
                req.prepayId		= payData.prepayid;
                req.nonceStr		= payData.noncestr;
                req.timeStamp		= payData.timestamp;
                req.packageValue	= payData.packageValue;
                req.sign			= payData.sign;
                req.extData			= "app data"; // optional
                ToastUitl.showToast("正常调起支付");
                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                wxApi.sendReq(req);
            }else{
                ToastUitl.showToast("返回错误");
            }
        }else{
            ToastUitl.showToast("服务器请求错误");
        }
    }

    /**
     * 支付宝
     * @param totalPrice 金额
     * @param code 订单
     */
    public void alipay(String totalPrice,String code){
        isYue=false;
        ZhifubaoPayUtil payUtil=new ZhifubaoPayUtil(context,mHandler);
        payUtil.pay("康品汇生鲜","康品汇生鲜", totalPrice+"",code);
    }
    /**
     * 支付宝余额充值
     * @param totalPrice 金额
     * @param code 订单
     */
    public void alipayyue(String totalPrice,String code){
        isYue=true;
        ZhifubaoPayUtil payUtil=new ZhifubaoPayUtil(context,mHandler);
        payUtil.pay("康品汇充值","康品汇充值", totalPrice+"",code);
    }

    /**
     * Handler处理
     */
    private  Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case ZhifubaoPayUtil.SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        ToastUitl.showToast("支付成功");
                        PayResultActivity.jumpPayResultActivity(context, true, isYue);
                        AppManager.getAppManager().finishActivity(TuangouChihuoActivity.class);
                        AppManager.getAppManager().finishActivity(AllOrderActivity.class);
                        AppManager.getAppManager().finishActivity(OrderDetailActivity.class);
                        AppManager.getAppManager().finishActivity(RechargeActivity.class);
                        AppManager.getAppManager().finishActivity(RechargeListActivity.class);
                        AppManager.getAppManager().finishActivity(CartListActivity.class);
                        AppManager.getAppManager().finishActivity(OrderActivity.class);
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                           ToastUitl.showToast("支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            ToastUitl.showToast("支付失败");
                        }
                        PayResultActivity.jumpPayResultActivity(context, false,isYue);
                        AppManager.getAppManager().finishActivity(TuangouChihuoActivity.class);
                        AppManager.getAppManager().finishActivity(AllOrderActivity.class);
                        AppManager.getAppManager().finishActivity(OrderDetailActivity.class);
                        AppManager.getAppManager().finishActivity(RechargeListActivity.class);
                        AppManager.getAppManager().finishActivity(RechargeActivity.class);
                        AppManager.getAppManager().finishActivity(CartListActivity.class);
                        AppManager.getAppManager().finishActivity(OrderActivity.class);
                    }
                    break;
                }
                case ZhifubaoPayUtil.SDK_CHECK_FLAG: {
                  ToastUitl.showToast("检查结果为：" + msg.obj);
                    break;
                }
                default:
                    break;
            }
        }
    };

}
