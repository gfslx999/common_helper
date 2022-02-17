package com.fs.freedom.basic.util

import android.content.Context
import android.widget.Toast

/**
 * 吐司工具类
 */
object ToastUtil {

    private var mToast: Toast? = null

    fun showToast(
        context: Context?,
        msg: Any?,
        duration: Int = Toast.LENGTH_SHORT) {
        if (context == null || msg == null) {
            return
        }

        // 防止出现重复显示
        mToast = if (mToast == null) {
            Toast.makeText(context, "$msg", duration)
        } else {
            mToast?.cancel()
            Toast.makeText(context, "$msg", duration)
        }
        mToast?.show()
    }

    fun hideToast() {
        mToast?.cancel()
    }

}