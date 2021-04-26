package com.kotlin.library.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.kotlin.library.R

/**
 * 进度条
 *
 * @author llw
 * @date 2021/2/25 14:34
 */
class LineProgressbar : View {

    private var mPaint //画笔
            : Paint? = null
    private val mPaintWidth = 6f //初始画笔宽度
    private var mProgressbarWidth = 0 //控件外边框宽度 = 0
    private var mProgressbarHeight = 0 //控件外边框高度 = 0
    private var mPercent = 0 //已转化为0至100范围的当前进度，随动画时间改变而改变

    private var mText: String? = "0%" //进度当前百分比值
    private var mTextColor = Color.WHITE //进度当前百分比值的颜色
    private var mTextSize = 32f //进度当前百分比值大小

    constructor(context: Context?) : super(context)

    @SuppressLint("Recycle")
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        val array = getContext().obtainStyledAttributes(attrs, R.styleable.LineProgressbar)
        mProgressbarWidth =
            array.getDimension(R.styleable.LineProgressbar_progressbar_width, 100f).toInt()
        mProgressbarHeight =
            array.getDimension(R.styleable.LineProgressbar_progressbar_height, 10f).toInt()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * 测量
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mProgressbarWidth, mProgressbarHeight)
    }

    /**
     * 绘制
     */
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        mPaint = Paint()

        mPaint = mPaint?.apply {
            color = resources.getColor(R.color.arc_bg_color)
            style = Paint.Style.FILL
            isAntiAlias = true
            strokeWidth = mPaintWidth
        }
        //绘制背景
        val frameRectF = RectF(
            mPaintWidth, mPaintWidth,
            mProgressbarWidth - mPaintWidth, mProgressbarHeight - mPaintWidth
        )
        canvas.drawRoundRect(frameRectF, 15f, 15f, mPaint!!)

        //填充内部进度
        mPaint = mPaint?.apply {
            pathEffect = null
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        //内部进度填充长度，随动画时间改变而改变
        val percent = mPercent.toFloat() / 100f
        val progressRectF = RectF(
            mPaintWidth,
            mPaintWidth,
            mPaintWidth + percent * (mProgressbarWidth - 2 * mPaintWidth - 2),
            mProgressbarHeight - mPaintWidth
        )
        canvas.drawRoundRect(progressRectF, 15f, 15f, mPaint!!)

        //绘制文字
        drawPercentageText(
            canvas,
            mProgressbarHeight - mPaintWidth,
            (mProgressbarWidth - mPaintWidth) + 10
        )

    }

    /**
     * 绘制当前百分比文本
     *
     * @param canvas  画笔
     * @param rightX  X轴位置
     * @param bottomY Y轴位置
     */
    private fun drawPercentageText(canvas: Canvas, rightX: Float, bottomY: Float) {
        val paint = Paint().apply {
            isAntiAlias = true
            color = mTextColor
            textAlign = Paint.Align.CENTER //设置绘制方式 中心对齐
            textSize = mTextSize
        }
        val bounds = Rect()
        paint.getTextBounds(mText, 0, mText!!.length, bounds) //TextView的高度和宽度
        canvas.drawText(mText!!, rightX - bounds.width(), bottomY + 16, paint)
    }

    /**
     * 设置最大值文本
     *
     * @param text
     */
    fun setPercentageText(text: String?) {
        mText = text
    }

    /**
     * 设置进度
     * @param progress 当前进度
     * @param maxProgress 最大进度
     */
    fun setProgress(progress: String, maxProgress: Int) {
        var percent = 0

        //得出当前progress占最大进度值百分比（0-100）
        percent = if (progress.contains(".")) { //float或者double类型
            progress.toFloat().toInt() * 10 * 100 / (maxProgress * 10)
        } else { //int类型
            progress.toInt() * 100 / maxProgress
        }
        if (percent < 0) {
            percent = 0
        }
        if (percent > 100) {
            percent = 100
        }

        //属性动画
        val animator = ValueAnimator.ofInt(0, percent)
        animator.duration = 1000
        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener { valueAnimator ->
            mPercent = valueAnimator.animatedValue as Int
            invalidate()
        }
        animator.start()
    }
}