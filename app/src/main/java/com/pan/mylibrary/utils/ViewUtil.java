package com.pan.mylibrary.utils;

import android.graphics.Paint;
import android.text.TextPaint;

/**
 * Create by panchenhuan on 2019-09-30
 * walkwindc8@foxmail.com
 * Description:
 */
public class ViewUtil {

    /**
     * 获取文字高度
     *
     * @param textPaint  文字画笔
     * @param baseLine   最底部
     * @param textHeight 文字高度
     * @return 文字的底部位置
     */
    public static float getBaseLine(TextPaint textPaint, float baseLine, float textHeight) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float fontHeight = fm.bottom - fm.top;
        return baseLine - (textHeight - fontHeight) / 2 - fm.bottom;
    }
}
