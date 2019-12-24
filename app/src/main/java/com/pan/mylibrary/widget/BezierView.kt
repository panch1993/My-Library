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
    private val points = ArrayList<Point>()

    private val SMOOTHNESS = 0.35f
    private lateinit var currentPoint: Point

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //起点
        val x1 = rect.left.toFloat()
        val x2 = rect.right.toFloat()
        //终点
        val y1 = rect.height() / 2f
        val y2 = y1
        /* //动点
         val px = point.x.toFloat()
         val py = point.y.toFloat()


         //直线
         paint.color = Color.LTGRAY
         paint.strokeWidth = 5f
         qPath.reset()
         qPath.moveTo(x1, y1)
         qPath.lineTo(px, py)
         qPath.lineTo(x2, y2)
         canvas.drawPath(qPath, paint)

         paint.strokeWidth = 15f
         //三阶
         paint.color = Color.BLACK
         qPath.reset()
         qPath.moveTo(x1, y1)
         qPath.cubicTo(x1, y1, px, py, x2, y2)
         canvas.drawPath(qPath, paint)
         //二阶
         paint.color = Color.BLUE
         qPath.reset()
         qPath.moveTo(x1, y1)
         qPath.quadTo(px, py, x2, y2)
         canvas.drawPath(qPath, paint)
 */

        qPath.reset()
        qPath.moveTo(x1, y1)

        cPath.reset()
        cPath.moveTo(x1, y1)

        lPath.reset()
        lPath.moveTo(x1, y1)


        //点
        paint.strokeWidth = 15f
        paint.color = Color.GRAY
        canvas.drawPoint(x1, y1, paint)
        canvas.drawPoint(x2, y2, paint)


        points.forEachIndexed { index, point ->
            lPath.lineTo(point.x.toFloat(), point.y.toFloat())
            canvas.drawPoint(point.x.toFloat(), point.y.toFloat(), paint)
            val xPre: Float
            val yPre: Float
            val xNext: Float
            val yNext: Float
            if (index == points.lastIndex) {
                xNext = x2
                yNext = y2
            } else {
                val next = points[index + 1]
                xNext = next.x.toFloat()
                yNext = next.y.toFloat()
            }

            if (index == 0) {
                xPre = x1
                yPre = y1
            } else {
                val pre = points[index - 1]
                xPre = pre.x.toFloat()
                yPre = pre.y.toFloat()
            }
            qPath.quadTo(point.x.toFloat(), point.y.toFloat(), xNext, yNext)
            if (index == 1) {

            cPath.cubicTo(xPre, yPre, point.x.toFloat(), point.y.toFloat(), xNext, yNext)
            }
        }
        /*for (int i = 1; i < pointList.size(); i++) {
            // 第一个控制点
            PointF p0 = pointList . get (i - 1);
            //当前点
            PointF p = pointList . get (i);
            // 第二个控制点
            PointF p1 = pointList . get ((i + 1) < pointList.size() ? (i+1) : i);// 下一个点
            float x1 = p0 . x +lX;
            float y1 = p0 . y +lY;
            lX = (p1.x - p0.x) / 2 * SMOOTHNESS;// (lX,lY) 是参考线的斜率
            lY = (p1.y - p0.y) / 2 * SMOOTHNESS;
            float x2 = p . x -lX;
            float y2 = p . y -lY;
            // 添加line
            path.cubicTo(x1, y1, x2, y2, p.x, p.y);
            fillPath.cubicTo(x1, y1, x2, y2, p.x, p.y);
        }*/
        paint.strokeWidth = 5f
        paint.color = Color.RED
        canvas.drawPath(cPath, paint)

        paint.strokeWidth = 4f
        paint.color = Color.BLUE
        canvas.drawPath(qPath, paint)

        paint.strokeWidth = 3f
        paint.color = Color.LTGRAY
        lPath.lineTo(x2, y2)
        canvas.drawPath(lPath, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        rect.left = paddingStart
        rect.right = w - paddingEnd
        rect.top = paddingTop
        rect.bottom = h - paddingBottom
        val p = Point()
        p.x = (rect.width() / 2f).toInt()
        p.y = (rect.height() / 2f).toInt()
        points.add(p)
    }

    public fun addPoint() {
        points.add(Point())
        resetPoints()
    }

    public fun removePoint() {
        if (points.size == 1) return
        points.removeAt(points.size - 1)
        resetPoints()
    }

    public fun resetPoints() {
        val temp = rect.width().toFloat() / (points.size + 1)
        points.forEachIndexed { index, point ->
            point.y = (rect.height() / 2f).toInt()
            point.x = rect.left + (temp * (index + 1)).toInt()
        }
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                var s = Float.MAX_VALUE
                var cIndex = 0
                points.forEachIndexed { index, it ->
                    val absX = abs(it.x.toFloat() - x)
                    val absY = abs(it.y.toFloat() - y)
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
            MotionEvent.ACTION_UP -> {
//                point.x = (rect.width() / 2f).toInt()
//                point.y = (rect.height() / 2f).toInt()
//                invalidate()
            }
        }
        return true
    }
}