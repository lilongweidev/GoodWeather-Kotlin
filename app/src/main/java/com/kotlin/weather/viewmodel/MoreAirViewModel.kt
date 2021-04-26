package com.kotlin.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kotlin.weather.api.Repository
import com.kotlin.weather.model.LocationBean

/**
 * 更多空气数据ViewModel
 *
 * @author llw
 * @date 2021/4/26 15:56
 */
class MoreAirViewModel: ViewModel() {

    //搜索城市
    private val searchLiveData = MutableLiveData<String>()

    //天气  当天空气质量   城市  区/县级
    private val airLiveData = MutableLiveData<String>()

    //更多空气预报  监测站  市级
    private val moreAirLiveData = MutableLiveData<String>()

    val locationBean = ArrayList<LocationBean>()

    //被观察的搜索城市返回数据
    val searchCityLiveData = Transformations.switchMap(searchLiveData) { location ->
        Repository.searchCity(location)
    }

    //被观察的当天空气质量返回数据
    val airNowLiveData = Transformations.switchMap(airLiveData){cityId ->
        Repository.airNowWeather(cityId)
    }

    //被观察的更多空气质量返回数据
    val airMoreLiveData = Transformations.switchMap(moreAirLiveData){cityId ->
        Repository.airMoreWeather(cityId)
    }

    //搜索城市
    fun searchCity(location: String) {
        searchLiveData.value = location
    }

    //空气质量
    fun airNowWeather(cityId: String){
        airLiveData.value = cityId
    }

    //更多空气质量
    fun airMoreWeather(cityId: String){
        moreAirLiveData.value = cityId
    }
}