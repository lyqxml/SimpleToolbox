package com.lyq.simpletoolbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val tools = listOf(
        Tool(R.string.tool_calculator, R.string.tool_calculator_desc, R.drawable.ic_tool_calculator, CalculatorActivity::class.java),
        Tool(R.string.tool_converter, R.string.tool_converter_desc, R.drawable.ic_tool_converter, UnitConverterActivity::class.java),
        Tool(R.string.tool_password, R.string.tool_password_desc, R.drawable.ic_tool_password, PasswordGeneratorActivity::class.java),
        Tool(R.string.tool_stopwatch, R.string.tool_stopwatch_desc, R.drawable.ic_tool_stopwatch, StopwatchActivity::class.java),
        Tool(R.string.tool_level, R.string.tool_level_desc, R.drawable.ic_tool_level, LevelActivity::class.java),
        Tool(R.string.tool_flashlight, R.string.tool_flashlight_desc, R.drawable.ic_tool_flashlight, FlashlightActivity::class.java),
        Tool(R.string.tool_qrcode, R.string.tool_qrcode_desc, R.drawable.ic_tool_qrcode, QrCodeActivity::class.java),
        Tool(R.string.tool_device, R.string.tool_device_desc, R.drawable.ic_tool_device, DeviceInfoActivity::class.java),
        Tool(R.string.tool_about, R.string.tool_about_desc, R.drawable.ic_tool_about, AboutActivity::class.java)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.toolList).apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = ToolAdapter(tools)
            setHasFixedSize(true)
        }
    }
}
