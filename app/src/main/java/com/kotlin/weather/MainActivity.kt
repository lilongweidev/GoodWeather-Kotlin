package com.kotlin.weather

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.google.gson.Gson
import com.kotlin.library.util.DateUtils
import com.kotlin.library.util.StatusBarUtil
import com.kotlin.library.util.WeatherUtil
import com.kotlin.library.util.showToast
import com.kotlin.weather.adapter.DailyAdapter
import com.kotlin.weather.adapter.HourlyAdapter
import com.kotlin.weather.viewmodel.MainViewModel
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    //定位器
    private var mLocationClient: LocationClient? = null
    private val myListener: MyLocationListener = MyLocationListener()

    private val mainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    //经度
    private var lon: String = ""

    //纬度
    private var lat: String = ""

    //区/县  城市id
    private var cityId: String = ""

    private var warnBodyString: String? = null //灾害预警数据字符串


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //透明状态栏
        StatusBarUtil.transparencyBar(this)
        //检查Android版本
        androidVersion()
    }

    /**
     * 判断当前Android版本
     */
    private fun androidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0或6.0以上

            PermissionX.init(this)
                .permissions(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .onExplainRequestReason { scope, deniedList ->
                    val message = "GoodWeather需要您同意以下权限才能正常使用"
                    scope.showRequestReasonDialog(deniedList, message, "确定", "取消")
                }
                .request { allGranted, _, deniedList ->
                    if (allGranted) {
                        startLocation()
                    } else {
                        "这些权限被拒绝: $deniedList".showToast()
                    }
                }
        } else {
            "不需要权限".showToast()
        }
    }

    /**
     * 开始定位
     */
    private fun startLocation() {
        //实例化mLocationClient
        mLocationClient = LocationClient(this)
        //注册定位监听
        mLocationClient!!.registerLocationListener(myListener)
        val option = LocationClientOption().apply {
            //是否需要地址信息
            setIsNeedAddress(true)
            isOnceLocation = true
        }
        //配置locOption
        mLocationClient!!.locOption = option
        //开始定位
        mLocationClient!!.start()
    }

    inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            //获取定位所在地的区/县
            val district = location.district
            //当前页面请求网络与回调
            mainRequestHelper(district)
        }

    }

    /**
     * Main网络请求与回调
     */
    @SuppressLint("SetTextI18n", "NewApi")
    private fun mainRequestHelper(district: String) {
        mainViewModel.apply {
            //发起搜索城市定位请求
            searchCity(district)

            //观察搜索城市返回
            locationLiveData.observe(this@MainActivity, Observer { result ->
                val searchCityResponse = result.getOrNull()
                if (searchCityResponse != null) {
                    Log.i(TAG, Gson().toJson(searchCityResponse))
                    mainViewModel.locationBean += searchCityResponse.location
                    val locationBean = mainViewModel.locationBean[0]
                    cityId = locationBean.id
                    lon = locationBean.lon
                    lat = locationBean.lat
                    //灾害预警
                    nowWarn(cityId)
                    //请求实时天气
                    nowWeather(cityId)
                    //请求逐小时天气
                    hourlyWeather(cityId)
                    //请求天气预报
                    dailyWeather(cityId)
                    //请求当天空气质量
                    airNowWeather(cityId)
                    //请求生活指数
                    lifestyle(cityId)
                } else {
                    "查询不到城市所在地区".showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
            })

            //观察灾害预警返回
            warningsLiveData.observe(this@MainActivity, Observer { result ->
                val warningResponse = result.getOrNull()
                val warning = warningResponse?.warning
                if (warning != null && warning.isNotEmpty()) {
                    warnBodyString = Gson().toJson(warningResponse)
                    tvWarn.text = "${warning[0].title}    ${warning[0].text}" //设置滚动标题和内容
                } else { //没有该城市预警有隐藏掉这个TextView
                    tvWarn.visibility = View.GONE
                }
            })

            //观察实况天气返回
            nowWeatherLiveData.observe(this@MainActivity, Observer { result ->
                val nowResponse = result.getOrNull()
                if (nowResponse != null) {
                    Log.i(TAG, Gson().toJson(nowResponse))
                    //温度
                    tvTemperature.text = nowResponse.now.temp
                    //设置字体
                    tvTemperature.typeface =
                        Typeface.createFromAsset(assets, "fonts/Roboto-Light.ttf")
                    //星期
                    tvWeek.text = DateUtils.getWeekOfDate(Date())
                    //天气状况
                    tvInfo.text = nowResponse.now.text
                    //城市名称
                    tvCity.text = district
                    //截去前面的字符，保留后面所有的字符，就剩下 22:00
                    val time = DateUtils.updateTime(nowResponse.updateTime)
                    //更新时间
                    tvOldTime.text = "最近更新时间：${WeatherUtil.showTimeInfo(time) + time}"

                    tvWindDirection.text = "风向     ${nowResponse.now.windDir}"  //风向
                    tvWindPower.text = "风力     ${nowResponse.now.windScale}级" //风力
                    wwBig.startRotate() //大风车开始转动
                    wwSmall.startRotate() //小风车开始转动
                }
            })

            //观察逐小时天气预报返回
            hourlyWeatherLiveData.observe(this@MainActivity, Observer { result ->
                val hourlyResponse = result.getOrNull()
                if (hourlyResponse?.hourly != null) {
                    hourlyBean += hourlyResponse.hourly
                    val hourlyAdapter = HourlyAdapter(R.layout.item_weather_hourly_list, hourlyBean)
                    val manager = LinearLayoutManager(this@MainActivity)
                    manager.orientation = RecyclerView.HORIZONTAL
                    rvHourly.layoutManager = manager
                    rvHourly.adapter = hourlyAdapter
                }
            })

            //观察预报天气返回
            dailyWeatherLiveData.observe(this@MainActivity, Observer { result ->
                val dailyResponse = result.getOrNull()
                if (dailyResponse != null) {
                    dailyBean += dailyResponse.daily
                    //当天最高温
                    tvTempHeight.text = "${dailyResponse.daily[0].tempMax}  ℃"
                    //当天最低温
                    tvTempLow.text = " /  ${dailyResponse.daily[0].tempMin} ℃"

                    val dailyAdapter = DailyAdapter(R.layout.item_weather_forecast_list, dailyBean)
                    rvDaily.layoutManager = LinearLayoutManager(this@MainActivity)
                    rvDaily.adapter = dailyAdapter
                }
            })

            //观察当天空气质量返回
            airNowLiveData.observe(this@MainActivity, Observer { result ->
                val airNowResponse = result.getOrNull()
                val data = airNowResponse?.now
                if (data != null) {
                    rpbAqi.setMaxProgress(300) //最大进度，用于计算
                    rpbAqi.setMinText("0") //设置显示最小值
                    rpbAqi.setMinTextSize(32f)
                    rpbAqi.setMaxText("300") //设置显示最大值
                    rpbAqi.setMaxTextSize(32f)
                    rpbAqi.setProgress(data.aqi.toFloat()) //当前进度
                    rpbAqi.setArcBgColor(getColor(R.color.arc_bg_color)) //圆弧的颜色
                    rpbAqi.setProgressColor(getColor(R.color.arc_progress_color)) //进度圆弧的颜色
                    rpbAqi.setFirstText(data.category) //空气质量描述 取值范围：优，良，轻度污染，中度污染，重度污染，严重污染
                    rpbAqi.setFirstTextSize(44f) //第一行文本的字体大小
                    rpbAqi.setSecondText(data.aqi) //空气质量值
                    rpbAqi.setSecondTextSize(64f) //第二行文本的字体大小
                    rpbAqi.setMinText("0")
                    rpbAqi.setMinTextColor(getColor(R.color.arc_progress_color))
                    tvAirInfo.text = "空气${data.category}"
                    tvPm10.text = data.pm10 //PM10
                    tvPm25.text = data.pm2p5 //PM2.5
                    tvNo2.text = data.no2 //二氧化氮
                    tvSo2.text = data.so2 //二氧化硫
                    tvO3.text = data.o3 //臭氧
                    tvCo.text = data.co //一氧化碳
                }
            })

            //观察生活指数返回
            lifestyleLiveData.observe(this@MainActivity, Observer { result ->
                val lifestyleResponse = result.getOrNull()
                val data = lifestyleResponse!!.daily
                for (item in data) when (item.type) {
                    "5" -> tvUv.text = "紫外线：" + item.text
                    "8" -> tvComf.text = "舒适度：" + item.text
                    "3" -> tvDrsg.text = "穿衣指数：" + item.text
                    "9" -> tvFlu.text = "感冒指数：" + item.text
                    "1" -> tvSport.text = "运动指数：" + item.text
                    "6" -> tvTrav.text = "旅游指数：" + item.text
                    "2" -> tvCw.text = "洗车指数：" + item.text
                    "10" -> tvAir.text = "空气指数：" + item.text
                }
            })

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        wwBig.stop() //停止大风车
        wwSmall.stop() //停止小风车
    }
}
