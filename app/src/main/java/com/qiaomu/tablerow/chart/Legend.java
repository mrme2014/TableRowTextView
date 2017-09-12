package com.qiaomu.tablerow.chart;

import android.graphics.RectF;

import java.util.List;

/**
 * Created by qiaomu on 2017/7/31.
 */
public class Legend {


    //图例风格，举行，圆，线条
    public @interface Style {
        int RECT = 1;
        int CIRCLE = 2;
        int LINE = 3;
    }

    //图例位置--右上角垂直分布,水平分布，右边居中，底部居中
    public @interface Position {
        int TOP_RIGHT_VERTICAL = 0;
        int TOP_RIGHT_HORIZONTAL = 1;
        int RIGHT = 2;
        int Bottom = 3;
    }

    //换行方式 -两端对齐，居中对齐
    public @interface NewLine {
        int ALIGN_START = 1;
        int ALIGN_CENTER = 2;
    }

    public static final int TOP_VERTICAL_MAX_LEGEND = 2;
    public static final int TOP_HORIZONTAL_MAX_LEGEND = 6;
    public static final int RIGHT_MAX_LEGEND = 6;
    public static final int BOTTOM_MAX_LEGEND = 6;

    private List<Integer> mColors;
    private List<CharSequence> mDescription;
    private float mDesLegendPad = ChartConf.DEFAULT_PADDING;
    private float mRectWidth = ChartConf.DEFAULT_RECT_WIDTH;
    private float mRadius = ChartConf.DEFAULT_RECT_WIDTH;
    private float mLineWidth = ChartConf.DEFAULT_RECT_WIDTH;
    private float mLineHeight = ChartConf.DEFAULT_PADDING;
    private float mLegendPad = ChartConf.DEFAULT_RECT_WIDTH;
    private RectF mLegendRect;
    private RectF mPaddingRectF;
    @Style
    private int mLegendStyle = Style.RECT;
    @Position
    private int mPosition = Position.TOP_RIGHT_VERTICAL;
    @NewLine
    private int mNewLine = NewLine.ALIGN_CENTER;

    public List getColors() {
        return mColors;
    }

    public void setColors(List<Integer> colors) {
        mColors = colors;
    }

    public List<CharSequence> getDescription() {
        return mDescription;
    }

    public void setDescription(List<CharSequence> description) {
        mDescription = description;
    }

    public float getDesLegendPad() {
        return mDesLegendPad;
    }

    public void setDesLegendPad(float desLegendPad) {
        mDesLegendPad = desLegendPad;
    }

    public float getRectWidth() {
        return mRectWidth;
    }

    public void setRectWidth(float rectWidth) {
        mRectWidth = rectWidth;
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    public float getLineWidth() {
        return mLineWidth;
    }

    public void setLineWidth(float lineWidth) {
        mLineWidth = lineWidth;
    }

    public int getLegendStyle() {
        return mLegendStyle;
    }

    public void setLegendStyle(int legendStyle) {
        mLegendStyle = legendStyle;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getNewLine() {
        return mNewLine;
    }

    public void setNewLine(int newLine) {
        mNewLine = newLine;
    }

    public float getLineHeight() {
        return mLineHeight;
    }

    public void setLineHeight(float lineHeight) {
        mLineHeight = lineHeight;
    }

    public void setPaddingRectF(float left, float top, float right, float bottom) {
        mPaddingRectF = new RectF(left, top, right, bottom);
    }

    public RectF getPaddingRectF() {
        return mPaddingRectF;
    }

    public float getLegendPad() {
        return mLegendPad;
    }

    public void setLegendPad(float legendPad) {
        mLegendPad = legendPad;
    }


    public float getLegendIndicatorWidthByStyle() {
        if (mLegendStyle == Style.RECT)
            return getRectWidth();
        else if (mLegendStyle == Style.CIRCLE)
            return getRadius();
        else return getLineWidth();
    }
    public void setLegendRect(RectF legendRect) {
        mLegendRect = legendRect;
    }
    public RectF getLegendRect(){
        return mLegendRect;
    }

}
