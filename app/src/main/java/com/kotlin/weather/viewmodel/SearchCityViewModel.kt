package com.kotlin.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kotlin.weather.api.Repository
import com.kotlin.weather.model.LocationBean

/**
 * 搜索城市ViewModel
 *
 * @author llw
 * @date 2021/4/27 16:31
 */
class SearchCityViewModel : ViewModel() {

    //搜索城市
    private val searchLiveData = MutableLiveData<String>()

    //搜索城市返回数据
    val locationBean = ArrayList<LocationBean>()


    //被观察的搜索城市返回数据
    val searchCityLiveData = Transformations.switchMap(searchLiveData) { location ->
        Repository.searchCity(location)
    }

    //搜索城市
    fun searchCity(location: String) {
        searchLiveData.value = location
    }
}