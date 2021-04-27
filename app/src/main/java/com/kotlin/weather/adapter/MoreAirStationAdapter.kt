package com.kotlin.weather.adapter

import StationBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.weather.R

/**
 * 更多空气质量监测站列表适配器
 *
 * @author llw
 * @date 2021/4/27 9:06
 */
class MoreAirStationAdapter(layoutResId: Int, data: List<StationBean>) :
    BaseQuickAdapter<StationBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: StationBean) {
        helper.setText(R.id.tv_station_name, item.name) //监测站名称
            .setText(R.id.tv_air_category, item.category) //空气质量
            .setText(R.id.tv_aqi, item.aqi) //空气质量指数
            .setText(R.id.tv_primary, if (item.primary == "NA") "无污染" else item.primary) //污染物
            .setText(R.id.tv_pm10, item.pm10) //pm10
            .setText(R.id.tv_pm25, item.pm2p5) //pm2.5
            .setText(R.id.tv_no2, item.no2) //二氧化氮
            .setText(R.id.tv_so2, item.so2) //二氧化硫
            .setText(R.id.tv_o3, item.o3) //臭氧
            .setText(R.id.tv_co, item.co) //一氧化碳
    }
}