package com.pan.mylibrary.utils;

import android.annotation.SuppressLint;
import android.widget.Toast;
import com.pan.mylibrary.base.AppContext;


/**
 * Create by panchenhuan on 2019/1/3 5:37 PM
 * Description:
 */
public class ToastUtil {
    private static Toast toast;

    @SuppressLint("ShowToast")
    public static void showToast(String text) {
        if (toast == null) {
            toast = Toast.makeText(AppContext.getInstance(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);//如果不为空，则直接改变当前toast的文本
        }
        toast.show();
    }
}
