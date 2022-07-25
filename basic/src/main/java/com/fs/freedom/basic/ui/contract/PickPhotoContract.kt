package com.fs.freedom.basic.ui.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.FragmentActivity
import com.fs.freedom.basic.expand.smartLog
import com.fs.freedom.basic.helper.FileHelper
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.model.PickPhotoModel
import com.fs.freedom.basic.ui.PickPhotoType
import kotlin.RuntimeException

class PickPhotoContract(private val activity: FragmentActivity?) : ActivityResultContract<PickPhotoModel, List<String>>() {

    private var mMaxNum = 1
    private var mPickPhotoListener: CommonResultListener<String>? = null

    override fun createIntent(context: Context, input: PickPhotoModel): Intent {
        mPickPhotoListener = input.pickPhotoListener
        // Android 13 以上打开图片自带选择器
        return if (Build.VERSION.SDK_INT >= 33) {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            val stringPickType = transformPickTypeToString(input.pickPhotoType)
            if (stringPickType.isNotEmpty()) {
                intent.type = stringPickType
            }
            mMaxNum = input.maxNum
            if (input.maxNum > 1) {
                intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, input.maxNum)
            }
            intent
        } else {
            Intent()
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<String> {
        return try {
            if (resultCode == Activity.RESULT_OK) {
                val realPathList = getRealPath(intent)
                mPickPhotoListener?.onSuccess(realPathList)
                realPathList
            } else {
                throw RuntimeException("result code is not ok, resultCode: $resultCode, intent: $intent")
            }
        } catch (e: Exception) {
            mPickPhotoListener?.onError("${e.message}")
            emptyList()
        }
    }

    /**
     * 获取真实路径
     */
    private fun getRealPath(intent: Intent?) : List<String> {
        if (mMaxNum == 1) {
            val uri = intent?.data
            if (uri != null) {
                return listOf(FileHelper.transformUriToRealPath(activity, uri))
            } else {
                throw NullPointerException("Uri is null!")
            }
        } else {
            val itemCount = intent?.clipData?.itemCount ?: 0

            if (itemCount > 0) {
                var i = 0
                val uriList = mutableListOf<String>()
                while (i < itemCount) {
                    val itemAt = intent!!.clipData!!.getItemAt(i)

                    uriList.add(FileHelper.transformUriToRealPath(activity, itemAt.uri))
                    i++
                }
                return uriList
            } else {
                throw RuntimeException("Select item count is 0!")
            }
        }
    }

    private fun transformPickTypeToString(pickType: PickPhotoType) : String {
        return when (pickType) {
            PickPhotoType.ONLY_IMAGE -> "image/*"
            PickPhotoType.ONLY_VIDEO -> "video/*"
            PickPhotoType.ALL -> ""
        }
    }

}