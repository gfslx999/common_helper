# CommonHelper

## 引入

#### 如果你Gradle Version < 7.0, 在 build.gradle内

```kotlin
repositories {
    maven { url 'https://jitpack.io' }
}
```

#### 如果你Gradle Version >= 7.0, 在 settings.gradle内

```kotlin
repositories {
    maven { url 'https://jitpack.io' }
}
```

[![](https://www.jitpack.io/v/gfslx999/common_helper.svg)](https://www.jitpack.io/#gfslx999/common_helper)

#### 在app/build.gradle

```kotlin
    implementation 'com.github.gfslx999.common_helper:gao:$newVersion' //newVersion就是上面图标里的 v.xx.xx
```

### 初始化

```kotlin
    /**
 * 初始化 basic
 *
 * [isCanLogInBasicModel] 是否允许打印，
 * 如为false，则basic中所有的异常信息和您在项目中通过LogUtil的打印都将不打印
 */
BasicInitial.initial(applicationContext, isCanLogInBasicModel = true)
```

### 如果你要使用安装apk相关的功能，一定要记得配置FileProvider!!!

## API 说明

#### CommonResultListener

```kotlin
/**
 * 获取系统铃声列表回调
 */
interface CommonResultListener<T> {
    //正在加载
    fun onStart() {}

    //加载成功，回调指定泛型对象
    fun onSuccess(result: T) {}

    //加载成功，回调指定泛型列表
    fun onSuccess(result: List<T>) {}

    //加载进度回调
    fun onProgress(currentProgress: Float) {}

    //回调结果为空
    fun onEmpty() {}

    //加载失败
    fun onError(message: String) {}
}
```

#### SystemHelper

```kotlin
    /**
 * 获取设备的品牌信息和型号
 */
val deviceName: String

/**
 * 控制手机震动
 * [millSeconds] 震动时长
 * [amplitude] 震动强度，默认为 [VibrationEffect.DEFAULT_AMPLITUDE]，或介于 1～255 之间，仅支持 Android 0及以上
 */
fun callPhoneToShake(
    context: Context?,
    millSeconds: Long = 500,
    amplitude: Int? = null
): Boolean {
}

/**
 * 安装apk
 *
 * 使用此功能需要在清单文件中配置 FileProvider（可参考 项目中 app-src-main-res-xml-file_path_provider.xml及AndroidManifes.xml）
 */
@SuppressLint("QueryPermissionsNeeded")
fun installApk(
    activity: Activity?,
    apkFile: File?,
    explainContent: String = "您必须同意 '应用内安装其他应用' 权限才能完成升级",
    positiveText: String = "确认",
    negativeText: String = "取消",
) {
}

/**
 * 下载并安装apk
 * 
 * 参数注释详见 DownloadHelper.downloadFile
 */
@SuppressLint("QueryPermissionsNeeded")
fun downloadAndInstallApk(
    activity: Activity?,
    fileUrl: String?,
    filePath: String?,
    fileName: String?,
    isDeleteOriginalFile: Boolean = true,
    explainContent: String = "您必须同意 '应用内安装其他应用' 权限才能完成升级",
    positiveText: String = "确认",
    negativeText: String = "取消",
    commonResultListener: CommonResultListener<File>
) {
}
```

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
) {
}
```

#### MediaHelper

```kotlin
    /**
 * 是否正在播放铃声
 */
val isRingtonePlaying: Boolean

/**
 * 调用手机发出系统铃声
 *
 * 如需停止，可通过返回对象调用stop方法
 * [assignUri] 指定铃声uri，如不指定，则会播放当前系统默认的铃声
 */
fun playSystemRingtone(context: Context?, assignUri: Uri? = null): Boolean {}

/**
 * 停止正在播放的系统铃声
 *
 * 该方法通过类内部维护Ringtone对象，来实现停止功能
 */
fun stopSystemRingtone(): Boolean {}

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
 */
fun getSystemRingtoneMap(
    context: Context?,
    ringtoneType: Int,
    commonResultListener: CommonResultListener<SystemRingtoneModel>
) {
}
```

##### AppHelper

```kotlin
    /**
 * 进入当前应用-设置-详情页面
 */
fun intoAppSettingDetail(activity: Activity): Boolean {}

/**
 * 获取应用版本号
 */
fun getAppVersion(context: Context?): String {}

/**
 * 检测当前是否在主线程
 */
fun checkIsInMainThread(): Boolean {}

/**
 * 切换到主线程
 */
fun runOnUiThread(function: () -> Unit) {}
```

```kotlin

```