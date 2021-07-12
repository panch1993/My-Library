package com.pan.mylibrary.widget.rangeseek

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.core.view.GestureDetectorCompat
import com.pan.mylibrary.R
import com.pan.mylibrary.utils.ImageBitmapUtil
import com.pan.mylibrary.utils.KLog
import kotlin.math.abs

/**
 * Create by panchenhuan on 1/20/21
 * walkwindc8@foxmail.com
 * Description:
 */
class RangeSeekBar : View, GestureDetector.OnGestureListener {
    private var mDetector: GestureDetectorCompat? = null
    var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener? = null

    //圆球半径
    private var cirRadius = 25
    private var cirCount = 5
    private val cirPoints = ArrayList<Point>()

    //主色
    private var primaryColor = Color.BLUE

    //副色
    private var normalColor = Color.GRAY

    //seek高度
    private var seekHeight = 20

    private val maxProgress = 100

    private var progress = 0
    private val dragPoint = Point()

    private val seekRect: Rect = Rect()

    private val primaryPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val normalPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPointBitmap: Bitmap? = null

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        mDetector = GestureDetectorCompat(context, this)
        mDetector?.setIsLongpressEnabled(false)
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar)
            normalColor = array.getColor(R.styleable.RangeSeekBar_normalColor, normalColor)
            primaryColor = array.getColor(R.styleable.RangeSeekBar_primaryColor, primaryColor)
            seekHeight =
                array.getDimensionPixelSize(R.styleable.RangeSeekBar_seekHeight, seekHeight)
            cirRadius = array.getDimensionPixelSize(R.styleable.RangeSeekBar_cirRadius, cirRadius)
            cirCount = array.getDimensionPixelSize(R.styleable.RangeSeekBar_cirCount, cirCount)
            array.recycle()
        }
        primaryPaint.color = primaryColor
        primaryPaint.style = Paint.Style.FILL

        normalPaint.color = normalColor
        normalPaint.style = Paint.Style.FILL

        whitePaint.color = Color.WHITE
        whitePaint.style = Paint.Style.FILL

        setDragBitmap(R.drawable.ic_drag_buy)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mDetector!!.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(seekRect, primaryPaint)
        val percent = (maxProgress - progress) * 1f / maxProgress
        val normalWidth = percent * seekRect.width()
        canvas.drawRect(
            seekRect.right - normalWidth,
            seekRect.top.toFloat(),
            seekRect.right.toFloat(),
            seekRect.bottom.toFloat(),
            normalPaint
        )


        val range = maxProgress / (cirCount - 1)
        cirPoints.forEachIndexed { index, it ->
            val pointRange = range * index
            if (progress >= pointRange) {
                canvas.drawCircle(
                    it.x.toFloat(),
                    it.y.toFloat(),
                    cirRadius.toFloat() + 1,
                    whitePaint
                )
                canvas.drawCircle(it.x.toFloat(), it.y.toFloat(), cirRadius.toFloat(), primaryPaint)
            } else {
                canvas.drawCircle(
                    it.x.toFloat(),
                    it.y.toFloat(),
                    cirRadius.toFloat() + 1,
                    normalPaint
                )
            }
        }

        dragPoint.let {
            canvas.drawCircle(it.x.toFloat(), it.y.toFloat(), 5f, whitePaint)
            mPointBitmap?.let { bitmap ->
                val left = it.x - bitmap.width / 2f
                val top = it.y - bitmap.height / 2f
                canvas.drawBitmap(bitmap, left, top, whitePaint)
            }
        }
    }

    override fun onDown(e: MotionEvent): Boolean {
        parent?.requestDisallowInterceptTouchEvent(true)
        return true
    }

    override fun onShowPress(e: MotionEvent) {}

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        var mx = e.x.toInt()
        if (mx < seekRect.left) {
            mx = seekRect.left
        } else if (mx > seekRect.right) {
            mx = seekRect.right
        }

        var s = Int.MAX_VALUE
        var cIndex = 0
        cirPoints.forEachIndexed { index, it ->
            val absX = abs(it.x - mx)
            if (absX < s) {
                s = absX
                cIndex = index
            }
        }
        val np = cIndex * (maxProgress / (cirCount - 1))
        updateProgress(np)
        return true
    }

    /**
     * e1:按下
     * e2:移动点
     */
    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        var mx = e2.x.toInt()
        if (mx < seekRect.left) {
            mx = seekRect.left
        } else if (mx > seekRect.right) {
            mx = seekRect.right
        }
        val np = (mx - seekRect.left) * 1f / seekRect.width() * 100
        updateProgress(np.toInt())
        return true
    }

    override fun onLongPress(e: MotionEvent) {}

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        calculate()
    }

    private fun calculate() {
        //seek信息
        val centerY = height / 2
        seekRect.set(
            cirRadius,
            centerY - seekHeight / 2,
            width - cirRadius,
            centerY + seekHeight / 2
        )
        //圆点信息
        cirPoints.clear()
        val cirSpace = seekRect.width() / (cirCount - 1)
        var p: Point
        for (i in 0 until cirCount) {
            val x = seekRect.left + i * cirSpace
            p = Point(x, centerY)
            cirPoints.add(p)
        }
        KLog.d(cirPoints.toString())
        dragPoint.y = centerY
        dragPoint.x = seekRect.left
    }

    public fun updateProgress(np: Int) {
        if (np == progress) return
        progress = np
        dragPoint.x = (progress * 1f / maxProgress * seekRect.width() + seekRect.left).toInt()
        invalidate()
        onSeekBarChangeListener?.onProgressChanged(null, progress, true)
    }

    public fun setPrimaryColor(color: Int) {
        primaryColor = color
        primaryPaint.color = color
        invalidate()
    }

    public fun setNormalColor(color: Int) {
        normalColor = color
        normalPaint.color = color
        invalidate()
    }

    public fun setDragBitmap(res: Int) {
        mPointBitmap = ImageBitmapUtil.getBitmapFromDrawable(context, res)
    }
}