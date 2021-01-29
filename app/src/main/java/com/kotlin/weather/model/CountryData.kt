package com.kotlin.weather.model

data class CountryData(
    val country: List<Province>
)

data class Province(
    val city: List<City>,
    val name: String
)

data class City(
    val area: List<String>,
    val name: String
)