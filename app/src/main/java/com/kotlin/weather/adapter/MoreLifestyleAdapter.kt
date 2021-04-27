package com.kotlin.weather.adapter

import android.widget.ProgressBar
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.weather.R
import com.kotlin.weather.model.Daily

/**
 * 更多生活指数适配器
 *
 * @author llw
 * @date 2021/4/27 10:35
 */
class MoreLifestyleAdapter(layoutResId: Int, data: List<Daily>) :
    BaseQuickAdapter<Daily, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Daily) {
        helper.setText(R.id.tv_name, item.name) //名称
            .setText(R.id.tv_content, "生活建议：${item.text}")//内容
        val progressBar = helper.getView(R.id.progressBar) as ProgressBar
        //配置进度条
        progressBar.apply {
            //根据不同的类型设置不同的最大进度
            max = when (item.type) {
                "1", "4" ->  3
                "2", "9", "11" ->  4
                "5", "6", "7", "10", "12", "15", "16" ->  5
                "14" ->  6
                "3", "8" ->  7
                "13" ->  8
                else ->  0
            }
            //当前等级，设置进度
            progress = item.level.toInt()
        }
    }
}