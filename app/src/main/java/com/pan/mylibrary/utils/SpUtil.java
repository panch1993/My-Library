package com.pan.mylibrary.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by panchenhuan on 2018/6/11 10:29.
 * SharedPreferences工具类
 */
public class SpUtil {

    private static SharedPreferences sp;
    private static SpUtil spUtil;

    public static SpUtil get() {
        if (spUtil == null) {
            synchronized (SpUtil.class) {
                if (spUtil == null) {
                    spUtil = new SpUtil();
                }
            }
        }
        return spUtil;
    }
    public void init(SharedPreferences sharedPreferences) {
        sp = sharedPreferences;
    }

    public static SharedPreferences getSp() {
        return sp;
    }

    /**
     * SharedPreferences 存入Value
     *
     * @param key   key值
     * @param value 存入值
     */
    public static void put(String key, Object value) {
        SharedPreferences.Editor editor = sp.edit();
        if (TextUtils.isEmpty(key) || value == null) return;
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        }
        editor.apply();
    }

    public static void put(String key, Set<String> value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(key, value).apply();
    }

    public static int get(String key, int def) {
        return sp.getInt(key, def);
    }

    public static String get(String key, String def) {
        return sp.getString(key, def);
    }

    public static Boolean get(String key, Boolean def) {
        return sp.getBoolean(key, def);
    }

    public static long get(String key, long def) {
        return sp.getLong(key, def);
    }

    public static float get(String key, float def) {
        return sp.getFloat(key, def);
    }

    public static HashSet<String> get(String key) {
        return (HashSet<String>) sp.getStringSet(key, new HashSet<String>());
    }


    public static void remove(String key) {
        SharedPreferences.Editor editor = sp.edit();

        editor.remove(key);
        editor.apply();
    }
}
