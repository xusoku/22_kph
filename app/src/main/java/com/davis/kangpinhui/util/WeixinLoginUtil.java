package com.davis.kangpinhui.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.activity.LoginActivity;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.model.Extendedinfo;
import com.davis.kangpinhui.model.UserInfo;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.mob.tools.utils.UIHandler;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by davis on 16/6/6.
 */
public class WeixinLoginUtil implements Handler.Callback,PlatformActionListener {
    private static final int MSG_ACTION_CCALLBACK = 2;
    private ProgressDialog progressDialog;
    private Context context;
    public WeixinLoginUtil(Context context){
        this.context=context;
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public void startLogin(){
        Platform weixinfd = ShareSDK.getPlatform(Wechat.NAME);
        progressDialog.show();
        weixinfd.setPlatformActionListener(this);

        if (weixinfd.isValid()) {
            weixinfd.removeAccount();
        }
        weixinfd.showUser(null);
    }

    // 回调
    @Override
    public void onCancel(Platform platform, int action) {
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 3;
        msg.arg2 = action;
        msg.obj = platform;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onComplete(Platform platform, int action,
                           HashMap<String, Object> res) {
        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = platform;

        if (platform.getName().equals(Wechat.NAME)) {

        }
        UIHandler.sendMessage(msg, this);



    }

    @Override
    public void onError(Platform platform, int action, Throwable t) {
        t.printStackTrace();

        Message msg = new Message();
        msg.what = MSG_ACTION_CCALLBACK;
        msg.arg1 = 2;
        msg.arg2 = action;
        msg.obj = t;
        UIHandler.sendMessage(msg, this);

        // 分享失败的统计
        ShareSDK.logDemoEvent(4, platform);
    }

    // 回调handleMessage
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.arg1) {
            case 1: {
                // 成功
                Platform platform= (Platform) msg.obj;

                //获取资料
                platform.getDb().getUserName();//获取用户名字
                platform.getDb().getUserIcon(); //获取用户头像
                platform.getDb().getUserId(); //
                platform.getDb().getUserGender();

                String sex="m".equals(platform.getDb().getUserGender())?"0":("f".equals(platform.getDb().getUserGender())?"1":null);
                String sss="{\"openid\":\""+platform.getDb().getUserId()+"\"," +
                        "\"nickname\":\""+platform.getDb().getUserName()+"\"," +
                        "\"sex\":1," +
                        "\"language\":\"zh_CN\"," +
                        "\"city\":\"\"," +
                        "\"province\":\"\"," +
                        "\"country\":\"CN\"," +
                        "\"headimgurl\":\""+platform.getDb().getUserIcon()+"\"," +
                        "\"privilege\":[]," +
                        "\"unionid\":\""+platform.getDb().getUserId()+"\"}";

                LogUtils.e("weixinLogin","aa"+sss);
                progressDialog.show();
                Call<BaseModel<UserInfo>> call = ApiInstant.getInstant().weixinLogin(AppApplication.apptype, sss);

                call.enqueue(new ApiCallback<BaseModel<UserInfo>>() {
                    @Override
                    public void onSucssce(BaseModel<UserInfo> userInfoBaseModel) {
                        if(progressDialog!=null)
                        progressDialog.dismiss();

                        ToastUitl.showToast("登录成功");
                        AppManager.getAppManager().finishActivity(LoginActivity.class);
                        AppApplication.userInfo = userInfoBaseModel.object;
                        AppApplication.token = userInfoBaseModel.object.token;
                        AppApplication.address = userInfoBaseModel.object.useraddress;
                        SharePreferenceUtils.getSharedPreferences("kph").putString("token", AppApplication.token);
                        SharePreferenceUtils.getSharedPreferences("kph").putString("pic", AppApplication.userInfo.iuserid);
                        SharePreferenceUtils.getSharedPreferences("kph").putString("username", AppApplication.userInfo.susername);
                        SharePreferenceUtils.getSharedPreferences("kph").putString("nickname", AppApplication.userInfo.snickname);

                        EventBus.getDefault().post(AppApplication.userInfo);
                        EventBus.getDefault().post(new Extendedinfo());
                    }

                    @Override
                    public void onFailure() {
                        if(progressDialog!=null)
                        progressDialog.dismiss();
                    }
                });
            }
            break;
            case 2: {
                // 失败
                progressDialog.dismiss();
                Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();

                String expName = msg.obj.getClass().getSimpleName();
                if ("WechatClientNotExistException".equals(expName)
                        || "WechatTimelineNotSupportedException".equals(expName)
                        || "WechatFavoriteNotSupportedException".equals(expName)) {
                    Toast.makeText(context, "请安装微信客户端", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case 3: {
                // 取消
                progressDialog.dismiss();
                Toast.makeText(context, "取消····", Toast.LENGTH_SHORT)
                        .show();
            }
            break;
        }

        return false;
    }
}
