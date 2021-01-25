package com.kotlin.weather.adapter

import HourlyBean
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.library.util.DateUtils.updateTime
import com.kotlin.library.util.WeatherUtil.changeIcon
import com.kotlin.library.util.WeatherUtil.showTimeInfo
import com.kotlin.weather.R

/**
 * V7 API 逐小时预报数据列表适配器
 *
 * @author llw
 */
class HourlyAdapter(layoutResId: Int, data: List<HourlyBean>?) :
    BaseQuickAdapter<HourlyBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: HourlyBean) {
        val time = updateTime(item.fxTime)
        //时间
        helper.setText(R.id.tv_time, showTimeInfo(time) + time)
            //温度
            .setText(R.id.tv_temperature, "${item.temp}℃")

        //天气状态图片
        val weatherStateIcon =
            helper.getView<ImageView>(R.id.iv_weather_state)
        //获取天气状态码，根据状态码来显示图标
        val code: Int = item.icon.toInt()
        changeIcon(weatherStateIcon, code)
        helper.addOnClickListener(R.id.item_hourly)
    }
}