package com.qiaomu.tablerow;

/**
 * Created by qiaomu on 2017/9/4.
 */


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.text.TextDirectionHeuristicsCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrs on 2016/9/13.
 */
public class TableRowTextView extends AppCompatTextView {
    private static final String TAG = "TableRowTextView";
    private static final int WRAP_CONTENT = 1;
    private static final int FIX_WIDTH = 0;
    private static final int ALIGN_NORMAL = 0;
    private static final int ALIGN_CENTER = 1;
    private static final String ELLIPSIZE = "...";
    private float mEllipsizeWidth;

    protected boolean mShouldDrawBotLine = false;//是否绘制表单底部线条
    protected boolean mShouldDrawTopLine = false;//是否绘制表单顶部线条
    protected boolean mShouldDrawLeftLine = false;//是否绘制表单最坐侧分割线
    protected boolean mShouldDrawRightLine = false;//是否绘制表单最右侧分割线

    private boolean mCellDivider;//是否回绘制单元格分割线
    protected int mFixCellWidth = dp2px(45);//单元格宽度，默认45dp，如果不够充满屏幕，单元格宽度会被增大
    private int mCellMode;
    private int mDividerColor = Color.parseColor("#f4f4f4");
    private float mCellDividerPadding;
    private int mRowDivider = 0x00;
    private int mCellAlign;//文本对齐方式
    private Layout.Alignment mAlignment;
    private StaticLayout mLayout;
    private int mMaxlines;
    protected List<CharSequence> mList;
    protected TextPaint mLinePaint, mTxtPaint;

    public TableRowTextView(Context context) {
        this(context, null);
    }

