package com.kotlin.weather.viewmodel

import HourlyBean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kotlin.weather.api.Repository
import com.kotlin.weather.model.*
import kotlin.math.ln

/**
 * 主页面ViewModel
 * @author llw
 */
class MainViewModel : ViewModel() {

    //搜索城市
    private val searchLiveData = MutableLiveData<String>()

    //天气  城市Id
    private val cityIdLiveData = MutableLiveData<String>()

    //天气  分钟级降水
    private val minuteLiveData = MutableLiveData<String>()

    //天气  逐小时天气
    private val hourlyLiveData = MutableLiveData<String>()

    //天气  灾害预警
    private val warnLiveData = MutableLiveData<String>()

    //天气  天气预报
    private val dailyLiveData = MutableLiveData<RequestDailyBean>()

    //天气  当天空气质量
    private val airLiveData = MutableLiveData<String>()

    //天气  生活质量
    private val lifeLiveData = MutableLiveData<RequestLifestyleBean>()

    val locationBean = ArrayList<LocationBean>()

    val minutelyBean = ArrayList<MinutelyBean>()

    val dailyBean = ArrayList<DailyBean>()

    val hourlyBean = ArrayList<HourlyBean>()

    //被观察的搜索城市返回数据
    val locationLiveData = Transformations.switchMap(searchLiveData) { location ->
        Repository.searchCity(location)
    }

    //被观察的灾害预警返回数据
    val warningsLiveData = Transformations.switchMap(warnLiveData){cityId->
        Repository.nowWarn(cityId)
    }

    //被观察的实况天气返回数据
    val nowWeatherLiveData = Transformations.switchMap(cityIdLiveData) { cityId ->
        Repository.nowWeather(cityId)
    }

    //被观察的分钟级降水返回数据
    val minutePrecLiveData = Transformations.switchMap(minuteLiveData){lngLat ->
        Repository.minutePrec(lngLat)
    }

    //被观察的逐小时天气返回
    val hourlyWeatherLiveData = Transformations.switchMap(hourlyLiveData){cityId->
        Repository.hourlyWeather(cityId)
    }

    //被观察的预报天气返回数据
    val dailyWeatherLiveData = Transformations.switchMap(dailyLiveData) { result ->
        Repository.dailyWeather(result.type, result.cityId)
    }

    //被观察的当天空气质量返回数据
    val airNowLiveData = Transformations.switchMap(airLiveData){cityId ->
        Repository.airNowWeather(cityId)
    }

    //被观察的生活指数返回数据
    val lifestyleLiveData = Transformations.switchMap(lifeLiveData) { result ->
        Repository.lifestyle(result.type, result.cityId)
    }

    //搜索城市
    fun searchCity(location: String) {
        searchLiveData.value = location
    }

    //灾害预警
    fun nowWarn(cityId: String){
        warnLiveData.value = cityId
    }

    //实况天气
    fun nowWeather(cityId: String) {
        cityIdLiveData.value = cityId
    }

    fun minutePrec(lngLat:String){
        minuteLiveData.value = lngLat
    }

    fun hourlyWeather(cityId: String){
        hourlyLiveData.value = cityId
    }

    //预报天气
    fun dailyWeather(cityId: String) {
        dailyLiveData.value = RequestDailyBean("7d", cityId)
    }

    //空气质量
    fun airNowWeather(cityId: String){
        airLiveData.value = cityId
    }

    //生活质量
    fun lifestyle(cityId: String) {
        lifeLiveData.value = RequestLifestyleBean("1,2,3,5,6,8,9,10", cityId)
    }


}