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
    public static final String DEFAULT_BIRTH_DAY = "1993.06.24 00:00:00";
    public static final String BACKGROUND_DRAWER_URL = "http://img3.imgtn.bdimg.com/it/u=2521264465,2666829798&fm=26&gp=0.jpg";
    public static final String BACKGROUND_LIFE_URL = "http://img1.imgtn.bdimg.com/it/u=486988684,1135095471&fm=26&gp=0.jpg";
    public static final String HEAD_URL = "http://b-ssl.duitang.com/uploads/item/201809/07/20180907093901_w3jEz.thumb.700_0.jpeg";

    public static void reload() {
        DEFAULT_ANIM_DURATION = SpUtil.get(ANIM_DURATION, 300L);
    }
}
