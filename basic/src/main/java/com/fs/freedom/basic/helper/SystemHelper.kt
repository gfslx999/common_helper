package com.fs.freedom.basic.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.fs.freedom.basic.R
import com.fs.freedom.basic.constant.CommonConstant
import com.fs.freedom.basic.expand.smartLog
import com.fs.freedom.basic.helper.DownloadHelper.cancelDownload
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.util.LogUtil
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import com.permissionx.guolindev.dialog.DefaultDialog
import java.io.File
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import kotlin.Exception

object SystemHelper : Activity() {

    //安装apk前进入 '允许安装其他应用' 时调用
    const val OPEN_INSTALL_PACKAGE_PERMISSION = "OPEN_INSTALL_PACKAGE_PERMISSION"
    //安装apk权限时版本判断条件
    private val installApkJudgeRule: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.R

    /**
     * 获取设备的品牌信息和型号
     */
    val deviceName: String
        get() {
            val deviceName = Build.BRAND + "|" + Build.MODEL
            if (TextUtils.isEmpty(deviceName)) {
                return CommonConstant.UNKNOWN
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
     * 下载并安装apk
     *
     * 如果连续调用多次此方法，并且[fileUrl]、[filePath]、[fileName]完全一致，则不会重复下载，回调onError = [CommonConstant.ERROR_SAME_FILE_DOWNLOADED]
     * 如需取消请求，可以调用[DownloadHelper.cancelDownload]，tag: 使用 onStart 中回调的参数。
     *
     * [commonResultListener] 回调 onSuccess 代表跳转安装界面成功，并不代表安装成功
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun downloadAndInstallApk(
        activity: Activity?,
        fileUrl: String?,
        filePath: String?,
        fileName: String?,
        isDeleteOriginalFile: Boolean = true,
        explainContent: String = "您必须同意 '应用内安装其他应用' 权限才能完成升级",
        positiveText: String = "确认",
        negativeText: String = "取消",
        commonResultListener: CommonResultListener<File>
    ) {
        if (activity == null) {
            commonResultListener.onError("context is null!")
            return
        }
        commonResultListener.onStart()
        if (installApkJudgeRule) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            intent.data = Uri.parse("package:${activity.packageName}")
            val resolveActivity = intent.resolveActivity(activity.packageManager)
            if (resolveActivity != null) {
                val isHasPermission = activity.packageManager?.canRequestPackageInstalls() ?: false
                if (!isHasPermission) {
                    //如果为 FragmentActivity 可直接通过三方库来完成；
                    //如果不是，则需要自己在Activity中根据 onError 回调值是否为[OPEN_INSTALL_PACKAGE_PERMISSION]，
                    //在onResume中再次调用此方法
                    if (activity is FragmentActivity) {
                        PermissionX.init(activity).permissions(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                            .onExplainRequestReason { scope, deniedlist ->
                                scope.showRequestReasonDialog(
                                    deniedlist, explainContent, positiveText, negativeText)
                            }.request { allGranted, _, _ ->
                                if (!allGranted) {
                                    return@request
                                }
                                //如同意权限了，再次调用此方法，达到继续刚才方法执行的目的
                                downloadAndInstallApk(activity, fileUrl, filePath, fileName, isDeleteOriginalFile, explainContent, positiveText, negativeText, commonResultListener)
                            }
                    } else {
                        commonResultListener.onError(OPEN_INSTALL_PACKAGE_PERMISSION)
                        intoManageUnknownAppPage(
                            activity,
                            explainContent = explainContent,
                            positiveText = positiveText,
                            negativeText = negativeText,
                            commonResultListener = commonResultListener
                        )
                    }
                    return
                }
            }
        }

        DownloadHelper.downloadFile(fileUrl, filePath, fileName, isDeleteOriginalFile, object :CommonResultListener<File> {
            override fun onStart(attachParam: Any?) {
                commonResultListener.onStart(attachParam)
            }

            override fun onSuccess(result: File) {
                installApk(
                    activity,
                    apkFile = result,
                    explainContent = explainContent,
                    positiveText = positiveText,
                    negativeText = negativeText,
                    commonResultListener = commonResultListener
                )
            }

            override fun onError(message: String) {
                commonResultListener.onError(message)
            }

            override fun onProgress(currentProgress: Float) {
                commonResultListener.onProgress(currentProgress)
            }
        })
    }

    /**
     * 安装apk
     *
     * 使用此功能需要在清单文件中配置 FileProvider
     * [commonResultListener] 仅回调 onStart、onError
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun installApk(
        activity: Activity?,
        apkFile: File?,
        explainContent: String = "您必须同意 '应用内安装其他应用' 权限才能完成升级",
        positiveText: String = "确认",
        negativeText: String = "取消",
        commonResultListener: CommonResultListener<File>? = null
    ) {
        if (activity == null) {
            LogUtil.logE("installApk: activity is null!")
            return
        }
        if (apkFile == null || !apkFile.exists()) {
            LogUtil.logE("installApk: install failed, apk file == null or it's not exists")
            return
        }
        commonResultListener?.onStart()
        if (installApkJudgeRule) {
            //检测当前机型是否支持跳转到 设置详情页，如不支持，则直接进行安装
            try {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.data = Uri.parse("package:${activity.packageName}")
                val resolveActivity = intent.resolveActivity(activity.packageManager)
                if (resolveActivity == null) {
                    toInstallApk(activity, apkFile, commonResultListener)
                    return
                }
            } catch (e: RuntimeException) {
                smartLog { e.printStackTrace() }
                toInstallApk(activity, apkFile, commonResultListener)
                return
            }

            val isGranted = activity.packageManager.canRequestPackageInstalls()
            if (isGranted) {
                toInstallApk(activity, apkFile, commonResultListener)
                return
            }

            if (activity is FragmentActivity) {
                PermissionX.init(activity).permissions(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                    .onExplainRequestReason { scope, deniedlist ->
                        scope.showRequestReasonDialog(
                            deniedlist, explainContent, positiveText, negativeText)
                    }
                    .request { allGranted, _, _ ->
                        if (!allGranted) {
                            return@request
                        }
                        toInstallApk(activity, apkFile, commonResultListener)
                    }
            } else {
                commonResultListener?.onError(OPEN_INSTALL_PACKAGE_PERMISSION)
                intoManageUnknownAppPage(
                    activity,
                    apkFile,
                    explainContent = explainContent,
                    positiveText = positiveText,
                    negativeText = negativeText,
                    commonResultListener = commonResultListener
                )
            }
        } else {
            toInstallApk(activity, apkFile, commonResultListener)
        }
    }

    //test
    @SuppressLint("QueryPermissionsNeeded")
    fun getAllAppNamesAndPackages(activity: Activity?) {
        if (activity == null) {
            LogUtil.logE("getAllAppNamesAndPackages: activity is null!")
            return
        }
        val installedApplications = activity.packageManager.getInstalledApplications(0)
        for (application in installedApplications) {
            val applicationLabel = activity.packageManager.getApplicationLabel(application)
            LogUtil.logI("${applicationLabel}:${application.packageName}")
        }
    }

    /**
     * ========================== PrivateApi ==========================
     */

    /**
     * 弹出弹窗提示，确认后，进入管理 '允许安装其他应用' 权限界面
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun intoManageUnknownAppPage(
        activity: Activity,
        apkFile: File? = null,
        explainContent: String = "您必须同意 '应用内安装其他应用' 权限才能完成升级",
        positiveText: String = "确认",
        negativeText: String = "取消",
        commonResultListener: CommonResultListener<File>? = null
    ) {
        //弹出弹窗提示
        val defaultDialog = DefaultDialog(
            activity,
            listOf(Manifest.permission.REQUEST_INSTALL_PACKAGES),
            message = explainContent,
            positiveText = positiveText,
            negativeText = negativeText,
            -1,
            -1
        )
        defaultDialog.show()

        defaultDialog.positiveButton.setOnClickListener {
            defaultDialog.dismiss()
            try {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.data = Uri.parse("package:${activity.packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                smartLog { e.printStackTrace() }
                if (apkFile != null) {
                    toInstallApk(activity, apkFile, commonResultListener = commonResultListener)
                }
            }
        }
        defaultDialog.negativeButton?.setOnClickListener {
            defaultDialog.dismiss()
        }
    }

    /**
     * 进入系统安装应用界面
     */
    @SuppressLint("SetWorldReadable", "SetWorldWritable")
    private fun toInstallApk(activity: Activity, apkFile: File, commonResultListener: CommonResultListener<File>? = null) {
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

            commonResultListener?.onSuccess(apkFile)
        } catch (e: ActivityNotFoundException) {
            commonResultListener?.onError("安装失败：${e.message}")
            smartLog { e.printStackTrace() }
        } catch (e: IllegalArgumentException) {
            commonResultListener?.onError("安装失败：${e.message}")
            smartLog { e.printStackTrace() }
        }
    }

}