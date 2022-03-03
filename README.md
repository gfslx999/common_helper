# CommonHelper

## 简单使用

```kotlin

BasicInitial.initial(applicationContext);

```

## API 说明

#### DownloadHelper
```kotlin

    /**
     * 下载文件
     *
     * [fileUrl] require 文件远程地址
     * [filePath] require 文件路径，不包含文件名称
     * [fileName] require 文件名称
     * [isDeleteOriginalFile] 是否自动删除已有的同名文件，默认为 true
     * [commonResultListener] require 成功时回调[CommonResultListener.onSuccess (result T)]，确保文件确实存在
     *
     * 注⚠️：本方法不处理权限请求，即如果需要使用应用沙盒以外的路径，需自行处理权限请求
     */
  fun downloadFile(
        fileUrl: String?,
        filePath: String?,
        fileName: String?,
        isDeleteOriginalFile: Boolean = true,
        commonResultListener: CommonResultListener<File>
    )
```

#### SystemHelper

```kotlin

  /**
  * 安装apk
  *
  * 使用此功能需要在清单文件中配置 FileProvider（可参考 项目中 app-src-main-res-xml-file_path_provider.xml及AndroidManifes.xml）
  */
  @SuppressLint("QueryPermissionsNeeded")
  fun installApk(
      activity: FragmentActivity,
      apkFile: File?,
      explainContent: String = "您必须同意 '应用内安装其他应用' 权限才能完成升级",
      positiveText: String = "确认",
      negativeText: String = "取消",
  )
```

#### MediaHelper

```kotlin
    /**
     * 是否正在播放铃声
     */
    val isRingtonePlaying : Boolean
        get() = SystemRingtoneHelper.isRingtonePlaying

    /**
     * 调用手机发出系统铃声
     *
     * 如需停止，可通过返回对象调用stop方法
     * [assignUri] 指定铃声uri，如不指定，则会播放当前系统默认的铃声
     */
    fun playSystemRingtone(context: Context?, assignUri: Uri? = null): Boolean {
        return SystemRingtoneHelper.playSystemRingtone(context, assignUri)
    }

    /**
     * 停止正在播放的系统铃声
     *
     * 该方法通过类内部维护Ringtone对象，来实现停止功能
     */
    fun stopSystemRingtone() : Boolean {
        return SystemRingtoneHelper.stopSystemRingtone()
    }

    /**
     * 获取系统铃声/通知列表
     *
     * [ringtoneType] 铃声类型：
     * [RingtoneManager.TYPE_RINGTONE]: 指代用于电话铃声的声音的类型。
     * [RingtoneManager.TYPE_NOTIFICATION]: 指代用于通知的声音的类型。
     * [RingtoneManager.TYPE_ALARM]: 指代用于警报的声音的类型。
     * [RingtoneManager.TYPE_ALL]: 所有类型的声音。
     *
     * [commonResultListener] result, key 为 铃声名称，value 为 铃声路径。
     * 该方法每个回调都会在调用线程回调。
     * 通过缓存map来提高效率，如有刷新需求，请调用 [clearRingtoneCachesMap]
     */
    fun getSystemRingtoneMap(
        context: Context?,
        ringtoneType: Int,
        commonResultListener: CommonResultListener<String>
    ){
        SystemRingtoneHelper.getSystemRingtoneMap(context, ringtoneType, commonResultListener)
    }

    /**
     * 清空系统铃声缓存
     */
    fun clearRingtoneCachesMap() {
        SystemRingtoneHelper.clearRingtoneCachesMap()
    }
```

```kotlin

```
