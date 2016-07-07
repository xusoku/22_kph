package com.davis.kangpinhui.fragment;

import android.os.Bundle;
import android.view.View;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.model.Coupon;
import com.davis.kangpinhui.model.basemodel.BaseModel;
import com.davis.kangpinhui.model.basemodel.Page;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.adapter.base.CommonBaseAdapter;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.api.ApiCallback;
import com.davis.kangpinhui.api.ApiInstant;
import com.davis.kangpinhui.fragment.base.BaseFragment;
import com.davis.kangpinhui.util.DateUtils;
import com.davis.kangpinhui.util.LogUtils;
import com.davis.kangpinhui.views.LoadMoreListView;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;

/**
 * Created by davis on 16/5/27.
 */
public class CouponFragment extends BaseFragment {

    private LoadMoreListView mine_allorder_list;
    private int id = 0;

    private int iPage = 1;
    private int iPageSize = 10;
    private int TotalPage = 0;
    private CommonBaseAdapter<Coupon> adapter;
    private ArrayList<Coupon> list;

    public static CouponFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt("id", id);
        CouponFragment sampleFragment = new CouponFragment();
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

        list = new ArrayList<>();
        adapter = new CommonBaseAdapter<Coupon>(getActivity(), list, R.layout.fragment_coupon_item) {
            @Override
            public void convert(ViewHolder holder, Coupon itemData, int position) {

                holder.setText(R.id.coupon_item_price, "¥" + itemData.fmoney);

                if (id == 0) {
                    long endtime=Long.parseLong(itemData.endtime);
                    LogUtils.e("end",DateUtils.date2Str(endtime));
                    long current=System.currentTimeMillis();
                    LogUtils.e("cur",DateUtils.date2Str(current));
                    int day=DateUtils.getDay(endtime-current);
                    holder.setText(R.id.coupon_item_price_item,"剩余"+day+"天过期" );
                } else if (id==1) {
                    holder.setText(R.id.coupon_item_price_item, "已使用");
                } else if (id==2) {
                    holder.setText(R.id.coupon_item_price_item, "已过期");
                }
                holder.setText(R.id.coupon_item_title, itemData.title);
                holder.setText(R.id.coupon_item_content, itemData.context);

                String string = DateUtils.date2Str(Long.parseLong(itemData.endtime), "yyyy-MM-dd");
                holder.setText(R.id.coupon_item_time, "有效期至:" + string);
                if (id == 1 || id == 2) {
                    holder.getView(R.id.coupon_item_time).setVisibility(View.GONE);
                } else if (id == 0) {
                    holder.getView(R.id.coupon_item_time).setVisibility(View.VISIBLE);
                }


            }
        };
        mine_allorder_list.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        startFragmentLoading();
    }

    @Override
    protected void onFragmentLoading() {
        super.onFragmentLoading();
        getCouponList(iPage, iPageSize);
    }

    @Override
    protected void setListener() {

        mine_allorder_list.setOnLoadListener(new LoadMoreListView.OnLoadListener() {
            @Override
            public void onLoad(LoadMoreListView listView) {
                getCouponList(++iPage, iPageSize);
            }
        });
    }

    private void getCouponList(int ipage, int iPageSize) {

        Call<BaseModel<Page<ArrayList<Coupon>>>> call = ApiInstant.getInstant().getCouponlist(AppApplication.apptype, id + "", ipage + "", iPageSize + "", AppApplication.token);
        call.enqueue(new ApiCallback<BaseModel<Page<ArrayList<Coupon>>>>() {
            @Override
            public void onSucssce(BaseModel<Page<ArrayList<Coupon>>> pageBaseModel) {

                onFragmentLoadingSuccess();

                Page<ArrayList<Coupon>> page = pageBaseModel.object;

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

            }
        });
    }
}
