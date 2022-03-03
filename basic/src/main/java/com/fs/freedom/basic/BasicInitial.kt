package com.fs.freedom.basic

import android.app.Application
import android.content.Context
import com.fs.freedom.basic.util.LogUtil
import com.lzy.okgo.OkGo

class BasicInitial {

    companion object {
        /**
         * 初始化 basic
         */
        fun initial(
            applicationContext: Application,
            isCanLogInBasicModel: Boolean = true
        ) {
            OkGo.getInstance().init(applicationContext)
            LogUtil.isCanLog = isCanLogInBasicModel
        }
    }

}