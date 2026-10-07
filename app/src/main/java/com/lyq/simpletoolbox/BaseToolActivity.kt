package com.lyq.simpletoolbox

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

/**
 * 所有工具页的基类：统一设置标题栏与返回按钮。
 */
abstract class BaseToolActivity : AppCompatActivity() {

    @get:StringRes
    abstract val titleRes: Int

    @get:LayoutRes
    abstract val contentLayoutRes: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentLayoutRes)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        if (toolbar != null) {
            toolbar.title = getString(titleRes)
            toolbar.setNavigationOnClickListener { finish() }
        }
    }
}
