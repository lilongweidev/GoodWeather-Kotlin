package com.kotlin.weather.adapter


import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.library.util.DateUtils.updateTime
import com.kotlin.library.util.WeatherUtil
import com.kotlin.weather.R
import com.kotlin.weather.model.MinutelyBean

/**
 * 分钟级降水列表适配器
 * @author llw
 */
class MinutePrecAdapter(layoutResId: Int, data: List<MinutelyBean>) : BaseQuickAdapter<MinutelyBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: MinutelyBean) {
        val time = updateTime(item.fxTime)

        //时间
        helper.setText(R.id.tv_time, WeatherUtil.showTimeInfo(time) + time)
        helper.setText(R.id.tv_precip_info, item.precip + "   " + if ("rain" == item.type) "雨" else "雪")
    }
}