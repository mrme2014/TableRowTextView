package com.qiaomu.tablerow.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by qiaomu on 2017/7/20.
 * <p>
 * 写在前面：
 * <p>
 * 1.设置数据唯一入口{@link PieChart#setDatas(List)}
 * <p>
 * 2.进入动画具体查看{@link PieAnimation},默认是{@link PieChart#mAnimation}
 * <p>
 * 3.扇形文字说明有三种位置具体查看{@link PiePosition},默认{@link PieEntry#mCharSequencePosition}
 * <p>
 * 4.设置扇形阶梯等比放大{@link PieChart#setUseLevel(boolean, boolean, float)}  }
 * <p>
 * 5.其他更多可配置属性查看{@link PieEntry}
 * <p>
 * 6.已知问题
 * --------目前只能从-90°开始绘制,也就是垂直方向，原因我还没看
 */

public class PieChart extends ChartView<PieEntry> {
    public static float startAngle = -90F;
    private int mAnimation = PieAnimation.ONE_AND_ONE;

    private boolean mUseLevel;
    private boolean mInner;
    private float mLevelStep;

    private float fraction = 0F;
    private boolean mMinMode;

    private float lastOutY;//上一次绘制文本的位置
    private float lastOutX;

    public PieChart(Context context) {
        super(context);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDatas(List<PieEntry> pieEntryList) {
        this.mDatas = pieEntryList;
        sortDatas();
        resetParams();
        calculateOffset();
        makeChartRegion();
        startValueAnimation();
    }

    //最大 最小 次大次小排序
    private void sortDatas() {
        if (mDatas == null || mDatas.size() == 0)
            return;
        if (mDatas.size() > 2) {
            Collections.sort(mDatas, new Comparator<PieEntry>() {
                @Override
                public int compare(PieEntry o1, PieEntry o2) {
                    return ((Double) o1.getPercent()).compareTo((Double) o2.getPercent());
                }
            });
            for (int i = 0; i < mDatas.size() / 2; i++) { //0 3 1  2
                int index = i + i + 1;
                if (index < mDatas.size())
                    mDatas.add(index, mDatas.remove(mDatas.size() - i - 1));
                else break;
            }
        }

        float start = startAngle;
        for (int i = 0; i < mDatas.size(); i++) {//  1   3   5   7   9   11  13   15
            PieEntry pieEntry = mDatas.get(i);
            pieEntry.setStartAngle(start);
            pieEntry.setSweepAngle(pieEntry.getPercent() * 360);
            start += pieEntry.getPercent() * 360;
        }

    }

    //画扇形之间的分割线 和指示器
    private void drawPieCharts(Canvas canvas) {
        //重新恢复矩形到初始状态
        // clearCanvas();
        float curStartAngle = startAngle;
        for (int i = 0; i < getChildCount(); i++) {//  1   3   5   7   9   11  13   15
            PieEntry pieEntry = mDatas.get(i);
            getGraphPaint().setStyle(Paint.Style.FILL);
            getGraphPaint().setColor(pieEntry.getChartColor());
            if (mAnimation == PieAnimation.ONE_BY_ONE) {
                double sweepPercent = pieEntry.getSweepAngle() * fraction;
                canvas.drawArc(mRectF, curStartAngle, (float) sweepPercent, true, getGraphPaint());
                curStartAngle += sweepPercent;
            } else if (mAnimation == PieAnimation.ONE_AND_ONE) {
                canvas.drawArc(mRectF, (float) pieEntry.getStartAngle(), (float) pieEntry.getSweepAngle() * fraction, true, getGraphPaint());
            } else {
                if (mUseLevel) {
                    if (mInner && i > 0) {
                        innerRect(mLevelStep);
                    } else if (!mInner) {
                        if (i == 0)
                            innerRect(mLevelStep);
                        else if (i == 1)
                            outRect(mLevelStep);
                    }
                }

                canvas.drawArc(mRectF, (float) pieEntry.getStartAngle(), (float) pieEntry.getSweepAngle(), true, getGraphPaint());
            }

        }
        drawDecorations(canvas);
    }

    //绘制图形修饰属性

    private void drawDecorations(Canvas canvas) {
        //重新恢复矩形到初始状态
        if (mUseLevel)
            outRect((getChildCount() - 1) * 10);

        float totalSweepAngle4Divider = 0F;
        float totalSweepAngle4CharSequence = 0F;
        for (int i = 0; i < getChildCount(); i++) {
            //如果需要等绘制那么 矩形框的外边界要扩大
            if (mUseLevel) {
                if (mInner && i > 0) {
                    innerRect(mLevelStep);
                } else if (!mInner) {
                    if (i == 0)
                        innerRect(mLevelStep);
                    else if (i == 1)
                        outRect(mLevelStep);
                }
            }
            totalSweepAngle4Divider = drawDivider(canvas, totalSweepAngle4Divider, mDatas.get(i));
            totalSweepAngle4CharSequence = drawCharSequence(canvas, totalSweepAngle4CharSequence, mDatas.get(i));

        }
    }

    //绘制分割线
    private float drawDivider(Canvas canvas, float sweepAngle, PieEntry pieEntry) {
        if (mAnimation == PieAnimation.ONE_BY_ONE && fraction < 1.0f)
            return 0;
        //如果就一个饼,或者分割线看度小于0 或者不显示说明文字 就不继续绘制分割线  || pieEntry.getCharSequencePosition() == PiePosition.INSIDE
        if (pieEntry.getSweepAngle() >= 360 || pieEntry.getSweepAngle() <= 0 || pieEntry.getDividerWidth() <= 0 || !pieEntry.isDisplayCharSequence()) {
            sweepAngle += pieEntry.getSweepAngle();
            return sweepAngle;
        }
        if (pieEntry.getDividerColor() == -1) {
            getDividerPaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            //getDividerPaint().setColor(getBackgroundColor());
        } else {
            getDividerPaint().setColor(pieEntry.getDividerColor());
        }
        getDividerPaint().setStyle(Paint.Style.STROKE);
        getDividerPaint().setStrokeWidth(pieEntry.getDividerWidth());

        canvas.save();
        canvas.translate(mRectF.centerX(), mRectF.centerY());

        Path path = new Path();
        float moveToX = (float) Math.sin(Math.PI * sweepAngle / 180.0) * pieEntry.getDividerWidth();
        float moveToY = -(float) Math.cos(Math.PI * sweepAngle / 180.0) * pieEntry.getDividerWidth();
        path.moveTo(moveToX, moveToY);
        path.lineTo(0, 0);

        sweepAngle += pieEntry.getSweepAngle();
        float startX = (float) Math.sin(Math.PI * sweepAngle / 180.0) * pieEntry.getDividerWidth();
        float startY = -(float) Math.cos(Math.PI * sweepAngle / 180.0) * pieEntry.getDividerWidth();
        path.lineTo(startX, startY);

        float endX = (float) Math.sin(Math.PI * sweepAngle / 180.0) * (mRectF.width() / 2 + 2);
        float endY = -(float) Math.cos(Math.PI * sweepAngle / 180.0) * (mRectF.width() / 2 + 2);
        path.lineTo(endX, endY);

        canvas.drawPath(path, getDividerPaint());
        canvas.restore();
        return sweepAngle;
    }

    //绘制图形化说明指示器 文字
    private float drawCharSequence(Canvas canvas, float middleSweepAngle, PieEntry pieEntry) {
        if (fraction < 1.0f)
            return 0;

        if (!pieEntry.isDisplayCharSequence() || pieEntry.getSweepAngle() <= 0) {
            middleSweepAngle += pieEntry.getSweepAngle();
            return middleSweepAngle;
        }
        //绘制线条 指示器
        canvas.save();
        canvas.translate(mRectF.centerX(), mRectF.centerY());

        CharSequence source = pieEntry.getCharSequence();
        RectF rectF = getCharSequenceRect(source);
        float textWidth = rectF.width();
        float textHeight = rectF.height();
        float baseLine = textHeight / 2;

        middleSweepAngle += pieEntry.getSweepAngle() / 2;//扇形扫过角度的一半
        boolean thanHalfX = middleSweepAngle > 180;
        boolean thanHalfY = middleSweepAngle >= 90 && middleSweepAngle <= 270;
        //指示器开始点x
        float startX = (float) Math.sin(Math.PI * middleSweepAngle / 180.0) * (mRectF.width() / 2 - pieEntry.getIndicatorLength());
        //指示器开始点y
        float startY = -(float) (Math.cos(Math.PI * middleSweepAngle / 180.0) * (mRectF.width() / 2 - pieEntry.getIndicatorLength()));
        //指示器中间点x
        float middleX = (float) (Math.sin(Math.PI * middleSweepAngle / 180.0) * (mRectF.width() / 2 + pieEntry.getIndicatorLength()));
        //指示器中间点y
        float middleY = -(float) (Math.cos(Math.PI * middleSweepAngle / 180.0) * (mRectF.width() / 2 + pieEntry.getIndicatorLength()));


        //为了避免指示器水平方向上过长
        float overLength = Math.abs(Math.abs(middleX) - mRectF.width() / 2 - pieEntry.getIndicatorLength());
        float endLength = 0;
        if (mMinMode) {
            endLength = thanHalfX ? (-Math.abs(pieEntry.getIndicatorLength() - overLength)) : (Math.abs(pieEntry.getIndicatorLength() - overLength));
        } else {
            endLength = (middleSweepAngle == 90 || middleSweepAngle == 270) ? 0 : (thanHalfX ? -pieEntry.getIndicatorLength() : pieEntry.getIndicatorLength());
        }

        if (pieEntry.getCharSequencePosition() != PiePosition.INSIDE && pieEntry.getIndicatorWidth() >= 1) {

            Path path = new Path();
            getNotesPaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            getNotesPaint().setColor(pieEntry.getIndicatorColor());
            getNotesPaint().setStrokeJoin(Paint.Join.ROUND);
            getNotesPaint().setStrokeCap(Paint.Cap.ROUND);
            getNotesPaint().setStyle(Paint.Style.STROKE);
            getNotesPaint().setStrokeWidth(pieEntry.getIndicatorWidth());
            path.moveTo(startX, startY);
            path.lineTo(middleX, middleY);

            path.lineTo(middleX + endLength, middleY);
            getNotesPaint().setDither(true);
            getNotesPaint().setAntiAlias(true);
            canvas.drawPath(path, getNotesPaint());
        }

        //绘制文字考虑位置
        getNotesPaint().reset();
        getNotesPaint().setTextSize(pieEntry.getPieTextSize());
        getNotesPaint().setColor(pieEntry.getCharSequenceColor());

        if (!(source instanceof Spanned))
            mLayout = new StaticLayout(source, getNotesPaint(), 10000, Layout.Alignment.ALIGN_NORMAL, pieEntry.getSpacingmult(), pieEntry.getSpacingadd(), false);

        if (pieEntry.getCharSequencePosition() == PiePosition.OUT_SIDE) {
            //大于180°,字体为止往左移,小于180°字体为止就是结束点x
            float outx = middleX + endLength + (thanHalfX ? -textWidth - pieEntry.getIndicatorCharPad() : pieEntry.getIndicatorCharPad());
            float outy = middleY - baseLine;

            //校正，防止绘制出边界
            if (thanHalfX)
                outx = Math.max(-getWidth() / 2, outx);
            else
                outx = Math.min(getWidth() / 2 - textWidth - pieEntry.getIndicatorCharPad(), outx);
            canvas.translate(outx, outy);
            lastOutY = outy;
            lastOutX = outx;
        } else if (pieEntry.getCharSequencePosition() == PiePosition.INSIDE) {
            float insideX = (float) Math.sin(Math.PI * middleSweepAngle / 180.0) * mRectF.width() / 4;
            float insideY = -(float) Math.cos(Math.PI * middleSweepAngle / 180.0) * mRectF.width() / 4;
            canvas.translate(insideX - textWidth / 4, insideY - baseLine);
        } else if (pieEntry.getCharSequencePosition() == PiePosition.OUT_TOP) {
            float dstX = middleX;
            if (middleSweepAngle == 90 || middleSweepAngle == 270) {
                if (textWidth >= pieEntry.getIndicatorLength()) {
                    if (thanHalfX) {
                        dstX += -textWidth + pieEntry.getIndicatorLength() - pieEntry.getIndicatorCharPad();
                    } else {
                        dstX += -pieEntry.getIndicatorLength() + pieEntry.getIndicatorCharPad();
                    }
                } else {
                    if (thanHalfX) {
                        dstX += pieEntry.getIndicatorLength() / 2 - textWidth / 2;
                    } else {
                        dstX += -pieEntry.getIndicatorLength() / 2 - textWidth / 2;
                    }
                }
            } else {
                if (textWidth >= pieEntry.getIndicatorLength()) {
                    if (thanHalfX) {
                        dstX += -textWidth;
                    } else {
                        dstX = middleX;
                    }
                } else {
                    if (thanHalfX) {
                        dstX += -pieEntry.getIndicatorLength() / 2 - textWidth / 2;
                    } else {
                        dstX += pieEntry.getIndicatorLength() / 2 - textWidth / 2;
                    }
                }
            }
            float dstY = middleY - 1 * (textHeight + pieEntry.getIndicatorCharPad());
            canvas.translate(dstX, dstY);
        }
        mLayout.draw(canvas);
        //累加扇形中间角度
        middleSweepAngle += pieEntry.getSweepAngle() / 2;
        canvas.restore();
        return middleSweepAngle;
    }


    @Override
    public void calculateOffset() {
        int size = getChildCount();
        for (int i = 0; i < size; i++) {

            PieEntry pieEntry = mDatas.get(i);
            //扇形中间角度
            middleSweepAngle += pieEntry.getSweepAngle() / 2;
            if (!pieEntry.isDisplayCharSequence())
                continue;
            CharSequence source = pieEntry.getCharSequence();
            getNotesPaint().setTextSize(pieEntry.getPieTextSize());
            RectF rectF = getCharSequenceRect(source);
            float textWidth = rectF.width();
            float textHeight = rectF.height();

            if (pieEntry.getCharSequencePosition() == PiePosition.OUT_SIDE) {
                calculateMaxOffset(textWidth, textHeight, i, 1, pieEntry);
            } else if (pieEntry.getCharSequencePosition() == PiePosition.OUT_TOP) {
                //指示器长度与文本长度之差
                float diffTxtLength = Math.abs(pieEntry.getIndicatorLength() - textWidth);
                calculateMaxOffset(diffTxtLength, textHeight, i, 2, pieEntry);
            }
            middleSweepAngle += pieEntry.getSweepAngle() / 2;

        }
    }

    float indicatorCharPad = 0F;
    float indicatorLength = 0F;
    float middleSweepAngle = 0F;
    float maxTxtLength = 0F;

    private void calculateMaxOffset(float textLength, float textHeight, int index, int position, PieEntry pieEntry) {
        //取指示器长度与文本长度之差得最大值
        maxTxtLength = Math.max(maxTxtLength, textLength);
        //取指示器与字体之间的最大值
        indicatorCharPad = Math.max(indicatorCharPad, pieEntry.getIndicatorCharPad());
        //取只是其长度最大值
        indicatorLength = Math.max(pieEntry.getIndicatorLength(), indicatorLength);
        //原始圆形半径
        float originalRadius = Math.min(getWidth() / 2, getHeight() / 2);
        //扇形指示器方向半径
        float angleRadius = originalRadius + indicatorLength;
        //每个扇形指示器方向的半径投影到水平方向的长度
        double angleRadiusLengthHori = Math.abs(Math.sin(Math.PI * middleSweepAngle / 180.0) * angleRadius);
        //每个扇形指示器方向的半径投影到垂直方向的长度
        double angleRadiusLengthVertical = Math.abs(Math.cos(Math.PI * middleSweepAngle / 180.0) * angleRadius);

        if (position == PiePosition.OUT_SIDE) {
            angleRadiusLengthVertical += (mUseLevel ? (index + 1) * mLevelStep : 0) + textHeight / 2;
            double overLength = angleRadiusLengthHori - originalRadius;
            angleRadiusLengthHori += textLength + pieEntry.getIndicatorCharPad() + (mMinMode ? pieEntry.getIndicatorLength() - overLength : pieEntry.getIndicatorLength());

        } else if (position == PiePosition.OUT_TOP) {
            angleRadiusLengthVertical += (mUseLevel ? (index + 1) * mLevelStep + pieEntry.getIndicatorCharPad() : 0) + pieEntry.getIndicatorWidth() + textHeight;
            angleRadiusLengthHori += textLength + pieEntry.getIndicatorCharPad() + pieEntry.getIndicatorLength();
        }

        //水平方向上 需要流出的额外空间
        if (angleRadiusLengthHori > originalRadius) {
            mExtraLeftPad = (float) Math.max(angleRadiusLengthHori - originalRadius, mExtraLeftPad);
        }
        //顶部留出的额外空间=大于半径部分+字体高度+指示器宽度+字体与指示器间隔
        if (angleRadiusLengthVertical >= originalRadius) {
            if (middleSweepAngle < 90 || middleSweepAngle > 270) {
                double topPad = angleRadiusLengthVertical - originalRadius;
                mExtraTopPad = (float) Math.max(topPad, mExtraTopPad);
            } else if (middleSweepAngle > 90 || middleSweepAngle < 270) {
                double botPad = angleRadiusLengthVertical - originalRadius;
                mExtraBottomPad = (float) Math.max(botPad, mExtraBottomPad);
            }
        }
        Log.e(TAG, "calculateMaxOffset: " + middleSweepAngle + "--" + angleRadiusLengthVertical + "--" + mExtraTopPad);
    }

    @Override
    public void makeChartRegion() {
        //通过先求得垂直方向半径
        float radius = (getHeight() - getRectTop() - getExtraPaddingBottom() - getPaddingBottom()) / 2;
        //求得水平方向左便宜量
        float left = getWidth() / 2 - radius;
        if (getWidth() / 2 - radius < getRectLeft()) {
            left = getRectLeft();
            float realRadius = getWidth() / 2 - left;
            mExtraTopPad += radius - realRadius;
            mExtraBottomPad += radius - realRadius;
            radius = realRadius;
        }

        mRectF = new RectF(left, getRectTop(), left + 2 * radius, getHeight() - getExtraPaddingBottom() - getPaddingBottom());
        // Log.e(TAG, "makeChartRegion: " + mExtraLeftPad + "--" + mExtraBottomPad + "--" + mExtraBottomPad);
    }

    @Override
    public void render(Canvas canvas) {
        drawPieCharts(canvas);
    }

    public void setPieChartAnimation(@PieAnimation int animation) {
        mAnimation = animation;
    }

    //使用分级
    public void setUseLevel(boolean useLevel, boolean inner, float levelStep) {
        mUseLevel = useLevel;
        mInner = inner;
        mLevelStep = dp2px(levelStep);
        if (mUseLevel)
            setPieChartAnimation(PieAnimation.NONE);
    }

    //宽高比较小的控件上使用适配更好
    public void setMinMode(boolean midMode) {
        mMinMode = midMode;
    }

    private void startValueAnimation() {
        if (mAnimation == PieAnimation.NONE) {
            fraction = 1f;
            invalidate();
            return;
        }
        startValueAnimation(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fraction = animation.getAnimatedFraction();
                invalidate();
            }
        });
    }

    private void resetParams() {
        fraction = 0f;
        indicatorCharPad = 0F;
        indicatorLength = 0F;
        middleSweepAngle = 0F;
        maxTxtLength = 0F;
    }
}
