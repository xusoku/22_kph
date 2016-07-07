package com.davis.kangpinhui.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.model.Address;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.util.ToastUitl;

public class LetterheadActivity extends BaseActivity {


    private TextView
            lerrerhead_text,
            lerrerhead_address_text,
            lerrerhead_people_text,
            lerrerhead_content_text;
    public static void jumpLetterheadActivity(Activity cot) {
        if(AppApplication.isLogin(cot)) {
            Intent it = new Intent(cot, LetterheadActivity.class);
            cot.startActivityForResult(it, 0);
        }
    }
    @Override
    protected int setLayoutView() {
        return R.layout.activity_letterhead;
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void findViews() {

        showTopBar();
        setTitle("开具发票");

        lerrerhead_text=$(R.id.lerrerhead_text);
                lerrerhead_address_text=$(R.id.lerrerhead_address_text);
                lerrerhead_people_text=$(R.id.lerrerhead_people_text);
                lerrerhead_content_text=$(R.id.lerrerhead_content_text);


    }

    @Override
    protected void initData() {

        if(AppApplication.address!=null){
                    lerrerhead_address_text.setText(AppApplication.address.saddress);
                    lerrerhead_people_text.setText(AppApplication.address.saddressname);
                    lerrerhead_content_text.setText(AppApplication.address.smobile);
        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void doClick(View view) {

        switch (view.getId()){
            case R.id.lerrerhead_ok:
                String lerrterhead=lerrerhead_text.getText().toString().trim();
                if(TextUtils.isEmpty(lerrterhead)){
                    ToastUitl.showToast("请填写发票抬头");
                    return;
                }
                String addr=lerrerhead_address_text.getText().toString().trim();
                if(TextUtils.isEmpty(addr)){
                    ToastUitl.showToast("请填写收货地址");
                    return;
                }
                String people=lerrerhead_people_text.getText().toString().trim();
                if(TextUtils.isEmpty(people)){
                    ToastUitl.showToast("请填写联系人");
                    return;
                }
                String phone=lerrerhead_content_text.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    ToastUitl.showToast("请填写联系方式");
                    return;
                }
                Address address=new Address();

                address.saddressname=people;
                address.smobile=phone;
                address.saddress=addr;
                address.slock=lerrterhead;
                Intent it=new Intent(this,RechargeActivity.class);
                it.putExtra("lerrter", address);
                setResult(0,it);
                finish();
                break;
            case R.id.lerrerhead_cancel:
                finish();
                break;
        }
    }
}
