package com.davis.kangpinhui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.model.Address;
import com.davis.kangpinhui.model.Cart;
import com.davis.kangpinhui.model.Coupon;
import com.davis.kangpinhui.model.Extendedinfo;
import com.davis.kangpinhui.model.Order;
import com.davis.kangpinhui.model.Product;
import com.davis.kangpinhui.model.TakeGoodsdate;
import com.davis.kangpinhui.model.WeixinInfo;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.adapter.base.CommonBaseAdapter;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.api.ApiService;
import com.davis.kangpinhui.util.AppManager;
import com.davis.kangpinhui.util.ThridPayUtil;
import com.davis.kangpinhui.util.ToastUitl;
import com.davis.kangpinhui.views.StretchedListView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import view.TwoPickerView;

public class OrderActivity extends BaseActivity {


    private TextView order_number_text, order_paytype_text, order_paytype_time, order_paytypecoup_text;

    private TextView order_address_text, order_address_phone, order_address_pepole, add_cart_add_passwrod;

    private EditText order_beizhu_text;

    private LinearLayout add_cart_add_passwrod_linear, order_coup_linear, order_paytype_linear;

    private StretchedListView order_address_lst;

    private String ids = "";

    private ArrayList<Cart> list;
    private ArrayList<Product> listproduct;
    private ArrayList<TakeGoodsdate> takeGoodsdateArrayList;
    private ArrayList<Coupon> couponArrayList;

    private TwoPickerView twoPickerView;

    private ThridPayUtil thridPayUtil;

    public static void jumpOrderActivity(Context cot, String ids) {
        Intent it = new Intent(cot, OrderActivity.class);
        it.putExtra("ids", ids);
        cot.startActivity(it);
    }

    private String code = "";

