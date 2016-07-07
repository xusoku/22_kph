package com.davis.kangpinhui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.activity.CartListActivity;
import com.davis.kangpinhui.activity.OrderDetailActivity;
import com.davis.kangpinhui.activity.PayResultActivity;
import com.davis.kangpinhui.model.Extendedinfo;
import com.davis.kangpinhui.model.Order;
import com.davis.kangpinhui.model.OrderDetail;
import com.davis.kangpinhui.model.WeixinInfo;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.model.basemodel.Page;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.AllOrderActivity;
import com.davis.kangpinhui.adapter.base.CommonBaseAdapter;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.fragment.base.BaseFragment;
import com.davis.kangpinhui.util.AppManager;
import com.davis.kangpinhui.util.DisplayMetricsUtils;
import com.davis.kangpinhui.util.ThridPayUtil;
import com.davis.kangpinhui.util.ToastUitl;
import com.davis.kangpinhui.views.CustomAlterDialog;
import com.davis.kangpinhui.views.CustomTypefaceEditText;
import com.davis.kangpinhui.views.LoadMoreListView;
import com.davis.kangpinhui.views.StretchedListView;


import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import retrofit2.Call;

/**
 * Created by davis on 16/5/27.
 */
public class AllorderFragment extends BaseFragment {

    private LoadMoreListView mine_allorder_list;
    private int id = 0;

    private int iPage = 1;
    private int iPageSize = 10;
    private int TotalPage = 0;
    private CommonBaseAdapter<Order<ArrayList<OrderDetail>>> adapter;
    private ArrayList<Order<ArrayList<OrderDetail>>> list;
    private ThridPayUtil thridPayUtil;


