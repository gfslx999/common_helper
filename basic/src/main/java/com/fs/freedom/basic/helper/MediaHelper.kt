package com.fs.freedom.basic.helper

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import com.fs.freedom.basic.helper.internal.SystemRingtoneHelper
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.model.SystemRingtoneModel

object MediaHelper {

    /**
     * =========================Public API==================================
     */

    /**
     * 是否正在播放铃声
     */
    val isRingtonePlaying : Boolean
        get() = SystemRingtoneHelper.isRingtonePlaying

    /**
     * 调用手机发出系统铃声
     *
     * 如需停止，可通过返回对象调用stop方法
     * [assignUri] 指定铃声uri，如不指定，则会播放当前系统默认的铃声
     */
    fun playSystemRingtone(context: Context?, assignUri: Uri? = null): Boolean {
        return SystemRingtoneHelper.playSystemRingtone(context, assignUri)
    }

    /**
     * 停止正在播放的系统铃声
     *
     * 该方法通过类内部维护Ringtone对象，来实现停止功能
     */
    fun stopSystemRingtone() : Boolean {
        return SystemRingtoneHelper.stopSystemRingtone()
    }

    /**
     * 获取系统铃声/通知列表
     *
     * [ringtoneType] 铃声类型：
     * [RingtoneManager.TYPE_RINGTONE]: 指代用于电话铃声的声音的类型。
     * [RingtoneManager.TYPE_NOTIFICATION]: 指代用于通知的声音的类型。
     * [RingtoneManager.TYPE_ALARM]: 指代用于警报的声音的类型。
     * [RingtoneManager.TYPE_ALL]: 所有类型的声音。
     *
     * 该方法每个回调都会在调用线程回调。
     */
    fun getSystemRingtoneList(
        context: Context?,
        ringtoneType: Int,
        commonResultListener: CommonResultListener<SystemRingtoneModel>
    ){
        SystemRingtoneHelper.getSystemRingtoneList(context, ringtoneType, commonResultListener)
    }

    /**
     * =========================Private API==================================
     */

}