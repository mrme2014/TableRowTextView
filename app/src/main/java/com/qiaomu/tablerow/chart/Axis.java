package com.qiaomu.tablerow.chart;

/**
 * Created by qiaomu on 2017/7/20.
 * 坐标轴的基础信息
 */
public class Axis {
    private int DEFAULT_COLOR = ChartConf.DEFAULT_COLOR;
    //是否显示分割线
    private boolean mDisplayDivline = false;
    //是否显示箭头
    private boolean mDisplayArrow = false;
    //坐标轴颜色
    private int mAxisColor = DEFAULT_COLOR;
    //最小值
    private int mMinValue = 0;
    //刻度步进值
    private int mStep;
    //最大值
    private int mMaxValue = Integer.MAX_VALUE;
    //坐标轴刻度值颜色
    private int mCoordinateColor = DEFAULT_COLOR;
    //坐标轴刻度值大小
    private int mCoordinateSize = ChartConf.DEFAULT_SUB_SIZE;//sp
    //是否显示坐标轴刻度值
    private boolean mDisplayCoordinateValue = true;
    //坐标轴刻度值旋转角度
    private int mRotateDegrees = 0;

    private float mCoordinateThick=ChartConf.DEFAULT_STROKE_WIDTH;

    private CharSequence mStartValue;
    private CharSequence mEndValue;


    public boolean isDisplayDivider() {
        return mDisplayDivline;
    }

    public void setDisplayDivider(boolean displayDivider) {
        mDisplayDivline = displayDivider;
    }

    public boolean isDisplayArrow() {
        return mDisplayArrow;
    }

    public void setDisplayArrow(boolean displayArrow) {
        mDisplayArrow = displayArrow;
    }

    public int getAxisColor() {
        return mAxisColor;
    }

    public void setAxisColor(int axisColor) {
        mAxisColor = axisColor;
    }

    public int getMinValue() {
        return mMinValue;
    }

    public void setMinValue(int minValue) {
        mMinValue = minValue;
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
    }

    public int getCoordinateColor() {
        return mCoordinateColor;
    }

    public void setCoordinateColor(int coordinateColor) {
        mCoordinateColor = coordinateColor;
    }

    public int getCoordinateSize() {
        return mCoordinateSize;
    }

    public void setCoordinateSize(int coordinateSize) {
        mCoordinateSize = coordinateSize;
    }

    public boolean isDisplayCoordinateValue() {
        return mDisplayCoordinateValue;
    }

    public void setDisplayCoordinateValue(boolean displayCoordinateValue) {
        mDisplayCoordinateValue = displayCoordinateValue;
    }

    public int getRotateDegrees() {
        return mRotateDegrees;
    }

    public void setRotateDegrees(int rotateDegrees) {
        mRotateDegrees = rotateDegrees;
    }

    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        mStep = step;
    }

    public void setStartValue(CharSequence startValue) {
        mStartValue = startValue;
    }

    public void setEndValue(CharSequence endValue) {
        mEndValue = endValue;
    }
    public float getCoordinateThick() {
        return mCoordinateThick;
    }

    public void setCoordinateThick(float coordinateThick) {
        mCoordinateThick = coordinateThick;
    }
}