    public TableRowTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TableRowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TableRowTextView);

        mCellDivider = ta.getBoolean(R.styleable.TableRowTextView_cell_divider, false);

        mFixCellWidth = ta.getDimensionPixelSize(R.styleable.TableRowTextView_fixWidth, mFixCellWidth);

        mCellMode = ta.getInt(R.styleable.TableRowTextView_cell_mode, FIX_WIDTH);
        mCellAlign = ta.getInt(R.styleable.TableRowTextView_align, 0);
        mAlignment = mCellAlign == ALIGN_NORMAL ? Layout.Alignment.ALIGN_NORMAL : Layout.Alignment.ALIGN_CENTER;

        mRowDivider = ta.getInt(R.styleable.TableRowTextView_row_divider, 0x00);
        mShouldDrawLeftLine = ((mRowDivider & 0x01) == 0x01);
        mShouldDrawTopLine = ((mRowDivider & 0x10) == 0x10);
        mShouldDrawRightLine = ((mRowDivider & 0x02) == 0x02);
        mShouldDrawBotLine = ((mRowDivider & 0x20) == 0x20);

        ta.recycle();

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mList == null || mList.size() <= 0)
            return;

        int totalWidth = 0;
        if (mCellMode == FIX_WIDTH) {
            totalWidth = mList.size() * mFixCellWidth;
            totalWidth = Math.max(totalWidth, getMeasuredWidth());
            mFixCellWidth = totalWidth / mList.size();
        } else if (mCellMode == WRAP_CONTENT) {
            float wrapLength = 0;
            int wrapCount = 0;
            for (int i = 0; i < mList.size(); i++) {
                CharSequence charSequence = mList.get(i);
                float measureText = getTextWidth(charSequence);
                if (measureText > mFixCellWidth) {
                    wrapLength += measureText;
                    totalWidth += measureText;
                    wrapCount++;
                } else {
                    totalWidth += mFixCellWidth;
                }

            }
            //如果内容区域一共是 500，控件宽1080  那么强制占满屏
            if (totalWidth <= getMeasuredWidth()) {
                totalWidth = getMeasuredWidth();
                mFixCellWidth = (int) ((totalWidth - wrapLength) / (mList.size() - wrapCount));
            }
            Log.e(TAG, "onMeasure: " + wrapLength + "--" + wrapCount + "--" + (totalWidth - wrapLength));
        }

        int measureSpec = MeasureSpec.makeMeasureSpec(totalWidth, MeasureSpec.EXACTLY);
        setMeasuredDimension(measureSpec, heightMeasureSpec);

    }


    protected void init() {
        if (mLinePaint == null) {
            mLinePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mLinePaint.setColor(getCurrentTextColor());
            mLinePaint.setStrokeWidth(1);

            mTxtPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mTxtPaint.setTextSize(getTextSize());
            mTxtPaint.setColor(getCurrentTextColor());
            mTxtPaint.setTextSize(getTextSize());

            mEllipsizeWidth = mTxtPaint.measureText(ELLIPSIZE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mList == null || mList.size() <= 0)
            return;
        drawFixWidthCharSequence(canvas);

        drawWrapContentCharSequence(canvas);

    }

    //绘制单元格宽度自适应下的文字分布
    private void drawWrapContentCharSequence(Canvas canvas) {
        if (mCellMode != WRAP_CONTENT)
            return;
        float previousRight = 0f;
        for (int i = 0; i < mList.size(); i++) {
            int height = getMeasuredHeight();
            CharSequence txt = mList.get(i) == null ? "" : mList.get(i);
            int textWidth = getTextWidth(txt);
            int outerWidth = textWidth <= mFixCellWidth ? mFixCellWidth : textWidth;
            //StaticLayout不知道是啥的自行百度
            mLayout = new StaticLayout(txt, 0, txt.length(),
                    mTxtPaint, outerWidth, mAlignment, 1.0f, 0f, false);

            float startX = 0f;
            if (textWidth < mFixCellWidth) {
                if (mCellAlign == ALIGN_NORMAL) {
                    startX = previousRight + mFixCellWidth / 2 - getMaxLineWidth() / 2;
                    previousRight += mFixCellWidth;
                } else {
                    startX = previousRight;
                    previousRight += mFixCellWidth;
                }
            } else {
                if (mCellAlign == ALIGN_NORMAL) {
                    startX = previousRight + (mLayout.getWidth() - mLayout.getLineWidth(0)) / 2;//previousRight;
                    previousRight += textWidth;
                } else {
                    startX = previousRight;
                    previousRight += textWidth;
                }
            }

            float baseline = getHeight() / 2 - mLayout.getHeight() / 2;
            canvas.save();
            canvas.translate(startX, baseline);
            mLayout.draw(canvas);
            canvas.restore();
            drawCellDivider(canvas, previousRight);
        }
    }

    //绘制单元格宽度固定下的文字分布
    private void drawFixWidthCharSequence(Canvas canvas) {
        if (mCellMode != FIX_WIDTH)
            return;

        for (int i = 0; i < mList.size(); i++) {
            CharSequence txt = getFixCharSequence(mList.get(i));
            //StaticLayout不知道是啥的自行百度
            mLayout = new StaticLayout(txt, 0, txt.length(), mTxtPaint,
                    mFixCellWidth, mAlignment, 1.0f, 0f, true);
            float x = 0;
            if (mCellAlign == ALIGN_NORMAL) {
                x = mFixCellWidth * (i + 1) - mFixCellWidth / 2 - getMaxLineWidth() / 2;
            } else {
                x = mFixCellWidth * i;
            }
            float baseline = getHeight() / 2 - mLayout.getHeight() / 2;
            canvas.save();
            canvas.translate(x, baseline);
            mLayout.draw(canvas);
            canvas.restore();
            drawCellDivider(canvas, mFixCellWidth * (i + 1));
        }
    }

    //绘制单元格分割线
    private void drawCellDivider(Canvas canvas, float startx) {
        if (mCellDivider) {
            canvas.drawLine(startx, 0, startx, getHeight(), mLinePaint);//右边的线
        }
    }

    public void setDrawTableDividers(boolean left, boolean top, boolean right, boolean bottom, boolean cellDivider, float cellDividerPadding) {
        mShouldDrawLeftLine = left;
        mShouldDrawTopLine = top;
        mShouldDrawRightLine = right;
        mShouldDrawBotLine = bottom;
        mCellDivider = cellDivider;
        mCellDividerPadding = cellDividerPadding;
    }

    public void setTextList(List<CharSequence> lists) {
        if (lists == null)
            return;
        this.mList = lists;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!isInLayout())
                requestLayout();
        }
        invalidate();
    }

    public void setTextArray(CharSequence... textArray) {
        if (textArray != null) {
            ArrayList<CharSequence> list = new ArrayList();
            for (int i = 0; i < textArray.length; i++) {
                list.add(textArray[i]);
            }
            setTextList(list);
        }
    }

    @Override
    public void setMaxLines(int maxlines) {
        mMaxlines = maxlines;
        super.setMaxLines(maxlines);
    }

    //计算需要截断的文本
    private CharSequence getFixCharSequence(CharSequence txt) {
        if (getFixWidthLines(txt) <= mMaxlines || mMaxlines == 0)
            return txt;
        float totalWidth = mEllipsizeWidth;
        int overIndex = 0;

        float[] widths = new float[txt.length()];
        mTxtPaint.getTextWidths(txt, 0, txt.length(), widths);
        for (int j = 0; j < txt.length(); j++) {
            totalWidth += (int) Math.ceil(widths[j]);
            if (totalWidth >= mFixCellWidth * mMaxlines) {
                overIndex = j;
                break;
            }
        }
        if (txt instanceof SpannableStringBuilder) {
            SpannableStringBuilder replace = ((SpannableStringBuilder) txt).delete(overIndex - 1, txt.length());
            return replace.append(ELLIPSIZE);
        }

        return txt.subSequence(0, overIndex) + ELLIPSIZE;
    }

    //精确地得到文字宽度
    private int getTextWidth(CharSequence str) {
        int w = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            mTxtPaint.getTextWidths(str, 0, str.length(), widths);
            for (int j = 0; j < len; j++) {
                w += (int) Math.ceil(widths[j]);
            }
        }
        return w;
    }

    //计算文本行数
    private int getFixWidthLines(CharSequence txt) {
        float measureText = mTxtPaint.measureText(txt, 0, txt.length());
        int lineCount = 1;
        if (measureText > mFixCellWidth) {
            lineCount = (int) (measureText / mFixCellWidth) + 1;
        }
        return lineCount;
    }

    //得到
    private RectF getCharSequenceRect(CharSequence sequence) {
        if (sequence instanceof Spannable) {
            mLayout = new StaticLayout(sequence, mTxtPaint, 10000, Layout.Alignment.ALIGN_CENTER, 1.0f, 0f, false);
            float textHeight = mLayout.getHeight();
            float maxLineWidth = getMaxLineWidth();
            return new RectF(0, 0, maxLineWidth, textHeight);
        } else {
            Paint.FontMetrics fontMetrics = mTxtPaint.getFontMetrics();
            float textHeight = fontMetrics.descent - fontMetrics.ascent;
            float textWidth = mTxtPaint.measureText(sequence, 0, getCharSequenceLength(sequence));
            return new RectF(0, 0, textWidth, textHeight);
        }

    }

    //获取所有行中字符最长的哪一行的宽度
    private float getMaxLineWidth() {
        int lineCount = mLayout.getLineCount();
        float maxWidth = mLayout.getLineWidth(0);

        for (int i = 0; i < lineCount; i++) {
            Math.max(maxWidth, mLayout.getLineWidth(i));
        }
        return maxWidth;

    }

    private int getCharSequenceLength(CharSequence source) {
        return TextUtils.isEmpty(source) ? 0 : source.length();
    }

    private int dp2px(float dpValue) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    private int sp2px(float spValue) {
        float density = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * density + 0.5f);
    }
}

