package com.lyq.simpletoolbox

import android.view.View
import android.widget.AdapterView

/**
 * Spinner 选中回调的简写包装。
 */
class SimpleItemSelected(
    private val onSelected: (position: Int, view: View?) -> Unit
) : AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onSelected(position, view)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
}
