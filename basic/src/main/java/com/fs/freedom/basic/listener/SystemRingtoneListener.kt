package com.fs.freedom.basic.listener

import android.net.Uri

interface SystemRingtoneListener {

    fun onLoading() {}

    fun onHideLoading() {}

    fun onSuccess(map: Map<String, Uri>)

    fun onEmpty() {}

    fun onError(message: String) {}

}