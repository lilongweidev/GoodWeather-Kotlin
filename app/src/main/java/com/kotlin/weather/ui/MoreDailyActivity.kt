package com.kotlin.weather.ui

import BaseActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.kotlin.library.util.StatusBarUtil
import com.kotlin.weather.R
import com.kotlin.weather.adapter.MoreDailyAdapter
import com.kotlin.weather.viewmodel.MoreDailyViewModel
import kotlinx.android.synthetic.main.activity_more_daily.*

/**
 * 更多天气预报
 *
 * @author llw
 * @date 2021/2/25 10:48
 */
class MoreDailyActivity : BaseActivity() {

    private val moreDailyViewModel by lazy {
        ViewModelProviders.of(this).get(MoreDailyViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_daily)

        //透明状态栏
        StatusBarUtil.transparencyBar(context)
        showLoadingDialog()
        Back(toolbar)

        showMoreDailyData()
    }

    /**
     * 显示更多天气预报数据
     */
    private fun showMoreDailyData() {
        moreDailyViewModel.apply {
            val cityId = intent.getStringExtra("cityId")
            if (cityId != null) {
                moreDailyWeather(cityId)
            }

            //观察天气详情数据返回
            moreDailyWeatherLiveData.observe(this@MoreDailyActivity, Observer { result ->
                val moreDailyResponse = result.getOrNull()
                if (moreDailyResponse != null) {
                    moreDailyBean.clear()
                    moreDailyBean += moreDailyResponse.daily

                    val adapter = MoreDailyAdapter(R.layout.item_more_daily_list, moreDailyBean)
                    val linearLayoutManager = LinearLayoutManager(context)
                    linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    rv.layoutManager = linearLayoutManager
                    val snapHelper = PagerSnapHelper()
                    rv.onFlingListener = null //避免抛异常
                    //滚动对齐，使RecyclerView像ViewPage一样，一次滑动一项,居中
                    snapHelper.attachToRecyclerView(rv)
                    rv.adapter = adapter
                    tvTitle.text = intent.getStringExtra("cityName")
                    dismissLoadingDialog()
                }
            })

        }
    }
}