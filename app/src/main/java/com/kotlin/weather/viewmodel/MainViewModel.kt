package com.kotlin.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kotlin.weather.api.Repository
import com.kotlin.weather.model.*

class MainViewModel : ViewModel() {

    //搜索城市
    private val searchLiveData = MutableLiveData<String>()

    //天气  城市Id
    private val cityIdLiveData = MutableLiveData<String>()

    //天气  城市Id
    private val dailyLiveData = MutableLiveData<RequestDailyBean>()

    val locationBean = ArrayList<LocationBean>()

    val dailyBean = ArrayList<DailyBean>()

    //被观察的搜索城市返回数据
    val locationLiveData = Transformations.switchMap(searchLiveData) { location ->
        Repository.searchCity(location)
    }

    //被观察的实况天气返回数据
    val nowWeatherLiveData = Transformations.switchMap(cityIdLiveData) { cityId ->
        Repository.nowWeather(cityId)
    }

    //被观察的预报天气返回数据
    val dailyWeatherLiveData = Transformations.switchMap(dailyLiveData) { result ->
        Repository.dailyWeather(result.type, result.cityId)
    }

    //搜索城市
    fun searchCity(location: String) {
        searchLiveData.value = location
    }

    //实况天气
    fun nowWeather(cityId: String) {
        cityIdLiveData.value = cityId
    }

    //预报天气
    fun dailyWeather(type:String,cityId: String) {
        val requestDailyBean = dailyLiveData.value
        if (requestDailyBean != null) {
            requestDailyBean.type = type
            requestDailyBean.cityId = cityId
        }
    }


}