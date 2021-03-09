package com.kotlin.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kotlin.weather.api.Repository
import com.kotlin.weather.model.DailyBean
import com.kotlin.weather.model.RequestDailyBean

/**
 * 更多天气数据ViewModel
 *
 * @author llw
 * @date 2021/2/25 11:08
 */
class MoreDailyViewModel: ViewModel() {

    //天气  更多天气预报
    private val moreDailyLiveData = MutableLiveData<RequestDailyBean>()

    val moreDailyBean = ArrayList<DailyBean>()

    //被观察的预报天气返回数据
    val moreDailyWeatherLiveData = Transformations.switchMap(moreDailyLiveData) { result ->
        Repository.dailyWeather(result.type, result.cityId)
    }

    //预报天气
    fun moreDailyWeather(cityId: String) {
        moreDailyLiveData.value = RequestDailyBean("15d", cityId)
    }
}