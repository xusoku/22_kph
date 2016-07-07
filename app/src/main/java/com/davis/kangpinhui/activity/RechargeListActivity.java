package com.davis.kangpinhui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.model.Recharge;
import com.davis.kangpinhui.model.WeixinInfo;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.model.basemodel.Page;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.adapter.base.CommonBaseAdapter;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.util.AppManager;
import com.davis.kangpinhui.util.ThridPayUtil;
import com.davis.kangpinhui.util.ToastUitl;
import com.davis.kangpinhui.util.UtilText;
import com.davis.kangpinhui.views.CustomAlterDialog;
import com.davis.kangpinhui.views.LoadMoreListView;

import java.util.ArrayList;

import retrofit2.Call;

public class RechargeListActivity extends BaseActivity {


    private LoadMoreListView listView;

    private int Page = 1;
    private int PageSize = 20;
    private int TotalPage = 0;
    private CommonBaseAdapter<Recharge> adapter;
    private ArrayList<Recharge> list;
    private ThridPayUtil thridPayUtil;

    public static void jumpRechargeListActivity(Context cot) {
        if (AppApplication.isLogin(cot)) {
            Intent it = new Intent(cot, RechargeListActivity.class);
            cot.startActivity(it);
        }
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_recharge_list;
    }

    @Override
    protected void initVariable() {
        list = new ArrayList<>();
    }

    @Override
    protected void findViews() {

        showTopBar();
        setTitle("查询订单");
        thridPayUtil = new ThridPayUtil(this);
        listView = $(R.id.content);

        adapter = new CommonBaseAdapter<Recharge>(this, list, R.layout.activity_recharge_list_item) {
            @Override
            public void convert(ViewHolder holder, final Recharge itemData, int position) {

//                srechargetype ：//付款方式  0:'现金' 4: '微信'   2:'支付宝'
//
//                Stype：订单状态  0：未付款成功， 3：已经付款成功，6已取消订单
//
//                if(stype==0&&( srechargetype==4|| srechargetype==2)){
//
//                }
                String srechargetype="";
                if(itemData.srechargetype.equals("0")){
                    srechargetype="现金";
                }else if(itemData.srechargetype.equals("4")){
                    srechargetype="微信支付";
                }else if(itemData.srechargetype.equals("2")){
                    srechargetype="支付宝支付";
                }
                String stype="";//已支付、待付款
                if(itemData.stype.equals("0")){
                    stype="待付款";
                }else if(itemData.stype.equals("3")){
                    stype="已支付";
                }else if(itemData.stype.equals("6")){
                    stype="已取消";
                }


                holder.setText(R.id.recharge_item_date_text, itemData.daddtime);
                holder.setText(R.id.recharge_item_paytype_text, srechargetype);
                holder.setText(R.id.recharge_item_code_text, itemData.schargenumber);
                TextView tv=holder.getView(R.id.recharge_item_price_text);
                tv.setText(UtilText.getRechargePrice("¥"+itemData.fmoney));
                holder.setText(R.id.recharge_item_type_text, stype);

                LinearLayout recharge_item_linear=holder.getView(R.id.recharge_item_linear);

                if(itemData.stype.equals("0")&&(itemData.srechargetype.equals("4")||itemData.srechargetype.equals("2"))){
                    recharge_item_linear.setVisibility(View.VISIBLE);
                }
                else if(itemData.stype.equals("3")){ //去掉支付按钮
                    recharge_item_linear.setVisibility(View.GONE);
                }else if(itemData.stype.equals("6")){ //去掉全部按钮
                    recharge_item_linear.setVisibility(View.GONE);
                }


                holder.getView(R.id.recharge_item_conuti).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CharSequence[] typepaytext = {"在线支付 微信支付", "在线支付 支付宝支付"};
                        final AlertDialog.Builder builder = new AlertDialog.Builder(RechargeListActivity.this);
                        builder.setTitle("支付方式")
                                .setItems(typepaytext, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AppApplication.getApplication().numberCode=itemData.schargenumber;
                                        if (which==1) {
                                            thridPayUtil.alipayyue(itemData.fmoney, itemData.schargenumber);
                                        } else if (which==0) {//微信
                                            getWeixinPay(itemData.schargenumber);
                                        }
                                    }
                                }).show();

                    }
                });

                holder.getView(R.id.recharge_item_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CustomAlterDialog dialog=new CustomAlterDialog(RechargeListActivity.this);
                                dialog.setTitle("是否取消订单？");
                        dialog.setConfirmButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View  which) {
                                dialog.dismiss();
                                Call<BaseModel> call = ApiInstant.getInstant().cancelRecharge(AppApplication.apptype, itemData.schargenumber, AppApplication.token);
                                call.enqueue(new ApiCallback<BaseModel>() {
                                    @Override
                                    public void onSucssce(BaseModel baseModel) {
                                        dialog.dismiss();
                                        ToastUitl.showToast("取消成功");
                                        initData();
                                        list.clear();
                                    }

                                    @Override
                                    public void onFailure() {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                        dialog.setCancelButton("取消");
                    }
                });

            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    public void startActivityLoading() {
        super.startActivityLoading();
        getListData(Page, PageSize);
    }

    private void getListData(int page, int pagesize) {

        Call<BaseModel<Page<ArrayList<Recharge>>>> call = ApiInstant.getInstant().getRechargelist(AppApplication.apptype,
                page + "", pagesize + "", AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<com.davis.kangpinhui.model.basemodel.Page<ArrayList<Recharge>>>>() {
            @Override
            public void onSucssce(BaseModel<Page<ArrayList<Recharge>>> pageBaseModel) {

                onActivityLoadingSuccess();
                Page<ArrayList<Recharge>> page = pageBaseModel.object;

                TotalPage = page.iTotalPage;

                list.addAll(page.list);
                adapter.notifyDataSetChanged();

                if (list.size() == 0) {
                    onActivityFirstLoadingNoData();
                    listView.onLoadUnavailable();
                } else if (TotalPage == Page) {
                    listView.onLoadSucess(false);
                } else {
                    listView.onLoadSucess(true);
                }
            }

            @Override
            public void onFailure() {
                onActivityLoadingFailed();
                listView.onLoadFailed();
            }
        });

    }

    @Override
    protected void initData() {
        startActivityLoading();
    }

    @Override
    protected void setListener() {
        listView.setOnLoadListener(new LoadMoreListView.OnLoadListener() {
            @Override
            public void onLoad(LoadMoreListView listView) {
                getListData(++Page, PageSize);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RechargeDetailActivity.jumpRechargeDetailActivity(RechargeListActivity.this, list.get(position).schargenumber);
            }
        });
    }

    @Override
    public void doClick(View view) {

    }

    public void getWeixinPay(String orderId) {

        Call<BaseModel<WeixinInfo>> call = ApiInstant.getInstant().getWeixinChongzhiInfo(AppApplication.apptype, orderId, AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<WeixinInfo>>() {
            @Override
            public void onSucssce(BaseModel<WeixinInfo> weixinInfoBaseModel) {
                AppApplication.getApplication().isYue=true;
                thridPayUtil.wxpay(weixinInfoBaseModel.object);
            }

            @Override
            public void onFailure() {
                PayResultActivity.jumpPayResultActivity(RechargeListActivity.this, false, true);
                finish();
                AppManager.getAppManager().finishActivity(CartListActivity.class);

            }
        });

    }
}
