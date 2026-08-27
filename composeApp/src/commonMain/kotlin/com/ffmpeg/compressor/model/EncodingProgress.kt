package com.ffmpeg.compressor.model

data class EncodingProgress(
    val percentage: Float = 0f,
    val frame: Long = 0,
    val fps: Float = 0f,
    val bitrate: String = "0 kbits/s",
    val speed: String = "0x",
    val timeElapsedSeconds: Long = 0,
    val totalDurationSeconds: Long = 0,
    val timeRemainingSeconds: Long = 0,
    val statusText: String = "Initializing FFmpeg Engine...",
    val consoleLog: List<String> = emptyList(),
    val isFinished: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
) {
    val formattedTimeElapsed: String
        get() = formatSeconds(timeElapsedSeconds)

    val formattedTimeRemaining: String
        get() = formatSeconds(timeRemainingSeconds)

    private fun formatSeconds(seconds: Long): String {
        if (seconds <= 0) return "00:00:00"
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hrs > 0) {
            "${hrs.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        } else {
            "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        }
    }
}
