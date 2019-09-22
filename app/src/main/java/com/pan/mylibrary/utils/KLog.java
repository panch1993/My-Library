package com.pan.mylibrary.utils;

import android.util.Log;
import com.pan.mylibrary.BuildConfig;

import java.util.Locale;

/**
 * Created by panchenhuan on 2018/6/11 14:45.
 * 自定义Log
 */
public class KLog {

    /**
     * 控制台打印日志
     */
    private static boolean LOG_DEBUG = BuildConfig.LOG_DEBUG;
    /**
     * 日志标签
     */
    private static final String TAG = "KLog-";

    private KLog() {
    }

    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(Locale.getDefault(), tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TAG + tag;
        return tag;
    }

    public static void v(String content) {
        if (!LOG_DEBUG || content == null) return;
        String tag = generateTag();
        Log.v(tag, content);
    }

    public static void d(String content) {
        if (!LOG_DEBUG || content == null) return;
        String tag = generateTag();
        Log.d(tag, content);
    }

    public static void i(String content) {
        if (!LOG_DEBUG || content == null) return;
        String tag = generateTag();
        Log.i(tag, content);
    }

    public static void w(String content) {
        if (!LOG_DEBUG || content == null) return;
        String tag = generateTag();
        Log.w(tag, content);
    }

    public static void w(String content, Throwable tr) {
        if (!LOG_DEBUG || content == null) return;
        String tag = generateTag();
        Log.w(tag, content, tr);
    }

    public static void w(Throwable tr) {
        if (!LOG_DEBUG) return;
        String tag = generateTag();
        Log.w(tag, tr);
    }

    public static void e(String content) {
        if (!LOG_DEBUG || content == null) return;
        String tag = generateTag();
        Log.e(tag, content);
    }

    public static void e(String content, Throwable tr) {
        if (!LOG_DEBUG || content == null) return;
        String tag = generateTag();

        Log.e(tag, content, tr);
    }

    public static void wtf(String content) {
        if (!LOG_DEBUG || content == null) return;
        String tag = generateTag();
        Log.wtf(tag, content);
    }
}

