package com.kotlin.library.util

import android.content.Context
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.library.R
import com.kotlin.library.util.Constant.BOTTOM
import com.kotlin.library.util.Constant.LEFT
import com.kotlin.library.util.Constant.RIGHT
import com.kotlin.library.util.Constant.TOP

fun RecyclerView.showAnimation(direction:Int){
    val context: Context = context


    val controller =
        AnimationUtils.loadLayoutAnimation(context, when(direction){
            TOP -> R.anim.layout_animation_from_top
            RIGHT -> R.anim.layout_animation_slide_right
            BOTTOM -> R.anim.layout_animation_from_bottom
            LEFT -> R.anim.layout_animation_slide_left
            else -> R.anim.layout_animation_slide_right
        })
    layoutAnimation = controller
    adapter!!.notifyDataSetChanged()
    scheduleLayoutAnimation()
}