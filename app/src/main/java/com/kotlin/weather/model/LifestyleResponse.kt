package com.kotlin.weather.model

/**
 * 生活指数
 * @author llw
 */
data class LifestyleResponse(
    val code: String,
    val updateTime: String,
    val fxLink: String,
    val daily: List<Daily>
)


data class Daily(
    val date: String,
    val type: String,
    val name: String,
    val level: String,
    val category: String,
    val text: String
)

data class RequestLifestyleBean(var type:String, var cityId :String)





