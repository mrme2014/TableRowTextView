package com.qiaomu.tablerow.chart;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;

/**
 * Created by qiaomu on 2017/7/20.
 * <p>
 * 这是饼图的每个扇形的实体类，每个字段的意思有必要大致看一下，很多属性都是可配置的
 */

public class PieEntry extends BaseEntry {
    private int DEFAULT_COLOR = ChartConf.DEFAULT_COLOR;
    /**
     * 每个扇形描述文字大小
     */
    private float mPieTextSize = ChartConf.DEFAULT_SUB_SIZE;
    /**
     * 每个扇形的颜色
     */
    private int mChartColor = DEFAULT_COLOR;
    /**
     * 每个扇形指示器颜色
     */
    private int mIndicatorColor = Color.RED;
    /**
     * 每个扇形的指示器宽度
     */
    private int mIndicatorWidth = ChartConf.DEFAULT_STROKE_WIDTH;
    /**
     * 每个扇形的指示器长度,假设你指定了5dp,那么将有5dp位于扇形内,非垂直或水平方向的将有10dp位于扇形外,垂直或水平将有5dp位于扇形外
     */
    private float mIndicatorLength = ChartConf.DEFAULT_LENGTH10dp;
    /**
     * 扇形指示器与文字描述之间的间距
     */
    private float mIndicatorCharPad = ChartConf.DEFAULT_PADDING;

    /**
     * 扇形描述性文字颜色
     */
    private int mCharSequenceColor = DEFAULT_COLOR;
    //暂时用不到
    private float spacingmult = ChartConf.DEFAULT_SPACINGMULT;
    private float spacingadd = ChartConf.DEFAULT_SPACINGADD;

    /**
     * 必传,该扇形在饼图中所占百分比
     */
    private double mPercent;
    /**
     * 扇形开始绘制角度
     */
    private double mStartAngle;
    /**
     * 扇形扫过角度
     */
    private double mSweepAngle;
    /**
     * 是否需要显示描述文字,如果不显示那么指示器也是一同不显示的
     */
    private boolean mDisplayCharSequence = true;
    /**
     * 扇形之间的分割线的宽度,传0不显示
     */
    private float mDividerWidth = ChartConf.DEFAULT_STROKE_WIDTH;
    /**
     * 扇形之间的分割线的颜色,如果mDividerWidth大于0,但是不设置分割线颜色,那么显示将是你设置的控件背景颜色
     */
    private int mDividerColor = -1;
    /**
     * 指示器文字说明的位置
     */
    @PiePosition
    private int mCharSequencePosition = PiePosition.OUT_SIDE;

    public PieEntry(@FloatRange(from = 0.0f, to = 1f) double percent, CharSequence charSequence) {
        setPercent(percent);
        setCharSequence(charSequence);
    }

    public int getChartColor() {
        return mChartColor;
    }

    public void setChartColor(@ColorInt int chartColor) {
        mChartColor = chartColor;
        setIndicatorColor(mChartColor);
    }

    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    public void setIndicatorColor(@ColorInt int indicatorColor) {
        mIndicatorColor = indicatorColor;
    }

    public int getIndicatorWidth() {
        return mIndicatorWidth;
    }

    public void setIndicatorWidth(int indicatorWidth) {
        mIndicatorWidth = indicatorWidth;
    }

    private void setPercent(@FloatRange(from = 0.0f, to = 100.0f) double percent) {
        mPercent = percent;
    }

    public double getPercent() {
        return mPercent;
    }

    public float getIndicatorLength() {
        return mIndicatorLength;
    }

    public void setIndicatorLength(float indicatorLength) {
        mIndicatorLength = indicatorLength;
    }

    public float getSpacingmult() {
        return spacingmult;
    }

    public void setSpacingmult(float spacingmult) {
        this.spacingmult = spacingmult;
    }

    public float getSpacingadd() {
        return spacingadd;
    }

    public void setSpacingadd(float spacingadd) {
        this.spacingadd = spacingadd;
    }

    public float getPieTextSize() {
        return mPieTextSize;
    }

    public void setPieTextSize(float pieTextSize) {
        this.mPieTextSize = pieTextSize;
    }

    public void setDisplayCharSequence(boolean displayCharSequence) {
        this.mDisplayCharSequence = displayCharSequence;
    }

    public boolean isDisplayCharSequence() {
        return mDisplayCharSequence;
    }

    public double getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(@FloatRange(from = -90.0f, to = 360.0f) double startAngle) {
        mStartAngle = startAngle;
    }

    public double getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(@FloatRange(from = 0.0f, to = 360.0f) double sweepAngle) {
        mSweepAngle = sweepAngle;
    }

    public void setDividerWidth(float dividerWidth) {
        mDividerWidth = dividerWidth;
    }

    public float getDividerWidth() {
        return mDividerWidth;
    }

    public void setDividerColor(@ColorInt int dividerColor) {
        mDividerColor = dividerColor;
    }

    public int getDividerColor() {
        return mDividerColor;
    }


    public float getIndicatorCharPad() {
        return mIndicatorCharPad;
    }

    public void setIndicatorCharPad(float indicatorCharPad) {
        mIndicatorCharPad = indicatorCharPad;
    }

    public void setCharSequencePosition(@PiePosition int charSequencePosition) {
        mCharSequencePosition = charSequencePosition;
    }

    public int getCharSequencePosition() {
        return mCharSequencePosition;
    }

    public int getCharSequenceColor() {
        return mCharSequenceColor;
    }

    public void setCharSequenceColor(@ColorInt int color) {
        this.mCharSequenceColor = color;
    }
}
