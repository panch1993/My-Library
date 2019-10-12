package com.pan.mylibrary.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.core.view.ViewPropertyAnimatorListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Create by panchenhuan on 2019-10-12
 * walkwindc8@foxmail.com
 * Description:
 */
class DragImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) :
    ImageView(context, attrs, defStyleAttr) {
    //原始位置
    private val rawLocation: Rect = Rect()

    //默认的
    private lateinit var defaultLayoutParams: ViewGroup.MarginLayoutParams
    //拖拽定位用
    private lateinit var dragLayoutParams: ViewGroup.MarginLayoutParams
    //阴影view
    private var shadowViews = arrayOfNulls<ImageView>(5)

    init {

        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                rawLocation.set(left, top, right, bottom)
                defaultLayoutParams = layoutParams as ViewGroup.MarginLayoutParams

                //反射获取
                val aClass = defaultLayoutParams.javaClass
                try {
                    val constructor = aClass.getConstructor(Int::class.java, Int::class.java)
                    dragLayoutParams = constructor.newInstance(
                        defaultLayoutParams.width,
                        defaultLayoutParams.height
                    )


                    //添加阴影view
                    val viewGroup = parent as ViewGroup
                    for (i in 0 until shadowViews.size) {
                        val imgShadow = ImageView(context)
                        imgShadow.layoutParams = constructor.newInstance(
                            defaultLayoutParams.width,
                            defaultLayoutParams.height
                        ).apply {
                            setMargins(left, top, 0, 0)
                        }
                        imgShadow.setImageDrawable(drawable)
//                        imgShadow.visibility = View.GONE
                        viewGroup.addView(imgShadow, viewGroup.childCount - 1)
                        imgShadow.alpha = (i + 1) * 0.18f
                        shadowViews[i] = imgShadow
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }

    private var lastX: Int = 0
    private var lastY: Int = 0
    //回弹动画
    private var animatorCompat: ViewPropertyAnimatorCompat? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isEnabled || animatorCompat != null) {
                    return false
                }

                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()

                dragLayoutParams.setMargins(left, top, 0, 0)
                layoutParams = dragLayoutParams
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX.toInt() - lastX
                val dy = event.rawY.toInt() - lastY
                //新位置信息
                val l = dragLayoutParams.leftMargin + dx
                val t = dragLayoutParams.topMargin + dy

                //更新位置
                dragLayoutParams.setMargins(l, t, 0, 0)
                requestLayout()

                //延迟拖拽阴影
                for (i in shadowViews.indices.reversed()) {
                    Observable.just(shadowViews[i])
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .delay(180L - 30L * (i + 1), TimeUnit.MILLISECONDS)
                        .subscribe {
                            (it?.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                                l,
                                t,
                                0,
                                0
                            )
                        }
                }
                //重新赋值
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
            }
            MotionEvent.ACTION_UP -> {
                shadowViews.forEach {
                    it?.visibility = View.GONE
                }
                //根据位移距离计算归位动画时间
                val left = dragLayoutParams.leftMargin
                val top = dragLayoutParams.topMargin

                val dx = rawLocation.left - left
                val dy = rawLocation.top - top

                val time = sqrt((abs(dx * dx) + abs(dy * dy)).toDouble()).toLong()
                //归位动画---移动至原位,缩放 1X,透明度 1
                animatorCompat = ViewCompat.animate(this)
                    .translationXBy((dx).toFloat())
                    .translationYBy((dy).toFloat())
                    .setInterpolator(BounceInterpolator())
                    .setDuration(min(time,600))
                    .setListener(object : ViewPropertyAnimatorListener {
                        override fun onAnimationStart(view: View) {}

                        override fun onAnimationEnd(view: View) {
                            animatorCompat = null
                            //归位
                            translationX = 0f
                            translationY = 0f
                            layoutParams = defaultLayoutParams
                            shadowViews.forEach {
                                (it?.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = rawLocation.left
                                (it?.layoutParams as ViewGroup.MarginLayoutParams).topMargin = rawLocation.top
                                it?.visibility = View.VISIBLE
                            }
                        }

                        override fun onAnimationCancel(view: View) {}
                    }).apply {
                        start()
                    }
            }
            else -> {

            }
        }
        return true
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        shadowViews?.forEach {
            it?.setImageBitmap(bm)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        shadowViews?.forEach {
            it?.setImageDrawable(drawable)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        shadowViews.forEach {
            it?.visibility = if (enabled)  View.VISIBLE else View.GONE
        }
    }

}