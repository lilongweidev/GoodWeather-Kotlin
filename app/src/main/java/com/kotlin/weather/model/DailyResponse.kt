package com.kotlin.weather.model

/**
 * 天气预报数据实体
 * @author llw
 */
data class DailyResponse(
    val code: String,
    val updateTime: String,
    val fxLink: String,
    val daily: List<DailyBean>
)

data class DailyBean(
    val fxDate: String,
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val moonPhase: String,
    val tempMax: String,
    val tempMin: String,
    val iconDay: String,
    val textDay: String,
    val iconNight: String,
    val textNight: String,
    val wind360Day: String,
    val windDirDay: String,
    val windScaleDay: String,
    val windSpeedDay: String,
    val wind360Night: String,
    val windDirNight: String,
    val windScaleNight: String,
    val windSpeedNight: String,
    val humidity: String,
    val precip: String,
    val pressure: String,
    val vis: String,
    val cloud: String,
    val uvIndex: String
)

data class RequestDailyBean(var type:String, var cityId :String)
