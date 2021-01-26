package com.kotlin.weather

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.google.gson.Gson
import com.kotlin.library.util.*
import com.kotlin.weather.adapter.DailyAdapter
import com.kotlin.weather.adapter.HourlyAdapter
import com.kotlin.weather.adapter.MinutePrecAdapter
import com.kotlin.weather.viewmodel.MainViewModel
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity(), View.OnClickListener,
    View.OnScrollChangeListener {

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

    private var state = false //分钟级降水数据 收缩状态  false 收缩  true 展开

    private var flag = true //图标显示标识,true显示，false不显示,只有定位的时候才为true,切换城市和常用城市都为false

    private val OPEN_LOCATION = 9527 //进入手机定位设置页面标识

    private var bgAlpha = 1f
    private var bright = false
    private val DURATION: Long = 500 //0.5s
    private val START_ALPHA = 0.7f //开始透明度
    private val END_ALPHA = 1f //结束透明度


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //透明状态栏
        StatusBarUtil.transparencyBar(this)
        //初始化
        initView()
        //检查Android版本
        androidVersion()
    }

    /**
     * 初始化
     */
    private fun initView() {
        ivAdd.setOnClickListener(this)
        tvCity.setOnClickListener(this)
        tvPrecDetail.setOnClickListener(this)
        scrollView.setOnScrollChangeListener(this) //指定当前页面，不写则滑动监听无效

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
                        //权限已经通过  检查手机是否开启定位功能
                        if (isOpenLocationServiceEnable()) {
                            tvCity.isEnabled = false //不可点击
                            //开始定位
                            startLocation()
                        } else {
                            tvCity.isEnabled = true //可以点击
                            "(((φ(◎ロ◎;)φ)))，你好像忘记打开定位功能了".showToast()
                            tvCity.text = "打开定位"
                        }
                    } else {
                        "这些权限被拒绝: $deniedList".showToast()
                    }
                }
        } else {
            "不需要权限".showToast()
        }
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么App将不能使用定位功能
     */
    private fun isOpenLocationServiceEnable(): Boolean {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gps || network
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
                    mainViewModel.locationBean += searchCityResponse.location
                    val locationBean = mainViewModel.locationBean[0]
                    cityId = locationBean.id
                    lon = locationBean.lon
                    lat = locationBean.lat
                    //灾害预警
                    nowWarn(cityId)
                    //请求实时天气
                    nowWeather(cityId)
                    //请求分钟级降水
                    minutePrec("$lon,$lat")
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
                } else {
                    "实况天气为空".showToast()
                }
            })

            //观察分钟级降水返回
            minutePrecLiveData.observe(this@MainActivity, Observer { result ->
                val minutePrecResponse = result.getOrNull()
                if (minutePrecResponse != null) {
                    //降水预告
                    tvPrecipitation.text = minutePrecResponse.summary
                    minutelyBean += minutePrecResponse.minutely
                    val minutePrecAdapter =
                        MinutePrecAdapter(R.layout.item_prec_detail_list, minutelyBean)
                    val manager = GridLayoutManager(this@MainActivity, 2)
                    manager.orientation = RecyclerView.HORIZONTAL
                    rvPrecDetail.layoutManager = manager
                    rvPrecDetail.adapter = minutePrecAdapter
                } else {
                    "分钟级降水为空".showToast()
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
                } else {
                    "逐小时天气为空".showToast()
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
                } else {
                    "预报天气为空".showToast()
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
                } else {
                    "空气质量为空".showToast()
                }
            })

            //观察生活指数返回
            lifestyleLiveData.observe(this@MainActivity, Observer { result ->
                val lifestyleResponse = result.getOrNull()
                if (lifestyleResponse != null) {
                    val data = lifestyleResponse.daily
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
                } else {
                    "生活指数为空".showToast()
                }

            })

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        wwBig.stop() //停止大风车
        wwSmall.stop() //停止小风车
    }

    /**
     * 页面点击事件
     */
    override fun onClick(v: View) {
        when (v.id) {
            //更多功能弹窗
            R.id.ivAdd -> {
                showAddWindow() //更多功能弹窗
                toggleBright() //计算动画时间
            }
            //打开定位
            R.id.tvCity -> {
                //当用户没有打开GPS定位时，则可以点击这个TextView去打开定位功能，然后进行定位
                if (isOpenLocationServiceEnable()) { //已开启定位
                    tvCity.text = "定位中"
                    startLocation() //开始定位
                } else { //未开启
                    //跳转到系统定位设置
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), OPEN_LOCATION
                    )
                }
            }
            //查看分钟降水详情
            R.id.tvPrecDetail -> {
                state = if (state) { //收缩
                    AnimationUtil.collapse(rvPrecDetail, tvPrecDetail)
                    false
                } else { //展开
                    AnimationUtil.expand(rvPrecDetail, tvPrecDetail)
                    true
                }
            }
        }
    }

    /**
     * 更多功能弹窗
     */
    @SuppressLint("InflateParams")
    private fun showAddWindow() {
        val popupWindow = PopupWindow(this)
        popupWindow.apply {
            contentView = LayoutInflater.from(this@MainActivity).inflate(R.layout.window_add, null)
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(0x0000))// 设置pop透明效果
            animationStyle = R.style.pop_add// 设置pop出入动画
            isFocusable =
                true// 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
            isTouchable = true// 设置pop可点击，为false点击事件无效，默认为true
            isOutsideTouchable = true// 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
            showAsDropDown(ivAdd, -100, 0)// 相对于 + 号正下面，同时可以设置偏移量
            setOnDismissListener { toggleBright() }// 设置pop关闭监听，用于改变背景透明度
            //绑定布局中的控件
            //绑定布局中的控件
            val changeCity  = contentView.findViewById(R.id.tvChangeCity) as TextView //切换城市
            val wallpaper = contentView.findViewById(R.id.tvWallpaper) as TextView//壁纸管理
            val searchCity =contentView.findViewById(R.id.tvSearchCity) as TextView//城市搜索
            val worldCity = contentView.findViewById(R.id.tvWorldCity) as TextView//世界城市
            val residentCity = contentView.findViewById(R.id.tvResidentCity) as TextView//常用城市
            val aboutUs = contentView.findViewById(R.id.tvAboutUs) as TextView//关于我们
            val setting = contentView.findViewById(R.id.tvSetting) as TextView//应用设置

            changeCity.setOnClickListener {//切换城市
                "切换城市".showToast()
                dismiss()
            }
            wallpaper.setOnClickListener {//壁纸管理
                "壁纸管理".showToast()
                dismiss()
            }
            searchCity.setOnClickListener {//城市搜索
                "城市搜索".showToast()
                dismiss()
            }
            worldCity.setOnClickListener {//世界城市
                "世界城市".showToast()
                dismiss()
            }
            residentCity.setOnClickListener {//常用城市
                "常用城市".showToast()
                dismiss()
            }
            aboutUs.setOnClickListener {//关于我们
                "关于我们".showToast()
                dismiss()
            }
            setting.setOnClickListener {//应用设置
                "应用设置".showToast()
                dismiss()
            }

        }
    }

    /**
     * 页面滑动监听
     */
    override fun onScrollChange(
        v: View,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        if (scrollY > oldScrollY) {
            "上滑".LogD("onScroll")
            //laySlideArea.getMeasuredHeight() 表示控件的绘制高度
            if (scrollY > laySlideArea.measuredHeight) {
                tvCity.text.toString().apply {
                    tvTitle.text = if (this.contains("定位中")) "城市天气" else this
                }
            }
        } else if (scrollY < oldScrollY) {
            "下滑".LogD("onScroll")
            if (scrollY < laySlideArea.measuredHeight) {
                tvTitle.text = "城市天气"
            }
        }
    }

    /**
     * 返回Activity的结果
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_LOCATION) {
            if (isOpenLocationServiceEnable()) { //已打开
                tvCity.text = "重新定位"
                tvCity.isEnabled = true //可以点击
            } else {
                "有意思吗？你跳过去又不打开定位，玩呢？嗯？我也是有脾气的好伐！".showToast()
                tvCity.text = "打开定位"
                tvCity.isEnabled = false //不可点击
            }
        }
    }

    /**
     * 计算动画时间
     */
    private fun toggleBright() {
        // 三个参数分别为：起始值 结束值 时长，那么整个动画回调过来的值就是从0.5f--1f的
        val animUtil = AnimationUtil()
        animUtil.setValueAnimator(START_ALPHA, END_ALPHA, DURATION)
        animUtil.addUpdateListener(object : AnimationUtil.UpdateListener {
            override fun progress(progress: Float) {
                // 此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
                bgAlpha = if (bright) progress else START_ALPHA + END_ALPHA - progress
                backgroundAlpha(bgAlpha)
            }
        })
        animUtil.addEndListner(object : AnimationUtil.EndListener {
            override fun endUpdate(animator: Animator?) {
                // 在一次动画结束的时候，翻转状态
                bright = !bright
            }
        })
        animUtil.startAnimator()
    }

    /**
     * 此方法用于改变背景的透明度，从而达到“变暗”的效果
     */
    private fun backgroundAlpha(bgAlpha: Float) {
        val lp = window.attributes
        // 0.0-1.0
        lp.alpha = bgAlpha
        window.attributes = lp
        // 此方法用来设置浮动层，防止部分手机变暗无效
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
}
