package com.davis.kangpinhui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.api.ApiService;
import com.davis.kangpinhui.service.ScreenSaverS;
import com.davis.kangpinhui.views.XWebView;

public class AboutActivity extends BaseActivity {

    private XWebView about_xweb;

    int type=0;
    public static void jumpAboutActivity(Context cot, int type) {
        Intent it = new Intent(cot, AboutActivity.class);
        it.putExtra("type",type);
        cot.startActivity(it);
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_about;
    }

    @Override
    protected void initVariable() {

        type=getIntent().getIntExtra("type",0);
    }

    @Override
    protected void findViews() {

        showTopBar();
        about_xweb = $(R.id.about_xweb);
        if (type==0){
            setTitle("关于我们");
            about_xweb.loadUrl(ApiService.baseurl + "/common/html.do?type=about&apptype=android");
        }else if(type==1){
            setTitle("修改密码");
            about_xweb.loadUrl(ApiService.baseurl + "/common/html.do?type=updatepwd&apptype=android&token="+AppApplication.token);
        }else if(type==2){
            setTitle("忘记密码");
            about_xweb.loadUrl(ApiService.baseurl + "/common/html.do?type=forgetpwd&apptype=android");
        }

    }

    @Override
    protected void initData() {


    }

    @Override
    protected void setListener() {

    }

    @Override
    public void doClick(View view) {

    }
}
