package com.fs.freedom.basic.helper

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.util.LogUtil
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import java.io.File

object DownloadHelper {

    /**
     * 下载文件
     *
     * [fileUrl] require 文件远程地址
     * [filePath] require 文件路径，不包含文件名称
     * [fileName] require 文件名称
     * [isDeleteOriginalFile] 是否自动删除已有的同名文件，默认为 true
     * [commonResultListener] require 成功时回调[CommonResultListener.onSuccess (result T)]，确保文件确实存在
     *
     * 注⚠️：本方法不处理权限请求，即如果需要使用应用沙盒以外的路径，需自行处理权限请求
     */
    fun downloadFile(
        fileUrl: String?,
        filePath: String?,
        fileName: String?,
        isDeleteOriginalFile: Boolean = true,
        commonResultListener: CommonResultListener<File>
    ) {
        if (fileUrl.isNullOrEmpty()) {
            commonResultListener.onError("downloadApk: download failed, apk url is empty!")
            return
        }
        if (filePath.isNullOrEmpty() || fileName.isNullOrEmpty()) {
            commonResultListener.onError("downloadApk: download failed, filePath or fileName is empty!")
            return
        }
        if (isDeleteOriginalFile) {
            val originalFile = File("$filePath$fileName")
            if (originalFile.exists()) {
                originalFile.delete()
            }
        }
        OkGo.get<File>(fileUrl)
            .execute(object : FileCallback(filePath, fileName) {
                override fun onSuccess(response: Response<File>?) {
                    val doneFilePath = response?.body()?.path ?: ""
                    if (doneFilePath.isEmpty()) {
                        commonResultListener.onError("Download failed, file is not exists, doneFilePath: $doneFilePath")
                        return
                    }

                    val file = File(doneFilePath)
                    if (file.exists()) {
                        commonResultListener.onSuccess(file)
                    } else {
                        commonResultListener.onError("Download failed, file is not exists, doneFilePath: $doneFilePath")
                    }
                }

                override fun onError(response: Response<File>?) {
                    super.onError(response)
                    commonResultListener.onError(response?.message() ?: "Download failed!")
                }

                override fun downloadProgress(progress: Progress?) {
                    super.downloadProgress(progress)
                    progress?.let {
                        val currentProgress = (it.currentSize.toFloat() / it.totalSize.toFloat()) * 100
                        commonResultListener.onProgress(currentProgress)
                    }
                }
            })
    }

}