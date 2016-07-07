package utils;

import android.content.Context;

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
public class DisplayMetrics
{
	private   Context mContext;
	private  android.util.DisplayMetrics dm;


	public DisplayMetrics(Context context){
		mContext=context;
		dm = mContext.getResources().getDisplayMetrics();
	}
	/**
	 * 获取屏幕高度
	 */
	public  float getHeight()
	{
		return dm.heightPixels;
	}

	/**
	 * 获取屏幕宽度
	 */
	public  float getWidth()
	{
		return dm.widthPixels;
	}

	/**
	 * 获取屏幕的密度
	 * @return
	 */
	public  float getDensity()
	{
		return dm.density;
	}

	/** 
	 * dp转换px
	 */
	public  float dp2px(float dpValue)
	{
		return dpValue * dm.density + 0.5f;
	}

	/** 
	 * px转换 dp 
	 */
	public  float px2dp(float pxValue)
	{
		return pxValue / dm.density + 0.5f;
	}

	public  int round( int paramInt) {
		return Math.round(paramInt / getDensity());
	}
}
