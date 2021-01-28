package com.kotlin.library.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.kotlin.library.R
import java.lang.ref.SoftReference

/**
 * 加载框
 * @author llw
 */
class LoadingView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context!!, attrs, defStyleAttr) {
    private var mCenterRotateX = 0 //图片旋转点x = 0
    private var mCenterRotateY = 0 //图片旋转点y = 0
    private var mRunnable: LoadingRunnable? = null
    private fun init() {
        scaleType = ScaleType.MATRIX
        val bitmap =
            BitmapFactory.decodeResource(resources, R.drawable.icon_loading)
        setImageBitmap(bitmap)
        mCenterRotateX = bitmap.width / 2
        mCenterRotateY = bitmap.height / 2
    }

    /**
     * onDraw()之前调用
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mRunnable == null) {
            mRunnable = LoadingRunnable(this)
        }
        if (!mRunnable!!.isLoading) {
            mRunnable!!.start()
        }
    }

    /**
     * view销毁时调用
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mRunnable != null) {
            mRunnable!!.stop()
        }
        mRunnable = null
    }

    internal inner class LoadingRunnable(loadingView: LoadingView) :
        Runnable {
        var isLoading = false
        private val mMatrix: Matrix?
        private val mLoadingViewSoftReference: SoftReference<LoadingView> = SoftReference(loadingView)
        private var mDegrees = 0f
        override fun run() {
            if (mLoadingViewSoftReference.get()!!.mRunnable != null && mMatrix != null) {
                mDegrees += 30f
                mMatrix.setRotate(mDegrees, mCenterRotateX.toFloat(), mCenterRotateY.toFloat())
                mLoadingViewSoftReference.get()!!.imageMatrix = mMatrix
                if (mDegrees == 360f) {
                    mDegrees = 0f
                }
                if (isLoading) {
                    mLoadingViewSoftReference.get()!!.postDelayed(mLoadingViewSoftReference.get()!!.mRunnable, 100)
                }
            }
        }

        fun stop() {
            isLoading = false
        }

        fun start() {
            isLoading = true
            if (mLoadingViewSoftReference.get()!!.mRunnable != null && mMatrix != null) {
                mLoadingViewSoftReference.get()!!
                    .postDelayed(mLoadingViewSoftReference.get()!!.mRunnable, 100)
            }
        }

        init {
            mMatrix = Matrix()
        }
    }

    init {
        init()
    }
}