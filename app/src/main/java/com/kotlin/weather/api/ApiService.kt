package com.kotlin.weather.api

import com.kotlin.library.util.Constant.API_KEY
import com.kotlin.weather.model.DailyResponse
import com.kotlin.weather.model.NowResponse
import com.kotlin.weather.model.SearchCityResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Api服务接口
 */
interface ApiService {

    /**
     * 搜索城市  V7版本  模糊搜索，国内范围 返回10条数据
     *
     * @param location 城市名
     * @return SearchCityResponse 搜索城市数据返回
     */
    @GET("/v2/city/lookup?key=$API_KEY&mode=exact&range=cn")
    fun searchCity(@Query("location") location: String): Call<SearchCityResponse>

    /**
     * 实况天气
     *
     * @param cityId 城市id
     * @return NowResponse 返回实况天气数据
     */
    @GET("/v7/weather/now?key=$API_KEY&gzip=n")
    fun nowWeather(@Query("location") cityId: String): Call<NowResponse>

    /**
     * @param type     天数类型  传入3d / 7d / 10d / 15d  通过Path拼接到请求的url里面
     * @param cityId 城市id
     * @return DailyResponse 返回天气预报数据
     */
    @GET("/v7/weather/{type}?key=$API_KEY")
    fun dailyWeather(@Path("type") type: String, @Query("location") cityId: String): Call<DailyResponse>
}