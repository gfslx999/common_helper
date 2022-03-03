package com.fs.freedom.basic.helper.internal

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import com.fs.freedom.basic.expand.smartLog
import com.fs.freedom.basic.helper.AppHelper
import com.fs.freedom.basic.helper.FileHelper
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.util.LogUtil

/**
 * 系统铃声帮助类
 */
internal object SystemRingtoneHelper {

    private var mCachesRingtoneMap = mutableMapOf<String, Uri>()
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
            LogUtil.logI("playSystemRingtone: Play failed, context is null!")
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
     * 通过缓存map来提高效率，如有刷新需求，请调用 [clearRingtoneCachesMap]
     */
    fun getSystemRingtoneMap(
        context: Context?,
        ringtoneType: Int,
        commonResultListener: CommonResultListener<String>,
    ){
        if (context == null) {
            commonResultListener.onError("Context is null!")
            return
        }
        if (ringtoneType != RingtoneManager.TYPE_RINGTONE && ringtoneType != RingtoneManager.TYPE_NOTIFICATION &&
                ringtoneType != RingtoneManager.TYPE_ALARM && ringtoneType != RingtoneManager.TYPE_ALL) {
            commonResultListener.onError("ringtoneType is does not fit any type!")
            return
        }
        val isInMainThread = AppHelper.checkIsInMainThread()
        if (mCachesRingtoneMap.isNotEmpty()) {
            handleRingtoneResult(
                mCachesRingtoneMap,
                commonResultListener,
                isInMainThread
            )
            return
        }
        commonResultListener.onLoading()
        Thread {
            val titleAndPathMap = mutableMapOf<String, Uri>()

            val manager = RingtoneManager(context)
            manager.setType(ringtoneType)
            val cursor = manager.cursor
            if (cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    val ringtoneTitle = manager.getRingtone(cursor.position).getTitle(context)
                    val ringtoneUri = manager.getRingtoneUri(cursor.position)
                    titleAndPathMap[ringtoneTitle] = ringtoneUri
                }
            }
            cursor.close()
            mCachesRingtoneMap = titleAndPathMap

            handleRingtoneResult(
                titleAndPathMap,
                commonResultListener,
                isInMainThread
            )
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
     * =========================Private API==================================
     */

    /**
     * 处理获取系统铃声返回
     */
    private fun handleRingtoneResult(
        titleAndPathMap: MutableMap<String, Uri>,
        commonResultListener: CommonResultListener<String>,
        isInMainThreadOriginal: Boolean) {
        //检测之前是否位于主线程
        if (isInMainThreadOriginal) {
            AppHelper.runOnUiThread {
                commonResultListener.onHideLoading()
                if (titleAndPathMap.isNotEmpty()) {
                    commonResultListener.onSuccess(titleAndPathMap)
                } else {
                    commonResultListener.onEmpty()
                }
            }
        } else{
            commonResultListener.onHideLoading()
            if (titleAndPathMap.isNotEmpty()) {
                commonResultListener.onSuccess(titleAndPathMap)
            } else {
                commonResultListener.onEmpty()
            }
        }
    }

}