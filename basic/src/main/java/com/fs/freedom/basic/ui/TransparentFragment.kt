package com.fs.freedom.basic.ui

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.model.PickPhotoModel
import com.fs.freedom.basic.ui.contract.PickPhotoContract

class TransparentFragment : Fragment() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPickPhotoLauncher = registerForActivityResult(PickPhotoContract(activity)) { _ -> }
    }

    /**
     * 执行选择图片操作
     *
     * [maxNum] 最大选择数
     * [pickType] 选择类型，参考[PickPhotoType]
     */
    fun executePickImage(maxNum: Int = 1, pickType: PickPhotoType = PickPhotoType.ALL, listener: CommonResultListener<String>) {
        mPickPhotoLauncher.launch(
            PickPhotoModel(
                maxNum = maxNum,
                pickPhotoType = pickType,
                pickPhotoListener = listener)
        )
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