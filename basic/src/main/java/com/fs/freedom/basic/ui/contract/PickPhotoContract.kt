package com.fs.freedom.basic.ui.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.FragmentActivity
import com.fs.freedom.basic.constant.CommonConstant
import com.fs.freedom.basic.expand.smartLog
import com.fs.freedom.basic.helper.FileHelper
import com.fs.freedom.basic.helper.PathHelper
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.model.PickPhotoModel
import com.fs.freedom.basic.ui.PickPhotoType
import com.fs.freedom.basic.util.LogUtil
import kotlin.RuntimeException

internal class PickPhotoContract(private val activity: FragmentActivity?) : ActivityResultContract<PickPhotoModel, List<String>>() {

    private var mMaxNum = 1
    private var mPickPhotoListener: CommonResultListener<String>? = null

    companion object {
        private const val STRING_ONLY_IMAGE = "image/*"
        private const val STRING_ONLY_VIDEO = "video/*"
    }

    override fun createIntent(context: Context, input: PickPhotoModel): Intent {
        mPickPhotoListener = input.pickPhotoListener
        val stringPickType = transformPickTypeToString(input.pickPhotoType)
        mMaxNum = input.maxNum
        // Android 13 以上打开图片自带选择器
        return if (Build.VERSION.SDK_INT >= 33) {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            if (stringPickType.isNotEmpty()) {
                intent.type = stringPickType
            }
            if (input.maxNum > 1) {
                intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, input.maxNum)
            }
            intent
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            if (stringPickType.isNotEmpty()) {
                intent.type = stringPickType
            } else {
                //todo 查找是否有仅选择图片、视频的选项 https://stackoverflow.com/questions/31380013/how-to-pick-image-or-video-on-android-l
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.type = "*/*"
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("", ""))
                }
            }
            // maxNum 大于1时允许多选
            if (input.maxNum > 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            intent
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
        if (intent?.data != null) {
            val uri = intent.data
            if (uri != null) {
                return listOf(PathHelper.transformContentUrlToRealPath(activity, uri))
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

                    uriList.add(PathHelper.transformContentUrlToRealPath(activity, itemAt.uri))
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
            PickPhotoType.ONLY_IMAGE -> STRING_ONLY_IMAGE
            PickPhotoType.ONLY_VIDEO -> STRING_ONLY_VIDEO
            PickPhotoType.ALL -> ""
//            PickPhotoType.ALL -> {
//                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    ""
//                } else {
//
//                }
//            }
        }
    }

}