package com.kotlin.weather.api

import com.kotlin.library.network.ServiceCreator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 请求网络
 */
object RequestNetwork {

    //创建搜索城市服务接口的动态代理对象
    private val searchCityService = ServiceCreator.create(ApiService::class.java, 2)

    private val weatherService = ServiceCreator.create(ApiService::class.java)

    //通过await()函数将searchCity()函数也声明成挂起函数。使用协程  搜索城市
    suspend fun searchCity(location: String) = searchCityService.searchCity(location).await()

    //当前城市灾害预警
    suspend fun nowWarn(cityId: String) = searchCityService.nowWarn(cityId).await()

    //获取实时天气
    suspend fun nowWeather(cityId: String) = weatherService.nowWeather(cityId).await()

    //获取逐小时天气
    suspend fun hourlyWeather(cityId: String) = weatherService.hourlyWeather(cityId).await()

    //获取预报天气 未来3天、7天、10天、15天
    suspend fun dailyWeather(type:String,cityId: String) = weatherService.dailyWeather(type,cityId).await()

    //获取当前城市空气质量
    suspend fun airNowWeather(cityId: String) = weatherService.airNowWeather(cityId).await()

    //获取生活质量数据
    suspend fun lifestyle(type: String,cityId: String) = weatherService.lifestyle(type, cityId).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                //正常返回
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                //异常返回
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}