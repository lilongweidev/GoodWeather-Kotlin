package com.kotlin.weather.model

/**
 * 壁纸列表返回实体
 *
 * @author llw
 * @date 2021/2/19 17:55
 */
data class WallPaperResponse(val msg: String, val res: ResBean, val code:Int)

data class ResBean(val vertical: List<VerticalBean>)

data class VerticalBean(
    val preview: String,//预览
    val thumb: String,//小图
    val img: String,
    val id: String,
    val desc:String
)

