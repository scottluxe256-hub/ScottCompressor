package com.ffmpeg.compressor.engine

import com.ffmpeg.compressor.model.CompressionSettings
import com.ffmpeg.compressor.model.EncodingProgress

interface FFmpegRunner {
    fun execute(
        settings: CompressionSettings,
        inputPath: String,
        outputPath: String,
        onProgress: (EncodingProgress) -> Unit
    )
    fun cancel()
}

object FFmpegLogParser {
    private val timeRegex = Regex("""time=(\d{2}):(\d{2}):(\d{2}\.\d+)""")
    private val frameRegex = Regex("""frame=\s*(\d+)""")
    private val fpsRegex = Regex("""fps=\s*([\d\.]+)""")
    private val bitrateRegex = Regex("""bitrate=\s*([^\s]+)""")
    private val speedRegex = Regex("""speed=\s*([^\s]+)""")

    fun parseLine(
        line: String,
        currentProgress: EncodingProgress,
        estimatedTotalDurationSec: Long = 60L
    ): EncodingProgress {
        val logs = currentProgress.consoleLog.toMutableList()
        if (logs.size > 200) {
            logs.removeAt(0)
        }
        logs.add(line)

        var timeElapsedSec = currentProgress.timeElapsedSeconds
        val timeMatch = timeRegex.find(line)
        if (timeMatch != null) {
            val (h, m, s) = timeMatch.destructured
            val hours = h.toLongOrNull() ?: 0L
            val minutes = m.toLongOrNull() ?: 0L
            val seconds = s.toDoubleOrNull() ?: 0.0
            timeElapsedSec = (hours * 3600 + minutes * 60 + seconds).toLong()
        }

        var frame = currentProgress.frame
        val frameMatch = frameRegex.find(line)
        if (frameMatch != null) {
            frame = frameMatch.groupValues[1].toLongOrNull() ?: frame
        }

        var fps = currentProgress.fps
        val fpsMatch = fpsRegex.find(line)
        if (fpsMatch != null) {
            fps = fpsMatch.groupValues[1].toFloatOrNull() ?: fps
        }

        var bitrate = currentProgress.bitrate
        val bitrateMatch = bitrateRegex.find(line)
        if (bitrateMatch != null) {
            bitrate = bitrateMatch.groupValues[1]
        }

        var speed = currentProgress.speed
        val speedMatch = speedRegex.find(line)
        if (speedMatch != null) {
            speed = speedMatch.groupValues[1]
        }

        val percentage = if (estimatedTotalDurationSec > 0) {
            ((timeElapsedSec.toFloat() / estimatedTotalDurationSec.toFloat()) * 100f).coerceIn(0f, 99f)
        } else 50f

        val totalDuration = if (estimatedTotalDurationSec > 0) estimatedTotalDurationSec else 60L
        val remaining = (totalDuration - timeElapsedSec).coerceAtLeast(0L)

        return currentProgress.copy(
            percentage = percentage,
            frame = frame,
            fps = fps,
            bitrate = bitrate,
            speed = speed,
            timeElapsedSeconds = timeElapsedSec,
            totalDurationSeconds = totalDuration,
            timeRemainingSeconds = remaining,
            statusText = "Encoding in progress...",
            consoleLog = logs
        )
    }
}
