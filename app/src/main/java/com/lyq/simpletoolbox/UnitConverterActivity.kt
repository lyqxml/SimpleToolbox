package com.lyq.simpletoolbox

import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import java.math.BigDecimal
import java.math.MathContext

class UnitConverterActivity : BaseToolActivity() {

    override val titleRes: Int = R.string.tool_converter
    override val contentLayoutRes: Int = R.layout.activity_unit_converter

    private class Category(
        val name: String,
        val units: List<String>,
        val factors: Map<String, Double> = emptyMap(),
        val converter: ((Double, String, String) -> Double)? = null
    ) {
        fun convert(value: Double, from: String, to: String): Double {
            converter?.let { return it(value, from, to) }
            val fromFactor = factors.getValue(from)
            val toFactor = factors.getValue(to)
            return value * fromFactor / toFactor
        }
    }

    private val categories = listOf(
        Category(
            name = "长度",
            units = listOf("米 m", "千米 km", "分米 dm", "厘米 cm", "毫米 mm", "英寸 in", "英尺 ft", "码 yd", "英里 mi", "海里 nmi"),
            factors = mapOf(
                "米 m" to 1.0,
                "千米 km" to 1000.0,
                "分米 dm" to 0.1,
                "厘米 cm" to 0.01,
                "毫米 mm" to 0.001,
                "英寸 in" to 0.0254,
                "英尺 ft" to 0.3048,
                "码 yd" to 0.9144,
                "英里 mi" to 1609.344,
                "海里 nmi" to 1852.0
            )
        ),
        Category(
            name = "重量",
            units = listOf("千克 kg", "克 g", "毫克 mg", "吨 t", "斤", "两", "磅 lb", "盎司 oz"),
            factors = mapOf(
                "千克 kg" to 1.0,
                "克 g" to 0.001,
                "毫克 mg" to 0.000001,
                "吨 t" to 1000.0,
                "斤" to 0.5,
                "两" to 0.05,
                "磅 lb" to 0.45359237,
                "盎司 oz" to 0.028349523125
            )
        ),
        Category(
            name = "面积",
            units = listOf("平方米 m²", "平方千米 km²", "公顷 ha", "亩", "平方厘米 cm²", "平方英尺 ft²", "英亩 acre"),
            factors = mapOf(
                "平方米 m²" to 1.0,
                "平方千米 km²" to 1000000.0,
                "公顷 ha" to 10000.0,
                "亩" to 666.6666667,
                "平方厘米 cm²" to 0.0001,
                "平方英尺 ft²" to 0.09290304,
                "英亩 acre" to 4046.8564224
            )
        ),
        Category(
            name = "体积",
            units = listOf("升 L", "毫升 mL", "立方米 m³", "立方厘米 cm³", "加仑(美) gal", "品脱(美) pt"),
            factors = mapOf(
                "升 L" to 1.0,
                "毫升 mL" to 0.001,
                "立方米 m³" to 1000.0,
                "立方厘米 cm³" to 0.001,
                "加仑(美) gal" to 3.785411784,
                "品脱(美) pt" to 0.473176473
            )
        ),
        Category(
            name = "时间",
            units = listOf("秒 s", "毫秒 ms", "分钟 min", "小时 h", "天 d", "周 w"),
            factors = mapOf(
                "秒 s" to 1.0,
                "毫秒 ms" to 0.001,
                "分钟 min" to 60.0,
                "小时 h" to 3600.0,
                "天 d" to 86400.0,
                "周 w" to 604800.0
            )
        ),
        Category(
            name = "数据",
            units = listOf("字节 B", "比特 bit", "KB", "MB", "GB", "TB"),
            factors = mapOf(
                "字节 B" to 1.0,
                "比特 bit" to 0.125,
                "KB" to 1024.0,
                "MB" to 1048576.0,
                "GB" to 1073741824.0,
                "TB" to 1099511627776.0
            )
        ),
        Category(
            name = "温度",
            units = listOf("摄氏度 ℃", "华氏度 ℉", "开尔文 K"),
            converter = { value, from, to ->
                val celsius = when (from) {
                    "摄氏度 ℃" -> value
                    "华氏度 ℉" -> (value - 32) * 5 / 9
                    else -> value - 273.15
                }
                when (to) {
                    "摄氏度 ℃" -> celsius
                    "华氏度 ℉" -> celsius * 9 / 5 + 32
                    else -> celsius + 273.15
                }
            }
        )
    )

    private lateinit var categorySpinner: Spinner
    private lateinit var fromSpinner: Spinner
    private lateinit var toSpinner: Spinner
    private lateinit var inputEdit: EditText
    private lateinit var resultView: TextView
    private lateinit var formulaView: TextView

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        categorySpinner = findViewById(R.id.spinnerCategory)
        fromSpinner = findViewById(R.id.spinnerFrom)
        toSpinner = findViewById(R.id.spinnerTo)
        inputEdit = findViewById(R.id.editValue)
        resultView = findViewById(R.id.textResult)
        formulaView = findViewById(R.id.textFormula)

        categorySpinner.adapter = simpleAdapter(categories.map { it.name })
        categorySpinner.setOnItemSelectedListener(SimpleItemSelected { position, _ ->
            bindUnits(position)
        })

        inputEdit.doAfterTextChanged { convert() }
        fromSpinner.setOnItemSelectedListener(SimpleItemSelected { _, _ -> convert() })
        toSpinner.setOnItemSelectedListener(SimpleItemSelected { _, _ -> convert() })

        findViewById<MaterialButton>(R.id.btnSwap).setOnClickListener {
            val from = fromSpinner.selectedItemPosition
            val to = toSpinner.selectedItemPosition
            fromSpinner.setSelection(to)
            toSpinner.setSelection(from)
        }

        bindUnits(0)
    }

    private fun simpleAdapter(items: List<String>): ArrayAdapter<String> =
        ArrayAdapter(this, android.R.layout.simple_spinner_item, items).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    private fun bindUnits(categoryIndex: Int) {
        val category = categories[categoryIndex]
        fromSpinner.adapter = simpleAdapter(category.units)
        toSpinner.adapter = simpleAdapter(category.units)
        fromSpinner.setSelection(0)
        toSpinner.setSelection(if (category.units.size > 1) 1 else 0)
        convert()
    }

    private fun convert() {
        val category = categories[categorySpinner.selectedItemPosition.coerceAtLeast(0)]
        val from = fromSpinner.selectedItem as? String ?: return
        val to = toSpinner.selectedItem as? String ?: return
        val raw = inputEdit.text.toString().trim()
        formulaView.text = "1 $from = ${format(category.convert(1.0, from, to))} $to"
        if (raw.isEmpty()) {
            resultView.text = "—"
            return
        }
        val value = raw.toDoubleOrNull()
        if (value == null) {
            resultView.text = getString(R.string.error_input)
            return
        }
        resultView.text = format(category.convert(value, from, to))
    }

    private fun format(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "—"
        return BigDecimal(value).round(MathContext(10)).stripTrailingZeros().toPlainString()
    }
}
