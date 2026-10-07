package com.lyq.simpletoolbox

import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.ConfigurationCompat
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class DeviceInfoActivity : BaseToolActivity() {

    override val titleRes: Int = R.string.tool_device
    override val contentLayoutRes: Int = R.layout.activity_device_info

    private lateinit var infoView: TextView

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        infoView = findViewById(R.id.textDeviceInfo)
        val text = buildInfo()
        infoView.text = text

        findViewById<MaterialButton>(R.id.btnCopyInfo).setOnClickListener {
            val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            manager.setPrimaryClip(ClipData.newPlainText("device", text))
            Toast.makeText(this, getString(R.string.device_copied), Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildInfo(): String {
        val builder = StringBuilder()
        val locale = ConfigurationCompat.getLocales(resources.configuration)[0] ?: Locale.getDefault()

        builder.line("设备", Build.MANUFACTURER)
        builder.line("品牌 / 型号", "${Build.BRAND} / ${Build.MODEL}")
        builder.line("设备代号", Build.DEVICE)
        builder.line("Android 版本", "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        builder.line("系统指纹", Build.FINGERPRINT)
        builder.line("CPU 架构", Build.SUPPORTED_ABIS.joinToString(", "))
        builder.line("CPU 核心数", Runtime.getRuntime().availableProcessors().toString())
        builder.line("语言 / 地区", locale.toString())
        builder.line("屏幕分辨率", screenSize())
        builder.line("屏幕密度", density())
        builder.line("运行内存", memoryInfo())
        builder.line("存储空间", storageInfo())
        builder.line("系统启动时间", bootTime())
        return builder.toString()
    }

    private fun StringBuilder.line(label: String, value: String) {
        append(label).append("：").append(value).append("\n\n")
    }

    private fun screenSize(): String {
        val metrics: DisplayMetrics = resources.displayMetrics
        return "${metrics.widthPixels} × ${metrics.heightPixels} px"
    }

    private fun density(): String {
        val metrics: DisplayMetrics = resources.displayMetrics
        return "${metrics.densityDpi} dpi  (x${String.format(Locale.US, "%.2f", metrics.density)})"
    }

    private fun memoryInfo(): String {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val total = memoryInfo.totalMem
        val available = memoryInfo.availMem
        return "${formatBytes(total - available)} / ${formatBytes(total)} 已用（可用 ${formatBytes(available)}）"
    }

    @Suppress("DEPRECATION")
    private fun storageInfo(): String {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val total = stat.blockCountLong * stat.blockSizeLong
            val available = stat.availableBlocksLong * stat.blockSizeLong
            "${formatBytes(total - available)} / ${formatBytes(total)} 已用（可用 ${formatBytes(available)}）"
        } catch (e: Exception) {
            "读取失败"
        }
    }

    private fun bootTime(): String {
        val uptime = android.os.SystemClock.elapsedRealtime()
        val boot = Date(System.currentTimeMillis() - uptime)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val days = TimeUnit.MILLISECONDS.toDays(uptime)
        val hours = TimeUnit.MILLISECONDS.toHours(uptime) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(uptime) % 60
        return "${formatter.format(boot)}（已运行 ${days}天${hours}小时${minutes}分）"
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var value = bytes.toDouble()
        var index = 0
        while (value >= 1024 && index < units.size - 1) {
            value /= 1024
            index++
        }
        return String.format(Locale.US, "%.2f %s", value, units[index])
    }
}
