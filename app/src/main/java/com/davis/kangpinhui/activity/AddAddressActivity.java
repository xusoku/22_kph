package com.davis.kangpinhui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.adapter.base.CommonBaseAdapter;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.model.Address;
import com.davis.kangpinhui.model.Region;
import com.davis.kangpinhui.model.Shop;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.util.RegexUtils;
import com.davis.kangpinhui.util.ToastUitl;
import com.davis.kangpinhui.views.CustomAlterDialog;
import com.davis.kangpinhui.views.CustomTypefaceTextView;

import java.util.ArrayList;

import retrofit2.Call;

public class AddAddressActivity extends BaseActivity {


    private TextView delete_address;
    private EditText add_des_address_text;
    private EditText add_address_people;
    private EditText add_address_phone;
    private TextView add_address_text;

    private String type = "";//1 添加  0 修改
    private String addressid = "";

    public static void jumpAddAddressActivity(Context cot, String type, String id) {
        if (AppApplication.isLogin(cot)) {
            Intent it = new Intent(cot, AddAddressActivity.class);
            it.putExtra("type", type);
            it.putExtra("id", id);
            cot.startActivity(it);
        }
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_add_address;
    }

    @Override
    protected void initVariable() {

        type = getIntent().getStringExtra("type");
        addressid = getIntent().getStringExtra("id");

    }

