package com.kotlin.weather

import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility
import com.kotlin.library.BaseApplication
import org.litepal.LitePal.initialize

/**
 * 项目管理
 *
 * @author llw
 * @date 2021/2/20 14:49
 */
class WeatherApplication:BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        //初始化数据库
        initialize(this)
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this)
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL)

        //配置讯飞语音SDK
        //配置讯飞语音SDK
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=6018c2cb")
    }

}