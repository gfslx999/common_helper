package com.fs.freedom.basic

import com.fs.freedom.basic.util.LogUtil

class BasicInitial {

    companion object {
        /**
         * 初始化 basic
         */
        fun initial(
            isCanLogInBasicModel: Boolean = true
        ) {
            LogUtil.isCanLog = isCanLogInBasicModel
        }
    }

}