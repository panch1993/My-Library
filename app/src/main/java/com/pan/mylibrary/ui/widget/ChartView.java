package com.pan.mylibrary.ui.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.pan.mylibrary.R;
import com.pan.mylibrary.utils.ResourceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by panchenhuan on 2018/12/3 11:36 AM
 * Description:学习图表
 */
public class ChartView extends View {
    private int mAxisColor = ResourceUtil.getColor(R.color.gray_88);
    private int mBarColor = ResourceUtil.getColor(R.color.blue_default);
    private int mLineChartColor = ResourceUtil.getColor(R.color.red_default);
    private int mTextColor = ResourceUtil.getColor(R.color.gray_44);
    private int mTextSize = ResourceUtil.getDimens(R.dimen.text_32px);

    public static final int DEFAULT_MAX_BAR_VALUE = 5;
    public static final int DEFAULT_MAX_LINE_VALUE = 10;

    private int mMaxBarValue, mMaxLineValue;

    public static final int X_AXIS_NUM = 10;
    private int mBarWidth = ResourceUtil.getDimens(R.dimen.sw_px_20);
    private int mXaxisTextWidth = ResourceUtil.getDimens(R.dimen.sw_px_60);

    private Rect mRect, mRectDraw;
    //线图/柱状图/轴
    private Paint mPaintLine, mPaintBar, mPaintAxis;
    //文字画笔
    private TextPaint mTextPaint;

    //    private List<Float> mBarValue;
//    private List<Integer> mLineValue;
//    private List<String> mStrings;
    private List<IData> mDataList;

    private int mItemWidth;
    private float mAnimatedFraction;
    private ValueAnimator mValueAnimator;
    private CornerPathEffect mCornerPathEffect;
    private LinearGradient mLinearGradient;

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaintAxis = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintAxis.setStyle(Paint.Style.STROKE);

