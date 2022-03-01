package com.fs.freedom.common_helper

import android.media.Ringtone
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.fs.freedom.basic.BasicInitial
import com.fs.freedom.basic.helper.SystemHelper
import com.fs.freedom.basic.listener.SystemRingtoneListener
import com.fs.freedom.basic.util.ToastUtil
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BasicInitial.initial(isCanLogInBasicModel = true)

        val btnFirst = findViewById<Button>(R.id.btn_first)
        val btnSecond = findViewById<Button>(R.id.btn_second)

        var ringtone: Ringtone? = null
        btnFirst.setOnClickListener {
            ringtone?.stop()
        }
        btnSecond.setOnClickListener {
            SystemHelper.getSystemRingtoneMap(
                this,
                systemRingtoneListener = object : SystemRingtoneListener {
                    override fun onSuccess(map: Map<String, Uri>) {
                        val index = Random.nextInt(from = 0, until = map.size)
                        val values = map.values.toList()
                        val uri = values[index]
                        ringtone =
                            SystemHelper.playSystemRingtone(this@MainActivity, assignUri = uri)
                    }

                    override fun onError(message: String) {
                        super.onError(message)
                        ToastUtil.showToast(this@MainActivity, message)
                    }
                })

        }

    }
}