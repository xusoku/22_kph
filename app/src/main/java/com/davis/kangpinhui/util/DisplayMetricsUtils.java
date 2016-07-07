package com.davis.kangpinhui.util;

import android.content.Context;
import android.util.DisplayMetrics;

import com.davis.kangpinhui.AppApplication;

/**
 * ****************************************************************
 * 文件名称	: DisplayMetricsUtils.java
 * 作    者	: hudongsheng
 * 创建时间	: 2014-6-9 下午5:21:50
* 文件描述	: 屏幕显示工具类
 * 版权声明	: Copyright © 2014 江苏钱旺智能系统有限公司
 * 修改历史	: 2014-6-9 1.00 初始版本
 *****************************************************************
 */
public class DisplayMetricsUtils
{
	private static final Context mContext;
	private static DisplayMetrics dm;

	static
	{
		mContext = AppApplication.getApplication();
		dm = mContext.getResources().getDisplayMetrics();
	}

	/**
	 * 获取屏幕高度
	 */
	public static float getHeight()
	{
		return dm.heightPixels;
	}

	/**
	 * 获取屏幕宽度
	 */
	public static float getWidth()
	{
		return dm.widthPixels;
	}

	/**
	 * 获取屏幕的密度
	 * @return
	 */
	public static float getDensity()
	{
		return dm.density;
	}

	/** 
	 * dp转换px
	 */
	public static float dp2px(float dpValue)
	{
		return dpValue * dm.density + 0.5f;
	}

	/** 
	 * px转换 dp 
	 */
	public static float px2dp(float pxValue)
	{
		return pxValue / dm.density + 0.5f;
	}

	public static int round( int paramInt) {
		return Math.round(paramInt / getDensity());
	}
}