    @Override
    protected void findViews() {
        showTopBar();
        setTitle("新增收获地址");
        getRightTextButton().setText("确定");

        add_des_address_text = $(R.id.add_des_address_text);
        add_address_people = $(R.id.add_address_people);
        add_address_phone = $(R.id.add_address_phone);
        delete_address = $(R.id.delete_address);
        add_address_text = $(R.id.add_address_text);


        if (type.equals("1")) {
            delete_address.setVisibility(View.GONE);
        } else {
            delete_address.setVisibility(View.VISIBLE);
        }

        getRightTextButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("1"))
                    addAddress();
                else
                    fixAddress();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        PoiItem poiItem = AppApplication.poiItem;
        if (poiItem != null) {
            add_address_text.setText(poiItem.getTitle() + "");
            add_des_address_text.setText(poiItem.getSnippet() + "");
        }
    }

    @Override
    protected void initData() {

        if (type.equals("0")) {
            startActivityLoading();
        }
    }

    @Override
    protected void onActivityLoading() {
        super.onActivityLoading();

        Call<BaseModel<Address>> call = ApiInstant.getInstant().getAddressById(AppApplication.apptype, AppApplication.token, addressid);
        call.enqueue(new ApiCallback<BaseModel<Address>>() {
            @Override
            public void onSucssce(BaseModel<Address> addressBaseModel) {
                onActivityLoadingSuccess();
                Address address = addressBaseModel.object;
                bindView(address);
            }

            @Override
            public void onFailure() {
                onActivityLoadingFailed();
            }
        });
    }

    @Override
    protected void setListener() {

    }

    private void addAddress() {
        String address = add_address_text.getText().toString().trim();
        String addressdes = add_des_address_text.getText().toString().trim();
        String pepole = add_address_people.getText().toString().trim();
        String phone = add_address_phone.getText().toString().trim();

        if (TextUtils.isEmpty(address)) {
            ToastUitl.showToast("地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(addressdes)) {
            ToastUitl.showToast("详细地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(pepole)) {
            ToastUitl.showToast("联系人不能为空");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUitl.showToast("联系方式不能为空");
            return;
        }
        if (!RegexUtils.isMobilePhoneNumber(phone)) {
            ToastUitl.showToast("联系方式不正确");
            return;
        }

        Call<BaseModel<Address>> call = ApiInstant.getInstant().Addaddress(AppApplication.apptype,
                AppApplication.token, "", pepole,  addressdes, phone, AppApplication.shopid, ilocationid,address);
        call.enqueue(new ApiCallback<BaseModel<Address>>() {
            @Override
            public void onSucssce(BaseModel<Address> addressBaseModel) {
                ToastUitl.showToast("添加成功");
                finish();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void fixAddress() {
        String address = add_address_text.getText().toString().trim();
        String addressdes = add_des_address_text.getText().toString().trim();
        String pepole = add_address_people.getText().toString().trim();
        String phone = add_address_phone.getText().toString().trim();

        if (TextUtils.isEmpty(address)) {
            ToastUitl.showToast("地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(addressdes)) {
            ToastUitl.showToast("详细地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(pepole)) {
            ToastUitl.showToast("联系人不能为空");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUitl.showToast("联系方式不能为空");
            return;
        }
        if (!RegexUtils.isMobilePhoneNumber(phone)) {
            ToastUitl.showToast("联系方式不正确");
            return;
        }

        Call<BaseModel<Address>> call = ApiInstant.getInstant().updateAddress(AppApplication.apptype,
                AppApplication.token, addressid, pepole, addressdes, phone, AppApplication.shopid,ilocationid, address);
        call.enqueue(new ApiCallback<BaseModel<Address>>() {
            @Override
            public void onSucssce(BaseModel<Address> addressBaseModel) {
                ToastUitl.showToast("修改成功");
                finish();
                ;
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void bindView(Address address) {

        if (address.shopid.equals("0")) {
            add_des_address_text.setText("");
            add_address_text.setText("");
        } else {
            add_des_address_text.setText(address.saddress);
            add_address_text.setText(address.saddressperfix);
        }

        add_address_people.setText(address.saddressname);
        add_address_phone.setText(address.smobile);
    }

    private void deleteaddress() {
        Call<BaseModel<Address>> call = ApiInstant.getInstant().deleteAddress(AppApplication.apptype, AppApplication.token, addressid);

        call.enqueue(new ApiCallback<BaseModel<Address>>() {
            @Override
            public void onSucssce(BaseModel<Address> addressBaseModel) {
                ToastUitl.showToast("删除成功");
                finish();
                ;
            }

            @Override
            public void onFailure() {

            }
        });

    }

    @Override
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.add_address_text:
                if (AppApplication.shopid.equals("99")) {
                    getShopidRegion();
                } else {
                    getShopidDetail();
                }
                break;
            case R.id.delete_address:
                final CustomAlterDialog dialog=new CustomAlterDialog(this);
                dialog.setTitle("确定要删除?");
                dialog.setConfirmButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View which) {
                        dialog.dismiss();
                        deleteaddress();
                    }
                });
                dialog.setCancelButton("取消");
                break;
        }
    }

    public void getShopidDetail() {
        Call<BaseModel<Shop>> call = ApiInstant.getInstant().getShopDetail(AppApplication.apptype, AppApplication.shopid);
        call.enqueue(new ApiCallback<BaseModel<Shop>>() {
            @Override
            public void onSucssce(BaseModel<Shop> shopBaseModel) {
                Shop shop = shopBaseModel.object;
                PoiKeywordSearchActivity.jumpPoiKeywordSearchActivity(AddAddressActivity.this, shop, "my_address");
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public String ilocationid="0";
    public void getShopidRegion() {
        Call<BaseModel<ArrayList<Region>>> call = ApiInstant.getInstant().getAddressRegion(AppApplication.apptype);
        call.enqueue(new ApiCallback<BaseModel<ArrayList<Region>>>() {
            @Override
            public void onSucssce(BaseModel<ArrayList<Region>> arrayListBaseModel) {

                final AlertDialog alertDialog = new AlertDialog.Builder(AddAddressActivity.this).create();
                alertDialog.setTitle("上海区域选择");
                GridView gridView = new GridView(AddAddressActivity.this);
                gridView.setNumColumns(3);
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.bottomMargin=10;
                gridView.setLayoutParams(layoutParams);
                gridView.setAdapter(new CommonBaseAdapter<Region>(AddAddressActivity.this, arrayListBaseModel.object, R.layout.region_item) {
                    @Override
                    public void convert(ViewHolder holder, final Region itemData, int position) {
                        holder.setText(R.id.region_item_text, itemData.slocationname);
                        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                ilocationid=itemData.ilocationid;
                                add_address_text.setText("上海"+itemData.slocationname);
                            }
                        });
                    }
                });
                alertDialog.setView(gridView);
                alertDialog.show();

            }

            @Override
            public void onFailure() {

            }
        });
    }
}
