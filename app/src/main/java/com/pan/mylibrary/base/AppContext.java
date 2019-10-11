package com.pan.mylibrary.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.pan.mylibrary.utils.SpUtil;

/**
 * @author Pan
 * @date 2019/9/21.
 * @time 13:45.
 */
public class AppContext extends Application {
    private static AppContext sContext = null;

    public static AppContext getInstance() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
//        Fresco.initialize(this);
        //信息摘要
        SharedPreferences sp = getSharedPreferences("pans_lib.pref", Context.MODE_PRIVATE);
        SpUtil.get().init(sp);

        Config.reload();

    }
}
