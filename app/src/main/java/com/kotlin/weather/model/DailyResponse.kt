package com.kotlin.weather.model

/**
 * 天气预报数据实体
 * @author llw
 */
data class DailyResponse(
    val code: String,//状态码 200
    val updateTime: String,//当前API更新时间
    val fxLink: String,
    val daily: List<DailyBean>
)

data class DailyBean(
    val fxDate: String,//预报日期
    val sunrise: String,//日出时间
    val sunset: String,//日落时间
    val moonrise: String,//月升时间
    val moonset: String,//月落时间
    val moonPhase: String,//月相名称
    val tempMax: String,//预报当天最高温度
    val tempMin: String,//预报当天最低温度
    val iconDay: String,//预报白天天气状况的图标代码
    val textDay: String,//预报白天天气状况文字描述
    val iconNight: String,//预报夜间天气状况的图标代码
    val textNight: String,//预报晚间天气状况文字描述
    val wind360Day: String,//预报白天风向360角度
    val windDirDay: String,//预报白天风向
    val windScaleDay: String,//预报白天风力等级
    val windSpeedDay: String,//预报白天风速，公里/小时
    val wind360Night: String,//预报夜间风向360角度
    val windDirNight: String,//预报夜间当天风向
    val windScaleNight: String,//预报夜间风力等级
    val windSpeedNight: String,//预报夜间风速，公里/小时
    val humidity: String,//预报当天相对湿度，百分比数值
    val precip: String,//预报当天降水量，默认单位：毫米
    val pressure: String,//预报当天大气压强，默认单位：百帕
    val vis: String,//预报当天能见度，默认单位：公里
    val cloud: String,//预报当天云量，百分比数值
    val uvIndex: String//预报当天紫外线强度指数
)

data class RequestDailyBean(var type:String, var cityId :String)
