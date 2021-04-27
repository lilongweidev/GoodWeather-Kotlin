package com.kotlin.library.util

import android.content.Context
import com.kotlin.library.BaseApplication

/**
 * SharedPreferences工具类  扩展函数
 *
 * @author llw
 * @date 2021/4/14 16:16
 */
const val NAME = "config"

val context = BaseApplication.context

fun Boolean.putBoolean(key: String) =
    context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit().putBoolean(key, this).apply()

fun Boolean.getBoolean(key: String): Boolean =
    context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getBoolean(key, this)

fun String?.putString(key: String) =
    context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit().putString(key, this).apply()

fun String.getString(key: String): String? =
    context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getString(key, this)

fun Int.putInt(key: String) =
    context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit().putInt(key, this).apply()

fun Int.getInt(key: String): Int =
    context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getInt(key, this)

fun Long.putLong(key: String) =
    context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit().putLong(key, this).apply()

fun Long.getLong(key: String): Long =
    context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getLong(key, this)