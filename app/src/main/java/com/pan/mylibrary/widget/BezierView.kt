package com.pan.mylibrary.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Create by panchenhuan on 2019-12-24
 * walkwindc8@foxmail.com
 * Description:
 */
class BezierView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : View(context, attrs, defStyleAttr) {
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = Rect()
    private val qPath = Path()
    private val cPath = Path()
    private val lPath = Path()
    private val points = ArrayList<PointF>()

    private var smoothness = 0.35f
    private lateinit var currentPoint: PointF

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (points.isEmpty()) return
        val start = points[0]

        qPath.reset()
        qPath.moveTo(start.x, start.y)

        cPath.reset()
        cPath.moveTo(start.x, start.y)

        lPath.reset()
        lPath.moveTo(start.x, start.y)


        //直线
        for (i in 1 until points.size) {
            val p = points[i]
            lPath.lineTo(p.x, p.y)
        }
        //俩阶
//        for (i in 1 until points.size step 2) {
//            val next = if (i + 1 < points.size) i + 1 else i
//            val p0 = points[i]
//            val p1 = points[next]
//            qPath.quadTo(p0.x, p0.y, p1.x, p1.y)
//        }
        //三阶
        var lX = 0f
        var lY = 0f
        for (i in 1 until points.size) {
            // 第一个控制点
            val p0 = points[i - 1]
            //当前点
            val p = points[i]
            // 第二个控制点
            val p1 = points[if ((i + 1) < points.size) (i + 1) else i]

            val x1 = p0.x + lX
            val y1 = p0.y + lY
            lX = (p1.x - p0.x) / 2 * smoothness// (lX,lY) 是参考线的斜率
            lY = (p1.y - p0.y) / 2 * smoothness
            val x2 = p.x - lX
            val y2 = p.y - lY
            // 添加line
            cPath.cubicTo(x1, y1, x2, y2, p.x, p.y)
        }

        paint.strokeWidth = 5f
        paint.color = Color.RED
        canvas.drawPath(cPath, paint)

//        paint.strokeWidth = 4f
//        paint.color = Color.BLUE
//        canvas.drawPath(qPath, paint)

        paint.strokeWidth = 3f
        paint.color = Color.LTGRAY
        canvas.drawPath(lPath, paint)

        //点
        paint.strokeWidth = 15f
        paint.color = Color.GRAY
        points.forEach {
            canvas.drawPoint(it.x, it.y, paint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        rect.left = paddingStart
        rect.right = w - paddingEnd
        rect.top = paddingTop
        rect.bottom = h - paddingBottom
    }

    fun addPoint() {
        points.add(PointF())
        resetPoints()
    }

    fun removePoint() {
        if (points.size == 1) return
        points.removeAt(points.size - 1)
        resetPoints()
    }

    fun resetPoints() {
        if (points.size <= 1) return
        val temp = rect.width() / (points.size - 1)
        points.forEachIndexed { index, point ->
            point.y = (rect.height() / 2f)
            point.x = (rect.left + (temp * (index))).toFloat()
        }
        invalidate()
    }

    fun setSmoothness(smoothness: Float) {
        this.smoothness = smoothness
        invalidate()
    }

    fun getSmoothness(): Float = smoothness

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                var s = Float.MAX_VALUE
                var cIndex = 0
                points.forEachIndexed { index, it ->
                    val absX = abs(it.x - x)
                    val absY = abs(it.y - y)
                    val sqrt = sqrt(absX * absX + absY * absY)
                    if (sqrt < s) {
                        s = sqrt
                        cIndex = index
                    }
                }
                currentPoint = points[cIndex]
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                currentPoint.x = x
                currentPoint.y = y
                invalidate()
            }
        }
        return true
    }
}