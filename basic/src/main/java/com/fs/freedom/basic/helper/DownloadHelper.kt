package com.fs.freedom.basic.helper

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.fs.freedom.basic.constant.CommonConstant
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.model.DownloadingFileModel
import com.fs.freedom.basic.util.LogUtil
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import java.io.File

object DownloadHelper {

    private val mDownloadingList = mutableListOf<DownloadingFileModel>()

    /**
     * 下载文件
     *
     * [fileUrl] require 文件远程地址
     * [filePath] require 文件路径，不包含文件名称
     * [fileName] require 文件名称
     * [isDeleteOriginalFile] 是否自动删除已有的同名文件，默认为 true
     * [commonResultListener] require 成功时回调[CommonResultListener.onSuccess (result T)]，确保文件确实存在
     *
     * 如果连续调用多次此方法，并且[fileUrl]、[filePath]、[fileName]完全一致，则会禁止继续下载，回调onError = [CommonConstant.ERROR_SAME_FILE_DOWNLOADED]
     * 如需取消请求，可以调用[cancelDownload]，tag: 使用 onStart 中回调的参数。
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
        val downloadingFileModel =
            DownloadingFileModel(fileUrl = fileUrl, fileName = fileName, filePath = filePath)
        if (mDownloadingList.contains(downloadingFileModel)) {
            commonResultListener.onError(CommonConstant.ERROR_SAME_FILE_DOWNLOADED)
            return
        }
        mDownloadingList.add(downloadingFileModel)
        if (isDeleteOriginalFile) {
            val originalFile = File("$filePath$fileName")
            if (originalFile.exists()) {
                originalFile.delete()
            }
        }

        OkGo.get<File>(fileUrl)
            .tag("$filePath/$fileName")
            .execute(object : FileCallback(filePath, fileName) {
                override fun onStart(request: Request<File, out Request<Any, Request<*, *>>>?) {
                    commonResultListener.onStart(attachParam = "$filePath/$fileName")
                }

                override fun onSuccess(response: Response<File>?) {
                    mDownloadingList.remove(downloadingFileModel)

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
                    mDownloadingList.remove(downloadingFileModel)

                    commonResultListener.onError(response?.message() ?: "Download failed!")
                }

                override fun downloadProgress(progress: Progress?) {
                    super.downloadProgress(progress)
                    progress?.let {
                        val currentProgress =
                            (it.currentSize.toFloat() / it.totalSize.toFloat()) * 100
                        commonResultListener.onProgress(currentProgress)
                    }
                }
            })
    }

    /**
     * 取消下载
     *
     * [tag] 调用下载时 commonResultListener.onStart 的回调参数
     */
    fun cancelDownload(tag: String) {
        if (tag.isEmpty()) {
            LogUtil.logE("cancelDownload: tag is empty!")
            return
        }
        val okHttpClient = OkGo.getInstance().okHttpClient
        if (okHttpClient != null) {
            OkGo.cancelTag(okHttpClient, tag)

            val find = mDownloadingList.find { "${it.filePath}/${it.fileName}" == tag }
            if (find != null) {
                mDownloadingList.remove(find)
            } else {
                LogUtil.logE("cancelDownload: current tag $tag, is already canceled")
            }
        }
    }

}