# CommonHelper

## 简单使用

```kotlin

BasicInitial.initial(applicationContext);

```

### 重点说明
```kotlin

/**
  * 安装apk
  *
  * 使用此功能需要在清单文件中配置 FileProvider（可参考 项目中 app-src-main-res-xml-file_path_provider.xml及AndroidManifes.xml）
  */
  @SuppressLint("QueryPermissionsNeeded")
  fun SystemHelper.installApk(
      activity: FragmentActivity,
      apkFile: File?,
      explainContent: String = "您必须同意 '应用内安装其他应用' 权限才能完成升级",
      positiveText: String = "确认",
      negativeText: String = "取消",
  )
```
