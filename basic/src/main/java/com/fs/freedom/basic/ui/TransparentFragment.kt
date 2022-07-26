package com.fs.freedom.basic.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.fs.freedom.basic.constant.CommonConstant
import com.fs.freedom.basic.expand.checkHasPermission
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.model.PickPhotoModel
import com.fs.freedom.basic.ui.contract.PickPhotoContract

internal class TransparentFragment : Fragment() {

    companion object {
        /**
         * 获取当前 Fragment 实例，并绑定到传入的 activity
         */
        fun getInstanceAndBindSelf(activity: FragmentActivity, tag: String = "add_fragment") : TransparentFragment {
            val transparentFragment = TransparentFragment()
            activity.supportFragmentManager
                .beginTransaction()
                .add(transparentFragment, tag)
                .commitNow()

            return transparentFragment
        }
    }

    private var mPickPhotoModel: PickPhotoModel? = null

    private lateinit var mPickPhotoLauncher: ActivityResultLauncher<PickPhotoModel>
    private lateinit var mPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerAllLauncher()
    }

    /**
     * 注册所有的 [ActivityResultLauncher]
     */
    private fun registerAllLauncher() {
        mPickPhotoLauncher = registerForActivityResult(PickPhotoContract(activity)) {

        }

        // Android 13及以上直接使用图片选择器，无需获取权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            mPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isAgreed ->
                if (isAgreed) {
                    mPickPhotoLauncher.launch(mPickPhotoModel)
                } else {
                    mPickPhotoModel?.pickPhotoListener?.onError(CommonConstant.ERROR_USER_DENIED_PERMISSION)
                }
            }
        }
    }

    /**
     * 选择图片或视频
     */
    fun toPickPhoto(
        maxNum: Int,
        pickType: PickPhotoType,
        listener: CommonResultListener<String>,
        isRequestPermissionSelf: Boolean
    ) {
        mPickPhotoModel = PickPhotoModel(
            maxNum = maxNum,
            pickPhotoType = pickType,
            pickPhotoListener = listener
        )
        // Android 13 以下需要'读取外部存储'权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mPickPhotoLauncher.launch(mPickPhotoModel)
        } else {
            val hasPermission =
                activity.checkHasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (isRequestPermissionSelf && !hasPermission) {
                //需要申请权限并且当前无权限，去申请
                mPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else if (!hasPermission) {
                //无需申请权限并且当前无权限，回调异常
                mPickPhotoModel!!.pickPhotoListener?.onError(CommonConstant.ERROR_PERMISSION_IS_DENIED)
            } else {
                //有权限，去选择
                mPickPhotoLauncher.launch(mPickPhotoModel)
            }
        }
    }

}

enum class PickPhotoType {
    //仅展示图片
    ONLY_IMAGE,
    //仅展示视频
    ONLY_VIDEO,
    //默认全部展示
    ALL
}