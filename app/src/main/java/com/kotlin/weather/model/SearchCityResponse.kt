package com.kotlin.weather.model

/**
 * 搜索城市数据返回
 * @author llw
 */
data class SearchCityResponse(val code: String, val location: List<LocationBean>)

data class LocationBean(
    val name: String,//区/县
    val id: String,//城市Id
    val lat: String,//纬度
    val lon: String,//经度
    val adm2: String,//当前区/县的上一级  市
    val adm1: String,//当前市的上一级  省
    val country: String,//国家
    val tz: String,//地区/城市所在时区
    val type: String,//该地区/城市的属性
    val rank: String//地区评分
)
