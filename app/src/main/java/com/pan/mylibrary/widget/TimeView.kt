package com.pan.mylibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.pan.mylibrary.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Create by panchenhuan on 2019-10-10
 * walkwindc8@foxmail.com
 * Description:
 */
class TimeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) :
    View(context, attrs, defStyleAttr) {
    private var currentType: Type? = null

    //value
    private var drawColor: Int = 0
    private var itemMargin: Int = 0
    //value

    private var paint: Paint
    //有效绘制区域
    private var rect: Rect

    private var disposable: Disposable? = null

    enum class Type {
        YEAR,//10 * 10
        MONTH,// 3 * 4
        DAY, //50 * 80
        HOUR, //4 * 6
        MIN, //6 * 10
        SEC, //6 * 10
        MSEC //20 * 50
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeView)
        if (typedArray != null) {
            drawColor = typedArray.getColor(R.styleable.TimeView_color, Color.BLACK)
            itemMargin = typedArray.getDimensionPixelOffset(R.styleable.TimeView_item_margin, 0)
            val type = typedArray.getInt(R.styleable.TimeView_show_type, 0)
            currentType = Type.values()[type]
            typedArray.recycle()
        }
        currentType = Type.YEAR

        rect = Rect()
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.color = drawColor
    }


    fun getCurrentType(): Type? {
        return currentType
    }

    fun setCurrentType(currentType: Type) {
        if (this.currentType == currentType) return
        this.currentType = currentType
        disposable?.dispose()
        disposable = Observable.interval(0, getPeriod(currentType), TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                invalidate()
            }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.style = Paint.Style.STROKE
        canvas.drawRect(rect, paint)
        val currentTime = System.currentTimeMillis()

        val xItemNum: Int
        val yItemNum: Int
        var drawIndex: Int = 0
        when (currentType) {
            Type.YEAR -> {
                //10 * 10
                xItemNum = 10
                yItemNum = 10
            }
            Type.MONTH -> {
                // 3 * 4
                xItemNum = 3
                yItemNum = 4
            }
            Type.DAY -> {
                //50 * 80
                xItemNum = 20
                yItemNum = 20
            }
            Type.HOUR -> {
                //4 * 6
                xItemNum = 4
                yItemNum = 6
            }
            Type.MIN -> {
                //6 * 10
                xItemNum = 6
                yItemNum = 10
            }
            Type.SEC -> {
                //6 * 10
                xItemNum = 6
                yItemNum = 10

            }
            Type.MSEC -> {
                //20 * 50
                xItemNum = 100
                yItemNum = 100
                val currStr = currentTime.toString()
                drawIndex = currStr.substring(currStr.length - 4).toInt()
//                drawIndex = 999
            }
            else -> {
                xItemNum = 0
                yItemNum = 0
            }
        }
        val itemWidth = (rect.width() - (xItemNum + 1) * itemMargin) * 1f / xItemNum
        val itemHeight = (rect.height() - (yItemNum + 1) * itemMargin) * 1f / yItemNum

        if (drawIndex != 0) {
            paint.style = Paint.Style.FILL_AND_STROKE
        } else {
            paint.style = Paint.Style.STROKE
        }
        for (i in 0 until yItemNum) {
            val top = (i + 1) * itemMargin + i * itemHeight
            val bottom = top + itemHeight
            for (j in 0 until xItemNum) {
                val left = (j + 1) * itemMargin + j * itemWidth
                val right = left + itemWidth
                canvas.drawRect(left, top, right, bottom, paint)
                if (paint.style == Paint.Style.FILL_AND_STROKE && i * xItemNum + j +1>= drawIndex) {
                    paint.style = Paint.Style.STROKE
                }
            }
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = measuredHeight
        val width = measuredWidth
        rect.set(paddingStart, paddingTop, width - paddingEnd, height - paddingBottom)
    }

    private fun getPeriod(type: Type): Long = when (type) {
        Type.YEAR -> 1000
        Type.MONTH -> 1000
        Type.DAY -> 1000
        Type.HOUR -> 1000
        Type.MIN -> 1000
        Type.SEC -> 1000
        Type.MSEC -> 1
    }
}
