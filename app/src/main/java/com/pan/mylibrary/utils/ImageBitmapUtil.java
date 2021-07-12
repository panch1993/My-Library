package com.pan.mylibrary.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.view.View;
import android.widget.ScrollView;

import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

/**
 * Created by panchenhuan on 2018/6/12 10:43.
 * <p>
 * panchenhuan@mirahome.me
 */
public class ImageBitmapUtil {

    /**
     * 截取view的屏幕
     *
     * @param view
     * @return bitmap
     */
    public static Bitmap getBitmapByView(View view) {
//        view.setBackgroundResource(R.color.gray1);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    /**
     * 截取scrollview的屏幕
     *
     * @param scrollView
     * @return bitmap
     */
    public static Bitmap getBitmapByScrollView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取scrollview实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
//            scrollView.getChildAt(i).setBackgroundResource(R.color.gray1);
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * Drawable-->Bitmap
     *
     * @param drawable Drawable
     * @return Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 自定义图片Option
     */
    public static BitmapFactory.Options getBitmapOption() {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;//分配到内存
        options.outHeight = 200;
        options.outWidth = 200;
        return options;
    }

    /**
     * 获取Bitmap
     */
    public static Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        return getBitmapFromDrawable(drawable);
    }

    /**
     * 获取Bitmap
     */
    @SuppressLint("NewApi")
    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("drawable is null");
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }


}
