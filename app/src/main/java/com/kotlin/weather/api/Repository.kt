package com.kotlin.weather.api

import android.util.Log
import androidx.lifecycle.liveData
import com.kotlin.library.util.Constant.SUCCESS_CODE
import com.kotlin.library.util.LogI
import kotlinx.coroutines.Dispatchers
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {

    /**
     * @param context 协程上下文
     */
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            //通知数据变化
            emit(result)
        }

    /**
     * 搜索城市
     */
    fun searchCity(location: String) = fire(Dispatchers.IO) {
        val searchCityResponse = RequestNetwork.searchCity(location)
        if (searchCityResponse.code == SUCCESS_CODE) {
            //包装获取的城市列表数据
            Result.success(searchCityResponse)
        } else {
            //包装一个异常信息
            Result.failure(RuntimeException("response code is ${searchCityResponse.code}"))
        }
    }

    /**
     * 灾害预警
     */
    fun nowWarn(cityId: String) = fire(Dispatchers.IO) {
        val warningResponse = RequestNetwork.nowWarn(cityId)
        if (warningResponse.code == SUCCESS_CODE) {
            Result.success(warningResponse)
        } else {
            Result.failure(RuntimeException("response code is ${warningResponse.code}"))
        }
    }


    /**
     * 实时天气
     */
    fun nowWeather(cityId: String) = fire(Dispatchers.IO) {
        val nowResponse = RequestNetwork.nowWeather(cityId)
        if (nowResponse.code == SUCCESS_CODE) {
            Result.success(nowResponse)
        } else {
            Result.failure(RuntimeException("response code is ${nowResponse.code}"))
        }
    }

    /**
     * 分钟级降水
     */
    fun minutePrec(lngLat: String) = fire(Dispatchers.IO) {
        val minutePrecResponse = RequestNetwork.minutePrec(lngLat)
        if (minutePrecResponse.code == SUCCESS_CODE) {
            Result.success(minutePrecResponse)
        } else {
            Result.failure(RuntimeException("response code is ${minutePrecResponse.code}"))
        }
    }

    /**
     * 实时天气
     */
    fun hourlyWeather(cityId: String) = fire(Dispatchers.IO) {
        val hourlyResponse = RequestNetwork.hourlyWeather(cityId)
        if (hourlyResponse.code == SUCCESS_CODE) {
            Result.success(hourlyResponse)
        } else {
            Result.failure(RuntimeException("response code is ${hourlyResponse.code}"))
        }
    }

    /**
     * 预报天气  3、7、10、15天
     */
    fun dailyWeather(type: String, cityId: String) = fire(Dispatchers.IO) {
        val dailyResponse = RequestNetwork.dailyWeather(type, cityId)
        if (dailyResponse.code == SUCCESS_CODE) {
            Result.success(dailyResponse)
        } else {
            Result.failure(RuntimeException("response code is ${dailyResponse.code}"))
        }
    }

    /**
     * 当天空气质量
     */
    fun airNowWeather(cityId: String) = fire(Dispatchers.IO) {
        val airNowResponse = RequestNetwork.airNowWeather(cityId)
        if (airNowResponse.code == SUCCESS_CODE) {
            Result.success(airNowResponse)
        } else {
            Result.failure(RuntimeException("response code is ${airNowResponse.code}"))
        }
    }

    /**
     * 生活质量
     */
    fun lifestyle(type: String, cityId: String) = fire(Dispatchers.IO) {
        val lifestyleResponse = RequestNetwork.lifestyle(type, cityId)
        if (lifestyleResponse.code == SUCCESS_CODE) {
            Result.success(lifestyleResponse)
        } else {
            Result.failure(RuntimeException("response code is ${lifestyleResponse.code}"))
        }
    }
}