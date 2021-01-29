package com.kotlin.weather.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.weather.R
import com.kotlin.weather.model.City

/**
 * 市列表适配器
 *
 * @author llw
 */
class CityAdapter(layoutResId: Int, data: List<City>) : BaseQuickAdapter<City, BaseViewHolder>
    (layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: City) {
        //市名称
        helper.setText(R.id.tv_city, item.name)
        //点击事件  点击进入区/县列表
        helper.addOnClickListener(R.id.item_city)
    }
}