package com.kotlin.weather

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.kotlin.weather.model.DailyBean
import com.kotlin.weather.viewmodel.MainViewModel
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    //定位器
    private var mLocationClient: LocationClient? = null
    private val myListener: MyLocationListener = MyLocationListener()

    val mainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    var lon: String = ""
    var lat: String = ""
    var cityId: String = ""

    //可变列表
    private var list: MutableList<DailyBean> = mutableListOf()


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
            mainRequestHelper(district)
        }

    }

    /**
     * Main网络请求与回调
     */
    @SuppressLint("SetTextI18n")
    private fun mainRequestHelper(district: String) {
        mainViewModel.apply {
            //发起搜索城市定位请求
            searchCity(district)

            //观察搜索城市返回
            locationLiveData.observe(this@MainActivity, Observer { result ->
                val searchCityResponse = result.getOrNull()
                if (searchCityResponse != null) {
                    Log.i(TAG,Gson().toJson(searchCityResponse))
                    mainViewModel.locationBean += searchCityResponse.location
                    val locationBean = mainViewModel.locationBean[0]
                    cityId = locationBean.id
                    lon = locationBean.lon
                    lat = locationBean.lat
                    //请求实时天气
                    nowWeather(cityId)
                    dailyWeather("7d",cityId)
                } else {
                    "查询不到城市所在地区".showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
            })
            //观察实况天气返回
            nowWeatherLiveData.observe(this@MainActivity, Observer { result ->
                val nowResponse = result.getOrNull()
                if (nowResponse != null) {
                    Log.i(TAG,Gson().toJson(nowResponse))
                    //温度
                    tvTemperature.text = nowResponse.now.temp
                    //设置字体
                    tvTemperature.typeface =
                        Typeface.createFromAsset(assets, "fonts/Roboto-Light.ttf")
                    //天气状况
                    tvInfo.text = nowResponse.now.text
                    //城市名称
                    tvCity.text = district
                    //截去前面的字符，保留后面所有的字符，就剩下 22:00
                    val time = DateUtils.updateTime(nowResponse.updateTime)
                    //更新时间
                    tvOldTime.text = "最近更新时间：${WeatherUtil.showTimeInfo(time) + time}"

                }
            })

            //观察预报天气返回
            dailyWeatherLiveData.observe(this@MainActivity, Observer { result ->
                val dailyResponse = result.getOrNull()
                if(dailyResponse != null){
                    Log.i(TAG,Gson().toJson(dailyResponse))
                    mainViewModel.dailyBean += dailyResponse.daily
                    val dailyAdapter = DailyAdapter(R.layout.item_weather_forecast_list,dailyBean)
                    rvDaily.layoutManager = LinearLayoutManager(this@MainActivity)
                    rvDaily.adapter = dailyAdapter
                }
            })
        }
    }
}
