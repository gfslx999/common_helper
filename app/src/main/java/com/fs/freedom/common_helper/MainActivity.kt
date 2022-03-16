package com.fs.freedom.common_helper

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.fs.freedom.basic.helper.AppHelper
import com.fs.freedom.basic.helper.DownloadHelper
import com.fs.freedom.basic.helper.MediaHelper
import com.fs.freedom.basic.helper.SystemHelper
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.model.SystemRingtoneModel
import com.fs.freedom.basic.util.LogUtil
import com.fs.freedom.basic.util.ToastUtil
import com.google.gson.Gson
import java.io.File
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private val mFileUrl = "https://hipos.oss-cn-shanghai.aliyuncs.com/hipos-kds-v.5.10.031-g.apk"
    private val mFileName = "newApk.apk"
    private val mFilePath by lazy {
        "${filesDir.path}/updateApk/"
    }
    private var mTag: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFirst = findViewById<Button>(R.id.btn_first)
        val btnSecond = findViewById<Button>(R.id.btn_second)

        btnFirst.text = "开始下载"
        btnSecond.text = "取消下载"
        btnFirst.setOnClickListener {
            testDownloadFile()
        }
        btnSecond.setOnClickListener {
            DownloadHelper.cancelDownload(mTag)
        }

    }

    private fun testDownloadFile() {
        SystemHelper.downloadAndInstallApk(
            this,
            fileUrl = mFileUrl,
            filePath = mFilePath,
            fileName = mFileName,
            commonResultListener = object : CommonResultListener<File> {
                override fun onStart(attachParam: Any?) {
                    if (attachParam is String) {
                        mTag = attachParam
                    }
                }

                override fun onSuccess(result: File) {
                    ToastUtil.showToast(this@MainActivity,"跳转安装界面成功")
                }
                override fun onProgress(currentProgress: Float) {
                    LogUtil.logI("currentProgress: $currentProgress")
                }

                override fun onError(message: String) {
                    ToastUtil.showToast(this@MainActivity, message)
                }
            }
        )
    }

}