package com.lyq.simpletoolbox

import android.widget.TextView

class AboutActivity : BaseToolActivity() {

    override val titleRes: Int = R.string.tool_about
    override val contentLayoutRes: Int = R.layout.activity_about

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        val versionView = findViewById<TextView>(R.id.textVersion)
        val versionName = runCatching {
            packageManager.getPackageInfo(packageName, 0).versionName
        }.getOrNull() ?: "1.0.0"
        versionView.text = getString(R.string.about_version_value, versionName)
    }
}
