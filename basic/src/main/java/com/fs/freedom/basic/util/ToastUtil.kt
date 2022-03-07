package com.fs.freedom.basic.util

import android.content.Context
import android.widget.Toast

/**
 * 吐司工具类
 */
object ToastUtil {

    private var mToast: Toast? = null

    /**
     * 弹出吐司提示
     */
    fun showToast(
        context: Context?,
        msg: Any?,
        duration: Int = Toast.LENGTH_SHORT) {
        if (context == null || msg == null) {
            return
        }

        mToast?.cancel()
        mToast = Toast.makeText(context, "$msg", duration)
        mToast!!.show()
    }

    /**
     * 隐藏吐司提示
     */
    fun hideToast() {
        mToast?.cancel()
    }

}