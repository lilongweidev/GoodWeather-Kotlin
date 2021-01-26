package com.kotlin.weather.model

/**
 * 实时天气数据返回
 * @author llw
 */
data class NowResponse(
    val code: String,//状态码 200
    val updateTime: String,//当前API更新时间
    var fxLink: String,
    var now: NowBean
)

data class NowBean(
    val obsTime: String,//实况观测时间
    val temp: String,//实况温度
    val feelsLike: String,//实况体感温度
    val icon: String,//当前天气状况和图标的代码
    val dew: String,//实况露点温度
    val cloud: String,//实况云量，百分比数值
    val text: String,//实况天气状况的文字描述
    val wind360: String,//实况风向360角度
    val windDir: String,//实况风向
    val windScale: String,//实况风力等级
    val windSpeed: String,//实况风速，公里/小时
    val humidity: String,//实况相对湿度，百分比数值
    val precip: String,//实况降水量，默认单位：毫米
    val pressure: String,//实况大气压强，默认单位：百帕
    val vis: String//实况能见度，默认单位：公里
)


