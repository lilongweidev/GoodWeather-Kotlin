package com.kotlin.weather.model

/**
 * 分钟级降水
 * @author llw
 */
data class MinutePrecResponse(
    val code: String,//状态码 200
    val updateTime: String,//当前API更新时间
    val fxLink: String,
    val summary: String,//分钟降水描述
    val minutely: List<MinutelyBean>
)

data class MinutelyBean(
    val fxTime: String,//预报时间
    val precip: String,//降水量
    val type: String//降水类型 rain雨 snow雪
)
