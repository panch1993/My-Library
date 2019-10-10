package com.pan.mylibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Create by panchenhuan on 2019-10-10
 * walkwindc8@foxmail.com
 * Description:
 */
public class TimeView extends View {
    public enum Type {
        YEAR, MONTH, DAY, HOUR, MIN, SEC
    }

    private Type currentType;

    public TimeView(Context context) {
        this(context, null);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        currentType = Type.YEAR;
    }


    public Type getCurrentType() {
        return currentType;
    }

    public void setCurrentType(Type currentType) {
        if (this.currentType == currentType) return;
        this.currentType = currentType;
        invalidate();
    }
}
