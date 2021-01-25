package com.kotlin.weather.api

import AirNowResponse
import HourlyResponse
import com.kotlin.library.util.Constant.API_KEY
import com.kotlin.weather.model.*
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
     * 当前城市灾害预警
     *
     * @param location 城市id ，通过搜索城市获得
     * @return WarningResponse 灾害预警返回
     */
    @GET("/v7/warning/now?key=$API_KEY")
    fun nowWarn(@Query("location") cityId: String?): Call<WarningResponse>

    /**
     * 实况天气
     *
     * @param cityId 城市id
     * @return NowResponse 返回实况天气数据
     */
    @GET("/v7/weather/now?key=$API_KEY&gzip=n")
    fun nowWeather(@Query("location") cityId: String): Call<NowResponse>

    /**
     * 逐小时预报（未来24小时）之前是逐三小时预报
     *
     * @param location 城市id
     * @return HourlyResponse 返回逐小时数据
     */
    @GET("/v7/weather/24h?key=$API_KEY")
    fun hourlyWeather(@Query("location") cityId: String): Call<HourlyResponse>

    /**
     * @param type     天数类型  传入3d / 7d / 10d / 15d  通过Path拼接到请求的url里面
     * @param cityId 城市id
     * @return DailyResponse 返回天气预报数据
     */
    @GET("/v7/weather/{type}?key=$API_KEY")
    fun dailyWeather(@Path("type") type: String, @Query("location") cityId: String): Call<DailyResponse>

    /**
     * 当天空气质量
     *
     * @param location 城市id
     * @return AirNowResponse 返回当天空气质量数据
     */
    @GET("/v7/air/now?key=$API_KEY")
    fun airNowWeather(@Query("location") cityId: String): Call<AirNowResponse>

    /**
     * 生活指数
     *
     * @param type     可以控制定向获取那几项数据 全部数据 0, 运动指数	1 ，洗车指数	2 ，穿衣指数	3 ，
     * 钓鱼指数	4 ，紫外线指数  5 ，旅游指数  6，花粉过敏指数	7，舒适度指数	8，
     * 感冒指数	9 ，空气污染扩散条件指数	10 ，空调开启指数	 11 ，太阳镜指数	12 ，
     * 化妆指数  13 ，晾晒指数  14 ，交通指数  15 ，防晒指数	16
     * @param location 城市id
     * @return LifestyleResponse 生活指数数据返回
     */
    @GET("/v7/indices/1d?key=$API_KEY")
    fun lifestyle(@Query("type") type: String, @Query("location") cityId: String): Call<LifestyleResponse>
}