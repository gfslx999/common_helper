package com.fs.freedom.common_helper

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.fs.freedom.basic.helper.MediaHelper
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.ui.PickPhotoType

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity_"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFirst = findViewById<Button>(R.id.btn_first)
        btnFirst.text = "打开图片选择器"
        btnFirst.setOnClickListener {
            MediaHelper.pickPhoto(this, maxNum = 3, pickType = PickPhotoType.ALL, listener = object : CommonResultListener<String> {
                override fun onSuccess(resultList: List<String>) {
                    Log.i(TAG, "onSelectMulti.onSelectMulti: $resultList")
                }

                override fun onError(message: String) {
                    Log.i(TAG, "onError.errorInfo: $message")
                }
            })
        }
    }

}