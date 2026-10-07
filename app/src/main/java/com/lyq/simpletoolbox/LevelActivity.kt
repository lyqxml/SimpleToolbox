package com.lyq.simpletoolbox

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.sqrt

class LevelActivity : BaseToolActivity(), SensorEventListener {

    override val titleRes: Int = R.string.tool_level
    override val contentLayoutRes: Int = R.layout.activity_level

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var bubbleView: BubbleView
    private lateinit var anglesView: TextView
    private lateinit var stateView: TextView

    private var offsetRoll = 0f
    private var offsetPitch = 0f

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        bubbleView = findViewById(R.id.bubbleView)
        anglesView = findViewById(R.id.textAngles)
        stateView = findViewById(R.id.textState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        findViewById<MaterialButton>(R.id.btnCalibrate).setOnClickListener {
            offsetRoll -= lastRoll
            offsetPitch -= lastPitch
        }

        if (accelerometer == null) {
            stateView.text = getString(R.string.level_unsupported)
        }
    }

    private var lastRoll = 0f
    private var lastPitch = 0f

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val values = event?.values ?: return
        val x = values[0]
        val y = values[1]
        val z = values[2]

        val roll = Math.toDegrees(atan2(x.toDouble(), sqrt((y * y + z * z).toDouble()))).toFloat()
        val pitch = Math.toDegrees(atan2(y.toDouble(), sqrt((x * x + z * z).toDouble()))).toFloat()

        lastRoll = roll
        lastPitch = pitch
        updateBubble(roll + offsetRoll, pitch + offsetPitch)
    }

    private fun updateBubble(roll: Float, pitch: Float) {
        bubbleView.roll = roll
        bubbleView.pitch = pitch
        anglesView.text = String.format(Locale.US, "左右 %+.1f°    前后 %+.1f°", roll, pitch)
        val level = kotlin.math.abs(roll) < 1.5f && kotlin.math.abs(pitch) < 1.5f
        stateView.text = getString(if (level) R.string.level_leveled else R.string.level_tilted)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
