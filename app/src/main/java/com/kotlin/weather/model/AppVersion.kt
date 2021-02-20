package com.kotlin.weather.model

import org.litepal.crud.LitePalSupport

/**
 * APP版本实体
 *
 * @author llw
 * @date 2021/2/20 10:59
 */
data class AppVersion(
    val name: String,//名称
    val version: String,//版本号
    val changelog: String,//更新内容日志
    val updated_at: Int,//更新时间
    val versionShort: String,//版本名
    val build: String,//构建
    val installUrl: String,//安装网址
    val install_url: String,//安装网址
    val direct_install_url: String,//直接安装网址
    val update_url: String,//更新地址
    val appSize: String//APP大小
) : LitePalSupport()