package com.pan.mylibrary.widget.chart;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.pan.mylibrary.R;
import com.pan.mylibrary.base.Config;
import com.pan.mylibrary.utils.MUtil;
import com.pan.mylibrary.utils.ResourceUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 雷达图
 * todo 需要修改数据曲 计算百分比方法
 */
public class RadarChartView extends View {
    //背景线条数
    private static final int mRadarLineCount = 5;
    //文字距离便宜
    private int mTextOffSet = ResourceUtil.getDimens(R.dimen.sw_px_15);
    private int mTextSize = MUtil.sp2px(12);
    //数据实心区域颜色
    private int mValuePathColor = ResourceUtil.getColor(R.color.colorPrimary) & 0xb4ffffff;
    //小圆半径
    private int mCirRadius = ResourceUtil.getDimens(R.dimen.sw_px_15);
    //背景线颜色
    private int mLineColor = Color.BLACK;
    //绘制圆形背景
    public boolean mDrawCirBg = true;
    //绘制多边形背景
    public boolean mDrawPolygonBg = true;

    private Paint mPaint = null;
    private TextPaint mTextPaint = null;

    //做正方形 宽高相同
    private int mHeight;
    //中点
    private Point mCenterPoint;
    //夹脚
    private float mAngle = 0;

    private List<IData> mDataList;

    private ValueAnimator mValueAnimator;

    private float mAnimatedFraction;

    public RadarChartView(Context context) {
        this(context, null);
    }