    public static void jumpOrderActivity(Context cot, String ids, String code) {
        Intent it = new Intent(cot, OrderActivity.class);
        it.putExtra("ids", ids);
        it.putExtra("code", code);
        cot.startActivity(it);
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_order;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppApplication.address != null) {
            order_address_text.setText(AppApplication.address.saddress);
            order_address_phone.setText(AppApplication.address.smobile);
            order_address_pepole.setText(AppApplication.address.saddressname);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            order_address_text.setLayoutParams(layoutParams);
        } else {
            order_address_text.setText("暂无地址,请点击添加您的收货地址");
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            order_address_text.setLayoutParams(layoutParams);

            order_address_phone.setText("");
            order_address_pepole.setText("");
        }
    }

    @Override
    protected void initVariable() {
        ids = getIntent().getStringExtra("ids");
        if (ids.equals("-1")) {
            code = getIntent().getStringExtra("code");
        }
        list = new ArrayList<>();
        listproduct = new ArrayList<>();
        takeGoodsdateArrayList = new ArrayList<>();
        couponArrayList = new ArrayList<>();
    }

    @Override
    protected void findViews() {
        showTopBar();
        setTitle("订单结算");
        thridPayUtil = new ThridPayUtil(this);
        twoPickerView = new TwoPickerView(this);
        order_number_text = $(R.id.order_number_text);
        order_paytype_text = $(R.id.order_paytype_text);
        order_paytype_time = $(R.id.order_paytype_time);
        order_paytypecoup_text = $(R.id.order_paytypecoup_text);
        order_address_text = $(R.id.order_address_text);
        order_address_phone = $(R.id.order_address_phone);
        order_address_pepole = $(R.id.order_address_pepole);
        order_beizhu_text = $(R.id.order_beizhu_text);
        order_address_lst = $(R.id.order_address_lst);
        add_cart_add_passwrod = $(R.id.add_cart_add_passwrod);
        add_cart_add_passwrod_linear = $(R.id.add_cart_add_passwrod_linear);
        order_coup_linear = $(R.id.order_coup_linear);
        order_paytype_linear = $(R.id.order_paytype_linear);
        if (ids.equals("-1")) {
            order_paytype_linear.setVisibility(View.GONE);
            order_coup_linear.setVisibility(View.GONE);
            add_cart_add_passwrod_linear.setVisibility(View.GONE);
        }

        if (AppApplication.address == null) {
            getAddresslist();
        }
    }

    @Override
    protected void onActivityLoading() {
        super.onActivityLoading();

        getTimeList();

        if (ids.equals("-1")) {
            getBycodeList();
        } else {
            getorderlist();
            getCouplist();
        }

    }

    private void getBycodeList() {
        Call<BaseModel<ArrayList<Product>>> call = ApiInstant.getInstant().getProductByCode(AppApplication.apptype, AppApplication.shopid, code, AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<ArrayList<Product>>>() {
            @Override
            public void onSucssce(BaseModel<ArrayList<Product>> arrayListBaseModel) {
                onActivityLoadingSuccess();

                listproduct.addAll(arrayListBaseModel.object);

                DecimalFormat fnum = new DecimalFormat("##0.0");
                String str = fnum.format(getTotalPPrice(listproduct));
                str = str.endsWith(".0") ? str.substring(0, str.length() - 2) : str;
                order_number_text.setText("¥" + str);

                order_address_lst.setAdapter(new CommonBaseAdapter<Product>(OrderActivity.this, listproduct, R.layout.activity_order_item) {
                    @Override
                    public void convert(ViewHolder holder, Product itemData, int position) {
                        holder.setImageByUrl(R.id.order_comfi_item_iv, itemData.picurl);
                        holder.setText(R.id.order_comfi_item_title, itemData.productname);
                        holder.setText(R.id.order_comfi_item_sstandent, itemData.sstandard);
                        holder.setText(R.id.order_comfi_item_price, "¥" + itemData.fprice);
                        String number = itemData.count;
                        if (TextUtils.isEmpty(number)) {
                            number = "0";
                        }
                        holder.setText(R.id.order_comfi_item_number, "数量:" + (int) Float.parseFloat(number));
                    }
                });
            }

            @Override
            public void onFailure() {
                onActivityLoadingFailed();
            }
        });
    }

    private void getorderlist() {
        Call<BaseModel<ArrayList<Cart>>> call = ApiInstant.getInstant().getCartlist(AppApplication.apptype, AppApplication.shopid, ids, AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<ArrayList<Cart>>>() {
            @Override
            public void onSucssce(BaseModel<ArrayList<Cart>> arrayListBaseModel) {
                onActivityLoadingSuccess();

                list.addAll(arrayListBaseModel.object);

                changePriceFun();
            }

            @Override
            public void onFailure() {
                onActivityLoadingFailed();
            }
        });
    }

    private void changePriceFun() {
        DecimalFormat fnum = new DecimalFormat("##0.0");
        String str = fnum.format(getTotalPrice(list));
        str = str.endsWith(".0") ? str.substring(0, str.length() - 2) : str;
        order_number_text.setText("¥" + str);

        order_address_lst.setAdapter(new CommonBaseAdapter<Cart>(OrderActivity.this, list, R.layout.activity_order_item) {
            @Override
            public void convert(ViewHolder holder, Cart itemData, int position) {
                holder.setImageByUrl(R.id.order_comfi_item_iv, ApiService.picurl + itemData.picurl);
                holder.setText(R.id.order_comfi_item_title, itemData.productName);
                holder.setText(R.id.order_comfi_item_sstandent, itemData.sstandard);
                holder.setText(R.id.order_comfi_item_price, "¥" + (payTape.equals("3") ? itemData.fvipprice : itemData.iprice));
                holder.setText(R.id.order_comfi_item_number, "数量:" + (int) Float.parseFloat(itemData.inumber));
            }
        });
    }

    public void getAddresslist() {
        Call<BaseModel<ArrayList<Address>>> call = ApiInstant.getInstant().getAddresslist(AppApplication.apptype, AppApplication.shopid, AppApplication.token);

        call.enqueue(new ApiCallback<BaseModel<ArrayList<Address>>>() {
            @Override
            public void onSucssce(BaseModel<ArrayList<Address>> arrayListBaseModel) {
                onActivityLoadingSuccess();
                ArrayList<Address> list = arrayListBaseModel.object;
                if (list.size() > 0) {
                    AppApplication.address = list.get(0);
                    order_address_text.setText(AppApplication.address.saddress);
                    order_address_phone.setText(AppApplication.address.smobile);
                    order_address_pepole.setText(AppApplication.address.saddressname);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    order_address_text.setLayoutParams(layoutParams);
                }

            }

            @Override
            public void onFailure() {
                onActivityLoadingFailed();
            }
        });
    }

    private void getCouplist() {

        Call<BaseModel<ArrayList<Coupon>>> callCoup = ApiInstant.getInstant().getCouponByUid(AppApplication.apptype, AppApplication.token);
        callCoup.enqueue(new ApiCallback<BaseModel<ArrayList<Coupon>>>() {
            @Override
            public void onSucssce(BaseModel<ArrayList<Coupon>> arrayListBaseModel) {
                couponArrayList.addAll(arrayListBaseModel.object);
                Coupon coupon = new Coupon();
                coupon.usercouponid = "";
                coupon.context = "不使用优惠券";
                couponArrayList.add(0, coupon);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void getTimeList() {
        Call<BaseModel<ArrayList<TakeGoodsdate>>> calltime = ApiInstant.getInstant().getTakegoodtimelist(AppApplication.apptype,
                AppApplication.shopid, ids, AppApplication.token);
        calltime.enqueue(new ApiCallback<BaseModel<ArrayList<TakeGoodsdate>>>() {
            @Override
            public void onSucssce(BaseModel<ArrayList<TakeGoodsdate>> arrayListBaseModel) {
                takeGoodsdateArrayList.addAll(arrayListBaseModel.object);
            }

            @Override
            public void onFailure() {

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
                String s = cart.inumber;
                if (TextUtils.isEmpty(s)) {
                    s = "0.0";
                }
                String ss = payTape.equals("3") ? cart.fvipprice : cart.iprice;
                if (TextUtils.isEmpty(ss)) {
                    ss = "0.0";
                }
                int n = (int) Float.parseFloat(s);
                Float f = Float.parseFloat(ss);
                total += n * f;
            }
        }
        return total;
    }

    private Float getTotalPPrice(ArrayList<Product> list) {
        Float total = 0.0f;

        for (Product product : list) {
            String s = product.count;
            if (TextUtils.isEmpty(s)) {
                s = "0.0";
            }
            String ss = product.fprice;
            if (TextUtils.isEmpty(ss)) {
                ss = "0.0";
            }
            int n = (int) Float.parseFloat(s);
            Float f = Float.parseFloat(ss);
            total += n * f;
        }
        return total;
    }

    private String payTape = "3";
    private String couponId = "";
    private String timeTape = "";

    @Override
    public void doClick(View view) {

        switch (view.getId()) {
            case R.id.order_coup_relatie:
                couponId = "";

                if (couponArrayList.size() == 0) {
                    ToastUitl.showToast("暂无优惠券");
                    return;
                }
                final CharSequence[] charSequencess = new CharSequence[couponArrayList.size()];
                for (int i = 0; i < couponArrayList.size(); i++) {
                    charSequencess[i] = couponArrayList.get(i).context;
                }
                AlertDialog.Builder builde = new AlertDialog.Builder(this);
                builde.setTitle("优惠券列表")
                        .setItems(charSequencess, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                order_paytypecoup_text.setText(charSequencess[which].toString());
                                couponId = couponArrayList.get(which).usercouponid;
                            }
                        }).show();
                break;
            case R.id.order_paytime_relative:
                timeTape = "";
                if (takeGoodsdateArrayList.size() == 0) {
                    ToastUitl.showToast("请等待");
                    getTimeList();
                    return;
                }
                final ArrayList<String> list = new ArrayList<String>();
                final ArrayList<ArrayList<String>> arrayLists = new ArrayList<ArrayList<String>>();
                for (TakeGoodsdate takeGoodsdate : takeGoodsdateArrayList) {

                    list.add(takeGoodsdate.date);
                    if (takeGoodsdate.time != null && takeGoodsdate.time.size() > 0) {
                        arrayLists.add(takeGoodsdate.time);
                    } else {
                        ArrayList<String> ssss=new ArrayList();
                        ssss.add("");
                        ssss.add("");
                        ssss.add("");
                        arrayLists.add(ssss);
                    }
                }


                twoPickerView.setPicker(list, arrayLists, false);
                twoPickerView.setCyclic(false);
                twoPickerView.setSelectOptions(0, 0);
                twoPickerView.show();
                twoPickerView.setOnoptionsSelectListener(new TwoPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2) {

                        if (arrayLists.get(options1).size() == 0) {
                            timeTape = (list.get(options1));

                        } else {
                            timeTape = (list.get(options1) + " " + arrayLists.get(options1).get(option2));
                        }
                        order_paytype_time.setText(timeTape);
                    }
                });

                break;
            case R.id.order_paytype_relative:
                //付款方式  3:余额支付  2：货到付款 0：支付宝  1：财付通  4微信支付

                final CharSequence[] charSequences = {"余额支付", "支付宝支付", "微信支付"};
                final String[] type = {"3", "0", "4"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("付款方式")
                        .setItems(charSequences, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ToastUitl.showToast(charSequences[which].toString());
                                payTape = type[which];
                                order_paytype_text.setText(charSequences[which].toString());

                                if (payTape.equals("3")) {
                                    add_cart_add_passwrod_linear.setVisibility(View.VISIBLE);
                                } else {
                                    add_cart_add_passwrod_linear.setVisibility(View.GONE);
                                }
                                changePriceFun();
                            }
                        }).show();
                break;
            case R.id.order_address_relative:
                MyAddressActivity.jumpMyAddressActivity(this, true);
                break;
            case R.id.order_list_addlinear:

                if (AppApplication.address == null || TextUtils.isEmpty(AppApplication.address.iuseraddressid)) {
                    ToastUitl.showToast("请选择收货地址");
                    return;
                }
                if (TextUtils.isEmpty(timeTape)) {
                    ToastUitl.showToast("请选择配送时间");
                    return;
                }


                String beizhu = order_beizhu_text.getText().toString().trim();

                if (TextUtils.isEmpty(beizhu)) {
                    beizhu = "";
                }

                if (ids.equals("-1")) {
                    saveByCode(beizhu);
                } else {
                    saveOrder(beizhu);
                }
                break;
        }
    }

    private void saveByCode(String beizhu) {
        Call<BaseModel<Order>> call = ApiInstant.getInstant().saveProductCode(AppApplication.apptype, AppApplication.shopid, AppApplication.address.iuseraddressid, timeTape, beizhu, code, AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<Order>>() {
            @Override
            public void onSucssce(BaseModel<Order> baseModel) {
                ToastUitl.showToast("订单提交成功");
                AppApplication.getApplication().numberCode = baseModel.object.sordernumber;
                PayResultActivity.jumpPayResultActivity(OrderActivity.this, true);
                AppManager.getAppManager().finishActivity(MyTiHuoActivity.class);
                AppManager.getAppManager().finishActivity(OrderActivity.class);
                finish();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void saveOrder(String beizhu) {

        String pass = add_cart_add_passwrod.getText().toString().trim();
        if (payTape.equals("3")) {
            if (TextUtils.isEmpty(pass)) {
                ToastUitl.showToast("请输入交易密码");
                return;
            }
        } else {
            pass = "";
        }

        Call<BaseModel<Order>> call = ApiInstant.getInstant().orderSave(AppApplication.apptype, AppApplication.shopid,
                ids, AppApplication.address.iuseraddressid, payTape, timeTape, beizhu, couponId, pass, AppApplication.token);

        call.enqueue(new ApiCallback<BaseModel<Order>>() {
            @Override
            public void onSucssce(BaseModel<Order> baseModel) {
                ToastUitl.showToast("订单提交成功");
//                EventBus.getDefault().post(new Extendedinfo());
                AppApplication.getApplication().numberCode = baseModel.object.sordernumber;
                if (payTape.equals("0")) {
                    thridPayUtil.alipay(baseModel.object.fmoney, baseModel.object.sordernumber);
                } else if (payTape.equals("4")) {//微信
                    getWeixinPay(baseModel.object.sordernumber);
                } else if (payTape.equals("3")) {//余额支付", "
                    PayResultActivity.jumpPayResultActivity(OrderActivity.this, true);
                    finish();
                    AppManager.getAppManager().finishActivity(CartListActivity.class);

                } else if (payTape.equals("2")) {//货到付款
                    PayResultActivity.jumpPayResultActivity(OrderActivity.this, true);
                    finish();
                    AppManager.getAppManager().finishActivity(CartListActivity.class);
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void getWeixinPay(String orderId) {

        Call<BaseModel<WeixinInfo>> call = ApiInstant.getInstant().getWeixinProductInfo(AppApplication.apptype, orderId, AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<WeixinInfo>>() {
            @Override
            public void onSucssce(BaseModel<WeixinInfo> weixinInfoBaseModel) {
                thridPayUtil.wxpay(weixinInfoBaseModel.object);
            }

            @Override
            public void onFailure() {
                PayResultActivity.jumpPayResultActivity(OrderActivity.this, false);
                finish();
                AppManager.getAppManager().finishActivity(CartListActivity.class);

            }
        });

    }
}