        mPaintBar = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBar.setStyle(Paint.Style.FILL);
        mPaintBar.setTextAlign(Paint.Align.CENTER);
        mPaintBar.setTextSize(mTextSize);
        mPaintBar.setColor(mBarColor);

        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setColor(mLineChartColor);
        mPaintLine.setTextSize(mTextSize);
        mCornerPathEffect = new CornerPathEffect(50);
        mPaintLine.setPathEffect(mCornerPathEffect);


        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);


        mRect = new Rect();
        mRectDraw = new Rect();

        mValueAnimator = ObjectAnimator.ofFloat(0, 1);
        mValueAnimator.setDuration(1500);
        mValueAnimator.addUpdateListener(animation -> {
            mAnimatedFraction = animation.getAnimatedFraction();
            invalidate();
        });
        mDataList = new ArrayList<>();
    }

    public void setData(List<IData> data) {
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRect.left = getPaddingStart();
        mRect.right = w - getPaddingEnd();
        mRect.top = getPaddingTop();
        mRect.bottom = h - getPaddingBottom();

        mRectDraw.left = mRect.left + mXaxisTextWidth;
        mRectDraw.right = mRect.right - mXaxisTextWidth;
        mRectDraw.bottom = mRect.bottom - mTextSize;
        mRectDraw.top = mRect.top + mTextSize + 10;


        mItemWidth = mRectDraw.width() / 7;


        mLinearGradient = new LinearGradient(0, mRectDraw.bottom, 0, mRectDraw.top, Color.TRANSPARENT, mLineChartColor, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawAxis(canvas);

        if (!mDataList.isEmpty()) {
            int max = DEFAULT_MAX_LINE_VALUE;
            for (IData iData : mDataList) {
                max = Math.max(max, iData.getDataValue());
            }
            double ceil = Math.ceil(max * 1.0f / DEFAULT_MAX_LINE_VALUE);
            mMaxLineValue = (int) (ceil * DEFAULT_MAX_LINE_VALUE);
            drawLineValue(canvas);
        }
        /*if (mBarValue != null && !mBarValue.isEmpty()) {
            float max = DEFAULT_MAX_BAR_VALUE;
            for (Float value : mBarValue) {
                max = Math.max(max, value);
            }
            double ceil = Math.ceil(max * 1.0f / DEFAULT_MAX_BAR_VALUE);
            mMaxBarValue = (int) (ceil * DEFAULT_MAX_BAR_VALUE);
            drawBarValue(canvas);
        }*/

    }
/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                ToastUtil.showToast(event.getX() + "/" + event.getY());
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        super.onTouchEvent(event);
        return true;
    }*/

    private void drawAxis(Canvas canvas) {
        int startX = mRectDraw.left;
        int endX = mRectDraw.right;
        int bottomY = mRectDraw.bottom;
        int topY = mRectDraw.top;
        mPaintAxis.setColor(mAxisColor);
        //底部x轴
        canvas.drawLine(startX, bottomY, endX, bottomY, mPaintAxis);
        //两边y轴
        canvas.drawLine(startX, bottomY, startX, mRect.top, mPaintAxis);
//        canvas.drawLine(endX, bottomY, endX, mRect.top, mPaintAxis);
        //x轴若干,竖轴坐标
        int space = (bottomY - topY) / X_AXIS_NUM;
//        int barSpace = mMaxBarValue / X_AXIS_NUM;
        int lineSpace = mMaxLineValue / X_AXIS_NUM;
        mPaintAxis.setColor(Color.parseColor("#eaeaea"));
        mTextPaint.setColor(mTextColor);
        for (int i = 1; i <= X_AXIS_NUM; i++) {
            int startY = bottomY - i * space;
            canvas.drawLine(startX, startY, endX, startY, mPaintAxis);
            mTextPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(lineSpace * i + "", startX - 5, startY, mTextPaint);
//            mTextPaint.setTextAlign(Paint.Align.LEFT);
//            canvas.drawText(lineSpace * i + "", endX + 5, startY, mTextPaint);
        }
    }

   /* private void drawBarValue(Canvas canvas) {
        int maxHeight = mRectDraw.bottom - mRectDraw.top;
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(mTextColor);
        for (int i = 0; i < 7; i++) {
            Float value = mBarValue.get(i);
            int left = mRectDraw.left + mItemWidth / 2 - mBarWidth / 2 + i * mItemWidth;
            int top = (int) (mRectDraw.bottom - mAnimatedFraction * maxHeight * value / mMaxBarValue);
            int right = left + mBarWidth;
            int bottom = mRectDraw.bottom;
            canvas.drawRect(left, top, right, bottom, mPaintBar);
            //绘制蓝字
            canvas.drawText(value.toString(), left + mBarWidth / 2, top - 5, mPaintBar);
            //绘制X轴
            canvas.drawText(mStrings.get(i), left + mBarWidth / 2, mRectDraw.bottom + mTextSize, mTextPaint);
        }
    }*/

    private void drawLineValue(Canvas canvas) {
        int maxHeight = mRectDraw.bottom - mRectDraw.top;
        Path path = new Path();
        path.moveTo(mRectDraw.left, mRectDraw.bottom);
        for (int i = 0; i < mDataList.size(); i++) {
            mTextPaint.setColor(mLineChartColor);
            IData iData = mDataList.get(i);
            int value = iData.getDataValue();
            int x = mRectDraw.left + mItemWidth / 2 - mBarWidth / 2 + i * mItemWidth+ mBarWidth / 2;
            float y = mRectDraw.bottom - mAnimatedFraction * maxHeight * value / mMaxLineValue;
            if (value > 0) {

            canvas.drawText(Integer.toString(value), x/* + mBarWidth + 5*/, y, mTextPaint);
            }
            path.lineTo(x, y);

            //绘制X轴
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setColor(mTextColor);
            canvas.drawText(iData.getDataLabel(), x , mRectDraw.bottom + mTextSize, mTextPaint);
        }
        path.lineTo(mRectDraw.right, mRectDraw.bottom);
        //绘制实心区域
        mPaintLine.setStyle(Paint.Style.FILL);
        mPaintLine.setShader(mLinearGradient);
        canvas.drawPath(path, mPaintLine);
        //描边
        mPaintLine.setShader(null);
        mPaintLine.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, mPaintLine);
    }

    public interface IData {
        int getDataValue();

        String getDataLabel();
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            invalidate();
//        }
//        return super.onTouchEvent(event);
//    }
}
