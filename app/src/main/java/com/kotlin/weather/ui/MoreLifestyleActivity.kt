package com.kotlin.weather.ui

import BaseActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.library.util.Constant
import com.kotlin.library.util.StatusBarUtil
import com.kotlin.library.util.showAnimation
import com.kotlin.library.util.showToast
import com.kotlin.weather.R
import com.kotlin.weather.adapter.MoreLifestyleAdapter
import com.kotlin.weather.viewmodel.MoreLifestyleViewModel
import kotlinx.android.synthetic.main.activity_more_lifestyle.*

/**
 * 更多生活指数信息
 *
 * @author llw
 * @date 2021/4/27 10:02
 */
class MoreLifestyleActivity : BaseActivity() {

    private val moreLifestyleViewModel by lazy {
        ViewModelProviders.of(this).get(MoreLifestyleViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_lifestyle)

        StatusBarUtil.transparencyBar(context) //透明状态栏
        back(toolbar)

        moreLifestyleRequestHelper()
    }

    /**
     * 更多生活质量页面网络请求帮助
     */
    private fun moreLifestyleRequestHelper() {
        tvTitle.text = intent.getStringExtra("cityName") ?: "未知城市"
        moreLifestyleViewModel.apply {
            lifestyle(intent.getStringExtra("cityId") ?: "未知城市")
            lifestyleLiveData.observe(this@MoreLifestyleActivity, Observer { result ->
                val lifestyleResponse = result.getOrNull()
                if (lifestyleResponse != null) {
                    rv.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = MoreLifestyleAdapter(R.layout.item_more_lifestyle_list, moreLifeDailyBean.apply { clear();this += lifestyleResponse.daily })
                        showAnimation(Constant.BOTTOM)
                    }
                } else {
                    "更多生活质量数据为空".showToast()
                }
            })
        }

    }
}