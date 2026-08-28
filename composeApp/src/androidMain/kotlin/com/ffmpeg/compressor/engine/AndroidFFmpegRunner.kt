package com.ffmpeg.compressor.engine

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

class AndroidFFmpegRunner {
    private var currentProcess: Process? = null
    private var runnerScope: CoroutineScope? = null

    fun executeRawCommand(
        ffmpegPath: String,
        rawCommand: String,
        onProgress: (floatProgress: Float, statusText: String, isFinished: Boolean, isError: Boolean) -> Unit
    ) {
        runnerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        runnerScope?.launch {
            withContext(Dispatchers.Main) {
                onProgress(0f, "Menyiapkan proses kompresi...", false, false)
            }

            try {
                val cleanCommand = rawCommand.trim()
                if (cleanCommand.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        onProgress(0f, "Error: Perintah FFmpeg kosong!", true, true)
                    }
                    return@launch
                }

                val parsedArgs = mutableListOf<String>()
                // Parser Regex presisi: Memisahkan string ber-spasi di dalam tanda petik ganda/tunggal
                val regex = Pattern.compile("\"([^\"]*)\"|'([^']*)'|(\\S+)")
                val matcher = regex.matcher(cleanCommand)

                while (matcher.find()) {
                    when {
                        matcher.group(1) != null -> parsedArgs.add(matcher.group(1)) // Isi tanda petik ganda "..."
                        matcher.group(2) != null -> parsedArgs.add(matcher.group(2)) // Isi tanda petik tunggal '...'
                        else -> parsedArgs.add(matcher.group(3))                     // Parameter biasa tanpa petik
                    }
                }

                if (parsedArgs.isNotEmpty() && (parsedArgs[0] == "ffmpeg" || parsedArgs[0].endsWith("/ffmpeg"))) {
                    parsedArgs.removeAt(0)
                }

                val commandArgs = mutableListOf<String>()
                commandArgs.add(ffmpegPath)
                commandArgs.addAll(parsedArgs)

                val pb = ProcessBuilder(commandArgs)
                pb.redirectErrorStream(true)
                val process = pb.start()
                currentProcess = process

                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String?
                var totalDurationSeconds = 0f

                val durationPattern = Pattern.compile("Duration: (\\d+):(\\d+):(\\d+\\.\\d+)")
                val timePattern = Pattern.compile("time=(\\d+):(\\d+):(\\d+\\.\\d+)")

                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line ?: break

                    val durMatcher = durationPattern.matcher(currentLine)
                    if (durMatcher.find()) {
                        val hours = durMatcher.group(1)?.toFloatOrNull() ?: 0f
                        val minutes = durMatcher.group(2)?.toFloatOrNull() ?: 0f
                        val seconds = durMatcher.group(3)?.toFloatOrNull() ?: 0f
                        totalDurationSeconds = (hours * 3600) + (minutes * 60) + seconds
                    }

                    val timeMatcher = timePattern.matcher(currentLine)
                    if (timeMatcher.find()) {
                        val hours = timeMatcher.group(1)?.toFloatOrNull() ?: 0f
                        val minutes = timeMatcher.group(2)?.toFloatOrNull() ?: 0f
                        val seconds = timeMatcher.group(3)?.toFloatOrNull() ?: 0f
                        val currentTimeSeconds = (hours * 3600) + (minutes * 60) + seconds

                        if (totalDurationSeconds > 0) {
                            val calculatedProgress = (currentTimeSeconds / totalDurationSeconds) * 100f
                            val clampedProgress = calculatedProgress.coerceIn(0f, 99f)

                            withContext(Dispatchers.Main) {
                                onProgress(clampedProgress, "Mengompresi...", false, false)
                            }
                        }
                    }
                }

                val exitCode = process.waitFor()
                withContext(Dispatchers.Main) {
                    if (exitCode == 0) {
                        onProgress(100f, "Ekspor Selesai!", true, false)
                    } else {
                        onProgress(0f, "Gagal! Exit code: $exitCode", true, true)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onProgress(0f, "Error: ${e.localizedMessage}", true, true)
                }
            }
        }
    }

    fun cancel() {
        try {
            currentProcess?.destroyForcibly()
            runnerScope?.cancel()

            Runtime.getRuntime().exec("killall -9 libffmpeg.so")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            currentProcess = null
        }
    }
}
