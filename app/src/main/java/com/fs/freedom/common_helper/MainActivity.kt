package com.fs.freedom.common_helper

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.fs.freedom.basic.helper.DownloadHelper
import com.fs.freedom.basic.helper.SystemHelper
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.util.LogUtil
import java.io.File


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFirst = findViewById<Button>(R.id.btn_first)
        val btnSecond = findViewById<Button>(R.id.btn_second)

        btnFirst.setOnClickListener {
            testDownloadFile()
        }
        btnSecond.setOnClickListener {
            SystemHelper.callPhoneToShake(this, amplitude = 255)
        }

    }

    private fun testDownloadFile() {
        DownloadHelper.downloadFile(
            "https://hipos.oss-cn-shanghai.aliyuncs.com/hipos-kds-v.5.10.031-g.apk",
            "${filesDir.path}/updateApk/",
            "newApk.apk",
            commonResultListener = object : CommonResultListener<File> {
                override fun onSuccess(result: File) {
                    LogUtil.logI("onSuccess")
                    SystemHelper.installApk(this@MainActivity, result)
                }

                override fun onError(message: String) {
                    LogUtil.logI("message: $message")
                }

                override fun onProgress(currentProgress: Float) {
//                    LogUtil.logI("currentProgress: $currentProgress")
                }
            }
        )
    }

}