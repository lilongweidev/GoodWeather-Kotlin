package com.kotlin.weather.model

/**
 * 必应每日一图数据实体
 *
 * @author llw
 * @date 2021/2/19 18:01
 */
data class BiYingImgResponse(val images: List<ImagesBean>)

data class ImagesBean(val url: String,//图片URL
                      val urlbase: String,//基础URL
                      val copyright: String,//所属
                      val copyrightlink: String,//所属链接
                      val title: String//标题
)