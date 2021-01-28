package com.kotlin.library.view

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * 颜色波浪TextView
 * @author llw
 */
class LoadingTextView(
    context: Context?,
    attrs: AttributeSet?
) : AppCompatTextView(context!!, attrs) {
    private var mLinearGradient: LinearGradient? = null
    private var mGradientMatrix: Matrix? = null
    private var mPaint: Paint? = null
    private var mViewWidth = 0
    private var mTranslate = 0
    private val mAnimating = true
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mViewWidth == 0) {
            mViewWidth = measuredWidth
            if (mViewWidth > 0) {
                mPaint = paint
                mLinearGradient = LinearGradient(
                    -mViewWidth.toFloat(),
                    0f,
                    0f,
                    0f,
                    intArrayOf(0x33ffffff, -0xcd7913, 0x33ffffff),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
                (mPaint as TextPaint?)?.shader = mLinearGradient
                mGradientMatrix = Matrix()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mAnimating && mGradientMatrix != null) {
            mTranslate += mViewWidth / 10
            if (mTranslate > 2 * mViewWidth) {
                mTranslate = -mViewWidth
            }
            mGradientMatrix!!.setTranslate(mTranslate.toFloat(), 0f)
            mLinearGradient!!.setLocalMatrix(mGradientMatrix)
            postInvalidateDelayed(20)
        }
    }
}