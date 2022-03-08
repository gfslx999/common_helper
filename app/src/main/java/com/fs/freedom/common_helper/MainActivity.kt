package com.fs.freedom.common_helper

import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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

        var assignUri: Uri? = null
        btnFirst.setOnClickListener {
            LogUtil.logI("assignUri: $assignUri")
            MediaHelper.playSystemRingtone(this, assignUri)
        }
        btnSecond.setOnClickListener {
            MediaHelper.getSystemRingtoneList(this, RingtoneManager.TYPE_RINGTONE, object : CommonResultListener<SystemRingtoneModel> {
                override fun onSuccess(result: List<SystemRingtoneModel>) {
                    val index = Random.nextInt(0, result.size - 1)
                    assignUri = Uri.parse(result[index].ringtoneUri)
                    val jsonList = Gson().toJson(result)
                    LogUtil.logI("onSuccess jsonList: $jsonList")
                }
            })
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