# SimpleToolbox · 简易工具箱

一个轻量、干净、**完全离线**的 Android 小工具合集。没有广告，没有后台服务，也没有申请网络权限——所有计算与生成都在手机本地完成。

> 本仓库是纯源码仓库，未附编译产物（APK），可自行用 Android Studio 打开编译。

## 功能

| 工具 | 说明 |
| --- | --- |
| 🧮 计算器 | 四则运算、百分号、括号，边输入边实时预览结果，点按结果即可复制 |
| 📏 单位换算 | 长度 / 重量 / 面积 / 体积 / 时间 / 数据 / 温度 七大类，支持一键交换单位 |
| 🔐 密码生成 | 长度 4~32 位可调，大小写、数字、符号自由组合，SecureRandom 生成 |
| ⏱ 秒表计时 | 30ms 刷新精度，支持计次（记录单圈用时）、暂停、继续、重置 |
| 📐 水平仪 | 加速度传感器驱动的气泡水平仪，显示左右 / 前后倾角，支持校准归零 |
| 🔦 手电筒 | 基于 Camera2 `setTorchMode` 一键开关闪光灯，退出页面自动关闭 |
| ⬛ 二维码 | 文本 / 网址本地生成二维码，可保存到相册（Android 10+） |
| 📱 设备信息 | 型号、系统版本、CPU 架构、内存、存储、屏幕参数等，一键复制 |
| ℹ️ 关于 | 版本号与开源说明 |

## 界面

- Material 3 设计，浅色 / 深色自动跟随系统
- 首页九宫格卡片式入口，每个工具独立页面
- 无第三方 UI 框架，仅依赖 AndroidX + Material Components

## 技术栈

| 项目 | 版本 |
| --- | --- |
| 语言 | Kotlin 2.0.21 |
| Android Gradle Plugin | 8.2.2 |
| Gradle | 8.2.1 |
| compileSdk / targetSdk | 34 |
| minSdk | 24（Android 7.0） |
| 唯一第三方库 | `com.google.zxing:core`（本地生成二维码） |

## 目录结构

```
SimpleToolbox
├── app/src/main
│   ├── java/com/lyq/simpletoolbox
│   │   ├── MainActivity.kt              首页九宫格
│   │   ├── ToolAdapter.kt / Tool.kt     工具条目与适配器
│   │   ├── BaseToolActivity.kt          工具页基类（统一标题栏 + 返回）
│   │   ├── ExpressionEvaluator.kt       递归下降表达式求值
│   │   ├── CalculatorActivity.kt
│   │   ├── UnitConverterActivity.kt
│   │   ├── PasswordGeneratorActivity.kt
│   │   ├── StopwatchActivity.kt
│   │   ├── LevelActivity.kt / BubbleView.kt
│   │   ├── FlashlightActivity.kt
│   │   ├── QrCodeActivity.kt
│   │   ├── DeviceInfoActivity.kt
│   │   └── AboutActivity.kt
│   ├── res/layout、res/values、res/drawable ...
│   └── AndroidManifest.xml
├── build.gradle、settings.gradle、gradle.properties
└── tools_check.py                       资源自检脚本（可选）
```

## 编译

1. 用 Android Studio（Giraffe 或更新）打开本目录，等待 Gradle 同步完成；
2. 未提交 `gradle-wrapper.jar`，首次同步时 IDE 会按 `gradle/wrapper/gradle-wrapper.properties` 自动下载 Gradle 8.2.1，或直接使用本机安装的 Gradle；
3. 命令行编译：

```bash
gradle assembleDebug          # 或 ./gradlew assembleDebug
```

## 权限

| 权限 | 用途 |
| --- | --- |
| `CAMERA` / `FLASHLIGHT` | 手电筒控制闪光灯 |
| `VIBRATE` | 预留 |

**没有** `INTERNET` 权限：应用无法联网，也不会收集任何数据。

## 许可

[MIT License](LICENSE) © 2026 lyqxml
