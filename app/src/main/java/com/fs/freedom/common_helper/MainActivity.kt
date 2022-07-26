package com.fs.freedom.common_helper

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fs.freedom.basic.constant.CommonConstant
import com.fs.freedom.basic.helper.AppHelper
import com.fs.freedom.basic.helper.FileHelper
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
        val tvPhotoList = findViewById<TextView>(R.id.tv_photo_list)
        btnFirst.text = "打开图片选择器"
        btnFirst.setOnClickListener {
            MediaHelper.pickPhoto(this, maxNum = 3, pickType = PickPhotoType.ONLY_IMAGE, listener = object : CommonResultListener<String> {
                override fun onSuccess(resultList: List<String>) {
                    val stringBuilder = StringBuilder()
                    for (s in resultList) {
                        stringBuilder.append("$s\n")
                    }
                    tvPhotoList.text = "$stringBuilder"
                }

                override fun onError(message: String) {
                    when (message) {
                        CommonConstant.ERROR_USER_DENIED_PERMISSION -> {
                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("选择图片失败")
                                .setMessage("缺少'存储权限'，请到设置页面开启存储权限")
                                .setPositiveButton("去开启") { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                    AppHelper.openAppSettingDetail(this@MainActivity)
                                }.setNegativeButton("取消") { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                }.show()
                        }
                        CommonConstant.ERROR_PERMISSION_IS_DENIED -> {
                            //去申请权限，成功后再次调用此方法
                        }
                        else -> {
                            Log.i(TAG, "onError.errorInfo: $message")
                        }
                    }
                }
            })
        }
    }

}