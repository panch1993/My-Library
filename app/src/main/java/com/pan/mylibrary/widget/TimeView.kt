package com.pan.mylibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.pan.mylibrary.R
import com.pan.mylibrary.utils.ResourceUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sqrt


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
    private var emptyColor: Int = 0
    private var itemMargin: Int = 0
    //value

    private var paint: Paint
    //有效绘制区域
    private var rect: Rect
    //刷新定时器
    private var disposable: Disposable? = null
    //对比日期
    private var timeIndex: Long = 0L
    //格式化
    private var simpleDateFormat: SimpleDateFormat

    enum class Type {
        YEAR,//10 * 10
        MONTH,// 3 * 4
        DAY, //50 * 80
        HOUR, //4 * 6
        MIN, //6 * 10
        SEC, //6 * 10
        MSEC //20 * 50
    }

    private val dayOffset = 24 * 60 * 60 * 1000L
    private val yearOffset = 365 * dayOffset

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeView)
        drawColor = typedArray.getColor(R.styleable.TimeView_color, Color.BLACK)
        emptyColor = typedArray.getColor(R.styleable.TimeView_emptyColor, Color.LTGRAY)
        itemMargin = typedArray.getDimensionPixelOffset(R.styleable.TimeView_itemMargin, 0)
        val type = typedArray.getInt(R.styleable.TimeView_showType, 0)
        setCurrentType(Type.values()[type])
        typedArray.recycle()

        rect = Rect()
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.textSize = ResourceUtil.getDimens(R.dimen.text_52px).toFloat()
        paint.textAlign = Paint.Align.CENTER
        paint.isFakeBoldText = true

        simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())
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
//        canvas.drawRect(rect, paint)

        val calendar = Calendar.getInstance()
        val currentTime = System.currentTimeMillis()
        val xItemNum: Int
        val yItemNum: Int
        var drawIndex = 0
        var drawText: String? = null
        when (currentType) {
            Type.YEAR -> {
                //10 * 10
                drawIndex = ((currentTime - timeIndex) / yearOffset).toInt()
                val ceil = ceil(sqrt(drawIndex.toDouble()))
                xItemNum = ceil.toInt()
                yItemNum = ceil.toInt()
                drawText =
                    String.format("%.9f", ((currentTime - timeIndex).toDouble() / yearOffset))
            }
            Type.MONTH -> {
                // 3 * 4
                drawIndex = getDifferMonth(Date(timeIndex), Date(currentTime))
                val ceil = ceil(sqrt(drawIndex.toDouble()))
                xItemNum = ceil.toInt()
                yItemNum = ceil.toInt()
            }
            Type.DAY -> {
                //50 * 80
                drawIndex = ((currentTime - timeIndex) / dayOffset).toInt()
                val ceil = ceil(sqrt(drawIndex.toDouble()))
                xItemNum = ceil.toInt()
                yItemNum = ceil.toInt()
                drawText = String.format("%.9f", ((currentTime - timeIndex).toDouble() / dayOffset))
            }
            Type.HOUR -> {
                //4 * 6
                xItemNum = 4
                yItemNum = 6
                drawIndex = calendar.get(Calendar.HOUR_OF_DAY)
            }
            Type.MIN -> {
                //6 * 10
                xItemNum = 6
                yItemNum = 10
                drawIndex = calendar.get(Calendar.MINUTE)
            }
            Type.SEC -> {
                //6 * 10
                xItemNum = 6
                yItemNum = 10
                drawIndex = calendar.get(Calendar.SECOND)
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

        paint.color = drawColor
        for (i in 0 until yItemNum) {
            val top = rect.top + (i + 1) * itemMargin + i * itemHeight
            val bottom = top + itemHeight
            for (j in 0 until xItemNum) {
                val left = rect.left + (j + 1) * itemMargin + j * itemWidth
                val right = left + itemWidth
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(left, top, right, bottom, 10f, 10f, paint)
                } else {
                    canvas.drawRect(left, top, right, bottom, paint)
                }
                if (i * xItemNum + j + 1 >= drawIndex) {
                    paint.color = emptyColor
                }
            }
        }
        paint.color = drawColor

        canvas.drawText(
            drawText ?: drawIndex.toString(),
            rect.centerX().toFloat(),
            rect.top.toFloat() - 15,
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = measuredHeight
        val width = measuredWidth
        rect.set(
            paddingStart,
            paddingTop + paint.textSize.toInt() + 30,
            width - paddingEnd,
            height - paddingBottom
        )
    }

    private fun getPeriod(type: Type): Long = when (type) {
        Type.YEAR -> 1000
        Type.MONTH -> Long.MAX_VALUE
        Type.DAY -> 1000
        Type.HOUR -> 1000
        Type.MIN -> 1000
        Type.SEC -> 1000
        Type.MSEC -> 10
    }

    fun setTimeIndex(index: String) {
        timeIndex = simpleDateFormat.parse(index).time
    }

    /**
     * 获取两个日期的月数差
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    private fun getDifferMonth(fromDate: Date, toDate: Date): Int {
        val fromDateCal = Calendar.getInstance()
        val toDateCal = Calendar.getInstance()
        fromDateCal.time = fromDate
        toDateCal.time = toDate

        val fromYear = fromDateCal.get(Calendar.YEAR)
        val toYear = toDateCal.get(Calendar.YEAR)
        return if (fromYear == toYear) {
            abs(fromDateCal.get(Calendar.MONTH) - toDateCal.get(Calendar.MONTH))
        } else {
            val fromMonth = 12 - (fromDateCal.get(Calendar.MONTH) + 1)
            val toMonth = toDateCal.get(Calendar.MONTH) + 1
            (abs(toYear - fromYear - 1) * 12 + fromMonth + toMonth)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposable?.dispose()
    }
}
