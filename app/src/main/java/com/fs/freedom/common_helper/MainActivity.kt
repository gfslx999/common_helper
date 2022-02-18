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
import com.google.gson.Gson

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

        val firstChildList = listOf(
            ChildData(useTime = "1"),
            ChildData(useTime = "2"),
            ChildData(useTime = "2"),
            ChildData(useTime = "1"),
            ChildData(useTime = "2"),
            ChildData(useTime = "1"),
            ChildData(useTime = "1"),
            ChildData(useTime = "2"),
            ChildData(useTime = "3"),
        )
        val secondChildList = listOf(
            ChildData(useTime = "2"),
            ChildData(useTime = "2"),
            ChildData(useTime = "2"),
            ChildData(useTime = "2"),
            ChildData(useTime = "2"),
            ChildData(useTime = "1"),
            ChildData(useTime = "1"),
            ChildData(useTime = "2"),
            ChildData(useTime = "1"),
        )
        val thirdChildList = listOf(
            ChildData(useTime = "2"),
            ChildData(useTime = "2"),
            ChildData(useTime = "2"),
        )
        val originalList = listOf(
            TestData(id = 1, name = "aa", childList = firstChildList),
            TestData(id = 2, name = "bb", childList = secondChildList),
            TestData(id = 3, name = "cc", childList = thirdChildList),
        )

        toPackage(originalList)
    }

    private fun toPackage(originalList: List<TestData>?) {
        if (originalList.isNullOrEmpty()) {
            return
        }
        LogUtil.logI("originalList: ${Gson().toJson(originalList)}")
        val firstList = mutableListOf<TestData>()
        val secondList = mutableListOf<TestData>()
        originalList.forEach { parentData ->
            val useOneTimeList: List<ChildData> = parentData.childList.filter { it.useTime == "1" }
            val newData = TestData(
                id = parentData.id,
                name = parentData.name,
                childList = useOneTimeList
            )
            firstList.add(newData)
            val useTwoTimeList: List<ChildData> = parentData.childList.filter { it.useTime == "2" }
            secondList.add(newData.copyOf(newList = useTwoTimeList))
        }
        LogUtil.logI("firstList: ${Gson().toJson(firstList)}")
        LogUtil.logI("secondList: ${Gson().toJson(secondList)}")
    }

}