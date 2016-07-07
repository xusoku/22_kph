package com.davis.kangpinhui.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.widget.Toast;

import com.davis.kangpinhui.AppApplication;

/**
 * Created by davis on 16/5/18.
 */
public class ToastUitl {

    private static final Context mContext;

    private static  Toast mToast = null;

    static {
        mContext = AppApplication.getApplication();
    }

    /**
     * toast提示
     *
     * @param resId
     */
    public static void showToast(int resId) {
        showToast(getString(resId));
    }
    /**
     * toast提示
     *
     * @param str
     */
    public static void showToast(String str) {
        if (TextUtils.isEmpty(str) || "null".equals(str))
        {
            return;
        }
        if (mToast == null)
        {
            mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(str);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }



    /**
     * 用application
     * context获取string，防止调用fragment.getString()时出现fragment脱离当前activity的情况
     *
     * @param resId
     * @return
     */
    public static String getString(int resId) {
        final Resources resources = getResources();

        if (resources != null) {
            return resources.getString(resId);
        }
        return "";
    }

    public static Resources getResources() {
        return mContext.getResources();
    }

}
