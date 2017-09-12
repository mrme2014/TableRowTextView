package com.qiaomu.tablerow.chart;

import android.support.annotation.ColorInt;

/**
 * Created by qiaomu on 2017/8/2.
 */

public class BaseEntry {
    /**
     * 扇形描述性文字,你可以设置spannableString等实现类对象
     */
    private CharSequence mCharSequence;
    /**
     * 每个扇形的颜色
     */
    private int mChartColor = ChartConf.DEFAULT_COLOR;


    public CharSequence getCharSequence() {
        return mCharSequence;
    }

    public void setCharSequence(CharSequence charSequence) {
        mCharSequence = charSequence;
    }


    public int getChartColor() {
        return mChartColor;
    }

    public void setChartColor(@ColorInt int chartColor) {
        mChartColor = chartColor;
    }
}