    public static AllorderFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt("id", id);
        AllorderFragment sampleFragment = new AllorderFragment();
        sampleFragment.setArguments(args);
        return sampleFragment;
    }


    @Override
    protected void initVariable() {
        id = getArguments().getInt("id", 0);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_allorder;
    }

    @Override
    protected void findViews(View view) {
        mine_allorder_list = $(view, R.id.mine_allorder_list);
        thridPayUtil = new ThridPayUtil(getActivity());
        list = new ArrayList<>();
        adapter = new CommonBaseAdapter<Order<ArrayList<OrderDetail>>>(getActivity(), list, R.layout.fragment_allorder_item) {
            @Override
            public void convert(ViewHolder holder, final Order<ArrayList<OrderDetail>> itemData, int position) {
//                Stype：  0：待配送  3：已付款成功  6：已关闭
                ArrayList<OrderDetail> orderDetails = itemData.list;
                bindItemView(holder.<StretchedListView>getView(R.id.allorder_lst_item), orderDetails);

                holder.setText(R.id.allorder_item_number, itemData.sordernumber + "[详情]");
                holder.getView(R.id.allorder_item_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderDetailActivity.jumpOrderDetailActivity(mContext, itemData.sordernumber);
                    }
                });

                TextView allorder_item_cancel = holder.getView(R.id.allorder_item_cancel);
                TextView conutiPay = holder.getView(R.id.allorder_item_conuti);
                LinearLayout linearLayout = holder.getView(R.id.allorder_item_linear);


//                if((payType==0|| payType==1|| payType==4)&& Stype==0){
////待支付
//                } else if(payType!=2&& Stype==1){
////已支付
//                }else if(Stype==2 || Stype==1){
////代配送
//                } else if(Stype==3){
////配送中
//                } else if(Stype==4|| Stype==5){
//已完成
//                }else if(Stype==6){
//已关闭
//                }else if(Stype==0&&paytype==2){
//待配送
//            }
                String payType = itemData.spaytype;
                if ((payType.equals("0") || payType.equals("4") || payType.equals("1")) && itemData.stype.equals("0")) {
                    holder.setText(R.id.allorder_item_type, "待付款");
                    conutiPay.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                } else if (!payType.equals("2") && itemData.stype.equals("1")) {
                    holder.setText(R.id.allorder_item_type, "已支付");
                    conutiPay.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                } else if (itemData.stype.equals("2")||itemData.stype.equals("1")) {
                    holder.setText(R.id.allorder_item_type, "待配送");
                    linearLayout.setVisibility(View.GONE);
                } else if (itemData.stype.equals("3")) {
                    holder.setText(R.id.allorder_item_type, "配送中");
                    linearLayout.setVisibility(View.GONE);
                }else if (itemData.stype.equals("4")||itemData.stype.equals("5")) {
                    holder.setText(R.id.allorder_item_type, "已完成");
                    linearLayout.setVisibility(View.GONE);
                } else if (itemData.stype.equals("6")) {
                    holder.setText(R.id.allorder_item_type, "已关闭");
                    linearLayout.setVisibility(View.GONE);
                } else if (payType.equals("2") &&itemData.stype.equals("0")) {
                    holder.setText(R.id.allorder_item_type, "待配送");
                    linearLayout.setVisibility(View.GONE);
                } else {
                    holder.setText(R.id.allorder_item_type, "未知");
                    linearLayout.setVisibility(View.GONE);
                }

                allorder_item_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final CustomAlterDialog dialog=new CustomAlterDialog(mContext);
                        dialog.setTitle("是否取消订单");
                        dialog.setCancelButton("取消");
                        dialog.setConfirmButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        Call<BaseModel> call = ApiInstant.getInstant().cancelOrder(AppApplication.apptype, itemData.sordernumber, AppApplication.token);
                                        call.enqueue(new ApiCallback<BaseModel>() {
                                            @Override
                                            public void onSucssce(BaseModel baseModel) {
                                                ToastUitl.showToast("取消成功");
                                                EventBus.getDefault().post(new Extendedinfo());
                                                list.clear();
                                                startFragmentLoading();
//                                                chageTitle();
                                            }

                                            @Override
                                            public void onFailure() {

                                            }
                                        });
                                    }
                                });
                    }
                });

                conutiPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CharSequence[] typepaytext = {"余额支付","在线支付 微信支付", "在线支付 支付宝支付"};
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                           final CustomAlterDialog builder=new CustomAlterDialog(getActivity());
                                            builder.setTitle("请输入密码");
                                            final CustomTypefaceEditText editText=new CustomTypefaceEditText(getActivity());
                                            editText.setTextColor(Color.parseColor("#000000"));
                                            editText.setTextSize(DisplayMetricsUtils.dp2px(15));
                                            editText.setPadding((int) DisplayMetricsUtils.dp2px(10), (int) DisplayMetricsUtils.dp2px(10), 10, 10);
                                            editText.setSingleLine();
                                            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                            builder.setContentView(editText);
                                            builder.setConfirmButton("确定", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
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


            }
        };
        mine_allorder_list.setAdapter(adapter);
    }

    private void chageTitle() {

        String[] tabNames = {"全部", "待付款", "待配送", "配送中"};

        tabNames[0] = "全部(" + (AppApplication.getOrderall()) + ")";
        if (id == 1) {
            tabNames[1] = "待付款(" + getString(AppApplication.getOrderunpaid()) + ")";
            tabNames[2] = "待配送(" + AppApplication.getOrderwaitsend() + ")";
            tabNames[3] = "配送中(" + AppApplication.getOrdersending() + ")";
        } else if (id == 2) {
            tabNames[2] = "待配送(" + getString(AppApplication.getOrderwaitsend()) + ")";
            tabNames[1] = "待付款(" + AppApplication.getOrderunpaid() + ")";
            tabNames[3] = "配送中(" + AppApplication.getOrdersending() + ")";
        } else if (id == 3) {
            tabNames[3] = "配送中(" + getString(AppApplication.getOrdersending()) + ")";
            tabNames[1] = "待付款(" + AppApplication.getOrderunpaid() + ")";
            tabNames[2] = "待配送(" + AppApplication.getOrderwaitsend() + ")";
        }else{
            tabNames[1] = "待付款(" + AppApplication.getOrderunpaid() + ")";
            tabNames[2] = "待配送(" + AppApplication.getOrderwaitsend() + ")";
            tabNames[3] = "配送中(" + AppApplication.getOrdersending() + ")";
        }

        AllOrderActivity allOrderActivity = (AllOrderActivity) getActivity();

        if (allOrderActivity != null) {
            allOrderActivity.chageTitle(tabNames, id);
        }
    }

    private String getString(String string) {
        int i = Integer.parseInt(string);
        if (i >= 1)
            i = i - 1;
        return i + "";
    }

    private void bindItemView(StretchedListView listView, ArrayList<OrderDetail> orderDetails) {
        listView.setAdapter(new CommonBaseAdapter<OrderDetail>(getActivity(), orderDetails, R.layout.activity_order_item) {
            @Override
            public void convert(ViewHolder holder, OrderDetail itemData, int position) {
                holder.setImageByUrl(R.id.order_comfi_item_iv, itemData.picurl);
                holder.setText(R.id.order_comfi_item_title, itemData.sproductname);
                holder.setText(R.id.order_comfi_item_sstandent, itemData.sstandard);
                holder.setText(R.id.order_comfi_item_price, "¥" + itemData.fprice);
                holder.setText(R.id.order_comfi_item_number, "数量:" + (int) Float.parseFloat(itemData.icount));
            }
        });
    }

    @Override
    protected void initData() {
        startFragmentLoading();
    }

    @Override
    protected void onFragmentLoading() {
        super.onFragmentLoading();
        getOrderList(iPage, iPageSize);
    }

    @Override
    protected void setListener() {

        mine_allorder_list.setOnLoadListener(new LoadMoreListView.OnLoadListener() {
            @Override
            public void onLoad(LoadMoreListView listView) {
                getOrderList(++iPage, iPageSize);
            }
        });
    }

    private void getOrderList(int ipage, int iPageSize) {

        Call<BaseModel<Page<ArrayList<Order<ArrayList<OrderDetail>>>>>> call = ApiInstant.getInstant().myOrderlist(AppApplication.apptype, ipage + "", iPageSize + "", AppApplication.token, id + "");
        call.enqueue(new ApiCallback<BaseModel<Page<ArrayList<Order<ArrayList<OrderDetail>>>>>>() {
            @Override
            public void onSucssce(BaseModel<Page<ArrayList<Order<ArrayList<OrderDetail>>>>> pageBaseModel) {

                onFragmentLoadingSuccess();

                Page<ArrayList<Order<ArrayList<OrderDetail>>>> page = pageBaseModel.object;

                TotalPage = page.iTotalPage;

                list.addAll(page.list);
                adapter.notifyDataSetChanged();

                if (list.size() == 0) {
                    onFragmentFirstLoadingNoData();
                    mine_allorder_list.onLoadUnavailable();
                } else if (TotalPage == iPage) {
                    mine_allorder_list.onLoadSucess(false);
                } else {
                    mine_allorder_list.onLoadSucess(true);
                }
            }

            @Override
            public void onFailure() {
                onFragmentLoadingFailed();
            }
        });
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
                PayResultActivity.jumpPayResultActivity(getActivity(), false, false);
                AppManager.getAppManager().finishActivity(AllOrderActivity.class);

            }
        });
    }

    public void getYuePay(String orderId,String password) {

        Call<BaseModel> call = ApiInstant.getInstant().getYueInfo(AppApplication.apptype, orderId, password,AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel>() {
            @Override
            public void onSucssce(BaseModel yueInfoBaseModel) {
                PayResultActivity.jumpPayResultActivity(getActivity(), true, false);
                AppManager.getAppManager().finishActivity(AllOrderActivity.class);
                EventBus.getDefault().post(new Extendedinfo());
            }
            @Override
            public void onFailure() {
            }
        });
    }
}
