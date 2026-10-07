package com.lyq.simpletoolbox

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import java.util.Locale

class StopwatchActivity : BaseToolActivity() {

    override val titleRes: Int = R.string.tool_stopwatch
    override val contentLayoutRes: Int = R.layout.activity_stopwatch

    private val handler = Handler(Looper.getMainLooper())
    private val laps = mutableListOf<String>()

    private lateinit var timeView: TextView
    private lateinit var startButton: MaterialButton
    private lateinit var lapButton: MaterialButton
    private lateinit var lapAdapter: ArrayAdapter<String>

    private var running = false
    private var baseElapsed = 0L      // 已累计的时间
    private var startRealtime = 0L    // 本次开始时的 elapsedRealtime

    private val ticker = object : Runnable {
        override fun run() {
            timeView.text = formatTime(currentElapsed())
            if (running) handler.postDelayed(this, 30L)
        }
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        timeView = findViewById(R.id.textTime)
        startButton = findViewById(R.id.btnStart)
        lapButton = findViewById(R.id.btnLap)
        val resetButton = findViewById<MaterialButton>(R.id.btnReset)
        val listView = findViewById<ListView>(R.id.listLaps)

        lapAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, laps)
        listView.adapter = lapAdapter

        startButton.setOnClickListener { if (running) pause() else start() }
        lapButton.setOnClickListener { addLap() }
        resetButton.setOnClickListener { reset() }

        timeView.text = formatTime(0L)
        refreshButtons()
    }

    override fun onDestroy() {
        handler.removeCallbacks(ticker)
        super.onDestroy()
    }

    private fun currentElapsed(): Long =
        baseElapsed + if (running) SystemClock.elapsedRealtime() - startRealtime else 0L

    private fun start() {
        running = true
        startRealtime = SystemClock.elapsedRealtime()
        handler.post(ticker)
        refreshButtons()
    }

    private fun pause() {
        baseElapsed = currentElapsed()
        running = false
        handler.removeCallbacks(ticker)
        timeView.text = formatTime(baseElapsed)
        refreshButtons()
    }

    private fun reset() {
        running = false
        handler.removeCallbacks(ticker)
        baseElapsed = 0L
        timeView.text = formatTime(0L)
        laps.clear()
        lapAdapter.notifyDataSetChanged()
        refreshButtons()
    }

    private fun addLap() {
        if (baseElapsed == 0L && !running) return
        val elapsed = currentElapsed()
        val previous = lastLapElapsed()
        laps.add(0, String.format(Locale.US, "第 %d 次   %s   (+%s)",
            laps.size + 1, formatTime(elapsed), formatTime(elapsed - previous)))
        lapAdapter.notifyDataSetChanged()
    }

    private fun lastLapElapsed(): Long {
        if (laps.isEmpty()) return 0L
        // 存的是最新在最前面，所以最后一次计次在列表末尾
        val last = laps.last()
        val parts = last.split("   ")
        return if (parts.size >= 2) parseTime(parts[1].trim()) else 0L
    }

    private fun parseTime(text: String): Long {
        val segments = text.split(":")
        if (segments.size != 2) return 0L
        val minutes = segments[0].toLongOrNull() ?: return 0L
        val secondsPart = segments[1].split(".")
        val seconds = secondsPart.getOrNull(0)?.toLongOrNull() ?: return 0L
        val centis = secondsPart.getOrNull(1)?.toLongOrNull() ?: 0L
        return minutes * 60_000L + seconds * 1000L + centis * 10L
    }

    private fun refreshButtons() {
        startButton.text = getString(if (running) R.string.sw_pause else if (baseElapsed > 0) R.string.sw_continue else R.string.sw_start)
        lapButton.isEnabled = running || baseElapsed > 0
    }

    private fun formatTime(millis: Long): String {
        val totalCentis = millis / 10
        val minutes = totalCentis / 6000
        val seconds = (totalCentis / 100) % 60
        val centis = totalCentis % 100
        return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, centis)
    }
}
