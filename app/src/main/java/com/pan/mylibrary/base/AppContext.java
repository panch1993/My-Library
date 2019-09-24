package com.pan.mylibrary.base;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

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
        Fresco.initialize(this);
    }
}
