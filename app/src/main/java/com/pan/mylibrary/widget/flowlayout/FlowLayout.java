package com.pan.mylibrary.widget.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by panchenhuan on 2/24/21
 * walkwindc8@foxmail.com
 * Description:
 */
public class FlowLayout extends ViewGroup {
    private final List<List<View>> allLines = new ArrayList<>();
    private final List<Integer> lineHeights = new ArrayList<>();

    private int space = 15;

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        allLines.clear();
        lineHeights.clear();

        int selfWidth = MeasureSpec.getSize(widthMeasureSpec);
        int selfHeight = MeasureSpec.getSize(heightMeasureSpec);

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //计算出需要的宽高总和
        int totalHeight = 0, totalWidth = 0;

        //每行的宽高
        int lineHeight = 0, lineWidth = 0;

        //每行的view
        List<View> line = new ArrayList<>();

        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                LayoutParams layoutParams = child.getLayoutParams();
                int childMeasureSpecW = getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), layoutParams.width);
                int childMeasureSpecH = getChildMeasureSpec(widthMeasureSpec, getPaddingTop() + getPaddingBottom(), layoutParams.height);
                child.measure(childMeasureSpecW, childMeasureSpecH);

                int measuredWidth = child.getMeasuredWidth();
                int measuredHeight = child.getMeasuredHeight();

                if (lineWidth + measuredWidth + space > selfWidth) {
                    allLines.add(line);
                    lineHeights.add(lineHeight);

                    totalWidth = Math.max(lineWidth + space, totalWidth);
                    totalHeight = totalHeight + lineHeight;

                    //换行
                    lineWidth = 0;
                    lineHeight = 0;
                    line = new ArrayList<>();
                }

                line.add(child);
                lineWidth = lineWidth + measuredWidth + space;
                lineHeight = Math.max(lineHeight, measuredHeight);
            }

            allLines.add(line);
            lineHeights.add(lineHeight);
            totalWidth = Math.max(lineWidth + space, totalWidth);
            totalHeight = totalHeight + lineHeight;
        }

        int realWidth = widthMode == MeasureSpec.EXACTLY ? selfWidth : totalWidth;
        int realHeight = heightMode == MeasureSpec.EXACTLY ? selfHeight : totalHeight;
        setMeasuredDimension(realWidth, realHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int curL = getPaddingLeft();
        int curT = getPaddingTop();

        int size = allLines.size();
        for (int i = 0; i < size; i++) {
            List<View> views = allLines.get(i);
            int lineHeight = lineHeights.get(i);
            for (int j = 0; j < views.size(); j++) {
                View view = views.get(j);
                int left = curL;
                int top = curT;
                int right = left + view.getMeasuredWidth();
                int bottom = top + view.getMeasuredHeight();
                view.layout(left, top, right, bottom);
                curL = right + space;//todo 间距
            }
            curL = getPaddingLeft();
            curT = curT + lineHeight;//todo 间距
        }

    }
}
