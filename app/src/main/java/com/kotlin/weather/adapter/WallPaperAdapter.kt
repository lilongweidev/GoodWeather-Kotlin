package com.kotlin.weather.adapter

import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.kotlin.weather.R
import com.kotlin.weather.model.VerticalBean

/**
 * 壁纸列表适配器
 *
 * @author llw
 * @date 2021/2/19 11:39
 */
class WallPaperAdapter(layoutResId: Int, data: List<VerticalBean>, private var mHeightList: List<Int>) :
    BaseQuickAdapter<VerticalBean, BaseViewHolder>(layoutResId, data) {

    /**
     * 头部广告
     */
    private val Top = "top"

    /**
     * 底部广告
     */
    private val Bottom = "bottom"

    override fun convert(helper: BaseViewHolder, item: VerticalBean) {
        //获取控件
        val imageView = helper.getView(R.id.iv_wallpaper) as ShapeableImageView
        //获取控件的LayoutParams
        val layoutParams = imageView.layoutParams as RelativeLayout.LayoutParams
        layoutParams.height = dip2px(mHeightList[helper.adapterPosition].toFloat())
        //重新设置ImageView的高度
        imageView.layoutParams = layoutParams
        //显示图片的内容
        when {
            Top == item.desc -> { imageView.setImageResource(R.mipmap.icon_top_wallpaper) }
            Bottom == item.desc -> { imageView.setImageResource(R.mipmap.icon_bottom_wallpaper) }
            else -> { Glide.with(mContext).load(item.img).into(imageView) }
        }
        helper.addOnClickListener(R.id.item_wallpaper)
    }

    // dp 转成 px
    private fun dip2px(dpVale: Float): Int {
        val scale = mContext.resources.displayMetrics.density
        return (dpVale * scale + 0.5f).toInt()
    }

}