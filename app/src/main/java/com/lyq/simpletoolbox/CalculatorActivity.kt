package com.lyq.simpletoolbox

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast

class CalculatorActivity : BaseToolActivity() {

    override val titleRes: Int = R.string.tool_calculator
    override val contentLayoutRes: Int = R.layout.activity_calculator

    private lateinit var exprView: TextView
    private lateinit var resultView: TextView
    private val input = StringBuilder()

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        exprView = findViewById(R.id.exprText)
        resultView = findViewById(R.id.resultText)

        val keypad = findViewById<GridLayout>(R.id.keypad)
        for (i in 0 until keypad.childCount) {
            val keyView = keypad.getChildAt(i) as? TextView ?: continue
            val key = keyView.tag as? String ?: keyView.text.toString()
            keyView.setOnClickListener { onKey(key) }
        }

        resultView.setOnClickListener { copyResult() }
        render()
    }

    private fun onKey(key: String) {
        when (key) {
            "C" -> {
                input.clear()
                exprView.text = ""
                resultView.text = "0"
                return
            }
            "⌫" -> if (input.isNotEmpty()) {
                input.deleteCharAt(input.length - 1)
            }
            "=" -> {
                val value = runCatching { ExpressionEvaluator.evaluate(input.toString()) }.getOrNull()
                if (value == null) {
                    toast(getString(R.string.error_expr))
                } else {
                    val text = ExpressionEvaluator.format(value)
                    input.clear()
                    input.append(text)
                    exprView.text = ""
                    resultView.text = text
                    return
                }
            }
            else -> input.append(key)
        }
        render()
    }

    private fun render() {
        val expression = input.toString()
        exprView.text = expression
        val preview = runCatching { ExpressionEvaluator.evaluate(expression) }
            .getOrNull()
            ?.let { ExpressionEvaluator.format(it) }
        resultView.text = preview ?: "0"
    }

    private fun copyResult() {
        val text = resultView.text?.toString() ?: return
        val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(ClipData.newPlainText("result", text))
        toast(getString(R.string.copied))
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
