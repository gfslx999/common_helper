package com.fs.freedom.basic.helper

import android.app.Service
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.text.TextUtils
import com.fs.freedom.basic.constant.CommonConstant
import com.fs.freedom.basic.listener.SystemRingtoneListener
import com.fs.freedom.basic.util.LogUtil

object SystemHelper {

    private var mCachesRingtoneMap = mutableMapOf<String, Uri>()

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
     * 调用手机发出系统铃声
     * 如需停止，可通过返回对象调用stop方法
     * [assignUri] 指定铃声uri
     */
    fun playSystemRingtone(context: Context?, assignUri: Uri? = null): Ringtone? {
        if (context == null) {
            return null
        }
        //todo 修改-暂停铃声方式需调整为内部处理
        return try {
            val ringtone = RingtoneManager.getRingtone(
                context,
                assignUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            )
            ringtone.play()
            ringtone
        } catch (e: RuntimeException) {
            if (LogUtil.isCanLog) {
                e.printStackTrace()
            }
            null
        }
    }

    /**
     * 调用手机震动
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
            val createOneShot = VibrationEffect.createOneShot(millSeconds, amplitude ?: VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(createOneShot)
        } else {
            val pattern = longArrayOf(millSeconds, 0, 0)
            vibrator.vibrate(pattern, -1)
        }

        return true
    }

    /**
     * 获取系统铃声列表
     * key 为 铃声名称；
     * value 为 铃声路径；
     * 该方法每个回调都会在调用线程回调。
     * 通过缓存map来提高效率，如有刷新需求，请调用 [clearRingtoneCachesMap]
     */
    fun getSystemRingtoneMap(context: Context?, systemRingtoneListener: SystemRingtoneListener){
        if (context == null) {
            systemRingtoneListener.onError("Context is null!")
            return
        }
        val isInMainThread = AppHelper.checkIsInMainThread()
        if (mCachesRingtoneMap.isNotEmpty()) {
            handleRingtoneResult(mCachesRingtoneMap, systemRingtoneListener, isInMainThread)
            return
        }
        systemRingtoneListener.onLoading()
        Thread {
            val titleAndPathMap = mutableMapOf<String, Uri>()

            val manager = RingtoneManager(context)
            val cursor = manager.cursor
            if (cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    val ringtoneTitle = manager.getRingtone(cursor.position).getTitle(context)
                    val ringtonePath = manager.getRingtoneUri(cursor.position)

                    titleAndPathMap[ringtoneTitle] = ringtonePath
                }
            }
            mCachesRingtoneMap = titleAndPathMap

            handleRingtoneResult(titleAndPathMap, systemRingtoneListener, isInMainThread)
        }.start()
    }

    /**
     * 清空系统铃声缓存
     */
    fun clearRingtoneCachesMap() {
        if (mCachesRingtoneMap.isNotEmpty()) {
            mCachesRingtoneMap.clear()
        }
    }

    /**
     * 处理获取系统铃声返回
     */
    private fun handleRingtoneResult(
        titleAndPathMap: MutableMap<String, Uri>,
        systemRingtoneListener: SystemRingtoneListener,
        isInMainThreadOriginal: Boolean) {
        //检测之前是否位于主线程
        if (isInMainThreadOriginal) {
            AppHelper.runOnUiThread {
                systemRingtoneListener.onHideLoading()
                if (titleAndPathMap.isNotEmpty()) {
                    systemRingtoneListener.onSuccess(titleAndPathMap)
                } else {
                    systemRingtoneListener.onEmpty()
                }
            }
        } else{
            systemRingtoneListener.onHideLoading()
            if (titleAndPathMap.isNotEmpty()) {
                systemRingtoneListener.onSuccess(titleAndPathMap)
            } else {
                systemRingtoneListener.onEmpty()
            }
        }
    }


}