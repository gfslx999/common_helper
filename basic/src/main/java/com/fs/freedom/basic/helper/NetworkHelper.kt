package com.fs.freedom.basic.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.fs.freedom.basic.constant.NetworkState

object NetworkHelper {

    /**
     * 当前网络是否可用
     */
    fun isNetworkAvailable(context: Context) : Boolean {
        val networkInfo = getNetworkInfo(context) ?: return false
        return networkInfo.isAvailable
    }

    /**
     * 获取当前网络状态
     * 参见：[NetworkState]
     */
    fun getNetworkState(context: Context) : NetworkState {
        val networkInfo = getNetworkInfo(context)
        if (networkInfo == null || !networkInfo.isAvailable) {
            return NetworkState.DISABLED
        }

        when (networkInfo.type) {
            ConnectivityManager.TYPE_MOBILE -> {
                return NetworkState.MOBILE
            }
            ConnectivityManager.TYPE_WIFI -> {
                return  NetworkState.WIFI
            }
        }

        return NetworkState.DISABLED
    }

    private fun getNetworkInfo(context: Context) : NetworkInfo? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo
    }

}