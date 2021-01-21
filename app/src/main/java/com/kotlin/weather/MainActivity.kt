package com.kotlin.weather

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.kotlin.library.util.showToast
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    //定位器
    private var mLocationClient: LocationClient? = null
    private val myListener: MyLocationListener = MyLocationListener()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
        override fun onReceiveLocation(location: BDLocation?) {
            val address = location!!.addrStr
            Log.i(TAG, "返回结果")
            address.showToast()
        }
    }
}
