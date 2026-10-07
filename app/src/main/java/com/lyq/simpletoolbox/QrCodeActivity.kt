package com.lyq.simpletoolbox

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.MultiFormatWriter
import java.io.OutputStream

class QrCodeActivity : BaseToolActivity() {

    override val titleRes: Int = R.string.tool_qrcode
    override val contentLayoutRes: Int = R.layout.activity_qrcode

    private lateinit var inputEdit: EditText
    private lateinit var qrImage: ImageView
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        inputEdit = findViewById(R.id.editQr)
        qrImage = findViewById(R.id.imageQr)

        findViewById<MaterialButton>(R.id.btnQr).setOnClickListener { generate() }
        findViewById<MaterialButton>(R.id.btnSave).setOnClickListener { save() }

        inputEdit.setText("https://github.com/lyqxml/SimpleToolbox")
        generate()
    }

    private fun generate() {
        val text = inputEdit.text.toString().trim()
        if (TextUtils.isEmpty(text)) {
            toast(getString(R.string.qr_empty))
            return
        }
        val size = 720
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1,
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M
        )
        val result = runCatching {
            MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
        }.getOrNull()

        if (result == null) {
            toast(getString(R.string.qr_failed))
            return
        }

        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        val created = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        created.setPixels(pixels, 0, width, 0, 0, width, height)
        bitmap = created
        qrImage.setImageBitmap(created)
    }

    private fun save() {
        val image = bitmap ?: run {
            toast(getString(R.string.qr_empty))
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            toast(getString(R.string.qr_save_unsupported))
            return
        }
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "qrcode_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SimpleToolbox")
        }
        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri == null) {
            toast(getString(R.string.qr_save_failed))
            return
        }
        val stream: OutputStream? = resolver.openOutputStream(uri)
        val ok = stream?.use { image.compress(Bitmap.CompressFormat.PNG, 100, it) } ?: false
        if (ok) {
            toast(getString(R.string.qr_saved))
        } else {
            resolver.delete(uri, null, null)
            toast(getString(R.string.qr_save_failed))
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
