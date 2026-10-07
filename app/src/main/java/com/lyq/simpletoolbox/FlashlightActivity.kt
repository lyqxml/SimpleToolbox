package com.lyq.simpletoolbox

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class FlashlightActivity : BaseToolActivity() {

    override val titleRes: Int = R.string.tool_flashlight
    override val contentLayoutRes: Int = R.layout.activity_flashlight

    private lateinit var cameraManager: CameraManager
    private lateinit var toggleButton: MaterialButton
    private lateinit var statusView: TextView
    private lateinit var iconView: ImageView

    private var cameraId: String? = null
    private var enabled = false

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        toggleButton = findViewById(R.id.btnToggle)
        statusView = findViewById(R.id.textStatus)
        iconView = findViewById(R.id.imageTorch)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = findTorchCamera()

        if (cameraId == null) {
            toggleButton.isEnabled = false
            statusView.text = getString(R.string.torch_unsupported)
        } else {
            toggleButton.setOnClickListener { setTorch(!enabled) }
        }
        refreshUi()
    }

    override fun onDestroy() {
        if (enabled) setTorch(false)
        super.onDestroy()
    }

    private fun findTorchCamera(): String? {
        return try {
            cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun setTorch(on: Boolean) {
        val id = cameraId ?: return
        try {
            cameraManager.setTorchMode(id, on)
            enabled = on
            refreshUi()
        } catch (e: Exception) {
            statusView.text = getString(R.string.torch_failed, e.message ?: "")
            Handler(Looper.getMainLooper()).postDelayed({ refreshUi() }, 1800L)
        }
    }

    private fun refreshUi() {
        iconView.setImageResource(if (enabled) R.drawable.ic_tool_flashlight_on else R.drawable.ic_tool_flashlight)
        iconView.alpha = if (enabled) 1f else 0.45f
        statusView.text = getString(if (enabled) R.string.torch_status_on else R.string.torch_status_off)
        toggleButton.text = getString(if (enabled) R.string.torch_turn_off else R.string.torch_turn_on)
    }
}
