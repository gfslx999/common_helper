package com.fs.freedom.basic.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
        // Android 13及以上直接使用图片选择器，无需获取权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mPickPhotoLauncher = registerForActivityResult(PickPhotoContract(activity)) { _ -> }
        } else {
            mPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isAgreed ->
                if (isAgreed) {

                }
            }
        }
    }

    /**
     * 执行选择图片操作
     *
     * [maxNum] 最大选择数
     * [pickType] 选择类型，参考[PickPhotoType]
     * [isRequestPermissionSelf] 如果没有 READ_EXTERNAL_STORAGE 权限，是否自动申请
     */
    fun toPickPhoto(
        maxNum: Int = 1,
        pickType: PickPhotoType = PickPhotoType.ALL,
        listener: CommonResultListener<String>,
        isRequestPermissionSelf: Boolean = true
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mPickPhotoLauncher.launch(
                PickPhotoModel(
                    maxNum = maxNum,
                    pickPhotoType = pickType,
                    pickPhotoListener = listener)
            )
        } else {
            val hasPermission =
                activity.checkHasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (isRequestPermissionSelf && !hasPermission) {
                //todo 自动申请权限，并且当前无权限
            } else if (!hasPermission) {
                //todo 不自动申请权限，并且当前无权限
            } else {
                //todo 当前有权限
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