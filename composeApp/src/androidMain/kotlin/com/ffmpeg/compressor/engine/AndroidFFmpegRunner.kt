package com.ffmpeg.compressor.engine

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

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

                // Parse argumen perintah secara aman (menangani tanda petik & spasi)
                val parsedArgs = parseCommandLine(cleanCommand).toMutableList()

                // Hapus token pertama jika pengguna mengetik "ffmpeg" atau "libffmpeg.so" di input text
                if (parsedArgs.isNotEmpty()) {
                    val firstToken = parsedArgs[0].lowercase()
                    if (firstToken == "ffmpeg" || firstToken == "libffmpeg.so" || 
                        firstToken.endsWith("/ffmpeg") || firstToken.endsWith("/libffmpeg.so")) {
                        parsedArgs.removeAt(0)
                    }
                }

                // Gabungkan path biner resmi native (.so) dengan argumen
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

                val durationRegex = Regex("""Duration:\s*(\d+):(\d+):(\d+\.\d+)""")
                val timeRegex = Regex("""time=\s*(\d+):(\d+):(\d+\.\d+)""")

                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line ?: break

                    // Hitung total durasi video
                    val durMatch = durationRegex.find(currentLine)
                    if (durMatch != null) {
                        val hours = durMatch.groupValues[1].toFloatOrNull() ?: 0f
                        val minutes = durMatch.groupValues[2].toFloatOrNull() ?: 0f
                        val seconds = durMatch.groupValues[3].toFloatOrNull() ?: 0f
                        totalDurationSeconds = (hours * 3600) + (minutes * 60) + seconds
                    }

                    // Hitung progress kompresi
                    val timeMatch = timeRegex.find(currentLine)
                    if (timeMatch != null) {
                        val hours = timeMatch.groupValues[1].toFloatOrNull() ?: 0f
                        val minutes = timeMatch.groupValues[2].toFloatOrNull() ?: 0f
                        val seconds = timeMatch.groupValues[3].toFloatOrNull() ?: 0f
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

    // Parser manual untuk menjaga keutuhan parameter ber-spasi atau berpetik
    private fun parseCommandLine(command: String): List<String> {
        val args = mutableListOf<String>()
        val currentArg = StringBuilder()
        var insideSingleQuotes = false
        var insideDoubleQuotes = false

        for (char in command) {
            when (char) {
                '\'' -> if (!insideDoubleQuotes) insideSingleQuotes = !insideSingleQuotes else currentArg.append(char)
                '"' -> if (!insideSingleQuotes) insideDoubleQuotes = !insideDoubleQuotes else currentArg.append(char)
                ' ' -> {
                    if (insideSingleQuotes || insideDoubleQuotes) {
                        currentArg.append(char)
                    } else if (currentArg.isNotEmpty()) {
                        args.add(currentArg.toString())
                        currentArg.setLength(0)
                    }
                }
                else -> currentArg.append(char)
            }
        }
        if (currentArg.isNotEmpty()) {
            args.add(currentArg.toString())
        }
        return args
    }

    fun cancel() {
        try {
            currentProcess?.destroyForcibly()
            runnerScope?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            currentProcess = null
        }
    }
}
