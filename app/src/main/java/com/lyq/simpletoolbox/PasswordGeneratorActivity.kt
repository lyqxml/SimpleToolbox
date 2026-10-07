package com.lyq.simpletoolbox

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import java.security.SecureRandom

class PasswordGeneratorActivity : BaseToolActivity() {

    override val titleRes: Int = R.string.tool_password
    override val contentLayoutRes: Int = R.layout.activity_password

    private companion object {
        const val LOWER = "abcdefghijkmnopqrstuvwxyz"
        const val UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        const val DIGIT = "23456789"
        const val SYMBOL = "!@#$%^&*()-_=+[]{}?~"
        const val MIN_LENGTH = 4
    }

    private val random = SecureRandom()
    private lateinit var passwordView: TextView
    private lateinit var lengthView: TextView
    private lateinit var lengthBar: SeekBar

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        passwordView = findViewById(R.id.textPassword)
        lengthView = findViewById(R.id.textLength)
        lengthBar = findViewById(R.id.seekLength)

        lengthBar.max = 32 - MIN_LENGTH
        lengthBar.progress = 16 - MIN_LENGTH
        lengthBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateLengthLabel()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                generate()
            }
        })

        val switches = listOf(
            R.id.switchLower,
            R.id.switchUpper,
            R.id.switchDigit,
            R.id.switchSymbol
        ).map { findViewById<MaterialSwitch>(it) }

        switches.forEach { switch ->
            switch.setOnCheckedChangeListener { _, _ ->
                if (switches.none { it.isChecked }) {
                    switch.isChecked = true
                    return@setOnCheckedChangeListener
                }
                generate()
            }
        }

        findViewById<MaterialButton>(R.id.btnGenerate).setOnClickListener { generate() }
        findViewById<MaterialButton>(R.id.btnCopy).setOnClickListener {
            copyToClipboard(passwordView.text.toString())
        }

        updateLengthLabel()
        generate()
    }

    private fun currentLength(): Int = lengthBar.progress + MIN_LENGTH

    private fun updateLengthLabel() {
        lengthView.text = getString(R.string.pw_length_value, currentLength())
    }

    private fun generate() {
        val pools = mutableListOf<String>()
        if (findViewById<MaterialSwitch>(R.id.switchLower).isChecked) pools += LOWER
        if (findViewById<MaterialSwitch>(R.id.switchUpper).isChecked) pools += UPPER
        if (findViewById<MaterialSwitch>(R.id.switchDigit).isChecked) pools += DIGIT
        if (findViewById<MaterialSwitch>(R.id.switchSymbol).isChecked) pools += SYMBOL
        if (pools.isEmpty()) {
            passwordView.text = "—"
            return
        }

        val length = currentLength()
        val chars = CharArray(length)
        // 先保证每个选中的字符集至少出现一次，再随机补足
        for (i in 0 until minOf(length, pools.size)) {
            chars[i] = pools[i][random.nextInt(pools[i].length)]
        }
        val all = pools.joinToString("")
        for (i in pools.size until length) {
            chars[i] = all[random.nextInt(all.length)]
        }
        // 洗牌，避免开头字符集固定
        for (i in chars.indices.reversed()) {
            val j = random.nextInt(i + 1)
            val tmp = chars[i]
            chars[i] = chars[j]
            chars[j] = tmp
        }

        val password = String(chars)
        passwordView.text = password
        passwordView.contentDescription = password
    }

    private fun copyToClipboard(text: String) {
        val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(ClipData.newPlainText("password", text))
        Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()
    }
}
