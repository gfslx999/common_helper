package com.fs.freedom.common_helper

import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.fs.freedom.basic.helper.MediaHelper
import com.fs.freedom.basic.helper.SystemHelper
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.util.LogUtil
import com.fs.freedom.basic.util.ToastUtil
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFirst = findViewById<Button>(R.id.btn_first)
        val btnSecond = findViewById<Button>(R.id.btn_second)

        btnFirst.setOnClickListener {
            MediaHelper.getSystemRingtoneMap(
                this,
                ringtoneType = RingtoneManager.TYPE_NOTIFICATION,
                commonResultListener = object : CommonResultListener {
                    override fun onSuccess(result: Map<String, Uri>) {
                        LogUtil.logI(result.keys)
                        val index = Random.nextInt(from = 0, until = result.size)
                        val values = result.values.toList()
                        val uri = values[index]
                        MediaHelper.playSystemRingtone(this@MainActivity, assignUri = uri)
                    }

                    override fun onError(message: String) {
                        super.onError(message)
                        ToastUtil.showToast(this@MainActivity, message)
                    }
                })
        }
        btnSecond.setOnClickListener {
            SystemHelper.callPhoneToShake(this, amplitude = 255)
        }

    }
}