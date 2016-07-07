package com.davis.kangpinhui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.model.Cart;
import com.davis.kangpinhui.model.Extendedinfo;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.adapter.CartListAdapter;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.util.ToastUitl;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import retrofit2.Call;

public class CartListActivity extends BaseActivity {

    private ListView cart_listvew;
    private TextView add_cart_number_text;
    private LinearLayout add_cart_list_addlinear;
    private CartListAdapter adapter;
    private RelativeLayout add_cart_relative;

    ArrayList<Cart> list;


    

    public static void jumpCartListActivity(Context context) {
        if (AppApplication.isLogin(context)) {

            Intent it = new Intent(context, CartListActivity.class);
            context.startActivity(it);
        }


    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_cart_list;
    }

    @Override
    protected void initVariable() {

    }

    @Override
    protected void findViews() {
        showTopBar();
        setTitle("购物车");
        cart_listvew = $(R.id.content);
        add_cart_relative = $(R.id.add_cart_relative);
        add_cart_number_text = $(R.id.add_cart_number_text);
        add_cart_list_addlinear = $(R.id.add_cart_list_addlinear);
        list = new ArrayList<>();
    }

    @Override
    protected void onActivityLoading() {
        super.onActivityLoading();

        Call<BaseModel<ArrayList<Cart>>> call = ApiInstant.getInstant().getCartlist(AppApplication.apptype, AppApplication.shopid, "", AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<ArrayList<Cart>>>() {
            @Override
            public void onSucssce(BaseModel<ArrayList<Cart>> arrayListBaseModel) {
                onActivityLoadingSuccess();

                list.addAll(arrayListBaseModel.object);

                adapter = new CartListAdapter(CartListActivity.this, list, R.layout.activity_cart_list_item);

                cart_listvew.setAdapter(adapter);

                DecimalFormat fnum = new DecimalFormat("##0.0");
                String str = fnum.format(getTotalPrice(list));
                str = str.endsWith(".0") ? str.substring(0, str.length() - 2) : str;
                add_cart_number_text.setText("¥" + str);

                adapter.setOnPriceChange(new CartListAdapter.OnPriceChange() {
                    @Override
                    public void priceChange() {

                        DecimalFormat fnum = new DecimalFormat("##0.0");
                        String str = fnum.format(getTotalPrice(list));
                        str = str.endsWith(".0") ? str.substring(0, str.length() - 2) : str;
                        add_cart_number_text.setText("¥" + str);
                    }

                    @Override
                    public void listChange() {
                        list.clear();

//                        EventBus.getDefault().post(new Extendedinfo());
                        startActivityLoading();
                    }
                });


                if (list.size() == 0) {
                    onActivityFirstLoadingNoData();
                    add_cart_relative.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure() {
                onActivityLoadingFailed();
            }
        });
    }

    @Override
    protected void initData() {
        startActivityLoading();
    }

    @Override
    protected void setListener() {

    }


    private Float getTotalPrice(ArrayList<Cart> list) {
        Float total = 0.0f;

        for (Cart cart : list) {
            if (cart.flag) {
                int n = (int) Float.parseFloat(cart.inumber);
                Float f = Float.parseFloat(cart.iprice);
                total += n * f;
            }
        }

        return total;
    }
    String ids = "";
    @Override
    public void doClick(View view) {

        switch (view.getId()) {
            case R.id.add_cart_list_addlinear:

                ids="";
                if (list == null || list.size() == 0) {
                    ToastUitl.showToast("暂无数据");
                    return;
                }
                boolean isCheck = false;
                for (Cart cart : list) {
                    if (cart.flag) {
                        ids += cart.iproductid + ",";
                        isCheck = true;
                    }
                }
                if (!isCheck) {
                    ToastUitl.showToast("请选择商品");
                    return;
                }

                ids = ids.substring(0, ids.length() - 1);
                Call<BaseModel> call = ApiInstant.getInstant().getcheck(AppApplication.apptype, AppApplication.shopid, ids, AppApplication.token);
                call.enqueue(new ApiCallback<BaseModel>() {
                    @Override
                    public void onSucssce(BaseModel baseModel) {
                        OrderActivity.jumpOrderActivity(CartListActivity.this,  ids);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
                break;
        }
    }

}
