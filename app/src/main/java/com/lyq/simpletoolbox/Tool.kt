package com.lyq.simpletoolbox

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * 首页九宫格里的一个工具条目。
 */
data class Tool(
    @StringRes val nameRes: Int,
    @StringRes val descRes: Int,
    @DrawableRes val iconRes: Int,
    val target: Class<out Activity>
)
