package com.kotlin.weather.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.library.util.DateUtils
import com.kotlin.weather.R
import com.kotlin.weather.model.MoreAirFiveResponse

/**
 * 5天空气质量预报适配器
 *
 * @author llw
 * @date 2021/4/27 9:49
 */
class MoreAirFiveAdapter(layoutResId: Int, data: List<MoreAirFiveResponse.DailyBean>) :
    BaseQuickAdapter<MoreAirFiveResponse.DailyBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MoreAirFiveResponse.DailyBean) {

        helper.setText(R.id.tv_date_info, DateUtils.week(item.fxDate)) //日期描述
            .setText(R.id.tv_date, DateUtils.dateSplit(item.fxDate)) //日期
            .setText(R.id.tv_aqi, item.aqi) //空气质量指数
            .setText(R.id.tv_category, item.category) //空气质量描述
            .setText(R.id.tv_primary, if (item.primary == "NA") "无污染" else item.primary) //污染物
    }
}