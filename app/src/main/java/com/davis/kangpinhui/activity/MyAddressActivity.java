package com.davis.kangpinhui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.model.Address;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.adapter.base.CommonBaseAdapter;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.util.ToastUitl;

import java.util.ArrayList;

import retrofit2.Call;

public class MyAddressActivity extends BaseActivity {


    private ListView listView;

    private boolean isOrder=false;

    public static void jumpMyAddressActivity(Context cot) {
        if(AppApplication.isLogin(cot)) {
            Intent it = new Intent(cot, MyAddressActivity.class);
            cot.startActivity(it);
        }
    }
    public static void jumpMyAddressActivity(Context cot,boolean flag) {
        if(AppApplication.isLogin(cot)) {
            Intent it = new Intent(cot, MyAddressActivity.class);
            it.putExtra("isOrder", flag);
            cot.startActivity(it);
        }
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_my_address;
    }

    @Override
    protected void initVariable() {

        isOrder=getIntent().getBooleanExtra("isOrder",false);
    }

    @Override
    protected void findViews() {
        showTopBar();
        setTitle("我的地址");
        listView = $(R.id.content);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startActivityLoading();
    }

    @Override
    protected void onActivityLoading() {
        super.onActivityLoading();

        Call<BaseModel<ArrayList<Address>>> call = ApiInstant.getInstant().getAddresslist(AppApplication.apptype, AppApplication.shopid,AppApplication.token);

        call.enqueue(new ApiCallback<BaseModel<ArrayList<Address>>>() {
            @Override
            public void onSucssce(BaseModel<ArrayList<Address>> arrayListBaseModel) {
                onActivityLoadingSuccess();
                ArrayList<Address> list = arrayListBaseModel.object;
                bindView(list);
            }

            @Override
            public void onFailure() {
                onActivityLoadingFailed();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    private void bindView(final ArrayList<Address> list) {

        listView.setAdapter(new CommonBaseAdapter<Address>(this, list, R.layout.activity_my_address_item) {
            @Override
            public void convert(ViewHolder holder, final Address itemData, int position) {
                holder.setText(R.id.my_address_item_people, "收货人 :" + itemData.saddressname);
                holder.setText(R.id.my_address_item_phone, itemData.smobile);
                holder.setText(R.id.my_address_item_text, itemData.saddress);

                TextView  my_address_item_info=holder.getView(R.id.my_address_item_info);
                if(itemData.shopid.equals("0")){
                    my_address_item_info.setVisibility(View.VISIBLE);
                }else{
                    my_address_item_info.setVisibility(View.GONE);
                }


                ImageView my_address_item_edit = holder.getView(R.id.my_address_item_edit);
                my_address_item_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddAddressActivity.jumpAddAddressActivity(MyAddressActivity.this, "0", itemData.iuseraddressid);
                    }
                });

                holder.getView(R.id.my_address_item_linear).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isOrder) {

                            if(AppApplication.shopid.equals(itemData.shopid)) {
                                AppApplication.address = itemData;
                                finish();
                            }else{
                                ToastUitl.showToast("该地址区域不符合");
                            }
                        }
                    }
                });
            }
        });

    }

    @Override
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.my_address_add_text:
                AddAddressActivity.jumpAddAddressActivity(this,"1","");
                break;
        }

    }
}
