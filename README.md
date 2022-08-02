# CommonHelper

## 功能：

### 应用内更新

* 下载并安装apk、仅安装apk - SystemHelper
* 跳转到应用市场-当前应用详情页 - AppHelper
* 跳转到设置-应用详情页 - SystemHelper

### 媒体相关

* 获取系统 铃声/通知/警报 列表 - MediaHelper
* 播放/暂停系统 铃声/通知/警报 - MediaHelper
* 控制设备震动 - SystemHelper

### 下载文件

* 下载文件/取消下载 - DownloadHelper

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
 * 通用异步回调
 */
interface CommonResultListener<T> {
    //开始加载
    fun onStart(attachParam: Any? = null) {}

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
 * 注意：如果想达到跳转页面去开启权限后还能自动执行刚才中断的流程，那就要保证 [activity] 为 FragmentActivity 或其子类；
 * 如不能保证，那么需要根据 commonResultListener.onError 的回调值为[SystemHelper.OPEN_INSTALL_PACKAGE_PERMISSION]的时候，
 * 在onResume方法内重新调用此方法，达到继续执行的目的
 * 
 * 参考：https://github.com/gfslx999/flutter_native_helper/blob/master/android/src/main/kotlin/com/gfs/helper/flutter_native_helper/FlutterNativeHelperPlugin.kt
 */
@SuppressLint("QueryPermissionsNeeded")
fun installApk(
    activity: Activity?,
    apkFile: File?,
    explainContent: String = "您必须同意 '应用内安装其他应用' 权限才能完成升级",
    positiveText: String = "确认",
    negativeText: String = "取消",
    commonResultListener: CommonResultListener<File>? = null
) {
}

/**
 * 下载并安装apk
 * 
 * 使用此功能需要在清单文件中配置 FileProvider（可参考 项目中 app-src-main-res-xml-file_path_provider.xml及AndroidManifes.xml）
 * 注意：如果想达到跳转页面去开启权限后还能自动执行刚才中断的流程，那就要保证 [activity] 为 FragmentActivity 或其子类；
 * 如不能保证，那么需要根据 commonResultListener.onError 的回调值为[SystemHelper.OPEN_INSTALL_PACKAGE_PERMISSION]的时候，
 * 在onResume方法内重新调用此方法，达到继续执行的目的
 * 参考：https://github.com/gfslx999/flutter_native_helper/blob/master/android/src/main/kotlin/com/gfs/helper/flutter_native_helper/FlutterNativeHelperPlugin.kt
 *
 * 参数注释详见 DownloadHelper.downloadFile
 * 
 * 如果连续调用多次此方法，并且[fileUrl]、[filePath]、[fileName]完全一致，则不会重复下载，回调onError = [CommonConstant.ERROR_SAME_FILE_DOWNLOADED]
 * 如需取消请求，可以调用[DownloadHelper.cancelDownload]，tag: 使用 commonResultListener.onStart 中回调的参数。
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
 * 如果连续调用多次此方法，并且[fileUrl]、[filePath]、[fileName]完全一致，则不会重复下载，回调onError = [CommonConstant.ERROR_SAME_FILE_DOWNLOADED]
 * 如需取消请求，可以调用[DownloadHelper.cancelDownload]，tag: 使用 commonResultListener.onStart 中回调的参数。
 * 注⚠️：本方法不处理权限请求，即如果需要使用应用沙盒以外的路径，需自行处理权限请求
 */
fun downloadFile(
    fileUrl: String?,
    filePath: String?,
    fileName: String?,
    isDeleteOriginalFile: Boolean = true,
    commonResultListener: CommonResultListener<File>
) {}

/**
 * 取消下载
 *
 * [tag] 调用下载时 commonResultListener.onStart 的回调参数
 */
fun cancelDownload(tag: String) {}

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
) {}

/**
 * 打开图片选择器，选择图片或视频
 *
 * Android 13以下的版本，需要申请 [Manifest.permission.READ_EXTERNAL_STORAGE] 才可以获取真实路径
 * [maxNum] 最大选择数，仅在 Android 13上生效，Android 13以下仅能控制是否多选，无法控制具体数量
 * [pickType] 选择类型，参考[PickPhotoType]
 * [isRequestPermissionSelf] Android 13 以下，如果没有 READ_EXTERNAL_STORAGE 权限，是否自动申请
 * [listener] 成功时回调 'onSuccess(resultList: List<T>)'
 *
 * 错误处理：
 * 1. onError 回调 [CommonConstant.ERROR_USER_DENIED_PERMISSION] 时代表自动申请了权限，但是用户拒绝了权限
 * 2. onError 回调 [CommonConstant.ERROR_PERMISSION_IS_DENIED] 时，代表[isRequestPermissionSelf] 为false，并且权限状态为拒绝，
 * 3. 其余情况为异常信息
 */
fun pickPhoto(
    activity: FragmentActivity,
    maxNum: Int = 1,
    pickType: PickPhotoType = PickPhotoType.ALL,
    isRequestPermissionSelf: Boolean = true,
    listener: CommonResultListener<String>,
) {}
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

/**
 * 打开应用市场-当前应用页面
 *
 * [targetMarketPackageName] 指定应用市场包名
 * [isOpenSystemMarket] 如 'targetMarketPackageName' 为空，是否打开本机自带应用市场，
 * 为true时，将直接尝试打开当前厂商系统应用商店，否则系统会弹出弹窗自行选择应用市场。
 *
 * 简单来说，如果你有指定的应用市场，就传递 'targetMarketPackageName' 为对应应用市场的包名；
 * 如果你没有指定的应用市场，但是想让大部分机型都打开厂商应用商店，那么就设置 'isOpenSystemMarket' 为true
 * 
 * 支持打开的厂商，在下方。不支持的、或处理失败的将交由系统处理
 */
fun openAppMarket(
    activity: Activity?,
    targetMarketPackageName: String = "",
    isOpenSystemMarket: Boolean = false
)
```

| 支持的厂商 |
| ------ |
| 华为 |
| 小米 |
| OPPO |
| VIVO |
| 三星 |
| 魅族 |
| 谷歌 |
| 联想 |
