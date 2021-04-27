package com.kotlin.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kotlin.weather.api.Repository
import com.kotlin.weather.model.Daily
import com.kotlin.weather.model.RequestLifestyleBean

/**
 * 更多生活指数ViewModel
 *
 * @author llw
 * @date 2021/4/27 10:07
 */

class MoreLifestyleViewModel : ViewModel(){

    //天气  生活质量
    private val moreLifeLiveData = MutableLiveData<RequestLifestyleBean>()

    val moreLifeDailyBean = ArrayList<Daily>()

    //被观察的生活指数返回数据
    val lifestyleLiveData = Transformations.switchMap(moreLifeLiveData) { result ->
        Repository.lifestyle(result.type, result.cityId)
    }

    //更多生活质量数据
    fun lifestyle(cityId: String) {
        moreLifeLiveData.value = RequestLifestyleBean("0", cityId)
    }
}