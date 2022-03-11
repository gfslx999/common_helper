package com.fs.freedom.common_helper

import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.fs.freedom.basic.helper.AppHelper
import com.fs.freedom.basic.helper.MediaHelper
import com.fs.freedom.basic.helper.SystemHelper
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.model.SystemRingtoneModel
import com.fs.freedom.basic.util.LogUtil
import com.google.gson.Gson
import java.io.File
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFirst = findViewById<Button>(R.id.btn_first)
        val btnSecond = findViewById<Button>(R.id.btn_second)

        btnFirst.text = "到应用商店"
        btnFirst.setOnClickListener {
            val deviceName = SystemHelper.deviceName
            LogUtil.logI("deviceName: $deviceName")
            AppHelper.openAppMarket(this, "com.huawei.appmarket")
        }
        btnSecond.setOnClickListener {
        }

    }

    private fun testDownloadFile() {
        SystemHelper.downloadAndInstallApk(
            this,
            "https://hipos.oss-cn-shanghai.aliyuncs.com/hipos-kds-v.5.10.031-g.apk",
            "${filesDir.path}/updateApk/",
            "newApk.apk",
            commonResultListener = object : CommonResultListener<File> {
                override fun onProgress(currentProgress: Float) {
                    LogUtil.logI("currentProgress: $currentProgress")
                }
            }
        )
    }

}