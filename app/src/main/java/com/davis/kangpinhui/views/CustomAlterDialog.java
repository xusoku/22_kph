package com.davis.kangpinhui.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.davis.kangpinhui.R;


public class CustomAlterDialog {
    /**
     * 上下文
     */
    Context context;
    /**
     * 对话框
     */
    Dialog dialog;
    /**
     * 窗体
     */
    Window window;
    /**
     * 标题
     */
    TextView titleView;

    /**
     *内容
     */
    TextView content_text;

    LinearLayout ll_title;

    /**
     * 中间的布局
     */
    LinearLayout contentView;
    /**
     * 底部的布局
     */
    LinearLayout bottomView;
    /**
     * 取消
     */
    TextView cancelButton;
    /**
     * 确认
     */
    TextView comfirmButton;
    /**
     * 底部按钮分割线
     */
    View spaceView;

    boolean isMiddleNeedPadding = true;
    /**
     * 底部button显示类型
     */
    public final static int VISIBLE_ALL_BUTTON = 0;
    public final static int VISIBLE_CANCEL_BUTTON = 1;
    public final static int VISIBLE_CONFIRM_BUTTON = 2;
    public final static int VISIBLE_NONE = 3;

    /**
     * 创建dialog
     *
     * @param context
     */
    public CustomAlterDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.BankListDialog)/*new AlertDialog.Builder(context).create()*/;
        window = dialog.getWindow();
        dialog.show();
        window.setContentView(R.layout.custom_alterdialog);
        setWidth();
        titleView = (TextView) window.findViewById(R.id.title);
        content_text = (TextView) window.findViewById(R.id.content_text);
        ll_title = (LinearLayout) window.findViewById(R.id.ll_title);
        ll_title.setVisibility(View.GONE);
        contentView = (LinearLayout) window.findViewById(R.id.content);
        bottomView = (LinearLayout) window.findViewById(R.id.bottom_layout);
        cancelButton = (TextView) window.findViewById(R.id.cancel);
        comfirmButton = (TextView) window.findViewById(R.id.confirm);
        spaceView = (View) window.findViewById(R.id.space_line);
        bottomButtonVisiblity(VISIBLE_ALL_BUTTON);
        dialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 设置对话框的宽度
     */
    private void setWidth() {
        WindowManager m = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = m.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = (int) (display.getWidth() * 0.875);
        lp.alpha = 1;
        window.setAttributes(lp);
    }

    /**
     * 设置点击对话框外是否消框
     *
     * @param cancel
     */
    public void setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
    }

    /**
     * 设置标题
     *
     * @param resId
     */
    public void setTitle(int resId) {
        titleView.setText(resId);
        ll_title.setVisibility(View.VISIBLE);
    }

    public void setContent_text(String str) {
        content_text.setVisibility(View.VISIBLE);
        content_text.setText(str);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleView.setText(title);
        ll_title.setVisibility(View.VISIBLE);
    }

    /**
     * 自定义布局
     *
     * @param layoutResID
     */
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutResID, null);
        setContentView(view);
    }

    /**
     * 自定义布局
     *
     * @param view
     */
    public void setContentView(View view) {
        if(!isMiddleNeedPadding){
            contentView.setPadding(0,0,0,0);
        }
        contentView.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
    }

    /**
     * 自定义布局
     *
     * @param view
     * @param params
     */
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (params == null) {
            setContentView(view);
            return;
        }
        if(!isMiddleNeedPadding){
            contentView.setPadding(0,0,0,0);
        }
        contentView.addView(view, params);
    }

    /**
     * 隐藏底部布局
     */
    public void bottomLayoutGone() {
        bottomView.setVisibility(View.GONE);
    }

    /**
     * 隐藏底部布局
     */
    public void bottomButtonVisiblity(int type) {
        switch (type) {
            case VISIBLE_ALL_BUTTON:
                spaceView.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                comfirmButton.setVisibility(View.VISIBLE);
                break;
            case VISIBLE_CANCEL_BUTTON:
                spaceView.setVisibility(View.GONE);
                comfirmButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
                break;
            case VISIBLE_CONFIRM_BUTTON:
                spaceView.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                comfirmButton.setVisibility(View.VISIBLE);
                break;
            case VISIBLE_NONE:
                bottomView.setVisibility(View.GONE);
                isMiddleNeedPadding = false;
                break;
            default:
                break;
        }
    }

    /**
     * 取消按钮
     *
     * @param textRes  资源id
     * @param listener 点击事件
     */
    public CustomAlterDialog setCancelButton(int textRes,
                                             final View.OnClickListener listener) {
        cancelButton.setText(textRes);
        cancelButton.setOnClickListener(listener);
        return this;
    }

    /**
     * 取消按钮
     *
     * @param listener 点击事件
     */
    public CustomAlterDialog setCancelButton(String text,
                                             final View.OnClickListener listener) {
        cancelButton.setText(text);
        cancelButton.setOnClickListener(listener);
        return this;
    }


    /**
     * 取消按钮
     *
     */
    public CustomAlterDialog setCancelButton(String text) {
        cancelButton.setText(text);
        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return this;
    }


    /**
     * 确认按钮
     *
     * @param listener 点击事件
     */
    public CustomAlterDialog setConfirmButton(String text,
                                              final View.OnClickListener listener) {
        comfirmButton.setText(text);
        comfirmButton.setOnClickListener(listener);
        return this;
    }

    /**
     * 确认按钮
     *
     * @param text  信息
     * @param textColor 颜色
     * @param listener 点击事件
     */
    public CustomAlterDialog setConfirmButton(String text, int textColor,
                                              final View.OnClickListener listener) {
        comfirmButton.setText(text);
        comfirmButton.setTextColor(textColor);
        comfirmButton.setOnClickListener(listener);
        return this;
    }

    /**
     * 设置按键时间对对话框的操作
     *
     * @param onKeyListener
     */
    public void setOnKeyListener(final OnKeyListener onKeyListener) {
        dialog.setOnKeyListener(onKeyListener);
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }

}
