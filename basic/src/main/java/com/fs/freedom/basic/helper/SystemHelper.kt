package com.fs.freedom.basic.helper

import android.app.Service
import android.content.Context
import android.os.*
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

    /**
     * 控制手机震动
     * [millSeconds] 震动时长
     * [amplitude] 震动强度，默认为 [VibrationEffect.DEFAULT_AMPLITUDE]，或介于 1～255 之间，仅支持 Android 0及以上
     */
    fun callPhoneToShake(
        context: Context?,
        millSeconds: Long = 500,
        amplitude: Int? = null
    ): Boolean {
        if (context == null) {
            return false
        }

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Service.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibe = VibrationEffect.createOneShot(millSeconds, amplitude ?: VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibe)
        } else {
            val pattern = longArrayOf(millSeconds, 0, 0)
            vibrator.vibrate(pattern, -1)
        }

        return true
    }


}