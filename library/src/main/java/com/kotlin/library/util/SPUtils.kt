package com.kotlin.library.util

import android.content.Context

/**
 * SharedPreferences工具类
 *
 * @author llw
 * @date 2021/2/20 15:51
 */
object SPUtils {
    private val NAME = "config"

    fun putBoolean(
        key: String?,
        value: Boolean,
        ctx: Context
    ) {
        val sp = ctx.getSharedPreferences(
            NAME,
            Context.MODE_PRIVATE
        )
        sp.edit().putBoolean(key, value).commit()
    }

    fun getBoolean(
        key: String?,
        defValue: Boolean,
        ctx: Context
    ): Boolean {
        val sp = ctx.getSharedPreferences(
            NAME,
            Context.MODE_PRIVATE
        )
        return sp.getBoolean(key, defValue)
    }

    fun putString(
        key: String?,
        value: String?,
        ctx: Context
    ) {
        val sp = ctx.getSharedPreferences(
            NAME,
            Context.MODE_PRIVATE
        )
        sp.edit().putString(key, value).commit()
    }

    fun getString(
        key: String?,
        defValue: String?,
        ctx: Context?
    ): String? {
        if (ctx != null) {
            val sp = ctx.getSharedPreferences(
                NAME,
                Context.MODE_PRIVATE
            )
            return sp.getString(key, defValue)
        }
        return ""
    }

    fun putInt(key: String?, value: Int, ctx: Context) {
        val sp = ctx.getSharedPreferences(
            NAME,
            Context.MODE_PRIVATE
        )
        sp.edit().putInt(key, value).commit()
    }


    fun getInt(key: String?, defValue: Int, ctx: Context): Int {
        val sp = ctx.getSharedPreferences(
            NAME,
            Context.MODE_PRIVATE
        )
        return sp.getInt(key, defValue)
    }

    fun remove(key: String?, ctx: Context) {
        val sp = ctx.getSharedPreferences(
            NAME,
            Context.MODE_PRIVATE
        )
        sp.edit().remove(key).commit()
    }
}