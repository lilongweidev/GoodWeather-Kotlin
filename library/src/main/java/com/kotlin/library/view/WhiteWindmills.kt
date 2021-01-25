package com.kotlin.library.view

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import com.kotlin.library.R
import java.lang.ref.WeakReference

/**
 * 白色风车
 */
class WhiteWindmills @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    /**
     * 叶片的长度
     */
    private var mBladeRadius = 0f

    /**
     * 风车叶片旋转中心x
     */
    private var mCenterY = 0

    /**
     * 风车叶片旋转中心y
     */
    private var mCenterX = 0

    /**
     * 风车旋转中心点圆的半径
     */
    private var mPivotRadius = 0f

    /**
     * 画笔
     */
    private val mPaint = Paint()

    /**
     * 风车旋转时叶片偏移的角度
     */
    private var mOffsetAngle = 0

    /**
     * 路径
     */
    private val mPath = Path()

    /**
     * 风车支柱顶部和底部为了画椭圆的矩形
     */
    private val mRect = RectF()

    /**
     * 控件的宽
     */
    private var mWidth = 0

    /**
     * 控件高
     */
    private var mHeight = 0

    /**
     * 控件颜色
     */
    private var mColor = 0
    private val mHandler = MsgHandler(this)

    private fun initView(context: Context, attrs: AttributeSet?) {
        val array =
            context.obtainStyledAttributes(attrs, R.styleable.WhiteWindmills)
        if (array != null) {
            mColor = array.getColor(R.styleable.WhiteWindmills_windColor, Color.WHITE)
            array.recycle()
        }
        //抗锯齿
        mPaint.isAntiAlias = true
        mPaint.color = mColor
    }

    /**
     * 测量
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightMeasure = MeasureSpec.getSize(heightMeasureSpec)
        val widthMeasure = MeasureSpec.getSize(widthMeasureSpec)
        mWidth = widthMeasure
        mHeight = heightMeasure
        mCenterY = mWidth / 2
        mCenterX = mWidth / 2
        mPivotRadius = mWidth.toFloat() / 40.toFloat()
        mBladeRadius = mCenterY - 2 * mPivotRadius
    }

    /**
     * 绘制
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //画扇叶旋转的中心
        drawPivot(canvas)

        //画扇叶
        drawWindBlade(canvas)

        //画底部支柱
        drawPillar(canvas)
    }

    /**
     * 画风车支点
     *
     * @param canvas
     */
    private fun drawPivot(canvas: Canvas) {
        mPaint.style = Paint.Style.FILL
        canvas.drawCircle(mCenterX.toFloat(), mCenterY.toFloat(), mPivotRadius, mPaint)
    }

    /**
     * 画叶片
     *
     * @param canvas
     */
    private fun drawWindBlade(canvas: Canvas) {
        canvas.save()
        mPath.reset()
        //根据偏移量画初始时画布的位置
        canvas.rotate(mOffsetAngle.toFloat(), mCenterX.toFloat(), mCenterY.toFloat())
        //画三角形扇叶
        mPath.moveTo(mCenterX.toFloat(), mCenterY - mPivotRadius) // 此点为多边形的起点
        mPath.lineTo(mCenterX.toFloat(), mCenterY - mPivotRadius - mBladeRadius)
        mPath.lineTo(
            mCenterX + mPivotRadius,
            mPivotRadius + mBladeRadius * 2.toFloat() / 3.toFloat()
        )
        mPath.close() // 使这些点构成封闭的多边形
        canvas.drawPath(mPath, mPaint)

        //旋转画布120度，画第二个扇叶
        canvas.rotate(120f, mCenterX.toFloat(), mCenterY.toFloat())
        canvas.drawPath(mPath, mPaint)

        //旋转画布120度，画第三个扇叶
        canvas.rotate(120f, mCenterX.toFloat(), mCenterY.toFloat())
        canvas.drawPath(mPath, mPaint)
        canvas.restore()
    }

    /**
     * 画支柱
     *
     * @param canvas
     */
    private fun drawPillar(canvas: Canvas) {
        mPath.reset()
        //画上下半圆之间的柱形
        mPath.moveTo(mCenterX - mPivotRadius / 2, mCenterY + mPivotRadius + mPivotRadius / 2)
        mPath.lineTo(mCenterX + mPivotRadius / 2, mCenterY + mPivotRadius + mPivotRadius / 2)
        mPath.lineTo(mCenterX + mPivotRadius, mHeight - 2 * mPivotRadius)
        mPath.lineTo(mCenterX - mPivotRadius, mHeight - 2 * mPivotRadius)
        mPath.close()

        //画顶部半圆
        mRect[mCenterX - mPivotRadius / 2, mCenterY + mPivotRadius, mCenterX + mPivotRadius / 2] =
            mCenterY + 2 * mPivotRadius
        mPath.addArc(mRect, 180f, 180f)
        //画底部半圆
        mRect[mCenterX - mPivotRadius, mHeight - 3 * mPivotRadius, mCenterX + mPivotRadius] =
            mHeight - mPivotRadius
        mPath.addArc(mRect, 0f, 180f)
        canvas.drawPath(mPath, mPaint)
    }

    /**
     * 开始旋转
     */
    fun startRotate() {
        stop()
        mHandler.sendEmptyMessageDelayed(0, 10)
    }

    /**
     * 停止旋转
     */
    fun stop() {
        mHandler.removeMessages(0)
    }

    internal class MsgHandler(view: WhiteWindmills) : Handler() {
        private val mView: WeakReference<WhiteWindmills> = WeakReference(view)
        override fun handleMessage(msg: Message) {
            val view = mView.get()
            view?.handleMessage(msg)
        }

    }

    private fun handleMessage(msg: Message) {
        mOffsetAngle = if (mOffsetAngle in 0..359) mOffsetAngle + 1 else 1
        invalidate()
        startRotate()
    }

    init {
        initView(context, attrs)
    }
}