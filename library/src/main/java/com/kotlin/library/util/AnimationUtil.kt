package com.kotlin.library.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.widget.TextView
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

/**
 * 动画工具类
 * UpdateListener： 动画过程中通过添加此监听来回调数据
 * EndListener： 动画结束的时候通过此监听器来做一些处理
 * @author llw
 */
class AnimationUtil {
    private lateinit var valueAnimator: ValueAnimator
    private var updateListener: UpdateListener? = null
    private var endListener: EndListener? = null
    private var duration: Long = 1000
    private var start = 0.0f
    private var end = 1.0f
    private var interpolator: Interpolator = LinearInterpolator()
    fun setDuration(timeLength: Int) {
        duration = timeLength.toLong()
    }

    fun setValueAnimator(
        start: Float,
        end: Float,
        duration: Long
    ) {
        this.start = start
        this.end = end
        this.duration = duration
    }

    fun setInterpolator(interpolator: Interpolator) {
        this.interpolator = interpolator
    }

    fun startAnimator() {
        valueAnimator = ValueAnimator.ofFloat(start, end)
        valueAnimator.duration = duration
        valueAnimator.interpolator = interpolator
        valueAnimator.addUpdateListener(AnimatorUpdateListener { valueAnimator ->
            if (updateListener == null) {
                return@AnimatorUpdateListener
            }
            val cur = valueAnimator.animatedValue as Float
            updateListener!!.progress(cur)
        })
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                if (endListener == null) {
                    return
                }
                endListener!!.endUpdate(animator)
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        valueAnimator.start()
    }

    fun addUpdateListener(updateListener: UpdateListener?) {
        this.updateListener = updateListener
    }

    fun addEndListner(endListener: EndListener?) {
        this.endListener = endListener
    }

    interface EndListener {
        fun endUpdate(animator: Animator?)
    }

    interface UpdateListener {
        fun progress(progress: Float)
    }


   companion object {
        fun expand(view: View, textView: TextView) {
            //视图测量 传入容器的宽高测量模式
            view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            //获取视图的测量高度
            val viewHeight = view.measuredHeight
            //设置布局参数高度
            view.layoutParams.height = 0
            //视图显示
            view.visibility = View.VISIBLE
            textView.text = "收起详情"
            val animation: Animation =
                object : Animation() {
                    /**
                     * 重写动画更新函数
                     * @param interpolatedTime 补插时间 计算动画进度
                     * @param t
                     */
                    override fun applyTransformation(
                        interpolatedTime: Float,
                        t: Transformation
                    ) {
                        if (interpolatedTime == 1f) {
                            //动画已完成
                            view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        } else {
                            //正在进行中
                            view.layoutParams.height = (viewHeight * interpolatedTime).toInt()
                        }
                        view.requestLayout()
                    }
                }
            animation.duration = 600
            //设置插值器，即动画改变速度
            animation.interpolator = LinearOutSlowInInterpolator()
            view.startAnimation(animation)
        }

        /**
         * 收缩动画
         * @param view 需要收缩的View
         * @param textView 修改文本
         */
        fun collapse(view: View, textView: TextView) {
            view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val viewHeight = view.measuredHeight
            val animation: Animation =
                object : Animation() {
                    override fun applyTransformation(
                        interpolatedTime: Float,
                        t: Transformation
                    ) {
                        if (interpolatedTime == 1f) {
                            view.visibility = View.GONE
                            textView.text = "查看详情"
                        } else {
                            view.layoutParams.height =
                                viewHeight - (viewHeight * interpolatedTime).toInt()
                            view.requestLayout()
                        }
                    }
                }
            animation.duration = 600
            animation.interpolator = LinearOutSlowInInterpolator()
            view.startAnimation(animation)
        }
    }


    init {
        //默认动画时常1s
        interpolator = LinearInterpolator() // 匀速的插值器
    }
}