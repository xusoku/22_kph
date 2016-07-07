package com.davis.kangpinhui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.adapter.base.CommonBaseAdapter;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.model.Extendedinfo;
import com.davis.kangpinhui.model.Order;
import com.davis.kangpinhui.model.OrderDetail;
import com.davis.kangpinhui.model.WeixinInfo;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.util.AppManager;
import com.davis.kangpinhui.util.DateUtils;
import com.davis.kangpinhui.util.DisplayMetricsUtils;
import com.davis.kangpinhui.util.ThridPayUtil;
import com.davis.kangpinhui.util.ToastUitl;
import com.davis.kangpinhui.util.UtilText;
import com.davis.kangpinhui.views.CustomAlterDialog;
import com.davis.kangpinhui.views.CustomTypefaceEditText;
import com.davis.kangpinhui.views.StretchedListView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import retrofit2.Call;

public class OrderDetailActivity extends BaseActivity {

    private ThridPayUtil thridPayUtil;
    public static void jumpOrderDetailActivity(Context cot, String code) {
        if (AppApplication.isLogin(cot)) {
            Intent it = new Intent(cot, OrderDetailActivity.class);
            it.putExtra("code", code);
            cot.startActivity(it);
        }
    }

    private String code = "";

    private TextView order_detail_state, order_detail_people, order_detail_phone,
            order_detail_address, order_detail_paytype, order_detail_m_oney, order_detail_time,
            order_detail_code, order_detail_kefu;

    private TextView order_detail_coup_money;
    private LinearLayout order_copu_linear;

    private TextView order_detail_sentting_money;
    private LinearLayout order_sending_linear;

    private TextView order_detail_jiaoyi_code;
    private LinearLayout order_jiayicode_linear;

    private StretchedListView order_detail_lst;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void initVariable() {

        code = getIntent().getStringExtra("code");

    }

    @Override
    protected void findViews() {
        showTopBar();
        setTitle("订单详情");
        thridPayUtil = new ThridPayUtil(this);
        order_detail_state = $(R.id.order_detail_state);
        order_detail_people = $(R.id.order_detail_people);
        order_detail_phone = $(R.id.order_detail_phone);
        order_detail_address = $(R.id.order_detail_address);
        order_detail_paytype = $(R.id.order_detail_paytype);
        order_copu_linear = $(R.id.order_copu_linear);
        order_detail_coup_money = $(R.id.order_detail_coup_money);
        order_detail_m_oney = $(R.id.order_detail_m_oney);
        order_detail_time = $(R.id.order_detail_time);
        order_detail_code = $(R.id.order_detail_code);
        order_detail_kefu = $(R.id.order_detail_kefu);
        order_detail_lst = $(R.id.order_detail_lst);
        order_detail_sentting_money = $(R.id.order_detail_sentting_money);
        order_sending_linear = $(R.id.order_sending_linear);
        order_detail_jiaoyi_code = $(R.id.order_detail_jiaoyi_code);
        order_jiayicode_linear = $(R.id.order_jiayicode_linear);
    }

    @Override
    protected void initData() {

        startActivityLoading();
    }

    @Override
    public void startActivityLoading() {
        super.startActivityLoading();

        Call<BaseModel<Order<ArrayList<OrderDetail>>>> call = ApiInstant.getInstant().myOrderDetail(AppApplication.apptype, code, AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<Order<ArrayList<OrderDetail>>>>() {
            @Override
            public void onSucssce(BaseModel<Order<ArrayList<OrderDetail>>> orderBaseModel) {
                onActivityLoadingSuccess();

                Order<ArrayList<OrderDetail>> orderDetailOrder = orderBaseModel.object;

                bindView(orderDetailOrder);


                ArrayList<OrderDetail> list = orderDetailOrder.list;
                bindList(list);
            }

            @Override
            public void onFailure() {
                onActivityLoadingFailed();
            }
        });

    }

    private void bindList(ArrayList<OrderDetail> list) {
        order_detail_lst.setAdapter(new CommonBaseAdapter<OrderDetail>(this, list, R.layout.activity_order_item) {
            @Override
            public void convert(ViewHolder holder, OrderDetail itemData, int position) {
                holder.setImageByUrl(R.id.order_comfi_item_iv, itemData.picurl);
                holder.setText(R.id.order_comfi_item_title, itemData.sproductname);
                holder.setText(R.id.order_comfi_item_sstandent, itemData.sstandard);
                holder.setText(R.id.order_comfi_item_price, "¥" + (itemData.fprice));
                holder.setText(R.id.order_comfi_item_number, "数量:" + (int) Float.parseFloat(itemData.icount));
            }
        });
    }

