package com.kotlin.library.util

import android.text.TextUtils
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private const val IS_SHOW_LOG = true

private val LINE_SEPARATOR = System.getProperty("line.separator")
private const val JSON_INDENT = 4

private const val V = 0x1
private const val D = 0x2
private const val I = 0x3
private const val W = 0x4
private const val E = 0x5
private const val A = 0x6
private const val JSON = 0x7


fun String.LogV(tag: String? = null) {
    printLog(V, tag, this)
}

fun String.LogD(tag: String? = null) {
    printLog(D, tag, this)
}

fun String.LogI(tag: String? = null) {
    printLog(I, tag, this)
}


fun String.LogW(tag: String? = null) {
    printLog(W, tag, this)
}


fun String.LogE(tag: String? = null) {
    printLog(E, tag, this)
}


fun String.LogA(tag: String? = null) {
    printLog(A, tag, this)
}


fun String.json(tag: String? = null) {
    printLog(JSON, tag, this)
}

fun printLog(type: Int, tagStr: String?, msg: String) {
    if (!IS_SHOW_LOG) {
        return
    }
    val stackTrace =
        Thread.currentThread().stackTrace
    val index = 4
    val className = stackTrace[index].fileName
    var methodName = stackTrace[index].methodName
    val lineNumber = stackTrace[index].lineNumber
    val tag = tagStr ?: className
    methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1)
    val stringBuilder = StringBuilder()
    if (msg != null && type != JSON) {
        stringBuilder.append(msg)
    }
    stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#")
        .append(methodName).append(" ] ")
    val logStr = stringBuilder.toString()
    when (type) {
        V -> Log.v(tag, logStr)
        D -> Log.d(tag, logStr)
        I -> Log.i(tag, logStr)
        W -> Log.w(tag, logStr)
        E -> Log.e(tag, logStr)
        A -> Log.wtf(tag, logStr)
        JSON -> {
            if (TextUtils.isEmpty(msg)) {
                Log.d(tag, "Empty or Null json content")
                return
            }
            var message: String? = null
            try {
                if (msg!!.startsWith("{")) {
                    val jsonObject = JSONObject(msg)
                    message = jsonObject.toString(JSON_INDENT)
                } else if (msg.startsWith("[")) {
                    val jsonArray = JSONArray(msg)
                    message = jsonArray.toString(JSON_INDENT)
                }
            } catch (e: JSONException) {
                Log.e(tag, """${e.cause!!.message}$msg""".trimIndent())
                return
            }
            printLine(tag, true)
            message = logStr + LINE_SEPARATOR + message
            val lines =
                message.split(LINE_SEPARATOR!!).toTypedArray()
            val jsonContent = StringBuilder()
            for (line in lines) {
                jsonContent.append("║ ").append(line).append(LINE_SEPARATOR)
            }
            Log.d(tag, jsonContent.toString())
            printLine(tag, false)
        }
    }
}

fun printLine(tag: String, isTop: Boolean) {
    if (isTop) {
        Log.d(
            tag,
            "╔═══════════════════════════════════════════════════════════════════════════════════════"
        )
    } else {
        Log.d(
            tag,
            "╚═══════════════════════════════════════════════════════════════════════════════════════"
        )
    }
}