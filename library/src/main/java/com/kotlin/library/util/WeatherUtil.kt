package com.kotlin.library.util

import android.util.Log
import android.widget.ImageView
import com.kotlin.library.R

/**
 * 天气工具类
 *
 * @author llw
 */
object WeatherUtil {
    /**
     * 根据传入的状态码修改填入的天气图标
     *
     * @param weatherStateIcon 显示的ImageView
     * @param code             天气状态码
     */
    fun changeIcon(weatherStateIcon: ImageView, code: Int) {
        weatherStateIcon.setImageResource(
            when (code) {
                100 -> R.mipmap.icon_100
                101 -> R.mipmap.icon_101
                102 -> R.mipmap.icon_102
                103 -> R.mipmap.icon_103
                104 -> R.mipmap.icon_104
                150 -> R.mipmap.icon_150
                153 -> R.mipmap.icon_153
                154 -> R.mipmap.icon_154
                200, 202, 203, 204 -> R.mipmap.icon_200
                201 -> R.mipmap.icon_201
                205, 206, 207 -> R.mipmap.icon_205
                208, 209, 210, 211, 212, 213 -> R.mipmap.icon_208
                300 -> R.mipmap.icon_300
                301 -> R.mipmap.icon_301
                302 -> R.mipmap.icon_302
                303 -> R.mipmap.icon_303
                304 -> R.mipmap.icon_304
                305 -> R.mipmap.icon_305
                306 -> R.mipmap.icon_306
                307 -> R.mipmap.icon_307
                308 -> R.mipmap.icon_312
                309 -> R.mipmap.icon_309
                310 -> R.mipmap.icon_310
                311 -> R.mipmap.icon_311
                312 -> R.mipmap.icon_312
                313 -> R.mipmap.icon_313
                314 -> R.mipmap.icon_306
                315 -> R.mipmap.icon_307
                316 -> R.mipmap.icon_310
                317 -> R.mipmap.icon_312
                399 -> R.mipmap.icon_399
                400 -> R.mipmap.icon_400
                401 -> R.mipmap.icon_401
                402 -> R.mipmap.icon_402
                403 -> R.mipmap.icon_403
                404 -> R.mipmap.icon_404
                405 -> R.mipmap.icon_405
                406 -> R.mipmap.icon_406
                407 -> R.mipmap.icon_407
                408 -> R.mipmap.icon_408
                409 -> R.mipmap.icon_409
                410 -> R.mipmap.icon_410
                499 -> R.mipmap.icon_499
                500 -> R.mipmap.icon_500
                501 -> R.mipmap.icon_501
                502 -> R.mipmap.icon_502
                503 -> R.mipmap.icon_503
                504 -> R.mipmap.icon_504
                507 -> R.mipmap.icon_507
                508 -> R.mipmap.icon_508
                509, 510, 514, 515 -> R.mipmap.icon_509
                511 -> R.mipmap.icon_511
                512 -> R.mipmap.icon_512
                513 -> R.mipmap.icon_513
                900 -> R.mipmap.icon_900
                901 -> R.mipmap.icon_901
                999 -> R.mipmap.icon_999
                else -> R.mipmap.icon_999
            }
        )
    }

    /**
     * 根据传入的时间显示时间段描述信息
     *
     * @param timeData
     * @return
     */
    fun showTimeInfo(timeData: String?): String {
        var timeInfo: String? = null
        var time = 0
        if (timeData == null || timeData == "") {
            timeInfo = "获取失败"
        } else {
            time = timeData.trim { it <= ' ' }.substring(0, 2).toInt()
            timeInfo = when (time) {
                in 0..6 -> "凌晨"
                in 7..12 -> "上午"
                in 13..13 -> "中午"
                in 14..18 -> "下午"
                in 19..24 -> "晚上"
                else -> "未知"
            }
        }
        return timeInfo
    }

    /**
     * 紫外线等级描述
     *
     * @param uvIndex
     * @return
     */
    fun uvIndexInfo(uvIndex: String): String? = when {
        uvIndex.toInt() <= 2 -> "较弱"
        uvIndex.toInt() <= 5 -> "弱"
        uvIndex.toInt() <= 7 -> "中等"
        uvIndex.toInt() <= 10 -> "强"
        uvIndex.toInt() <= 15 -> "很强"
        else -> "较弱"
    }


    /**
     * 根据api的提示转为更为人性化的提醒
     *
     * @param apiInfo
     * @return
     */
    fun apiToTip(apiInfo: String): String? {
        var result: String? = null
        var str: String? = null
        str = if (apiInfo.contains("AQI ")) {
            apiInfo.replace("AQI ", " ")
        } else {
            apiInfo
        }
        result = when (str) {
            "优" -> "♪(^∇^*)  空气很好。"
            "良" -> "ヽ(✿ﾟ▽ﾟ)ノ  空气不错。"
            "轻度污染" -> "(⊙﹏⊙)  空气有些糟糕。"
            "中度污染" -> " ε=(´ο｀*)))  唉 空气污染较为严重，注意防护。"
            "重度污染" -> "o(≧口≦)o  空气污染很严重，记得戴口罩哦！"
            "严重污染" -> "ヽ(*。>Д<)o゜  完犊子了!空气污染非常严重，要减少出门，定期检查身体，能搬家就搬家吧！"
            else -> "♪(^∇^*)  空气还行。"
        }
        return result
    }

    /**
     * 紫外线详细描述
     *
     * @param uvIndexInfo
     * @return
     */
    fun uvIndexToTip(uvIndexInfo: String?): String? =
        when (uvIndexInfo) {
            "较弱" -> "紫外线较弱，不需要采取防护措施；若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"
            "弱" -> "紫外线弱，可以适当采取一些防护措施，涂擦SPF在12-15之间、PA+的防晒护肤品。"
            "中等" -> "紫外线中等，外出时戴好遮阳帽、太阳镜和太阳伞等；涂擦SPF高于15、PA+的防晒护肤品。"
            "强" -> "紫外线较强，避免在10点至14点暴露于日光下.外出时戴好遮阳帽、太阳镜和太阳伞等，涂擦SPF20左右、PA++的防晒护肤品。"
            "很强" -> "紫外线很强，尽可能不在室外活动，必须外出时，要采取各种有效的防护措施。"
            else -> "紫外线一般，不需要采取防护措施；若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"
        }


    /**
     * 早晚温差提示
     *
     * @param height 当天最高温
     * @param low    当天最低温
     */
    fun differenceTempTip(height: Int, low: Int): String =
        StringBuffer().apply {
            append("    今天最高温$height℃，最低温$low℃。")
            if (height - low > 5) { //温差大
                when {
                    height < 25 -> append("早晚温差较大，加强自我防护，防治感冒，对自己好一点(*￣︶￣)")
                    height < 20 -> append("天气转阴温度低，上下班请注意添衣保暖(*^▽^*)")
                    height < 15 -> append("关怀不是今天才开始，关心也不是今天就结束，希望你注意保暖ヾ(◍°∇°◍)ﾉﾞ")
                }
            } else { //温差小
                when {
                    low < 25 -> append("多运动，多喝水，注意补充水分(*￣︶￣)")
                    low < 20 -> append("早睡早起，别熬夜，无论晴天还是雨天，每天都是新的一天(*^▽^*)")
                    low < 15 -> append("天气寒冷，注意防寒和保暖，也不要忘记锻炼喔ヾ(◍°∇°◍)ﾉﾞ")
                }
            }
        }.toString()


}