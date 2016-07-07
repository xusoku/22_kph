package com.davis.kangpinhui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.adapter.base.CommonFragmentAdapter;
import com.davis.kangpinhui.fragment.AllorderFragment;
import com.davis.kangpinhui.fragment.ClassicFragment;
import com.davis.kangpinhui.util.CommonManager;
import com.davis.kangpinhui.views.CustomTypefaceEditText;
import com.davis.kangpinhui.views.CustomTypefaceTextView;
import com.davis.kangpinhui.views.FlowLayout;
import com.davis.kangpinhui.views.HackyViewPager;
import com.davis.kangpinhui.views.viewpagerindicator.FixPageIndicator;
import com.davis.kangpinhui.views.viewpagerindicator.PageIndicator;
import com.davis.kangpinhui.views.viewpagerindicator.scrollbar.ColorBar;
import com.davis.kangpinhui.views.viewpagerindicator.scrollbar.ScrollBar;

import java.util.ArrayList;

public class AllOrderActivity extends BaseActivity {

    String[] tabNames = {"全部", "待付款", "待配送","配送中"};

    HackyViewPager viewPager;
    FixPageIndicator indicator;

//    0:全部订单 、 1：代付款 2、代配送 3、配送中
    private int id=0;

    public static void jumpAllOrderActivity(Context cot, int id) {
        if(AppApplication.isLogin(cot)) {
            Intent it = new Intent(cot, AllOrderActivity.class);
            it.putExtra("id", id);
            cot.startActivity(it);
        }
    }


    @Override
    protected int setLayoutView() {
        return R.layout.activity_all_order;
    }

    @Override
    protected void initVariable() {

        id=getIntent().getIntExtra("id",0);

        tabNames[0]="全部("+AppApplication.getOrderall()+")";
        tabNames[1]="待付款("+AppApplication.getOrderunpaid()+")";
        tabNames[2]="待配送("+AppApplication.getOrderwaitsend()+")";
        tabNames[3]="配送中("+AppApplication.getOrdersending()+")";
    }

    @Override
    protected void findViews() {

        showTopBar();
        setTitle("我的订单");
        viewPager = $(R.id.viewPager);
        indicator = $(R.id.indicator);
        viewPager.setOffscreenPageLimit(6);
    }

    @Override
    protected void initData() {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        for (int i = 0; i <4 ; i++) {
            fragments.add(AllorderFragment.newInstance(i));
        }
        viewPager.setAdapter(new CommonFragmentAdapter(getSupportFragmentManager(), fragments));
        indicator.setViewPager(viewPager, id);


        indicator.setIndicatorAdapter(new PageIndicator.IndicatorAdapter() {
            @Override
            public View getIndicatorView(int position) {
                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.layout_allorder_tab_item, null);
//                textView.setTextSize(CommonManager.dpToPx(20));
                textView.setText(tabNames[position]);
                return textView;
            }

            @Override
            public void onPageScrolled(View view, int position, float selectPercent) {

            }
        });
        ColorBar colorBar = new ColorBar(mContext, getResources().getColor(R.color.colormain), ScrollBar.Gravity.BOTTOM);
        colorBar.setHeight(CommonManager.dpToPx(10));

        indicator.setScrollBar(colorBar);

    }

    @Override
    protected void setListener() {

    }

    public void chageTitle(final String [] str,final int i){

//        tabNames = {"全部", "待付款", "待配送","配送中"};

        indicator.setIndicatorAdapter(new PageIndicator.IndicatorAdapter() {
            @Override
            public View getIndicatorView(int position) {
                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.layout_allorder_tab_item, null);
//                textView.setTextSize(CommonManager.dpToPx(20));
                textView.setText(str[position]);

                return textView;
            }

            @Override
            public void onPageScrolled(View view, int position, float selectPercent) {
//                if(position==i){
//                    ((TextView)(view)).setSelected(true);
//                }
            }


        });
        indicator.setViewPager(viewPager, i);
    }

    @Override
    public void doClick(View view) {

    }
}
