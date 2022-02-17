package com.fs.freedom.basic.util

import android.util.Log
import com.fs.freedom.basic.BasicInitial

object LogUtil {
    //basic 中是否可以打印
    internal var isCanLog = true
    private const val REMIND_LINE = "=================================================================="

    fun logI(msg: Any?) {
        if (!isCanLog) {
            return
        }
        Log.i("LogUtil", "\n"+REMIND_LINE)
        Log.i("LogUtil", "$msg")
        Log.i("LogUtil", REMIND_LINE)
    }

    fun logE(msg: Any?) {
        if (!isCanLog) {
            return
        }
        Log.e("LogUtil", REMIND_LINE)
        Log.e("LogUtil", "$msg")
        Log.e("LogUtil", REMIND_LINE)
    }

    fun logD(msg: Any?) {
        if (!isCanLog) {
            return
        }

        Log.d("LogUtil", REMIND_LINE)
        Log.d("LogUtil", "$msg")
        Log.d("LogUtil", REMIND_LINE)
    }

    fun logV(msg: Any?) {
        if (!isCanLog) {
            return
        }
        Log.v("LogUtil", REMIND_LINE)
        Log.v("LogUtil", "$msg")
        Log.v("LogUtil", REMIND_LINE)
    }

}