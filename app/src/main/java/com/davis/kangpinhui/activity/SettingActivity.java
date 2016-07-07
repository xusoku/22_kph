package com.davis.kangpinhui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.model.Extendedinfo;
import com.davis.kangpinhui.model.UserInfo;
import com.davis.kangpinhui.util.DownLoadSoftUpdate;
import com.davis.kangpinhui.util.ShareManager;
import com.davis.kangpinhui.util.SharePreferenceUtils;
import com.davis.kangpinhui.util.ToastUitl;
import com.davis.kangpinhui.views.CustomDialog;

import de.greenrobot.event.EventBus;

public class SettingActivity extends BaseActivity {


    private CustomDialog dialog;
    public static void jumpSettingActivity(Context cot){
        Intent it=new Intent(cot,SettingActivity.class);
        cot.startActivity(it);
    }
    @Override
    protected int setLayoutView() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void findViews() {

        showTopBar();
        setTitle("设置");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {


    }

    @Override
    public void doClick(View view) {

        if(view.getId()==R.id.setting_logout){

            AppApplication.token = "";
            AppApplication.userInfo=new UserInfo();
            AppApplication.extendedinfo=null;
            SharePreferenceUtils.getSharedPreferences().putString("nickname","");
            SharePreferenceUtils.getSharedPreferences().putString("token","");
            LoginActivity.jumpLoginActivity(this);
            EventBus.getDefault().post("loginout");
            finish();
        }else if(view.getId()==R.id.setting_about){
            AboutActivity.jumpAboutActivity(this,0);
        }else if(view.getId()==R.id.fix_pass){
            AboutActivity.jumpAboutActivity(this,1);
        }else if(view.getId()==R.id.forget_pass){
            AboutActivity.jumpAboutActivity(this,2);
        }else if(view.getId()==R.id.setting_share){
            ShareManager mShareManager = new ShareManager(mActivity);
            mShareManager.setTitle("康品汇-家门口的生鲜店");
            mShareManager.setText("家门口的康品汇又有好赞的生鲜啦，我猜你肯定喜欢，快来戳我啊~");
            mShareManager.setWebUrl("http://m.kangpinhui.com/common/html.do?type=shareurl");
            mShareManager.setTitleUrl("http://m.kangpinhui.com/common/html.do?type=shareurl");
            mShareManager.setImageUrl("http://img.kangpinhui.com/images/logo200.png");
            mShareManager.showShareDialog(this);

        }else if(view.getId()==R.id.setting_update){
            new DownLoadSoftUpdate(this).checkVersionThread(true);
        }
    }
}
