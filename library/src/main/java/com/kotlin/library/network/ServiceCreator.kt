package com.kotlin.library.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 创建网络服务
 * @author llw
 */
object ServiceCreator {

    private fun getBaseUrl(type: Int): String? = when (type) {
        1 -> "https://devapi.qweather.net"//V7版本和风天气基本接口地址
        2 -> "https://geoapi.qweather.net"//V7版本下的搜索城市地址
        3 -> "http://api.bq04.com"//Fr.im更新地址
        4 -> "https://cn.bing.com"//必应壁纸地址
        5 -> "http://service.picasso.adesk.com"//网络手机壁纸返回地址
        else -> "https://search.heweather.net"//这是不重要的地址
    }


    private fun getRetrofit(type: Int = 1): Retrofit? =
        Retrofit.Builder()
            .baseUrl(getBaseUrl(type))
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    fun <T> create(serviceClass: Class<T>, type: Int = 1): T =
        getRetrofit(type)!!.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

}