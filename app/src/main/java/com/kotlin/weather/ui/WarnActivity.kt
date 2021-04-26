package com.kotlin.weather.ui

import BaseActivity
import BaseActivity.Companion.context
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
import com.kotlin.library.util.Constant.BOTTOM
import com.kotlin.library.util.DateUtils
import com.kotlin.library.util.StatusBarUtil
import com.kotlin.library.util.WeatherUtil
import com.kotlin.library.util.showAnimation
import com.kotlin.weather.R
import com.kotlin.weather.model.WarningBean
import com.kotlin.weather.model.WarningResponse
import kotlinx.android.synthetic.main.activity_warn.*

/**
 * 灾害预警详情信息页面
 *
 * @author llw
 * @date 2021/2/25 9:47
 */
class WarnActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_warn)

        initView()
    }

    private fun initView() {
        //透明状态栏
        StatusBarUtil.transparencyBar(context)
        Back(toolbar)
        val data: WarningResponse = Gson().fromJson(
            intent.getStringExtra("warnBodyString"),
            WarningResponse::class.java
        )
        val mAdapter = WarnAdapter(R.layout.item_warn_list, data.warning)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
        rv.showAnimation(BOTTOM)
    }

    /**
     * 内部灾害预警适配器
     */
    inner class WarnAdapter(layoutResId: Int, data: List<WarningBean>) :
        BaseQuickAdapter<WarningBean, BaseViewHolder>(layoutResId, data) {
        @SuppressLint("SetTextI18n")
        override fun convert(helper: BaseViewHolder, item: WarningBean) {
            val tvTime = helper.getView(R.id.tv_time) as TextView
            val time: String = DateUtils.updateTime(item.pubTime)
            tvTime.text = "预警发布时间：${WeatherUtil.showTimeInfo(time)} $time"

            helper.setText(R.id.tv_city, item.sender) //地区
                .setText(R.id.tv_type_name_and_level, "${item.typeName} ${item.level} 预警") //预警类型名称和等级
                .setText(R.id.tv_content, item.text) //预警详情内容

        }

    }
}
