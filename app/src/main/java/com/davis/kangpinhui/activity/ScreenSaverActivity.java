package com.davis.kangpinhui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.davis.kangpinhui.R;
import com.davis.kangpinhui.activity.base.BaseActivity;
import com.davis.kangpinhui.adapter.base.ViewHolder;
import com.davis.kangpinhui.util.CommonManager;
import com.davis.kangpinhui.util.DisplayMetricsUtils;
import com.davis.kangpinhui.views.loopbanner.LoopBanner;
import com.davis.kangpinhui.views.loopbanner.LoopPageAdapter;

import java.util.ArrayList;
import java.util.Random;

public class ScreenSaverActivity extends Activity  {

    private LoopBanner loopBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Random random=new Random();
        int i=random.nextInt(2);
        if(i==0){
            Intent it=new Intent(this,VideoPlayerActivity.class);
            startActivity(it);
            finish();
        }else{

        }

        loopBanner = new LoopBanner(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        loopBanner.setLayoutParams(params);
        setContentView(loopBanner);

        ArrayList<String> str=new ArrayList<>();
        str.add("http://m.kangpinhui.com/images/1.jpg");
        str.add("http://m.kangpinhui.com/images/2.jpg");

        loopBanner.setPageAdapter(new LoopPageAdapter<String>(this, str, R.layout.activity_screen_saver) {

            @Override
            public void convert(ViewHolder holder, final String itemData, final int position) {
                // TODO Auto-generated method stub
                ImageView imageView = (ImageView) holder.getConvertView();
                Glide.with(ScreenSaverActivity.this).load(itemData)
                        .placeholder(R.mipmap.img_defualt_bg)
                        .error(R.mipmap.img_defualt_bg)
                        .into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        loopBanner.startTurning(3000);
    }

    @Override
    public void onStop()
    {
        // TODO Auto-generated method stub
        super.onStop();
        loopBanner.stopTurning();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        loopBanner.stopTurning();
    }

}
