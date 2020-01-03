package com.pan.mylibrary.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.pan.mylibrary.utils.KLog

/**
 * Create by panchenhuan on 2019-12-30
 * walkwindc8@foxmail.com
 * Description:
 */
class TouchLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : FrameLayout(
    context,
    attrs,
    defStyleAttr
) {

    var touch = false
    var intercept = false
    var dispatch = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
//                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP -> {

            }
        }
//        val onTouchEvent = super.onTouchEvent(event)
//        KLog.d("TouchLayout - onTouchEvent - ${event.actionMasked} = $onTouchEvent")
        return touch && super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
//        val dispatchTouchEvent = super.dispatchTouchEvent(event)
//        KLog.d("TouchLayout - dispatchTouchEvent - ${event.actionMasked} = $dispatchTouchEvent")
        return dispatch && super.dispatchTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val onInterceptTouchEvent = super.onInterceptTouchEvent(event)
        KLog.d("TouchLayout - onInterceptTouchEvent - ${event.actionMasked} = $onInterceptTouchEvent")
        return intercept && super.onInterceptTouchEvent(event)
    }
}