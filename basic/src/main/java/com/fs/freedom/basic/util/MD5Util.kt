package com.fs.freedom.basic.util

import java.io.FileInputStream
import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

object MD5Util {

    private const val MD5_KEY = "MD5"

    /**
     * 对字符串md5加密
     */
    fun encryptString(originContent: String) : String {
        return try {
            val digest = MessageDigest.getInstance(MD5_KEY)
            digest.update(originContent.toByte())
            String(ByteUtil.toHex(digest.digest()));
        } catch (e : NoSuchAlgorithmException) {
            if (LogUtil.isCanLog) {
                e.printStackTrace()
            }
            ""
        }
    }

    /**
     * 获取文件的MD5值
     */
    fun encryptFile(path: String?): String {
        if (path.isNullOrEmpty()) {
            return ""
        }
        try {
            val fis = FileInputStream(path)
            val md = MessageDigest.getInstance("MD5")
            val buffer = ByteArray(1024)
            var length: Int
            while (fis.read(buffer, 0, 1024).also { length = it } != -1) {
                md.update(buffer, 0, length)
            }
            val bigInt = BigInteger(1, md.digest())
            return bigInt.toString(16).uppercase(Locale.getDefault())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }



}