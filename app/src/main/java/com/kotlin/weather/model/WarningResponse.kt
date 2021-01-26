package com.kotlin.weather.model

/**
 * 灾害预警返回实体
 *
 * @author llw
 */
data class WarningResponse(
    val code: String,//状态码 200
    val updateTime: String,//当前API更新时间
    val fxLink: String,
    val warning: List<WarningBean>
)

data class WarningBean(
    val id: String,//本条预警的唯一标识，可判断本条预警是否已经存在，id有效期不超过72小时
    val related: String,//与本条预警相关联的预警ID
    val text: String,//预警详细文字描述
    val typeName: String,//预警等级名称
    val type: String,//预警类型
    val level: String,//预警等级
    val status: String,//预警状态  active 预警中或首次预警 update 预警信息更新 cancel 取消预警
    val endTime: String,//预警结束时间
    val startTime: String,//预警开始时间
    val title: String,//预警信息标题
    val pubTime: String,//预警发布时间
    val sender: String//预警发布单位
)
