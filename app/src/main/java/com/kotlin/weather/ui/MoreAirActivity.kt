package com.kotlin.weather.ui

import BaseActivity
import NowBean
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.kotlin.library.util.DateUtils
import com.kotlin.library.util.StatusBarUtil
import com.kotlin.library.util.WeatherUtil
import com.kotlin.library.util.showToast
import com.kotlin.weather.R
import com.kotlin.weather.adapter.MoreAirFiveAdapter
import com.kotlin.weather.adapter.MoreAirStationAdapter
import com.kotlin.weather.viewmodel.MoreAirViewModel
import kotlinx.android.synthetic.main.activity_more_air.*

/**
 * 更多空气质量信息
 *
 * @author llw
 * @date 2021/2/25 14:28
 */
class MoreAirActivity : BaseActivity() {

    private val moreAirViewModel by lazy {
        ViewModelProviders.of(this).get(MoreAirViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_air)

        StatusBarUtil.transparencyBar(context) //透明状态栏
        back(toolbar)

        moreAirRequestHelper()
    }

    /**
     * 更多空气页面网络请求帮助
     */
    @SuppressLint("SetTextI18n")
    private fun moreAirRequestHelper() {
        val stationName = intent.getStringExtra("stationName") ?: "市"
        val cityName = intent.getStringExtra("cityName") ?: "区/县"
        //设置标题
        tvTitle.text = "$stationName - $cityName"
        moreAirViewModel.apply {
            //搜索城市
            searchCity(stationName)

            //观察搜索城市返回
            searchCityLiveData.observe(this@MoreAirActivity, Observer { result ->
                val searchCityResponse = result.getOrNull()
                if (searchCityResponse != null) {
                    locationBean.clear()
                    locationBean += searchCityResponse.location
                    val locationBean = locationBean[0]

                    val cityId = locationBean.id
                    //查询当前检测站的空气质量
                    airNowWeather(cityId)
                    airNowLiveData.observe(this@MoreAirActivity, Observer { airNowResult ->
                        val airNowResponse = airNowResult.getOrNull()
                        if (airNowResponse != null) {
                            //截去前面的字符，保留后面所有的字符，就剩下 22:00
                            val time = DateUtils.updateTime(airNowResponse.updateTime)
                            //更新时间
                            tvOldTime.text = "最近更新时间：${WeatherUtil.showTimeInfo(time) + time}"
                            //显示基础数据
                            showAirBasicData(airNowResponse.now)
                            //配置检测站空气质量显示
                            rvStation.apply {
                                layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
                                onFlingListener = null
                                PagerSnapHelper().attachToRecyclerView(this)
                                adapter = MoreAirStationAdapter(R.layout.item_more_air_station_list, airNowStationBean.apply { clear(); this += airNowResponse.station })
                            }
                        } else {
                            "空气质量数据为空".showToast()
                        }
                    })

                    //查询城市更多空气质量
                    airMoreWeather(cityId)
                    airMoreLiveData.observe(this@MoreAirActivity, Observer { airMoreResult ->
                        val moreAirFiveResponse = airMoreResult.getOrNull()
                        if(moreAirFiveResponse != null){
                            //配置更多列表
                            rvFiveAir.apply {
                                layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
                                adapter = MoreAirFiveAdapter(R.layout.item_more_air_five_list,airMoreBean.apply { clear();this += moreAirFiveResponse.daily })
                            }
                        } else {
                            "更多空气质量数据为空".showToast()
                        }
                    })

                } else {
                    "搜索不到城市".showToast()
                }
            })
        }
    }

    /**
     * 展示基础数据
     *
     * @param data 数据源
     */
    private fun showAirBasicData(data: NowBean) {
        rpbAqi.setMaxProgress(300) //最大进度，用于计算
        rpbAqi.setMinText("0") //设置显示最小值
        rpbAqi.setMinTextSize(32f)
        rpbAqi.setMaxText("300") //设置显示最大值
        rpbAqi.setMaxTextSize(32f)
        rpbAqi.setProgress(java.lang.Float.valueOf(data.aqi)) //当前进度
        rpbAqi.setArcBgColor(ContextCompat.getColor(context,R.color.arc_bg_color)) //圆弧的颜色
        rpbAqi.setProgressColor(ContextCompat.getColor(context,R.color.arc_progress_color)) //进度圆弧的颜色
        rpbAqi.setFirstText(data.category) //空气质量描述 取值范围：优，良，轻度污染，中度污染，重度污染，严重污染
        rpbAqi.setFirstTextSize(44f) //第一行文本的字体大小
        rpbAqi.setSecondText(data.aqi) //空气质量值
        rpbAqi.setSecondTextSize(64f) //第二行文本的字体大小
        rpbAqi.setMinText("0")
        rpbAqi.setMinTextColor(ContextCompat.getColor(context,R.color.arc_progress_color))
        tvPm10.text = data.pm10 //PM10  + " μg/m3"
        progressPm10.setProgress(data.pm10, 100)
        tvPm25.text = data.pm2p5 //PM2.5
        progressPm25.setProgress(data.pm2p5, 100)
        tvNo2.text = data.no2 //二氧化氮
        progressNo2.setProgress(data.no2, 100)
        tvSo2.text = data.so2 //二氧化硫
        progressSo2.setProgress(data.so2, 100)
        tvO3.text = data.o3 //臭氧
        progressO3.setProgress(data.o3, 100)
        tvCo.text = data.co //一氧化碳
        progressCo.setProgress(data.co, 100)
    }

}