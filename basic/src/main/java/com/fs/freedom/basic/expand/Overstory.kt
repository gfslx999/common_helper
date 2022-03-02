package com.fs.freedom.basic.expand

import com.fs.freedom.basic.util.LogUtil

/**
 * 根据是否可以打印log来控制输出
 */
fun smartLog(printFunction:() -> Unit) {
    if (LogUtil.isCanLog) {
        printFunction()
    }
}