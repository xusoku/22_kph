package com.davis.kangpinhui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.adapter.base.CommonFragmentAdapter;
import com.davis.kangpinhui.fragment.AllorderFragment;
import com.davis.kangpinhui.fragment.CouponFragment;
import com.davis.kangpinhui.util.CommonManager;
import com.davis.kangpinhui.views.HackyViewPager;
import com.davis.kangpinhui.views.viewpagerindicator.FixPageIndicator;
import com.davis.kangpinhui.views.viewpagerindicator.PageIndicator;
import com.davis.kangpinhui.views.viewpagerindicator.scrollbar.ColorBar;
import com.davis.kangpinhui.views.viewpagerindicator.scrollbar.ScrollBar;

import java.util.ArrayList;

public class CouponActivity extends BaseActivity {


    String[] tabNames = {"未使用", "已使用", "已过期"};

    HackyViewPager viewPager;
    FixPageIndicator indicator;

    //   0:未使用  1：已使用  2：已过期
    private int id=0;

    public static void jumpCouponActivity(Context cot, int id) {
        if(AppApplication.isLogin(cot)) {
            Intent it = new Intent(cot, CouponActivity.class);
            it.putExtra("id", id);
            cot.startActivity(it);
        }
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_coupon;
    }

    @Override
    protected void initVariable() {

        id=getIntent().getIntExtra("id",0);
    }

    @Override
    protected void findViews() {

        showTopBar();
        setTitle("我的优惠券");
        viewPager = $(R.id.viewPager);
        indicator = $(R.id.indicator);
        viewPager.setOffscreenPageLimit(6);
    }

    @Override
    protected void initData() {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        for (int i = 0; i <3 ; i++) {
            fragments.add(CouponFragment.newInstance(i));
        }
        viewPager.setAdapter(new CommonFragmentAdapter(getSupportFragmentManager(), fragments));
        indicator.setViewPager(viewPager, id);


        indicator.setIndicatorAdapter(new PageIndicator.IndicatorAdapter() {
            @Override
            public View getIndicatorView(int position) {
                TextView textView = (TextView) getLayoutInflater().inflate(R.layout.layout_allorder_tab_item, null);
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

    @Override
    public void doClick(View view) {

    }
}
