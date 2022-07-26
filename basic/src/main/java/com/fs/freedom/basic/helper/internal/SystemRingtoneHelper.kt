package com.fs.freedom.basic.helper.internal

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import com.fs.freedom.basic.expand.smartLog
import com.fs.freedom.basic.helper.AppHelper
import com.fs.freedom.basic.helper.FileHelper
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.model.SystemRingtoneModel
import com.fs.freedom.basic.util.LogUtil
import java.lang.Exception

/**
 * 系统铃声帮助类
 */
internal object SystemRingtoneHelper {

    private var mRingtone: Ringtone? = null

    /**
     * 是否正在播放铃声
     */
    val isRingtonePlaying : Boolean
        get() = mRingtone?.isPlaying ?: false

    /**
     * 调用手机发出系统铃声
     *
     * 如需停止，可通过返回对象调用stop方法
     * [assignUri] 指定铃声uri，如不指定，则会播放当前系统默认的铃声
     */
    fun playSystemRingtone(context: Context?, assignUri: Uri? = null): Boolean {
        if (context == null) {
            LogUtil.logE("playSystemRingtone: Play failed, context is null!")
            return false
        }
        //防止上一个还没有播放完毕
        stopSystemRingtone()

        return try {
            mRingtone = RingtoneManager.getRingtone(
                context,
                assignUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            )
            mRingtone?.play()
            true
        } catch (e: RuntimeException) {
            smartLog { e.printStackTrace() }
            false
        }
    }

    /**
     * 停止已播放的系统铃声
     *
     * 该方法通过类内部维护Ringtone对象，来实现停止功能
     */
    fun stopSystemRingtone() : Boolean {
        if (mRingtone == null) {
            return false
        }
        if (isRingtonePlaying) {
            mRingtone!!.stop()
            mRingtone = null
            return true
        }
        return false
    }

    /**
     * 获取系统铃声/通知列表
     *
     * [ringtoneType] 铃声类型, 参见 [RingtoneManager].TYPE_XXX
     * [commonResultListener] key 为 铃声名称，value 为 铃声路径。
     * 该方法每个回调都会在调用线程回调。
     */
    fun getSystemRingtoneList(
        context: Context?,
        ringtoneType: Int,
        commonResultListener: CommonResultListener<SystemRingtoneModel>,
    ){
        if (context == null) {
            commonResultListener.onError("Context is null!")
            return
        }
        //传递的type类型是否符合我们预期的类型
        if (ringtoneType != RingtoneManager.TYPE_RINGTONE && ringtoneType != RingtoneManager.TYPE_NOTIFICATION &&
                ringtoneType != RingtoneManager.TYPE_ALARM && ringtoneType != RingtoneManager.TYPE_ALL) {
            commonResultListener.onError("ringtoneType is does not fit any type!")
            return
        }
        val isInMainThread = AppHelper.checkIsInMainThread()

        commonResultListener.onStart()
        Thread {
            try {
                val list = mutableListOf<SystemRingtoneModel>()

                val manager = RingtoneManager(context)
                manager.setType(ringtoneType)
                val cursor = manager.cursor
                //获取到每个铃声信息
                if (cursor.moveToFirst()) {
                    while (cursor.moveToNext()) {
                        val ringtoneTitle = manager.getRingtone(cursor.position).getTitle(context)
                        val ringtoneUri = manager.getRingtoneUri(cursor.position)
                        list.add(SystemRingtoneModel(ringtoneTitle, ringtoneUri.toString()))
                    }
                }
                cursor.close()

                handleRingtoneResult(
                    list,
                    commonResultListener,
                    isInMainThread
                )
            } catch (e: Exception) {
                commonResultListener.onError(e.message ?: "$e")
                smartLog { e.printStackTrace() }
            }
        }.start()
    }

    /**
     * =========================Private API==================================
     */

    /**
     * 处理获取系统铃声返回
     */
    private fun handleRingtoneResult(
        ringtoneModelList: MutableList<SystemRingtoneModel>,
        commonResultListener: CommonResultListener<SystemRingtoneModel>,
        isInMainThreadOriginal: Boolean) {
        //检测之前是否位于主线程
        if (isInMainThreadOriginal) {
            AppHelper.runOnUiThread {
                if (ringtoneModelList.isNotEmpty()) {
                    commonResultListener.onSuccess(ringtoneModelList)
                } else {
                    commonResultListener.onEmpty()
                }
            }
        } else{
            if (ringtoneModelList.isNotEmpty()) {
                commonResultListener.onSuccess(ringtoneModelList)
            } else {
                commonResultListener.onEmpty()
            }
        }
    }

}