    private void bindView(final Order<ArrayList<OrderDetail>> itemData) {


        String payType = itemData.spaytype;

        if ((payType.equals("0") || payType.equals("4") || payType.equals("1")) && itemData.stype.equals("0")) {
            order_detail_state.append("待付款    ");
            order_detail_state.append(UtilText.getOrderDetail("继续付款"));
            order_detail_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] typepaytext = {"余额支付","在线支付 微信支付", "在线支付 支付宝支付"};
                    final AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                    builder.setTitle("支付方式")
                            .setItems(typepaytext, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AppApplication.getApplication().numberCode=itemData.sordernumber;
                                    if (which==2) {
                                        thridPayUtil.alipay(itemData.fmoney, itemData.sordernumber);
                                    } else if (which==1) {//微信
                                        getWeixinPay(itemData.sordernumber);
                                    }else{
                                        final CustomAlterDialog builder=new CustomAlterDialog(OrderDetailActivity.this);
                                        builder.setTitle("请输入密码");
                                        final CustomTypefaceEditText editText=new CustomTypefaceEditText(OrderDetailActivity.this);
                                        editText.setTextColor(Color.parseColor("#000000"));
                                        editText.setTextSize(DisplayMetricsUtils.dp2px(15));
                                        editText.setPadding((int) DisplayMetricsUtils.dp2px(10), (int) DisplayMetricsUtils.dp2px(10), 10, 10);
                                        editText.setSingleLine();
                                        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        builder.setContentView(editText);
                                        builder.setConfirmButton("确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View hich) {
                                                builder.dismiss();
                                                String pass = editText.getText().toString().trim();
                                                if (TextUtils.isEmpty(pass)) {
                                                    ToastUitl.showToast("请输入密码");
                                                } else {
                                                    getYuePay(itemData.sordernumber, pass);
                                                }
                                            }
                                        });
                                        builder.setCancelButton("取消");
                                    }
                                }
                            }).show();

                }
            });
        } else if (!payType.equals("2") && itemData.stype.equals("1")) {
            order_detail_state.setText("已支付");
        } else if (itemData.stype.equals("2")||itemData.stype.equals("1")) {
            order_detail_state.setText("待配送");
        } else if (itemData.stype.equals("3")) {
            order_detail_state.setText("配送中");
        } else if (itemData.stype.equals("4")||itemData.stype.equals("5")) {
            order_detail_state.setText("已完成");
        } else if (itemData.stype.equals("6")) {
            order_detail_state.setText("已关闭");
        } else if (payType.equals("2") &&itemData.stype.equals("0")) {
            order_detail_state.setText("待配送");
        } else {
            order_detail_state.setText( "未知");
        }



        CharSequence[] charSequences = {"余额支付", "货到付款", "支付宝支付", "微信支付"};
        String[] type = {"3", "2", "0", "4"};

        for (int i = 0; i < type.length; i++) {
            if (type[i].equals(itemData.spaytype)) {
                order_detail_paytype.setText(charSequences[i]);
            }
        }

        order_detail_people.setText(itemData.sconsignee);
        order_detail_phone.setText(itemData.smobile);
        order_detail_address.setText(itemData.saddress);
        order_detail_time.setText(DateUtils.date2Str(Long.parseLong(itemData.daddtime),"yyyy-MM-dd"));
        order_detail_code.setText(itemData.sordernumber);

        String coup_money=itemData.coupontotal;
        if(TextUtils.isEmpty(coup_money)||coup_money.equals("0.0")) {
            order_copu_linear.setVisibility(View.GONE);
        }else{
            order_detail_coup_money.setText("¥" + UtilText.getFloatToString(coup_money));
        }

        String code=itemData.stradeno;
        if(TextUtils.isEmpty(code)) {
            order_jiayicode_linear.setVisibility(View.GONE);
        }else{
            order_detail_jiaoyi_code.setText(code);
        }

        String send=itemData.fpostprice;
        if(TextUtils.isEmpty(send)) {
            send="";
        }
        order_detail_sentting_money.setText("¥" + UtilText.getFloatToString(send));

        Float f=Float.parseFloat(itemData.fvipmoney)+Float.parseFloat(itemData.fmoney)-Float.parseFloat(itemData.freturnmoney);
        order_detail_m_oney.setText("¥" + UtilText.getFloatToString(f+""));
    }


    @Override
    protected void setListener() {

    }

    @Override
    public void doClick(View view) {

        switch (view.getId()) {
            case R.id.order_detail_kefu:
                final CustomAlterDialog dialog=new CustomAlterDialog(this);
                        dialog.setTitle("联系客服");
                dialog.setContent_text("客服电话：" + AppApplication.kefu);
                dialog.setConfirmButton("呼叫", new View.OnClickListener() {
                    @Override
                    public void onClick(View which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + AppApplication.kefu));
                        startActivity(intent);
                    }
                });
                dialog.setCancelButton("取消");
                break;
        }
    }
    public void getWeixinPay(String orderId) {

        Call<BaseModel<WeixinInfo>> call = ApiInstant.getInstant().getWeixinProductInfo(AppApplication.apptype, orderId, AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<WeixinInfo>>() {
            @Override
            public void onSucssce(BaseModel<WeixinInfo> weixinInfoBaseModel) {
                AppApplication.getApplication().isYue=false;
                thridPayUtil.wxpay(weixinInfoBaseModel.object);
            }

            @Override
            public void onFailure() {
                PayResultActivity.jumpPayResultActivity(OrderDetailActivity.this, false, false);
                finish();
                AppManager.getAppManager().finishActivity(AllOrderActivity.class);

            }
        });

    }
    public void getYuePay(String orderId,String password) {

        Call<BaseModel> call = ApiInstant.getInstant().getYueInfo(AppApplication.apptype, orderId, password, AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel>() {
            @Override
            public void onSucssce(BaseModel yueInfoBaseModel) {
                PayResultActivity.jumpPayResultActivity(OrderDetailActivity.this, true, false);
                AppManager.getAppManager().finishActivity(AllOrderActivity.class);
                EventBus.getDefault().post(new Extendedinfo());
            }
            @Override
            public void onFailure() {
            }
        });
    }
}
