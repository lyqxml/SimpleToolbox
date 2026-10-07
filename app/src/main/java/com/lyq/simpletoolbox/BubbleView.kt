package com.lyq.simpletoolbox

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/**
 * 水平仪气泡：外圈 + 内圈 + 十字辅助线 + 气泡。
 */
class BubbleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /** 左右倾斜角度（度） */
    var roll: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /** 前后倾斜角度（度） */
    var pitch: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val maxAngle = 45f

    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(1.5f)
        color = Color.parseColor("#8A8A8A")
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(1f)
        color = Color.parseColor("#55800000")
    }
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(1.5f)
        color = Color.parseColor("#8A8A8A")
    }
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#2E7D32")
    }
    private val bubbleRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp(1f)
        color = Color.parseColor("#80000000")
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val radius = min(width, height) / 2f - dp(10f)
        if (radius <= 0f) return

        canvas.drawCircle(cx, cy, radius, ringPaint)
        canvas.drawCircle(cx, cy, radius * 0.5f, centerPaint)
        canvas.drawLine(cx - radius, cy, cx + radius, cy, linePaint)
        canvas.drawLine(cx, cy - radius, cx, cy + radius, linePaint)

        val ratioX = (roll / maxAngle).coerceIn(-1f, 1f)
        val ratioY = (pitch / maxAngle).coerceIn(-1f, 1f)
        val bubbleRadius = radius * 0.13f
        val bx = cx + ratioX * (radius - bubbleRadius - dp(4f))
        val by = cy + ratioY * (radius - bubbleRadius - dp(4f))

        val level = kotlin.math.abs(roll) < 1.5f && kotlin.math.abs(pitch) < 1.5f
        bubblePaint.color = if (level) Color.parseColor("#2E7D32") else Color.parseColor("#1976D2")
        canvas.drawCircle(bx, by, bubbleRadius, bubblePaint)
        canvas.drawCircle(bx, by, bubbleRadius, bubbleRingPaint)
    }
}