    public RadarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mDataList = new ArrayList<>();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mLineColor);


        mValueAnimator = ObjectAnimator.ofFloat(0, 1);
        mValueAnimator.setDuration(Config.DEFAULT_ANIM_DURATION);
        mValueAnimator.addUpdateListener(animation -> {
            mAnimatedFraction = animation.getAnimatedFraction();
            invalidate();
        });
    }

    public float getBaseLine(TextPaint textPaint, float baseLine) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float fontHeight = fm.bottom - fm.top;
        return baseLine - (textPaint.getTextSize() - fontHeight) / 2 - fm.bottom;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = Math.min(w, h);
        mCenterPoint = new Point(w / 2, h / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //最小圆圈半径
        float radius;
        radius = (mHeight - 2 * mTextPaint.getTextSize() - 6 * mTextOffSet) * 0.5f / (mRadarLineCount + 0.5f);
        //夹角
        mAngle = (float) (2 * Math.PI / mDataList.size());


        mPaint.setColor(mLineColor);
        mPaint.setStyle(Paint.Style.STROKE);
        if (mDrawCirBg) {
            drawCircleRadar(canvas, radius);
        }
        if (mDrawPolygonBg) {
            drawBgRadarLine(canvas, radius);
        }
        drawRadarLine(canvas, radius * 4.8f);

        drawText(canvas, radius * 5.1f);


        mPaint.setStyle(Paint.Style.FILL);
        drawPoints(canvas, radius * 5.1f - mTextOffSet);

        drawValuePart(canvas, radius * 4);


    }

    /**
     * 圆形背景线
     *
     * @param unitRadius 简单理解为最小圆圈半径
     */
    private void drawCircleRadar(Canvas canvas, float unitRadius) {
        for (int i = 1; i < mRadarLineCount; i++) {
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, unitRadius * i, mPaint);
        }
    }

    /**
     * 绘制多边形背景线
     */
    private void drawBgRadarLine(Canvas canvas, float unitRadius) {
        Path path = new Path();
        float lastStartX = 0, lastStartY = 0;
        int size = mDataList.size();
        for (int i = 0; i < mRadarLineCount; i++) {
            path.reset();
            for (int j = 0; j < size; j++) {
                float x = (float) (mCenterPoint.x + unitRadius * i * Math.sin(mAngle * j));
                float y = (float) (mCenterPoint.y - unitRadius * i * Math.cos(mAngle * j));
                if (j == 0) {
                    //第一个坐标
                    path.moveTo(x, y);
                    lastStartX = x;
                    lastStartY = y;
                } else if (j == size - 1) {
                    //最后一个坐标,连接起点 闭环
                    path.lineTo(x, y);
                    path.lineTo(lastStartX, lastStartY);
                } else {
                    path.lineTo(x, y);
                }
            }
            path.close();
            mPaint.setPathEffect(new CornerPathEffect(10 + 10 * i));
            canvas.drawPath(path, mPaint);
        }
    }

    /**
     * 直角三角形:
     * sin(x)是对边比斜边
     * cos(x)是底边比斜边
     * tan(x)是对边比底边
     * 推导出:底边(x坐标)=斜边(半径)*cos(夹角角度),对边(y坐标)=斜边(半径)*sin(夹角角度)
     * 中心点 射线
     */
    private void drawRadarLine(Canvas canvas, float radius) {
        Path path = new Path();
        for (int i = 0; i < mDataList.size(); i++) {
            path.reset();
            path.moveTo(mCenterPoint.x, mCenterPoint.y);
            float x = (float) (mCenterPoint.x + radius * Math.sin(mAngle * i));
            float y = (float) (mCenterPoint.y - radius * Math.cos(mAngle * i));
            path.lineTo(x, y);

            canvas.drawPath(path, mPaint);
        }
    }

    /**
     * 绘制实心数据区域
     */
    private void drawValuePart(Canvas canvas, float radius) {
        mPaint.setColor(mValuePathColor);
        Path path = new Path();
        for (int i = 0; i < mDataList.size(); i++) {
            float value = mDataList.get(i).getDataValue() * 1f / 100 * mAnimatedFraction;
            float perRadius = value * radius;
            float x = (float) (mCenterPoint.x + perRadius * Math.sin(mAngle * i));
            float y = (float) (mCenterPoint.y - perRadius * Math.cos(mAngle * i));
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.close();
        canvas.drawPath(path, mPaint);
    }

    /**
     * 文字
     */
    private void drawText(Canvas canvas, float radius) {
        for (int i = 0; i < mDataList.size(); i++) {
            IData iData = mDataList.get(i);
            float specialYOffset = 0, specialXOffset = 0;
            String text = iData.getDataLabel();
            float x = (float) (mCenterPoint.x + radius * Math.sin(mAngle * i));
            float y = (float) (mCenterPoint.y - radius * Math.cos(mAngle * i));
            if (i == 0) {
                mTextPaint.setTextAlign(Paint.Align.CENTER);
                specialYOffset = -mTextOffSet;
            } else {
                float temp = i * 1.0f / mDataList.size();
                if (temp < 0.5f) {
                    mTextPaint.setTextAlign(Paint.Align.LEFT);
                    specialXOffset = mTextOffSet;
                } else if (temp == 0.5f) {
                    specialYOffset = mTextPaint.getTextSize();
                    mTextPaint.setTextAlign(Paint.Align.CENTER);
                } else {
                    mTextPaint.setTextAlign(Paint.Align.RIGHT);
                    specialXOffset = -mTextOffSet;
                }
                if (y > mCenterPoint.y) {
                    specialYOffset += mTextOffSet;
                } /*else if (y < mCenterPoint.y) {
                    specialYOffset-=mTextOffSet;
                }*/
            }
            canvas.drawText(text, x + specialXOffset, getBaseLine(mTextPaint, y + specialYOffset), mTextPaint);
        }
    }

    /**
     * 小圆球
     */
    private void drawPoints(Canvas canvas, float radius) {
        mPaint.setColor(Color.RED);
        for (int i = 0; i < mDataList.size(); i++) {
            float x = (float) (mCenterPoint.x + radius * Math.sin(mAngle * i));
            float y = (float) (mCenterPoint.y - radius * Math.cos(mAngle * i));
            canvas.drawCircle(x, y, mCirRadius, mPaint);
        }
    }

    public void setNewData(List<IData> data) {
        if (mDataList.equals(data)) {
            return;
        }
        mDataList.clear();
        mDataList.addAll(data);
        mValueAnimator.start();
    }

    public void playAnim() {
        mValueAnimator.start();
    }

    public void setDrawCirBg(boolean mDrawCirBg) {
        this.mDrawCirBg = mDrawCirBg;
        invalidate();
    }

    public void setDrawPolygonBg(boolean mDrawPolygonBg) {
        this.mDrawPolygonBg = mDrawPolygonBg;
        invalidate();
    }

    public boolean isDrawCirBg() {
        return mDrawCirBg;
    }

    public boolean isDrawPolygonBg() {
        return mDrawPolygonBg;
    }
}
