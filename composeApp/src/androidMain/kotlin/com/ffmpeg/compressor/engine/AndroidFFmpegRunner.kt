package com.ffmpeg.compressor.engine

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
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
                // Pecah command string menjadi argumen list
                val cleanCommand = rawCommand.trim()
                val commandArgs = mutableListOf<String>()
                
                // Gunakan ffmpeg executable dari internal storage
                commandArgs.add(ffmpegPath)

                // Split argumen dengan memperhatikan tanda petik
                val regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'")
                val matcher = regex.matcher(cleanCommand)
                while (matcher.find()) {
                    if (matcher.group(1) != null) {
                        commandArgs.add(matcher.group(1))
                    } else if (matcher.group(2) != null) {
                        commandArgs.add(matcher.group(2))
                    } else {
                        commandArgs.add(matcher.group())
                    }
                }

                // Abaikan jika kata pertama dalam rawCommand adalah 'ffmpeg'
                if (commandArgs.size > 1 && (commandArgs[1] == "ffmpeg" || commandArgs[1].endsWith("/ffmpeg"))) {
                    commandArgs.removeAt(1)
                }

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

                    // Parser Durasi Total Video
                    val durMatcher = durationPattern.matcher(currentLine)
                    if (durMatcher.find()) {
                        val hours = durMatcher.group(1)?.toFloatOrNull() ?: 0f
                        val minutes = durMatcher.group(2)?.toFloatOrNull() ?: 0f
                        val seconds = durMatcher.group(3)?.toFloatOrNull() ?: 0f
                        totalDurationSeconds = (hours * 3600) + (minutes * 60) + seconds
                    }

                    // Parser Waktu Berjalan (Time) & Hitung Persentase
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
                        onProgress(0f, "Gagal! Process exit code: $exitCode", true, true)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onProgress(0f, "Error: ${e.message}", true, true)
                }
            }
        }
    }

    fun cancel() {
        try {
            currentProcess?.destroyForcibly()
            runnerScope?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
