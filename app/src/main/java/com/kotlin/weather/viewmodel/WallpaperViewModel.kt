package com.kotlin.weather.viewmodel

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kotlin.weather.api.Repository
import com.kotlin.weather.model.VerticalBean

/**
 * 壁纸ViewModel
 *
 * @author llw
 * @date 2021/2/19 17:56
 */
class WallpaperViewModel : ViewModel() {

    val biyingLiveData = Repository.biying()

    val wallpaperLiveData = Repository.wallpaper()

    val wallpaperBean = ArrayList<VerticalBean>()

    //必应
    fun biying() {
        biyingLiveData.value
    }

    //壁纸列表
    fun wallpaper(){
        wallpaperLiveData.value
    }
}