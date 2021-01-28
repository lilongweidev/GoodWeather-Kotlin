package com.kotlin.weather.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.weather.R
import com.kotlin.weather.model.Province

/**
 * 省列表适配器
 *
 * @author llw
 */
class ProvinceAdapter(layoutResId: Int, data: List<Province>) :
    BaseQuickAdapter<Province, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: Province) {
        //省名称
        helper.setText(R.id.tv_city, item.name)
        //点击之后进入市级列表
        helper.addOnClickListener(R.id.item_city)
    }
}