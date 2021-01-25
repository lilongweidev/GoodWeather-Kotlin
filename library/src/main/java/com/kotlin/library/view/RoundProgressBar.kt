package com.kotlin.library.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.kotlin.library.R

/**
 * 圆弧进度条
 */
class RoundProgressBar : View {
    private var mStrokeWidth = dp2px(8f) //圆弧的宽度
    private var mStartAngle = 135f //圆弧开始的角度
    private var mAngleSize = 270f //起点角度和终点角度对应的夹角大小
    private var mArcBgColor = 0 //圆弧背景颜色
    private var mMaxProgress = 0f //最大的进度，用于计算进度与夹角的比例
    private var mCurrentAngleSize = 0f //当前进度对应的起点角度到当前进度角度夹角的大小
    private var mCurrentProgress = 0f //当前进度
    private var mDuration: Long = 2000 //动画的执行时长
    private var mProgressColor = 0 //进度圆弧的颜色
    private var mFirstText: String? = "0" //第一行文本
    private var mFirstTextColor = Color.WHITE //第一行文本的颜色
    private var mFirstTextSize = 56f //第一行文本的字体大小
    private var mSecondText: String? = " " //第二行文本
    private var mSecondTextColor = Color.WHITE //第二行文本的颜色
    private var mSecondTextSize = 56f //第二行文本的字体大小
    private var mMinText: String? = "0" //进度最小值
    private var mMinTextColor = Color.WHITE //最小值文本的颜色
    private var mMinTextSize = 32f //最小值字体大小
    private var mMaxText: String? = "0" //进度最大值
    private var mMaxTextColor = Color.WHITE //最大值文本的颜色
    private var mMaxTextSize = 32f //最大值字体大小

