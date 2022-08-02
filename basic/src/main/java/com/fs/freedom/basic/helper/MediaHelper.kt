package com.fs.freedom.basic.helper

import android.Manifest
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.fs.freedom.basic.constant.CommonConstant
import com.fs.freedom.basic.helper.internal.SystemRingtoneHelper
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.model.SystemRingtoneModel
import com.fs.freedom.basic.ui.PickPhotoType
import com.fs.freedom.basic.ui.TransparentFragment

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
     * 选择图片或视频
     *
     * Android 13以下的版本，需要申请 [Manifest.permission.READ_EXTERNAL_STORAGE] 才可以获取真实路径
     * [maxNum] 最大选择数，仅在 Android 13上生效，Android 13以下仅能控制是否多选，无法控制具体数量
     * [pickType] 选择类型，参考[PickPhotoType]
     * [isRequestPermissionSelf] Android 13 以下，如果没有 READ_EXTERNAL_STORAGE 权限，是否自动申请
     * [listener] 成功时回调 'onSuccess(resultList: List<T>)'
     *
     * 错误处理：
     * 1. onError 回调 [CommonConstant.ERROR_USER_DENIED_PERMISSION] 时代表自动申请了权限，但是用户拒绝了权限
     * 2. onError 回调 [CommonConstant.ERROR_PERMISSION_IS_DENIED] 时，代表[isRequestPermissionSelf] 为false，并且权限状态为拒绝，
     * 3. 其余情况为异常信息
     */
    fun pickPhoto(
        activity: FragmentActivity,
        maxNum: Int = 1,
        pickType: PickPhotoType = PickPhotoType.ALL,
        listener: CommonResultListener<String>,
        isRequestPermissionSelf: Boolean = true
    ) {
        val transparentFragment = TransparentFragment.getInstanceAndBindSelf(activity)
        transparentFragment.toPickPhoto(maxNum, pickType, listener, isRequestPermissionSelf = isRequestPermissionSelf)
    }

    /**
     * =========================Private API==================================
     */

}