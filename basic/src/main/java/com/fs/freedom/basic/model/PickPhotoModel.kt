package com.fs.freedom.basic.model

import com.fs.freedom.basic.listener.CommonResultListener
import com.fs.freedom.basic.ui.PickPhotoType

data class PickPhotoModel(
    val maxNum: Int = 1,
    val pickPhotoType: PickPhotoType = PickPhotoType.ALL,
    val pickPhotoListener: CommonResultListener<String>? = null,
)