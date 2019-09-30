package com.pan.mylibrary.base;

import com.pan.mylibrary.utils.SpUtil;

/**
 * Create by panchenhuan on 2019-09-30
 * walkwindc8@foxmail.com
 * Description:
 */
public class Config {

    //field
    public static final String ANIM_DURATION = "ANIM_DURATION";

    //value
    public static long DEFAULT_ANIM_DURATION = 0;


    public static void reload() {
        DEFAULT_ANIM_DURATION = SpUtil.get(ANIM_DURATION,300L);
    }
}
