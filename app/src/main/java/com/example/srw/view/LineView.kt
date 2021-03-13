package com.example.srw.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.example.srw.R
import java.util.*

/**
 * author : kevin
 * date : 2021/3/12 10:36 PM
 * description :
 */
class LineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    /**
     * View起点距离顶部和底部的距离
     */
    private var mViewMargin = 0

    /**
     * 网格线的颜色
     */
    private var mLineColor = 0

    /**
     * 阴影部分的颜色
     */
    private var mShadowColor = 0

    /**
     * 字体大小
     */
    private var mTextSize = 0

    /**
     * 字体颜色
     */
    private var mTextColor = 0

    /**
     * 纵坐标刻度
     */
    private var mYList: List<String>? = null

    /**
     * 横坐标刻度
     */
    private var mXList: List<String>? = null

    /**
     * 网格线的高度
     */
    private var mHeight = 0

    /**
     * 网格线距离左边的距离
     */
    private var mMarginLeft = 0f
    private var mListPoint: List<Point>? = null
    private var mLinePaint: Paint? = null
    private var mBackShaderPaint: Paint? = null
    private var mWatchShaderPaint: Paint? = null


    private val originX = 0f
    private val originY by lazy { height - mViewMargin.toFloat() }

    private val maxWidth by lazy { width - (mMarginLeft + mViewMargin) }

    private var hasDraw = false

    private var startTime = 0f
        set(value) {
            if (field in 0f..maxWidth) {
                field = value
                invalidate()
            }
        }

    var endTime = 0f
        set(value) {
            if (field in 0f..maxWidth) {
                field = value
                invalidate()
            }
        }


    init {
        initAttrs(context, attrs, defStyleAttr)
        initPaint()
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val array =
            context.theme.obtainStyledAttributes(attrs, R.styleable.LineView, defStyleAttr, 0)
        val count = array.indexCount
        for (i in 0 until count) {
            val index = array.getIndex(i)
            when (index) {
                R.styleable.LineView_viewMargin -> mViewMargin = array.getDimensionPixelSize(
                    index,
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        10f,
                        resources.displayMetrics
                    ).toInt()
                )
                R.styleable.LineView_lineColor -> mLineColor =
                    array.getColor(index, Color.BLACK)
                R.styleable.LineView_shadowColor -> mShadowColor =
                    array.getColor(index, Color.BLACK)
                R.styleable.LineView_lineTextColor -> mTextColor =
                    array.getColor(index, Color.BLACK)
                R.styleable.LineView_lineTextSize -> mTextSize = array.getDimensionPixelSize(
                    index,
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        10f,
                        resources.displayMetrics
                    ).toInt()
                )
            }
        }
        array.recycle()
    }

    private fun initPaint() {
        mMarginLeft = (mViewMargin * 2).toFloat() //设置左边的偏移距离

        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint?.color = mLineColor
        mLinePaint?.textSize = mTextSize.toFloat()
        mLinePaint?.isAntiAlias = true //取消锯齿
        mLinePaint?.style = Paint.Style.STROKE //设置画笔为空心
        mLinePaint?.strokeWidth = 2f
        mLinePaint?.color = mTextColor

        mBackShaderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackShaderPaint?.color = mLineColor
        mBackShaderPaint?.textSize = mTextSize.toFloat()
        mBackShaderPaint?.isAntiAlias = true //取消锯齿
        mBackShaderPaint?.style = Paint.Style.FILL //设置画笔为空心
        mBackShaderPaint?.strokeWidth = 2f
        mBackShaderPaint?.color = mTextColor
        mBackShaderPaint?.strokeWidth = 3f

        mWatchShaderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mWatchShaderPaint?.color = mLineColor
        mWatchShaderPaint?.textSize = mTextSize.toFloat()
        mWatchShaderPaint?.isAntiAlias = true //取消锯齿
        mBackShaderPaint?.style = Paint.Style.FILL
        mWatchShaderPaint?.color = Color.RED
        mWatchShaderPaint?.strokeWidth = 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mHeight == 0) {
            mHeight = height - mViewMargin * 2
        }

        mListPoint = pointList
        drawScrollLine(canvas)
        drawBackShader(canvas)
        drawWatchShader(canvas, startTime, endTime)
    }


    /**
     * 画阴影
     */
    private fun drawBackShader(canvas: Canvas) {
        if (mListPoint.isNullOrEmpty() || mListPoint!!.size < 2) {
            return
        }

        var pStart = mListPoint!![0]
        var pEnd = mListPoint!![1]
        val path = Path()

        //去原点
        path.moveTo(originX, originY)
        path.moveTo(pStart.x, pStart.y)
        for (i in 0 until mListPoint!!.size - 1) {
            pStart = mListPoint!![i]
            pEnd = mListPoint!![i + 1]

            val point3 = Point()
            val point4 = Point()
            val wd = (pStart.x + pEnd.x) / 2
            point3.x = wd
            point3.y = pStart.y
            point4.x = wd
            point4.y = pEnd.y
            path.cubicTo(point3.x, point3.y, point4.x, point4.y, pEnd.x, pEnd.y)
        }
        path.lineTo(pEnd.x, height - mViewMargin.toFloat())
        path.lineTo(pStart.x, height - mViewMargin.toFloat())
        path.close()
        mBackShaderPaint?.style = Paint.Style.FILL
        mBackShaderPaint?.color = mShadowColor
        canvas.clipPath(path)
        canvas.drawColor(mShadowColor)
    }


    private fun drawWatchShader(canvas: Canvas, startTime: Float, endTime: Float) {
        Log.i("hutcwp", "drawWatchShader")
        if (mListPoint.isNullOrEmpty() || mListPoint!!.size < 2) {
            return
        }

        val dst = Path()
        dst.moveTo(startTime, 0f)
        dst.lineTo(endTime, 0f)
        dst.lineTo(endTime, originY)
        dst.lineTo(startTime, originY)
//        dst.close()
        canvas.drawPath(dst, mWatchShaderPaint!!)
    }

    /**
     * 绘制曲线图
     */
    private fun drawScrollLine(canvas: Canvas) {
        var pStart = Point()
        var pEnd = Point()
        val path = Path()
        for (i in 0..4) {
            pStart = mListPoint!![i]
            pEnd = mListPoint!![i + 1]
            val point3 = Point()
            val point4 = Point()
            val wd = (pStart.x + pEnd.x) / 2
            point3.x = wd
            point3.y = pStart.y
            point4.x = wd
            point4.y = pEnd.y
            path.moveTo(pStart.x, pStart.y)
            path.cubicTo(point3.x, point3.y, point4.x, point4.y, pEnd.x, pEnd.y)
            canvas.drawPath(path, mLinePaint!!)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    fun setViewData(
        yList: List<String>?,
        xList: List<String>?
    ) {
        mYList = yList
        mXList = xList
    }

    /**
     * 根据手机分辨率将px 转为 dp
     */
    private fun dpToPx(context: Context, pxValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return pxValue * scale + 0.5f
    }

    private val pointList: List<Point>
        private get() {
            val mList: MutableList<Point> =
                ArrayList()
            val width = width - (mMarginLeft + mViewMargin)
            val height = height - mViewMargin * 2.toFloat()
            for (i in 0..5) {
                val point = Point()
                if (i == 0) {
                    point.x = mMarginLeft
                    point.y = getHeight() - mViewMargin.toFloat()
                }
                if (i == 1) {
                    point.x = mMarginLeft + width * 0.2f
                    point.y = mViewMargin + height * 0.5f
                }
                if (i == 2) {
                    point.x = mMarginLeft + width * 0.4f
                    point.y = mViewMargin + height * 0.8f
                }
                if (i == 3) {
                    point.x = mMarginLeft + width * 0.6f
                    point.y = mViewMargin + height * 0.6f
                }
                if (i == 4) {
                    point.x = mMarginLeft + width * 0.8f
                    point.y = mViewMargin + height * 0.8f
                }
                if (i == 5) {
                    point.x = mMarginLeft + width
                    point.y = mViewMargin + height * 0.6f
                }
                mList.add(point)
            }
            return mList
        }


    private inner class Point {
        var x = 0f
        var y = 0f

        constructor() {}
        constructor(x: Float, y: Float) {
            this.x = x
            this.y = y
        }
    }


}