package com.davis.kangpinhui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davis.kangpinhui.R;
import com.davis.kangpinhui.util.ToastUitl;


/**
 * Created by davis on 16/5/19.
 */
public class MineCustomLayout extends LinearLayout {
    TextView tv;
    public MineCustomLayout(Context context) {
        this(context, null);
    }


    public MineCustomLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MineCustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MineCustomLayout, defStyleAttr, 0);

        String name = typedArray.getString(R.styleable.MineCustomLayout_text);
        boolean show = typedArray.getBoolean(R.styleable.MineCustomLayout_show, true);

        int ids = typedArray.getResourceId(R.styleable.MineCustomLayout_src, 0);
        boolean line = typedArray.getBoolean(R.styleable.MineCustomLayout_line, true);

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_mine_item, this, true);

        this.setClickable(true);
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        LinearLayout linearLayout= (LinearLayout) view.findViewById(R.id.mine_linear);
        linearLayout.setBackgroundResource(outValue.resourceId);
        ImageView iv = (ImageView) view.findViewById(R.id.mine_left_image);
         tv = (TextView) view.findViewById(R.id.mine_center_name);
        ImageView miv = (ImageView) view.findViewById(R.id.mine_right_iv);
        View viewline =  view.findViewById(R.id.mine_line_view);

        iv.setImageResource(ids);
        tv.setText(name);

        if (show) {
            miv.setVisibility(View.VISIBLE);
        } else {
            miv.setVisibility(View.GONE);
        }
        if (line) {
            viewline.setVisibility(View.VISIBLE);
        } else {
            viewline.setVisibility(View.GONE);
        }
        typedArray.recycle();
    }

    public void setText(SpannableString str){
        tv.setText(str);
    }
    public void setText(String str){
        tv.setText(str);
    }

}
