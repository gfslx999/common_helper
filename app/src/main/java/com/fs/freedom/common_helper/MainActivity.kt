package com.fs.freedom.common_helper

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.Toast
import com.fs.freedom.basic.helper.NetworkHelper
import com.fs.freedom.basic.util.LogUtil
import com.fs.freedom.basic.util.ToastUtil

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFirst = findViewById<Button>(R.id.btn_first)
        val btnSecond = findViewById<Button>(R.id.btn_second)

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.networkCountryIso
        btnFirst.setOnClickListener {
            val networkState = NetworkHelper.getNetworkState(this)
            LogUtil.logI("networkState: $networkState")
        }
        btnSecond.setOnClickListener {
            ToastUtil.showToast(this,"我才是第一")
        }
    }
}