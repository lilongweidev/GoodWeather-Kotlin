package com.kotlin.weather.model

/**
 * 更多空气质量数据实体
 *
 * @author llw
 */
data class MoreAirFiveResponse(
    val code: String, var updateTime: String, var daily: List<DailyBean>
) {

    data class DailyBean(
        val fxDate: String,//日期
        val aqi: String,//空气质量
        val level: String,//等级
        val category: String,//描述
        val primary: String//基本
    )
}