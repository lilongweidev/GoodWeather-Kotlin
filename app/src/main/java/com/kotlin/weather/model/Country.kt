package com.kotlin.weather.model

/**
 * 城市数据实体
 *
 * @author llw
 */

data class Country(val province: List<Province>)

data class Province(val name: String, val city: List<City>)

data class City(val name: String, var area: List<String>)





