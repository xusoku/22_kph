package com.davis.kangpinhui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.util.ToastUitl;

import retrofit2.Call;

public class MyTiHuoActivity extends BaseActivity {

    private EditText ti_huo_et;
    private TextView ti_huo_tv;



    public static void jumpMyTiHuoActivity(Context cot){
        Intent it=new Intent(cot,MyTiHuoActivity.class);
        cot.startActivity(it);
    }
    @Override
    protected int setLayoutView() {
        return R.layout.activity_my_ti_huo;
    }

    @Override
    protected void initVariable() {


    }

    @Override
    protected void findViews() {

        showTopBar();
        setTitle("我要提货");

        ti_huo_et=$(R.id.ti_huo_et);
        ti_huo_tv=$(R.id.ti_huo_tv);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    public void doClick(View view) {

        switch (view.getId()){
            case R.id.ti_huo_tv:
                final String string=ti_huo_et.getText().toString().trim();
                if(TextUtils.isEmpty(string)){
                    ToastUitl.showToast("请输入提货码");
                    return;
                }

                Call<BaseModel> call= ApiInstant.getInstant().checkquancode(AppApplication.apptype,
                        AppApplication.shopid,string,AppApplication.token);

                call.enqueue(new ApiCallback<BaseModel>() {
                    @Override
                    public void onSucssce(BaseModel baseModel) {

                        OrderActivity.jumpOrderActivity(MyTiHuoActivity.this,"-1",string);
                    }
                    @Override
                    public void onFailure() {

                    }
                });

                break;
        }

    }


}
