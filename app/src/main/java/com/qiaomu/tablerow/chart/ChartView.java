package com.qiaomu.tablerow.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiaomu on 2017/7/20.
 */

public abstract class ChartView<T extends BaseEntry> extends View {
    public final String TAG = getClass().toString();
    private Paint mAxisPaint; //坐标轴画笔
    private Paint mLegendPaint;//图例画笔
    private Paint mGraphPaint;//图形画笔
    private TextPaint mNotesPaint;//注释画笔
    private Paint mDividerPaint;//分割线
    private Paint mTitlePaint;//标题
    private Paint clearPaint;

    protected Axis mXaxis;
    protected Axis mYaxis;

    private Legend mLegend;//图例
    private CharSequence mTitle;//标题

    protected Bitmap mCacheBitmap;
    private Canvas mCacheCanvas;//双缓冲

    protected List<T> mDatas;
    private ValueAnimator mValueAnimator;
    public RectF mRectF;//图形区域
    private long mAnimationDuration = 3000;
    protected float mExtraLeftPad, mExtraTopPad, mExtraRightPad, mExtraBottomPad;
    protected float mMaxPadding;//四周应该间隔的距离
    protected StaticLayout mLayout;
    private CharSequence mDescription = "暂无数据";//空状态下文字描述

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mDatas == null || mDatas.size() == 0)
            return;

        calculateOffset();

        makeChartRegion();
    }

    /**
     * 在这个方法里设定 左{@link ChartView#getExtraPaddingLeft()}
     * --------------上{@link ChartView#getExtraPaddingTop()}
     * --------------右{@link ChartView#getExtraPaddingRight()}
     * --------------下 {@link ChartView#getExtraPaddingBottom()}
     * ---------------图形所在矩形应该偏移多少px
     */


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDatas == null || mDatas.size() == 0) {
            drawDescription(canvas);
            return;
        }
        render(canvas);
    }

    /*mRectF = new RectF(getRectLeft(),
                    getRectTop(),
                    getRectWidth() + getRectLeft(),
                    getRectHeight() + getRectTop()
            );*/
    public abstract void calculateOffset();

    public abstract void makeChartRegion();

    public abstract void render(Canvas canvas);

    public Paint getAxisPaint() {
        if (mAxisPaint == null) {
            mAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mAxisPaint.setAntiAlias(true);
            mAxisPaint.setDither(true);
        }
        return mAxisPaint;
    }

    public Paint getLegendPaint() {
        if (mLegendPaint == null) {
            mLegendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mLegendPaint.setAntiAlias(true);
            mLegendPaint.setDither(true);
        }
        return mLegendPaint;
    }

    public Paint getGraphPaint() {
        if (mGraphPaint == null) {
            mGraphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mGraphPaint.setAntiAlias(true);
            mGraphPaint.setDither(true);
        }
        return mGraphPaint;
    }

    public Paint getDividerPaint() {
        if (mDividerPaint == null) {
            mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDividerPaint.setAntiAlias(true);
            mDividerPaint.setDither(true);
        }
        return mDividerPaint;
    }

    public TextPaint getNotesPaint() {
        if (mNotesPaint == null) {
            mNotesPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mNotesPaint.setTextSize(ChartConf.DEFAULT_SIZE);
        }
        return mNotesPaint;
    }

    public Paint getTitlePaint() {
        if (mTitlePaint == null) {
            mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTitlePaint.setTextSize(ChartConf.DEFAULT_SIZE);
        }
        return mTitlePaint;
    }

    public Axis getXaxis() {
        if (mXaxis == null) mXaxis = new Axis();
        return mXaxis;
    }

    public Axis getYaxis() {
        if (mYaxis == null) mYaxis = new Axis();
        return mYaxis;
    }

    public Legend getLegend() {
        if (mLegend == null)
            mLegend = new Legend();
        return mLegend;
    }

    public void setLegend(Legend legend) {
        mLegend = legend;
    }


    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public int dp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().density;
        return (int) (spValue * fontScale + 0.5f);
    }


    public float getRectWidth() {
        return getWidth() - getExtraPaddingLeft() -
                getPaddingLeft() - getExtraPaddingRight() - getPaddingRight();
    }

    public float getRectHeight() {
        return getHeight() - getExtraPaddingTop() - getPaddingTop() - getPaddingBottom();
    }

    public float getRectLeft() {
        return getExtraPaddingLeft() + getPaddingLeft();
    }

    public float getRectTop() {
        return getExtraPaddingTop() + getPaddingTop();
    }

    public float getExtraPaddingLeft() {
        return mExtraLeftPad == 0 ? mMaxPadding : mExtraLeftPad;
    }

    public float getExtraPaddingTop() {
        return mExtraTopPad; //== 0 ? mMaxPadding : mExtraTopPad;
    }

    public float getExtraPaddingRight() {
        return mExtraRightPad == 0 ? getExtraPaddingLeft() : mExtraRightPad;
    }

    public float getExtraPaddingBottom() {
        return mExtraBottomPad;//== 0 ? getExtraPaddingTop() : mExtraBottomPad;
    }

    public int getChildCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


    public void clearAnimation() {
        if (mValueAnimator != null && mValueAnimator.isRunning())
            mValueAnimator.cancel();
    }

    public ValueAnimator getValueAnimator() {
        if (mValueAnimator == null) {
            mValueAnimator = ValueAnimator.ofFloat(0, 1);
            mValueAnimator.setInterpolator(new DecelerateInterpolator(3f));
            mValueAnimator.setDuration(mAnimationDuration);
        }
        return mValueAnimator;
    }

    float lastAnimatedFraction = 0F;

    public void startValueAnimation(final ValueAnimator.AnimatorUpdateListener updateListener) {
        lastAnimatedFraction = 0F;
        getValueAnimator().addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                if (lastAnimatedFraction == animatedFraction) return;
                lastAnimatedFraction = animatedFraction;
                if (updateListener != null) updateListener.onAnimationUpdate(animation);
            }
        });
        getValueAnimator().start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (getValueAnimator() != null && getValueAnimator().isRunning()) {
            getValueAnimator().cancel();
            getValueAnimator().removeAllListeners();
            getValueAnimator().removeAllUpdateListeners();
        }

    }

    //获取图形背景色
    public int getBackgroundColor() {
        Drawable drawable = this.getBackground();
        if (drawable instanceof ColorDrawable) {
            return ((ColorDrawable) drawable).getColor();
        }
        return Color.WHITE;
    }

    public int getCharSequenceLength(CharSequence source) {
        return TextUtils.isEmpty(source) ? 0 : source.length();
    }

    //得到
    public RectF getCharSequenceRect(CharSequence sequence) {
        if (sequence instanceof Spannable) {
            mLayout = new StaticLayout(sequence, getNotesPaint(), 10000, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, false);
            float textHeight = mLayout.getHeight();
            float maxLineWidth = getMaxLineWidth();
            return new RectF(0, 0, maxLineWidth, textHeight);
        } else {
            Paint.FontMetrics fontMetrics = getNotesPaint().getFontMetrics();
            float textHeight = fontMetrics.descent - fontMetrics.ascent;
            float textWidth = getNotesPaint().measureText(sequence, 0, getCharSequenceLength(sequence));
            return new RectF(0, 0, textWidth, textHeight);
        }

    }

    //获取所有行中字符最长的哪一行的宽度
    public float getMaxLineWidth() {
        int lineCount = mLayout.getLineCount();
        float maxWidth = mLayout.getLineWidth(0);
        for (int i = 0; i < lineCount; i++) {
            Math.max(maxWidth, mLayout.getLineWidth(i));
        }
        return maxWidth;

    }

    public RectF getLegendRect() {
        if (getLegend().getLegendRect() != null)
            return getLegend().getLegendRect();

        List<CharSequence> descriptions;
        descriptions = getLegend().getDescription();
        if (descriptions == null || descriptions.size() == 0) {
            descriptions = new ArrayList<>();
            for (int i = 0; i < getChildCount(); i++) {
                CharSequence sequence = mDatas.get(i).getCharSequence();
                descriptions.add(sequence);
            }
        }

        if (descriptions == null || descriptions.size() == 0)
            return null;

        int position = getLegend().getPosition();
        RectF paddingRectF = getLegend().getPaddingRectF();

        float sumLegendWidth = 0F;
        float sumLegendHeight = 0F;

        float maxLegendWidth = 0F;
        float maxLegendHeight = 0F;

        float desLegendPad = getLegend().getDesLegendPad();
        float legendPad = getLegend().getLegendPad();
        float legendIndicatorWidth = getLegend().getLegendIndicatorWidthByStyle();

        for (int i = 0; i < descriptions.size(); i++) {
            CharSequence charSequence = descriptions.get(i);
            RectF rectF = getCharSequenceRect(charSequence);
            float legendWidth = rectF.width() + desLegendPad + legendPad + legendIndicatorWidth;
            if (position == Legend.Position.TOP_RIGHT_VERTICAL) {
                if (i % Legend.TOP_VERTICAL_MAX_LEGEND == 0 && i > 0) {//3  0  1  2  0
                    sumLegendWidth += maxLegendWidth;
                    sumLegendHeight = Math.max(sumLegendHeight, maxLegendHeight);
                    maxLegendHeight = 0;
                }
                sumLegendWidth = Math.max(sumLegendWidth, legendWidth);
                maxLegendHeight += rectF.height();
            } else if (position == Legend.Position.TOP_RIGHT_HORIZONTAL) {
                if (i % Legend.TOP_HORIZONTAL_MAX_LEGEND == 0 && i > 0) {// 6  0 1 2 3 4 5 0
                    sumLegendHeight += maxLegendHeight;
                    sumLegendWidth = Math.max(sumLegendWidth, maxLegendWidth);
                    maxLegendHeight = 0;
                    maxLegendWidth = 0;
                }
                maxLegendWidth += legendWidth;
                maxLegendHeight = Math.max(maxLegendHeight, rectF.height());

            } else if (position == Legend.Position.RIGHT) {
                if (i % Legend.RIGHT_MAX_LEGEND == 0 && i > 0) {//6  0  1  2  3
                    sumLegendWidth += maxLegendWidth;
                    sumLegendHeight = Math.max(sumLegendHeight, maxLegendHeight);
                    maxLegendHeight = 0;
                }
                sumLegendWidth = Math.max(sumLegendWidth, legendWidth);
                maxLegendHeight += rectF.height();
            } else if (position == Legend.Position.Bottom) {
                if (i % Legend.BOTTOM_MAX_LEGEND == 0 && i > 0) {// 6  0 1 2 3 4 5 0
                    sumLegendHeight += maxLegendHeight;
                    sumLegendWidth = Math.max(sumLegendWidth, maxLegendWidth);
                    maxLegendHeight = 0;
                    maxLegendWidth = 0;
                }
                maxLegendWidth += legendWidth;
                maxLegendHeight = Math.max(maxLegendHeight, rectF.height());
            }
        }

        RectF rectF = new RectF(0, 0, sumLegendWidth, sumLegendHeight);
        getLegend().setLegendRect(rectF);
        return rectF;
    }

    public double scaleDouble(int newScale, double dv) {
        BigDecimal bg = new BigDecimal(dv);
        return bg.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    //缩进矩形区域
    public void innerRect(float scale) {
        mRectF.set(mRectF.left + scale, mRectF.top + scale, mRectF.right - scale, mRectF.bottom - scale);
    }

    //放大矩形区域
    public void outRect(float scale) {
        mRectF.set(mRectF.left - scale, mRectF.top - scale, mRectF.right + scale, mRectF.bottom + scale);
    }

    //设置空状态文字
    public void setEmptyDescription(final CharSequence description) {
        mDescription = description;
    }

    public void drawDescription(Canvas canvas) {
        if (TextUtils.isEmpty(mDescription))
            return;

        getNotesPaint().setTextSize(sp2px(12));
        getNotesPaint().setColor(Color.GRAY);
        float desWidth = getNotesPaint().measureText(mDescription, 0, getCharSequenceLength(mDescription));
        Paint.FontMetrics metrics = getNotesPaint().getFontMetrics();
        float baseLine = getHeight() / 2 + ((metrics.bottom - metrics.top) / 2 - metrics.bottom) / 3;
        canvas.drawText(mDescription, 0, getCharSequenceLength(mDescription), getWidth() / 2 - desWidth / 2, baseLine, getNotesPaint());
    }
}
