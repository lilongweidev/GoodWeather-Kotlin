package com.kotlin.weather.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.library.util.DateUtils
import com.kotlin.library.util.WeatherUtil
import com.kotlin.weather.R
import com.kotlin.weather.model.DailyBean

/**
 * 更多天气预报信息数据适配器
 *
 * @author llw
 * @date 2021/2/25 10:55
 */
class MoreDailyAdapter(layoutResId: Int, data: List<DailyBean>) :
    BaseQuickAdapter<DailyBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: DailyBean) {
        helper.setText(R.id.tv_temp_max, "${item.tempMax}°") //最高温
            .setText(R.id.tv_temp_min, "${item.tempMin}°") //最低温
            .setText(R.id.tv_date_info, DateUtils.week(item.fxDate)) //日期描述
            .setText(R.id.tv_date, DateUtils.dateSplit(item.fxDate))//日期
            .setText(R.id.tv_weather_state_d, item.textDay) //白天天气状况文字描述
            .setText(R.id.tv_weather_state_n, item.textNight)//晚间天气状况文字描述
            .setText(R.id.tv_wind_360_d, "${item.wind360Day}°") //白天风力信息
            .setText(R.id.tv_wind_dir_d, item.windDirDay)
            .setText(R.id.tv_wind_scale_d, "${item.windScaleDay}级")
            .setText(R.id.tv_wind_speed_d, "${item.windSpeedDay}km/h") //晚上风力信息
            .setText(R.id.tv_wind_360_n, "${item.wind360Night}°")
            .setText(R.id.tv_wind_dir_n, item.windDirNight)
            .setText(R.id.tv_wind_scale_n, "${item.windScaleNight}级")
            .setText(R.id.tv_wind_speed_n, "${item.windSpeedNight}km/h") //云量
            .setText(R.id.tv_cloud, "${item.cloud}%") //紫外线
            .setText(R.id.tv_uvIndex, uvIndexToString(item.uvIndex)) //能见度
            .setText(R.id.tv_vis, "${item.vis}km") //降水量
            .setText(R.id.tv_precip, "${item.precip}mm") //相对湿度
            .setText(R.id.tv_humidity, "${item.humidity}%") //大气压强
            .setText(R.id.tv_pressure, "${item.pressure}hPa")

        //白天天气状态图片描述
        WeatherUtil.changeIcon(helper.getView(R.id.iv_weather_state_d), item.iconDay.toInt())
        //晚上天气状态图片描述
        WeatherUtil.changeIcon(helper.getView(R.id.iv_weather_state_n), item.iconNight.toInt())
    }

    //最弱(1)、弱(2)、中等(3)、强(4)、很强(5)
    private fun uvIndexToString(code: String): String? = when (code) {
        "1" -> "最弱"
        "2" -> "弱"
        "3" -> "中等"
        "4" -> "强"
        "5" -> "很强"
        else -> "无紫外线"
    }
}