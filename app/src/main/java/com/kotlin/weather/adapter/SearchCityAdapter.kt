package com.kotlin.weather.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kotlin.weather.R
import com.kotlin.weather.model.LocationBean

/**
 * 搜索城市结果列表适配器
 *
 * @author llw
 * @date 2021/4/27 15:29
 */
class SearchCityAdapter(layoutResId: Int, data: List<LocationBean>) :
    BaseQuickAdapter<LocationBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: LocationBean) {
        //城市名称
        helper.setText(R.id.tv_city_name, item.name)
            //绑定点击事件
            .addOnClickListener(R.id.tv_city_name)
    }
}