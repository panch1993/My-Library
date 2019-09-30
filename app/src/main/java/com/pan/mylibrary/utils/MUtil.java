package com.pan.mylibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.FileProvider;

import com.pan.mylibrary.base.AppContext;

import java.io.File;

/**
 * Created by panchenhuan on 2018/6/11 13:46.
 * 常用方法
 */
public class MUtil {


    public static int getWindowWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
            return outMetrics.widthPixels;
        }
        return 0;
    }

    public static int getWindowHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
            return outMetrics.heightPixels;
        }
        return 0;
    }

    /**
     * dp转换成px单位
     */
    public static int dp2px( float dp) {
        float density = AppContext.getInstance().getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px( float sp) {
        float scaledDensity = AppContext.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scaledDensity + 0.5f);
    }

    /**
     * 强制隐藏输入法键盘
     */
    public static void hideInputMethod(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) return;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static void showInputMethod(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) return;
        inputMethodManager.showSoftInput(view, 0);
    }


    /**
     * 获取版本号
     *
     * @return version code 15
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();//获取包管理器
        try {//获取包信息
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;//返回版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取版本名称
     *
     * @return 版本名称 2.0.0
     */
    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();//获取包管理器
        try {  //获取包信息
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 是否需要升级当前APP
     */
    public static boolean isNeedUpdateApp(Context ctx) {
        String currVersionName = getVersionName(ctx);
        //debug 用的,release 不会有 -
        if (currVersionName.contains("debug") || currVersionName.contains("-")) return false;

        String onlineVersionName = /*MGlobal.get().getNewVersion()*/"";
        if (TextUtils.isEmpty(onlineVersionName)) return false;
        int currVerNum, onlineVerNum;
        try {
            currVerNum = Integer.parseInt(currVersionName.replace(".", ""));
            onlineVerNum = Integer.parseInt(onlineVersionName.replace(".", ""));
        } catch (Exception e) {
            e.printStackTrace();
            return !currVersionName.equals(onlineVersionName);
        }
        return onlineVerNum > currVerNum;
    }

    public static String getVersionNum(String version) {
        if (TextUtils.isEmpty(version) || !version.contains("_")) {
            return "";
        }
        String[] split = version.split("_");
        return split[split.length - 1];
    }


    /**
     * 安装新版APK
     *
     * @param ctx  上下文
     * @param file 安装文件
     */
    public static void installApk(Context ctx, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(ctx, ctx.getPackageName() + ".provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        ctx.startActivity(intent);
    }
}
