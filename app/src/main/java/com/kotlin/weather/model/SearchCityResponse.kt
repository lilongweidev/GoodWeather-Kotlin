package com.kotlin.weather.model

/**
 * 搜索城市数据返回
 * @author llw
 */
data class SearchCityResponse(val code: String, val location: List<LocationBean>)

data class LocationBean(
    /**
     * name : 南山
     * id : 101280604
     * lat : 22.53122
     * lon : 113.92942
     * adm2 : 深圳
     * adm1 : 广东省
     * country : 中国
     * tz : Asia/Shanghai
     * type : city
     * rank : 25
     */
    val name: String,
    val id: String,
    val lat: String,
    val lon: String,
    val adm2: String,
    val adm1: String,
    val country: String,
    val tz: String,
    val type: String,
    val rank: String
)
