package com.davis.kangpinhui.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;

import com.davis.kangpinhui.AppApplication;

import java.util.Set;

/**
 */
public class SharePreferenceUtils
{
	private static Context mContext = null;
	static
	{
		mContext = AppApplication.getApplication();
	}

	private SharedPreferences sp = null;

	private SharePreferenceUtils(String fileName)
	{
		sp = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
	}

	public static SharePreferenceUtils getSharedPreferences(String fileName)
	{
		return new SharePreferenceUtils(fileName);
	};
	public static SharePreferenceUtils getSharedPreferences()
	{
		return new SharePreferenceUtils("kph");
	};

	public void putString(String key, String value)
	{
		sp.edit().putString(key, value).commit();
	}

	public void putLong(String key, long value)
	{
		sp.edit().putLong(key, value).commit();
	}

	public void putBoolean(String key, boolean value)
	{
		sp.edit().putBoolean(key, value).commit();
	}

	public void putInt(String key, int value)
	{
		sp.edit().putInt(key, value).commit();
	}

	@TargetApi(11)
	public void putStringSet(String key, Set<String> values)
	{
		sp.edit().putStringSet(key, values).commit();
	}

	public void putFloat(String key, float value)
	{
		sp.edit().putFloat(key, value).commit();
	}

	public String getString(String key, String defValue)
	{
		return sp.getString(key, defValue);
	}

	public boolean getBoolean(String key, boolean defValue)
	{
		return sp.getBoolean(key, defValue);
	}

	public float getFloat(String key, float defValue)
	{
		return sp.getFloat(key, defValue);
	}

	public int getInt(String key, int defValue)
	{
		return sp.getInt(key, defValue);
	}

	public long getLong(String key, long defValue)
	{
		return sp.getLong(key, defValue);

	}

	public void clear()
	{
		sp.edit().clear().commit();
	}
}
