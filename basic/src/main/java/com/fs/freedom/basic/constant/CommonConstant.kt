package com.fs.freedom.basic.constant

class CommonConstant {

    companion object {
        internal const val UNKNOWN = "UNKNOWN"

        // 已经有相同文件正在下载
        const val ERROR_SAME_FILE_DOWNLOADED = "The same file has been downloaded."
        // 用户拒绝了权限
        const val ERROR_USER_DENIED_PERMISSION = "ERROR_USER_DENIED_PERMISSION"
        // 权限状态为拒绝
        const val ERROR_PERMISSION_IS_DENIED = "ERROR_PERMISSION_DENIED"

    }

}