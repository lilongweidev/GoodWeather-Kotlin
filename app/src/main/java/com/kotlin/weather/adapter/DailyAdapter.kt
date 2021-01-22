package com.kotlin.weather.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.library.util.DateUtils
import com.kotlin.library.util.WeatherUtil
import com.kotlin.weather.R
import com.kotlin.weather.model.DailyBean

/**
 * 天气预报数据列表适配器
 * @author llw
 */
class DailyAdapter(layoutResId: Int, data: MutableList<DailyBean>) :
    BaseQuickAdapter<DailyBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DailyBean) {
        //日期
        helper.setText(R.id.tv_date,DateUtils.dateSplitPlus(item.fxDate)+DateUtils.Week(item.fxDate))
            .setText(R.id.tv_temp_height, item.tempMax + "℃")//最高温
            .setText(R.id.tv_temp_low, " / " + item.tempMin + "℃")//最低温
        //天气状态图片
        val weatherStateIcon = helper.getView(R.id.iv_weather_state) as ImageView
        //获取天气状态码，根据状态码来显示图标
        val code: Int = item.iconDay.toInt()
        //调用工具类中写好的方法
        WeatherUtil.changeIcon(weatherStateIcon, code)
        //绑定点击事件的id
        helper.addOnClickListener(R.id.item_forecast)
    }
}