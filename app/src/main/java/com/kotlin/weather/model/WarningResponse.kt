package com.kotlin.weather.model

/**
 * 灾害预警返回实体
 *
 * @author llw
 */
data class WarningResponse(
    val code: String,
    val updateTime: String,
    val fxLink: String,
    val warning: List<WarningBean>
)

data class WarningBean(
    val id: String,
    val related: String,
    val text: String,
    val typeName: String,
    val type: String,
    val level: String,
    val status: String,
    val endTime: String,
    val startTime: String,
    val title: String,
    val pubTime: String,
    val sender: String
)
