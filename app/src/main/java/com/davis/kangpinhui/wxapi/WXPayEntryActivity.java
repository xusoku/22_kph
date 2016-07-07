package com.davis.kangpinhui.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.activity.CartListActivity;
import com.davis.kangpinhui.activity.OrderActivity;
import com.davis.kangpinhui.activity.OrderDetailActivity;
import com.davis.kangpinhui.activity.PayResultActivity;
import com.davis.kangpinhui.activity.RechargeActivity;
import com.davis.kangpinhui.activity.RechargeListActivity;
import com.davis.kangpinhui.activity.TuangouChihuoActivity;
import com.davis.kangpinhui.util.AppManager;
import com.davis.kangpinhui.util.ToastUitl;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import de.greenrobot.event.EventBus;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	

    private IWXAPI api;

	private  int REQUEST_UPDATE_ORDER_STATE=0x111;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	api = AppApplication.getApplication().wxApi;
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}



	@Override
	public void onResp(BaseResp resp) {
//		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

			if (resp.errCode == 0) {//成功
//				EventBus.getDefault().post(new WxPayEvent());
				ToastUitl.showToast("支付成功");
				PayResultActivity.jumpPayResultActivity(this, true, AppApplication.getApplication().isYue);
				finish();
				AppManager.getAppManager().finishActivity(RechargeActivity.class);
				AppManager.getAppManager().finishActivity(TuangouChihuoActivity.class);
				AppManager.getAppManager().finishActivity(OrderDetailActivity.class);
				AppManager.getAppManager().finishActivity(RechargeListActivity.class);
				AppManager.getAppManager().finishActivity(CartListActivity.class);
				AppManager.getAppManager().finishActivity(OrderActivity.class);
				AppApplication.getApplication().isYue=false;
			} else {
				String messge="";
				if (resp.errCode == -1) {
					messge ="可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。";
				} else if (resp.errCode == -2) {
					messge = "用户取消";
				}
				messge="支付失败";
				ToastUitl.showToast(messge);
				PayResultActivity.jumpPayResultActivity(this, false, AppApplication.getApplication().isYue);
				finish();
				AppManager.getAppManager().finishActivity(RechargeActivity.class);
				AppManager.getAppManager().finishActivity(TuangouChihuoActivity.class);
				AppManager.getAppManager().finishActivity(OrderDetailActivity.class);
				AppManager.getAppManager().finishActivity(RechargeListActivity.class);
				AppManager.getAppManager().finishActivity(CartListActivity.class);
				AppManager.getAppManager().finishActivity(OrderActivity.class);
				AppApplication.getApplication().isYue=false;
			}
			finish();
		}
	}

}