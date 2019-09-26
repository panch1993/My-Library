package com.pan.mylibrary.utils;

import java.util.ArrayList;

/**
 * Create by panchenhuan on 2019-09-26
 * walkwindc8@foxmail.com
 * Description:
 */
public class DataUtil {
    public static ArrayList<Integer> ints(int size) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        return list;
    }
}
