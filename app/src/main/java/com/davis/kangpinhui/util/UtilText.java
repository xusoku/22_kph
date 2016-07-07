package com.davis.kangpinhui.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import com.davis.kangpinhui.AppApplication;
import com.davis.kangpinhui.R;

import java.text.DecimalFormat;

/**
 * Created by davis on 16/5/23.
 */
public class UtilText {

    public static String getFloatToString(String str){
        Float f=Float.parseFloat(str);
        DecimalFormat fnum  =   new  DecimalFormat("##0.00");
         String  dd=fnum.format(f);
        return  dd;
    }
    public static SpannableString getFCToS(String str){
        Float f=Float.parseFloat(str);
        DecimalFormat fnum  =   new  DecimalFormat("##0.00");
         String  dd=fnum.format(f);

        SpannableString spanString = new SpannableString(dd);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#F19a00"));
        spanString.setSpan(span, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }
    public static SpannableString getProductDetail(String str){
        if(str.contains(".0")){
            str=str.substring(0,str.length()-2);
        }
        SpannableString spanString = new SpannableString(str);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#ff0000"));
        spanString.setSpan(span, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }
    public static SpannableString getBigProductDetail(String str){
        if(str.contains(".0")){
            str=str.substring(0,str.length()-2);
        }
        SpannableString spanString = new SpannableString(str);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#ff0000"));
        spanString.setSpan(span, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        AbsoluteSizeSpan spana = new AbsoluteSizeSpan(CommonManager.dpToPx(80));
        spanString.setSpan(spana, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }
    public static SpannableString getOrderDetail(String str){
        if(str.contains(".0")){
            str=str.substring(0,str.length()-2);
        }
        SpannableString spanString = new SpannableString(str);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#F19a00"));
        spanString.setSpan(span, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }
    public static String getDivideZero(String str){
        if(str.contains(".0")){
            str=str.substring(0,str.length()-2);
        }
        return str;
    }
    public static SpannableString getIndexPrice(String str){
        if(str.contains(".0")){
            str=str.substring(0,str.length()-2);
        }
        SpannableString spanString = new SpannableString(str);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#ff0000"));
        spanString.setSpan(span, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public static SpannableString getRechargePrice(String str){
        if(str.contains(".0")){
            str=str.substring(0,str.length()-2);
        }
        SpannableString spanString = new SpannableString(str);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#ff0000"));
        spanString.setSpan(span, 1, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }



    public static SpannableString gettest(String str, int size1,
                                                   int size2) {
        SpannableString spanString = new SpannableString(str);
        AbsoluteSizeSpan spanSzieLeft = new AbsoluteSizeSpan(
                (int) (size1 * DisplayMetricsUtils.getDensity()));
        spanString.setSpan(spanSzieLeft, 0, 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        AbsoluteSizeSpan spanSzieMiddle = new AbsoluteSizeSpan(
                (int) (size2 * DisplayMetricsUtils.getDensity()));
        spanString.setSpan(spanSzieMiddle, 1, spanString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#ff0000"));
        spanString.setSpan(span, 0, spanString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }


    public static SpannableString getminenumber(String str) {
        SpannableString spanString = new SpannableString(str);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#f19a00"));
        spanString.setSpan(span, 3, spanString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }


    /**
     * 超链接
     */
    private void addUrlSpan() {
        SpannableString spanString = new SpannableString("超链接");
        URLSpan span = new URLSpan("tel:0123456789");
        spanString.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    /**
     * 文字背景颜色
     */
    private void addBackColorSpan() {
        SpannableString spanString = new SpannableString("颜色2");
        BackgroundColorSpan span = new BackgroundColorSpan(Color.YELLOW);
        spanString.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    /**
     * 文字颜色
     */
    private void addForeColorSpan() {
        SpannableString spanString = new SpannableString("颜色1");
        ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
        spanString.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    /**
     * 字体大小
     */
    private void addFontSpan() {
        SpannableString spanString = new SpannableString("36号字体");
        AbsoluteSizeSpan span = new AbsoluteSizeSpan(36);
        spanString.setSpan(span, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    /**
     * 粗体，斜体
     */
    private void addStyleSpan() {
        SpannableString spanString = new SpannableString("BIBI");
        StyleSpan span = new StyleSpan(Typeface.BOLD_ITALIC);
        spanString.setSpan(span, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    /**
     * 删除线
     */
    private void addStrikeSpan() {
        SpannableString spanString = new SpannableString("删除线");
        StrikethroughSpan span = new StrikethroughSpan();
        spanString.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * 下划线
     */
    private void addUnderLineSpan() {
        SpannableString spanString = new SpannableString("下划线");
        UnderlineSpan span = new UnderlineSpan();
        spanString.setSpan(span, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * 图片
     */
    private void addImageSpan() {
        SpannableString spanString = new SpannableString(" ");
        Drawable d = AppApplication.getApplication().getResources().getDrawable(R.mipmap.img_defualt_bg);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
        spanString.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
