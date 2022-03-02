package com.fs.freedom.basic.listener

import android.net.Uri

/**
 * 获取系统铃声列表回调
 */
interface CommonResultListener {

    fun onLoading() {}

    fun onHideLoading() {}

    fun onSuccess(result: Map<String, Uri>)

    fun onEmpty() {}

    fun onError(message: String) {}

}