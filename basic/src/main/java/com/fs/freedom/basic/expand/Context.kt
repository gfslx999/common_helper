package com.fs.freedom.basic.expand

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

fun Context?.checkHasPermission(permission: String) : Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return true
    }
    this?.let {
        val result = it.checkSelfPermission(permission)
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true
        }
    }
    return false
}