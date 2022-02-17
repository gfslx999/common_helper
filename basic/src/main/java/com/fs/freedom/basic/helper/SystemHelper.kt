package com.fs.freedom.basic.helper

import android.os.Build
import android.text.TextUtils
import com.fs.freedom.basic.constant.CommonConstant

object SystemHelper {

    /**
     * 获取设备的品牌信息和型号
     */
    val deviceName: String
        get() {
            val deviceName = Build.BRAND + " " + Build.MODEL
            if (TextUtils.isEmpty(deviceName)) {
                return  CommonConstant.UNKNOWN
            }
            return deviceName
        }

}