package com.fs.freedom.basic.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.fs.freedom.basic.constant.CommonConstant
import com.fs.freedom.basic.expand.smartLog
import com.fs.freedom.basic.util.LogUtil
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import java.io.File
import java.lang.RuntimeException
import kotlin.Exception

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
            val vibe = VibrationEffect.createOneShot(
                millSeconds,
                amplitude ?: VibrationEffect.DEFAULT_AMPLITUDE
            )
            vibrator.vibrate(vibe)
        } else {
            val pattern = longArrayOf(millSeconds, 0, 0)
            vibrator.vibrate(pattern, -1)
        }

        return true
    }

    /**
     * 安装apk
     *
     * 使用此功能需要在清单文件中配置 FileProvider
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun installApk(
        activity: FragmentActivity,
        apkFile: File?,
        explainContent: String = "您必须同意 '应用内安装其他应用' 权限才能完成升级",
        positiveText: String = "确认",
        negativeText: String = "取消",
    ) {
        if (apkFile == null || !apkFile.exists()) {
            LogUtil.logI("installApk: install failed, apk file == null or it's not exists")
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //检测当前机型是否支持跳转到 设置详情页，如不支持，则直接进行安装
            try {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.data = Uri.parse("package:${activity.packageName}")
                val resolveActivity = intent.resolveActivity(activity.packageManager)
                if (resolveActivity == null) {
                    toInstallApk(activity, apkFile)
                    return
                }
            } catch (e: RuntimeException) {
                smartLog { e.printStackTrace() }
                toInstallApk(activity, apkFile)
                return
            }

            val isGranted =
                PermissionX.isGranted(activity, Manifest.permission.REQUEST_INSTALL_PACKAGES)
            if (!isGranted) {
                PermissionX.init(activity).permissions(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                    .onExplainRequestReason { scope, deniedlist ->
                        scope.showRequestReasonDialog(
                            deniedlist, explainContent, positiveText, negativeText)
                    }
                    .request { allGranted, _, _ ->
                        if (!allGranted) {
                            return@request
                        }
                        toInstallApk(activity, apkFile)
                    }
            } else {
                toInstallApk(activity, apkFile)
            }
        } else {
            toInstallApk(activity, apkFile)
        }
    }

    @SuppressLint("SetWorldReadable", "SetWorldWritable")
    private fun toInstallApk(activity: Activity, apkFile: File) {
        try {
            apkFile.setExecutable(true, false)
            apkFile.setReadable(true, false)
            apkFile.setWritable(true, false)
        } catch (e: Exception) {
            smartLog { e.printStackTrace() }
        }
        try {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.action = Intent.ACTION_VIEW

            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", apkFile)
            } else {
                Uri.fromFile(apkFile)
            }

            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            smartLog { e.printStackTrace() }
        }
    }

}