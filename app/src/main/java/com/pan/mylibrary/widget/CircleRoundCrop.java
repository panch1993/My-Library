package com.pan.mylibrary.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * A Glide {@link BitmapTransformation} to circle crop an image. Behaves similar to a {@link
 * FitCenter} transform, but the resulting image is masked to a circle.
 *
 * <p>Uses a PorterDuff blend mode, see http://ssp.impulsetrain.com/porterduff.html.
 */
public class CircleRoundCrop extends BitmapTransformation {
    // The version of this transformation, incremented to correct an error in a previous version.
    // See #455.
    private static final int VERSION = 5;
    private static final String ID = "com.pan.mylibrary.widget." + VERSION;
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CircleRoundCrop() {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15f);
    }

    // Bitmap doesn't implement equals, so == and .equals are equivalent here.
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    @Override
    protected Bitmap transform(
            @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = TransformationUtils.circleCrop(pool, toTransform, outWidth, outHeight);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(bitmap.getWidth() / 2f, bitmap.getHeight() / 2f, (bitmap.getWidth() -paint.getStrokeWidth())/ 2f , paint);
        return bitmap;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CircleRoundCrop;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}
