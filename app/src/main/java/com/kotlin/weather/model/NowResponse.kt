package com.kotlin.weather.model

/**
 * 实时天气数据返回
 * @author llw
 */
data class NowResponse(
    var code: String,
    var updateTime: String,
    var fxLink: String,
    var now: NowBean
)

data class NowBean(
    val obsTime: String,
    val temp: String,
    val feelsLike: String,
    val icon: String,
    val dew: String,
    val cloud: String,
    val text: String,
    val wind360: String,
    val windDir: String,
    val windScale: String,
    val windSpeed: String,
    val humidity: String,
    val precip: String,
    val pressure: String,
    val vis: String
)


