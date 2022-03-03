package com.fs.freedom.basic.listener

import android.net.Uri

/**
 * 获取系统铃声列表回调
 */
interface CommonResultListener <T> {

    fun onLoading() {}

    fun onHideLoading() {}

    fun onSuccess(result: Map<String, Uri>) {}

    fun onSuccess(result: T) {}

    fun onSuccess(result: List<T>) {}

    fun onProgress(currentProgress: Float) {}

    fun onEmpty() {}

    fun onError(message: String) {}

}