    constructor(context: Context?) : super(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs,
        0
    )


    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initAttr(context, attrs)
    }

    /**
     * 设置初始化的参数
     *
     * @param context
     * @param attrs
     */
    private fun initAttr(
        context: Context,
        attrs: AttributeSet?
    ) {
        val array =
            context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar)
        mMaxProgress =
            array.getFloat(R.styleable.RoundProgressBar_round_max_progress, 500f)
        mArcBgColor = array.getColor(
            R.styleable.RoundProgressBar_round_bg_color,
            Color.YELLOW
        )
        mStrokeWidth = dp2px(
            array.getDimension(
                R.styleable.RoundProgressBar_round_stroke_width,
                12f
            )
        )
        mCurrentProgress =
            array.getFloat(R.styleable.RoundProgressBar_round_progress, 300f)
        mProgressColor = array.getColor(
            R.styleable.RoundProgressBar_round_progress_color,
            Color.RED
        )
        mFirstText =
            array.getString(R.styleable.RoundProgressBar_round_first_text)
        mFirstTextSize = dp2px(
            array.getDimension(
                R.styleable.RoundProgressBar_round_first_text_size,
                20f
            )
        ).toFloat()
        mFirstTextColor = array.getColor(
            R.styleable.RoundProgressBar_round_first_text_color,
            Color.RED
        )
        mSecondText =
            array.getString(R.styleable.RoundProgressBar_round_second_text)
        mSecondTextSize = dp2px(
            array.getDimension(
                R.styleable.RoundProgressBar_round_second_text_size,
                20f
            )
        ).toFloat()
        mSecondTextColor = array.getColor(
            R.styleable.RoundProgressBar_round_second_text_color,
            Color.RED
        )
        mMinText = array.getString(R.styleable.RoundProgressBar_round_min_text)
        mMinTextSize = dp2px(
            array.getDimension(
                R.styleable.RoundProgressBar_round_min_text_size,
                20f
            )
        ).toFloat()
        mMinTextColor = array.getColor(
            R.styleable.RoundProgressBar_round_min_text_color,
            Color.RED
        )
        mMaxText = array.getString(R.styleable.RoundProgressBar_round_max_text)
        mMaxTextSize = dp2px(
            array.getDimension(
                R.styleable.RoundProgressBar_round_max_text_size,
                20f
            )
        ).toFloat()
        mMaxTextColor = array.getColor(
            R.styleable.RoundProgressBar_round_max_text_color,
            Color.RED
        )
        mAngleSize =
            array.getFloat(R.styleable.RoundProgressBar_round_angle_size, 270f)
        mStartAngle =
            array.getFloat(R.styleable.RoundProgressBar_round_start_angle, 135f)
        array.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2
        val rectF = RectF()
        rectF.left = mStrokeWidth.toFloat()
        rectF.top = mStrokeWidth.toFloat()
        rectF.right = centerX * 2 - mStrokeWidth.toFloat()
        rectF.bottom = centerX * 2 - mStrokeWidth.toFloat()

        //画最外层的圆弧
        drawArcBg(canvas, rectF)
        //画进度
        drawArcProgress(canvas, rectF)
        //绘制第一级文本
        drawFirstText(canvas, centerX.toFloat())
        //绘制第二级文本
        drawSecondText(canvas, centerX.toFloat())
        //绘制最小值文本
        drawMinText(canvas, rectF.left, rectF.bottom)
        //绘制最大值文本
        drawMaxText(canvas, rectF.right, rectF.bottom)
    }

    /**
     * 画最开始的圆弧
     *
     * @param canvas
     * @param rectF
     */
    private fun drawArcBg(canvas: Canvas, rectF: RectF) {
        val mPaint = Paint()
        //画笔的填充样式，Paint.Style.FILL 填充内部;Paint.Style.FILL_AND_STROKE 填充内部和描边;Paint.Style.STROKE 描边
        mPaint.style = Paint.Style.STROKE
        //圆弧的宽度
        mPaint.strokeWidth = mStrokeWidth.toFloat()
        //抗锯齿
        mPaint.isAntiAlias = true
        //画笔的颜色
        mPaint.color = mArcBgColor
        //画笔的样式 Paint.Cap.Round 圆形,Cap.SQUARE 方形
        mPaint.strokeCap = Paint.Cap.ROUND
        //开始画圆弧
        canvas.drawArc(rectF, mStartAngle, mAngleSize, false, mPaint)
    }

    /**
     * 画进度的圆弧
     *
     * @param canvas
     * @param rectF
     */
    private fun drawArcProgress(canvas: Canvas, rectF: RectF) {
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = mStrokeWidth.toFloat()
        paint.color = mProgressColor
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(rectF, mStartAngle, mCurrentAngleSize, false, paint)
    }

    /**
     * 绘制第一级文字
     *
     * @param canvas  画笔
     * @param centerX 位置
     */
    private fun drawFirstText(canvas: Canvas, centerX: Float) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = mFirstTextColor
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = mFirstTextSize
        val firstTextBounds = Rect()
        paint.getTextBounds(mFirstText, 0, mFirstText!!.length, firstTextBounds)
        canvas.drawText(
            mFirstText!!,
            centerX,
            firstTextBounds.height() / 2 + height * 2 / 5.toFloat(),
            paint
        )
    }

    /**
     * 绘制第二级文本
     *
     * @param canvas  画笔
     * @param centerX 文本
     */
    private fun drawSecondText(
        canvas: Canvas,
        centerX: Float
    ) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = mSecondTextColor
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = mSecondTextSize
        val bounds = Rect()
        paint.getTextBounds(mSecondText, 0, mSecondText!!.length, bounds)
        canvas.drawText(
            mSecondText!!, centerX, height / 2 + bounds.height() / 2 +
                    getFontHeight(mSecondText, mSecondTextSize), paint
        )
    }

    /**
     * 绘制最小值文本
     *
     * @param canvas  画笔
     * @param leftX   X轴位置
     * @param bottomY Y轴位置
     */
    private fun drawMinText(
        canvas: Canvas,
        leftX: Float,
        bottomY: Float
    ) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = mMinTextColor
        paint.textAlign = Paint.Align.CENTER //设置绘制方式 中心对齐
        paint.textSize = mMinTextSize
        val bounds = Rect()
        paint.getTextBounds(mMinText, 0, mMinText!!.length, bounds) //TextView的高度和宽度
        canvas.drawText(mMinText!!, leftX + bounds.width() * 4, bottomY + 16, paint)
    }

    /**
     * 绘制最大值文本
     *
     * @param canvas  画笔
     * @param rightX  X轴位置
     * @param bottomY Y轴位置
     */
    private fun drawMaxText(
        canvas: Canvas,
        rightX: Float,
        bottomY: Float
    ) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = mMaxTextColor
        paint.textAlign = Paint.Align.CENTER //设置绘制方式 中心对齐
        paint.textSize = mMaxTextSize
        val bounds = Rect()
        paint.getTextBounds(mMaxText, 0, mMaxText!!.length, bounds) //TextView的高度和宽度
        canvas.drawText(mMaxText!!, rightX - bounds.width(), bottomY + 16, paint)
    }

    /**
     * 设置最大的进度
     *
     * @param progress
     */
    fun setMaxProgress(progress: Int) {
        require(progress >= 0) { "Progress value can not be less than 0 " }
        mMaxProgress = progress.toFloat()
    }

    /**
     * 设置当前进度
     *
     * @param progress
     */
    fun setProgress(progress: Float) {
        var progress = progress
        require(progress >= 0) { "Progress value can not be less than 0" }
        if (progress > mMaxProgress) {
            progress = mMaxProgress
        }
        mCurrentProgress = progress
        val size = mCurrentProgress / mMaxProgress
        mCurrentAngleSize = (mAngleSize * size) as Float
        setAnimator(0f, mCurrentAngleSize)
    }

    /**
     * 设置进度圆弧的颜色
     *
     * @param color
     */
    fun setProgressColor(color: Int) {
        require(color != 0) { "Color can no be 0" }
        mProgressColor = color
    }

    /**
     * 设置圆弧的颜色
     *
     * @param color
     */
    fun setArcBgColor(color: Int) {
        require(color != 0) { "Color can no be 0" }
        mArcBgColor = color
    }

    /**
     * 设置圆弧的宽度
     *
     * @param strokeWidth
     */
    fun setStrokeWidth(strokeWidth: Int) {
        require(strokeWidth >= 0) { "strokeWidth value can not be less than 0" }
        mStrokeWidth = dp2px(strokeWidth.toFloat())
    }

    /**
     * 设置动画的执行时长
     *
     * @param duration
     */
    fun setAnimatorDuration(duration: Long) {
        require(duration >= 0) { "Duration value can not be less than 0" }
        mDuration = duration
    }

    /**
     * 设置第一行文本
     *
     * @param text
     */
    fun setFirstText(text: String?) {
        mFirstText = text
    }

    /**
     * 设置第一行文本的颜色
     *
     * @param color
     */
    fun setFirstTextColor(color: Int) {
        require(color > 0) { "Color value can not be less than 0" }
        mFirstTextColor = color
    }

    /**
     * 设置第一行文本的大小
     *
     * @param textSize
     */
    fun setFirstTextSize(textSize: Float) {
        require(textSize > 0) { "textSize can not be less than 0" }
        mFirstTextSize = textSize
    }

    /**
     * 设置第二行文本
     *
     * @param text
     */
    fun setSecondText(text: String?) {
        mSecondText = text
    }

    /**
     * 设置第二行文本的颜色
     *
     * @param color
     */
    fun setSecondTextColor(color: Int) {
        require(color != 0) { "Color value can not be less than 0" }
        mSecondTextColor = color
    }

    /**
     * 设置第二行文本的大小
     *
     * @param textSize
     */
    fun setSecondTextSize(textSize: Float) {
        require(textSize > 0) { "textSize can not be less than 0" }
        mSecondTextSize = textSize
    }

    /**
     * 设置最小值文本
     *
     * @param text
     */
    fun setMinText(text: String?) {
        mMinText = text
    }

    /**
     * 设置最小值文本的颜色
     *
     * @param color
     */
    fun setMinTextColor(color: Int) {
        require(color != 0) { "Color value can not be less than 0" }
        mMinTextColor = color
    }

    /**
     * 设置最小值文本的大小
     *
     * @param textSize
     */
    fun setMinTextSize(textSize: Float) {
        require(textSize > 0) { "textSize can not be less than 0" }
        mMinTextSize = textSize
    }

    /**
     * 设置最大值文本
     *
     * @param text
     */
    fun setMaxText(text: String?) {
        mMaxText = text
    }

    /**
     * 设置最大值文本的颜色
     *
     * @param color
     */
    fun setMaxTextColor(color: Int) {
        require(color != 0) { "Color value can not be less than 0" }
        mMaxTextColor = color
    }

    /**
     * 设置最大值文本的大小
     *
     * @param textSize
     */
    fun setMaxTextSize(textSize: Float) {
        require(textSize > 0) { "textSize can not be less than 0" }
        mMaxTextSize = textSize
    }

    /**
     * 设置圆弧开始的角度
     *
     * @param startAngle
     */
    fun setStartAngle(startAngle: Int) {
        mStartAngle = startAngle.toFloat()
    }

    /**
     * 设置圆弧的起始角度到终点角度的大小
     *
     * @param angleSize
     */
    fun setAngleSize(angleSize: Int) {
        mAngleSize = angleSize.toFloat()
    }

    /**
     * dp转成px
     *
     * @param dp
     * @return
     */
    private fun dp2px(dp: Float): Int {
        val density = resources.displayMetrics.density
        return (dp * density + 0.5f * if (dp >= 0) 1 else -1).toInt()
    }

    /**
     * 设置动画
     *
     * @param start  开始位置
     * @param target 结束位置
     */
    private fun setAnimator(start: Float, target: Float) {
        val valueAnimator = ValueAnimator.ofFloat(start, target)
        valueAnimator.duration = mDuration
        valueAnimator.setTarget(mCurrentAngleSize)
        valueAnimator.addUpdateListener { valueAnimator ->
            mCurrentAngleSize = valueAnimator.animatedValue as Float
            invalidate()
        }
        valueAnimator.start()
    }

    /**
     * 测量字体的高度
     *
     * @param textStr
     * @param fontSize
     * @return
     */
    private fun getFontHeight(textStr: String?, fontSize: Float): Float {
        val paint = Paint()
        paint.textSize = fontSize
        val bounds = Rect()
        paint.getTextBounds(textStr, 0, textStr!!.length, bounds)
        return bounds.height().toFloat()
    }
}