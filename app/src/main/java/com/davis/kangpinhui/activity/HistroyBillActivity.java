package com.davis.kangpinhui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.adapter.base.CommonBaseAdapter;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.model.Consume;
import com.davis.kangpinhui.model.Product;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.model.basemodel.Page;
import com.davis.kangpinhui.util.DateUtils;
import com.davis.kangpinhui.util.UtilText;
import com.davis.kangpinhui.views.LoadMoreListView;
import com.davis.kangpinhui.views.LoadMoreRecyclerView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import retrofit2.Call;

public class HistroyBillActivity extends BaseActivity {

    private LoadMoreListView histroy_bill_lst;
    private TextView histroy_bill_header_text;
    private View viewHeader;
    private ArrayList<Consume> list;
    private CommonBaseAdapter adapter;

    private int Page = 1;

    private int PageSize = 20;
    private int TotalPage = 0;

    public static void jumpHistroyBillActivity(Context cot) {
        if (AppApplication.isLogin(cot)) {
            Intent it = new Intent(cot, HistroyBillActivity.class);
            cot.startActivity(it);
        }
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_histroy_bill;
    }

    @Override
    protected void initVariable() {

        list = new ArrayList<>();

    }

    @Override
    protected void findViews() {

        showTopBar();
        setTitle("个人账单");
        viewHeader = getLayoutInflater().inflate(R.layout.activity_histroy_bill_header, null);
        histroy_bill_header_text=$(viewHeader,R.id.histroy_bill_header_text);
        histroy_bill_lst = $(R.id.content);
        histroy_bill_lst.addHeaderView(viewHeader);
        adapter = new CommonBaseAdapter<Consume>(this, list, R.layout.activity_histroy_bill_item) {
            @Override
            public void convert(ViewHolder holder, Consume itemData, int position) {
                holder.setText(R.id.histroy_bill_item_text_time, DateUtils.date2Str(Long.parseLong(itemData.daddtime), "yyyy-MM-dd HH:mm"));
                holder.setText(R.id.histroy_bill_item_text_code, itemData.scontent);
                if (itemData.stype.equals("1")) {
                    holder.setText(R.id.histroy_bill_item_text_money, "-" + UtilText.getFloatToString(itemData.fmoney));
                } else if (itemData.stype.equals("0")) {
                    holder.setText(R.id.histroy_bill_item_text_money, "+" + UtilText.getFloatToString(itemData.fmoney));
                }

                holder.getView(R.id.histroy_bill_item_relative).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });


            }
        };
        histroy_bill_lst.setAdapter(adapter);

    }

    @Override
    public void startActivityLoading() {
        super.startActivityLoading();

        getConsumeList(Page, PageSize);

    }

    private void getConsumeList(int page, int pageSize) {
        Call<BaseModel<Page<ArrayList<Consume>>>> call = ApiInstant.getInstant().getConsumelist(AppApplication.apptype,
                page + "", pageSize + "", AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<com.davis.kangpinhui.model.basemodel.Page<ArrayList<Consume>>>>() {
            @Override
            public void onSucssce(BaseModel<Page<ArrayList<Consume>>> pageBaseModel) {

                onActivityLoadingSuccess();
                String s=pageBaseModel.errorinfo;
                histroy_bill_header_text.setText("");
                histroy_bill_header_text.append("账户余额:  ");
                histroy_bill_header_text.append(UtilText.getOrderDetail("¥"));
                histroy_bill_header_text.append(UtilText.getFCToS(s));

                        Page < ArrayList < Consume >> page = pageBaseModel.object;

                TotalPage = page.iTotalPage;

                list.addAll(page.list);
                adapter.notifyDataSetChanged();

                if (list.size() == 0) {
                    onActivityFirstLoadingNoData();
                    histroy_bill_lst.onLoadUnavailable();
                } else if (TotalPage != Page) {
                    histroy_bill_lst.onLoadSucess(true);
                } else {
                    histroy_bill_lst.onLoadSucess(false);
                }

            }

            @Override
            public void onFailure() {
                onActivityLoadingFailed();
                histroy_bill_lst.onLoadFailed();
            }
        });
    }

    @Override
    protected void initData() {
        startActivityLoading();
    }

    @Override
    protected void setListener() {
        histroy_bill_lst.setOnLoadListener(new LoadMoreListView.OnLoadListener() {
            @Override
            public void onLoad(LoadMoreListView load) {
                getConsumeList(++Page, PageSize);
            }
        });
    }

    @Override
    public void doClick(View view) {

    }

}
