package com.kotlin.weather.api

import androidx.lifecycle.liveData
import com.kotlin.library.util.Constant.SUCCESS_CODE
import com.kotlin.weather.model.DailyResponse
import com.kotlin.weather.model.NowResponse
import com.kotlin.weather.model.SearchCityResponse
import kotlinx.coroutines.Dispatchers
import java.lang.RuntimeException

object Repository {

    /**
     * 搜索城市
     */
    fun searchCity(location: String) = liveData(Dispatchers.IO) {
        val result = try {
            val searchCityResponse = RequestNetwork.searchCity(location)
            if (searchCityResponse.code == SUCCESS_CODE) {
                //包装获取的城市列表数据
                Result.success(searchCityResponse)
            } else {
                //包装一个异常信息
                Result.failure(RuntimeException("response code is ${searchCityResponse.code}"))
            }
        } catch (e: Exception) {
            //包装一个异常信息
            Result.failure<SearchCityResponse>(e)
        }
        //通知数据变化
        emit(result)
    }

    /**
     * 实时天气
     */
    fun nowWeather(cityId: String) = liveData(Dispatchers.IO) {
        val result = try {
            val nowResponse = RequestNetwork.nowWeather(cityId)
            if (nowResponse.code == SUCCESS_CODE) {
                Result.success(nowResponse)
            } else {
                Result.failure(RuntimeException("response code is ${nowResponse.code}"))
            }
        } catch (e: Exception) {
            Result.failure<NowResponse>(e)
        }
        emit(result)
    }

    /**
     * 预报天气  3、7、10、15天
     */
    fun dailyWeather(type:String,cityId:String) = liveData(Dispatchers.IO) {
        val result = try {
            val dailyResponse = RequestNetwork.dailyWeather(type,cityId)
            if (dailyResponse.code == SUCCESS_CODE) {
                Result.success(dailyResponse)
            } else {
                Result.failure(RuntimeException("response code is ${dailyResponse.code}"))
            }
        } catch (e: Exception) {
            Result.failure<DailyResponse>(e)
        }
        emit(result)
    }
}