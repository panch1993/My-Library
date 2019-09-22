package com.pan.mylibrary.utils;

import android.content.res.Resources;
import androidx.core.content.ContextCompat;
import com.pan.mylibrary.base.AppContext;


/**
 * Created by panchenhuan on 16/12/14..
 */

public class ResourceUtil {
    //得到资源管理的类
    public static Resources getResources() {
        return AppContext.getInstance().getResources();
    }

    //在屏幕适配时候使用
    public static int getDimens(int resId) {
        return getResources().getDimensionPixelSize(resId);
    }
    //得到颜色
    public static int getColor(int resId) {
        return ContextCompat.getColor(AppContext.getInstance(), resId);
    }

    //得到文字
    public static String getString(int strId) {
        return getResources().getString(strId);
    }

    public static String[] getStringArray(int arrayId) {
        return getResources().getStringArray(arrayId);
    }
}
