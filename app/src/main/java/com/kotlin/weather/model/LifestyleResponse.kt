package com.kotlin.weather.model

/**
 * 生活指数
 * @author llw
 */
data class LifestyleResponse(
    val code: String,//状态码 200
    val updateTime: String,//当前API更新时间
    val fxLink: String,
    val daily: List<Daily>
)


data class Daily(
    val date: String,//预报日期
    val type: String,//生活指数预报类型
    val name: String,//生活指数预报类型的名称
    val level: String,//生活指数预报等级
    val category: String,//生活指数预报级别名称
    val text: String//生活指数预报的详细描述，可能为空
)

data class RequestLifestyleBean(var type:String, var cityId :String)





