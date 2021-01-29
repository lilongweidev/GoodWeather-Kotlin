package com.kotlin.weather.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.weather.R

class AreaAdapter(layoutResId:Int,data :List<String>):BaseQuickAdapter<String,BaseViewHolder>(layoutResId,data) {

    override fun convert(helper: BaseViewHolder, item: String) {
        //区/县的名称
        helper.setText(R.id.tv_city, item)
        //点击事件 点击之后得到区/县  然后查询天气数据
        helper.addOnClickListener(R.id.item_city)
    }
}