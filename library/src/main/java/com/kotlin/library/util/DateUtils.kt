package com.kotlin.library.util

import android.annotation.SuppressLint
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期工具类
 *
 * @author llw
 */
object DateUtils {
    var dit = 10

    /**
     * 获取当前完整的日期和时间
     * @return 时间
     */
    val nowDateTime: String
        @SuppressLint("SimpleDateFormat")
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return sdf.format(Date())
        }

    /**
     * 获取当前日期
     * @return 日期
     */
    val nowDate: String
        @SuppressLint("SimpleDateFormat")
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            return sdf.format(Date())
        }

    /**
     * 获取当前日期  没有分隔符
     * @return
     */
    val nowDateNoLimiter: String
        @SuppressLint("SimpleDateFormat")
        get() {
            val sdf = SimpleDateFormat("yyyyMMdd")
            return sdf.format(Date())
        }

    /**
     * 前一天
     * @param date
     * @return
     */
    fun getYesterday(date: Date?): String {
        var date = date
        var tomorrow = ""
        val calendar: Calendar = GregorianCalendar()
        calendar.time = date
        calendar.add(Calendar.DATE, -1)
        date = calendar.time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        tomorrow = formatter.format(date)
        return tomorrow
    }

    /**
     * 后一天
     * @param date
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun getTomorrow(date: Date?): String {
        var date = date
        var tomorrow = ""
        val calendar: Calendar = GregorianCalendar()
        calendar.time = date
        calendar.add(Calendar.DATE, +1)
        date = calendar.time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        tomorrow = formatter.format(date)
        return tomorrow
    }

    /**
     * 获取当前时间
     * @return
     */
    val nowTime: String
        @SuppressLint("SimpleDateFormat")
        get() {
            val sdf = SimpleDateFormat("HH:mm:ss")
            return sdf.format(Date())
        }

    /**
     * 获取当前日期(精确到毫秒)
     * @return
     */
    val nowTimeDetail: String
        @SuppressLint("SimpleDateFormat")
        get() {
            val sdf = SimpleDateFormat("HH:mm:ss.SSS")
            return sdf.format(Date())
        }

    /**
     * 根据传入的时间，先转换再截取，得到更新时间  传入  "2020-07-16T09:39+08:00"
     * @param dateTime
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun updateTime(dateTime: String?): String? {
        var result: String? = null
        Log.d("dateTime-->", dateTime + "")
        if (dateTime == null) {
            val sdf = SimpleDateFormat("HH:mm")
            result = sdf.format(Date())
        } else {
            result = dateTime.substring(11, 16)
            Log.d("dateTime-->", result)
        }
        return result
    }

    /**
     * 获取今天是星期几
     * @param date
     * @return
     */
    fun getWeekOfDate(date: Date?): String {
        val weekDays =
            arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        val cal = Calendar.getInstance()
        cal.time = date
        var w = cal[Calendar.DAY_OF_WEEK] - 1
        if (w < 0) {
            w = 0
        }
        return weekDays[w]
    }

    /**
     * 计算星期几
     * @param dateTime
     * @return
     */
    fun getDayOfWeek(dateTime: String): Int {
        val cal = Calendar.getInstance()
        if (dateTime == "") {
            cal.time = Date(System.currentTimeMillis())
        } else {
            val sdf =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            var date: Date?
            try {
                date = sdf.parse(dateTime)
            } catch (e: ParseException) {
                date = null
                e.printStackTrace()
            }
            if (date != null) {
                cal.time = Date(date.time)
            }
        }
        return cal[Calendar.DAY_OF_WEEK]
    }

    /**
     * 根据年月日计算是星期几并与当前日期判断  非昨天、今天、明天 则以星期显示
     * @param dateTime
     * @return
     */
    fun Week(dateTime: String): String {
        var week = ""
        var yesterday = ""
        var today = ""
        var tomorrow = ""
        yesterday = getYesterday(Date())
        today = nowDate
        tomorrow = getTomorrow(Date())
        if (dateTime == yesterday) {
            week = "昨天"
        } else if (dateTime == today) {
            week = "今天"
        } else if (dateTime == tomorrow) {
            week = "明天"
        } else {
            when (getDayOfWeek(dateTime)) {
                1 -> week = "星期日"
                2 -> week = "星期一"
                3 -> week = "星期二"
                4 -> week = "星期三"
                5 -> week = "星期四"
                6 -> week = "星期五"
                7 -> week = "星期六"
            }
        }
        return week
    }

    /**
     * 时间截取
     * @param date
     * @return
     */
    fun dateSplit(date: String): String { //2020-08-04
        var result: String? = null
        val array = date.split("-").toTypedArray()
        result = array[1] + "/" + array[2]
        return result
    }

    /**
     * 时间截取plus
     * @param date 时间
     * @return
     */
    fun dateSplitPlus(date: String): String { //2020-08-07
        var result: String? = null
        val array = date.split("-").toTypedArray()
        result = array[1].toInt().toString() + "月" + array[2].toInt() + "号"
        return result
    }

    /**
     * 将时间戳转化为对应的时间(10位或者13位都可以)
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun formatTime(time: Long): String? {
        var times: String? = null
        times = if (time.toString().length > dit) {
            // 10位的秒级别的时间戳
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Date(time * 1000))
        } else { // 13位的秒级别的时间戳
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time)
        }
        return times
    }

    /**
     * 将时间字符串转为时间戳字符串
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun getStringTimestamp(time: String?): String? {
        var timestamp: String? = null
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val longTime = sdf.parse(time).time / 1000
            timestamp = java.lang.Long.toString(longTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timestamp
    }
}