package com.fs.freedom.basic.listener

import android.net.Uri

/**
 * 通用结果回调
 */
interface CommonResultListener <T> {
    //开始加载
    fun onStart(attachParam: Any? = null) {}

    //加载成功，回调指定泛型对象
    fun onSuccess(resultData: T) {}

    //加载成功，回调指定泛型列表
    fun onSuccess(resultList: List<T>) {}

    //加载进度回调
    fun onProgress(currentProgress: Float) {}

    //回调结果为空
    fun onEmpty() {}

    //加载失败
    fun onError(message: String) {}
}