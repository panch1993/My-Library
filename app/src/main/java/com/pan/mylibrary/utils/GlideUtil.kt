package com.pan.mylibrary.utils

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.pan.mylibrary.R
import com.pan.mylibrary.widget.CircleRoundCrop
import jp.wasabeef.blurry.Blurry

/**
 * Create by panchenhuan on 2019-10-11
 * walkwindc8@foxmail.com
 * Description:
 */
object GlideUtil {

    fun load(
        imageView: ImageView, obj: Any,
        @DrawableRes placeholder: Int = R.drawable.ic_loading,
        asCircle: Boolean = false
    ) {
        Glide.with(imageView)
            .load(obj)
            .apply(RequestOptions().placeholder(placeholder))
            .apply {
                if (asCircle) {
//                    apply(RequestOptions.bitmapTransform(CircleCrop()))
                    apply(
                        RequestOptions.bitmapTransform(
                            CircleRoundCrop()/*object : BitmapTransformation() {
                        override fun updateDiskCacheKey(messageDigest: MessageDigest) {
                            messageDigest.update("circle round".toByte())
                        }

                        override fun transform(
                            pool: BitmapPool,
                            toTransform: Bitmap,
                            outWidth: Int,
                            outHeight: Int
                        ): Bitmap {
                            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                            paint.color= Color.RED
                            paint.style = Paint.Style.STROKE
                            paint.strokeWidth = 10f
                            val circleBitmap = TransformationUtils.circleCrop(
                                pool,
                                toTransform,
                                outWidth,
                                outHeight
                            )
//                            val canvas = Canvas(circleBitmap)
//                            canvas.drawCircle(circleBitmap.width/2f,circleBitmap.height/2f,50f,paint)
                            return circleBitmap
                        }
                    }*/
                        )
                    )
                }
            }
            .into(imageView)
    }

    fun loadBlurry(imageView: ImageView, obj: Any) {
        var drawable: Drawable? = imageView.drawable
        if (drawable == null) {
            drawable = ContextCompat.getDrawable(imageView.context, R.drawable.ic_loading)
        }
        Glide.with(imageView.context)
            .load(obj)
            .apply(RequestOptions().placeholder(drawable).error(drawable).centerCrop())
            .transition(DrawableTransitionOptions().crossFade())
            .into(object : DrawableImageViewTarget(imageView) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    val bitmap = (resource as BitmapDrawable).bitmap
                    Blurry.with(imageView.context).from(bitmap).into(imageView)
                }
            })
    }
}
