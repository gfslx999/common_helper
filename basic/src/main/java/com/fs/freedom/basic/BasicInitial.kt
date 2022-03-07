package com.fs.freedom.basic

import android.app.Application
import android.content.Context
import com.fs.freedom.basic.util.LogUtil
import com.lzy.okgo.OkGo

class BasicInitial {

    companion object {
        /**
         * 初始化 basic
         *
         * [isCanLogInBasicModel] 是否允许打印，
         * 如为false，则basic中所有的异常信息和您在项目中通过LogUtil的打印都将不打印
         */
        fun initial(
            applicationContext: Application? = null,
            isCanLogInBasicModel: Boolean = true
        ) {
            if (applicationContext != null) {
                OkGo.getInstance().init(applicationContext)
            }
            LogUtil.isCanLog = isCanLogInBasicModel
        }
    }

}