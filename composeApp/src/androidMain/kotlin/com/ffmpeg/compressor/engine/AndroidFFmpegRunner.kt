package com.ffmpeg.compressor.engine

import com.ffmpeg.compressor.model.CompressionSettings
import com.ffmpeg.compressor.model.EncodingProgress
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class AndroidFFmpegRunner : FFmpegRunner {
    private var currentProcess: Process? = null
    private var runnerScope: CoroutineScope? = null

    override fun execute(
        settings: CompressionSettings,
        inputPath: String,
        outputPath: String,
        onProgress: (EncodingProgress) -> Unit
    ) {
        runnerScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        runnerScope?.launch {
            var progress = EncodingProgress(statusText = "Initializing Android Executable Launcher...")
            onProgress(progress)

            try {
                // Determine binary path for Termux / executable launcher context
                val binaryPath = if (File(settings.ffmpegPath).exists()) {
                    settings.ffmpegPath
                } else if (File("/data/data/com.termux/files/usr/bin/ffmpeg").exists()) {
                    "/data/data/com.termux/files/usr/bin/ffmpeg"
                } else {
                    "ffmpeg"
                }

                val args = settings.buildCommandArgsList(inputPath, outputPath).toMutableList()
                args[0] = binaryPath

                val pb = ProcessBuilder(args)
                pb.redirectErrorStream(true)
                val process = pb.start()
                currentProcess = process

                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line ?: break
                    progress = FFmpegLogParser.parseLine(currentLine, progress)
                    withContext(Dispatchers.Main) {
                        onProgress(progress)
                    }
                }

                val exitCode = process.waitFor()
                withContext(Dispatchers.Main) {
                    if (exitCode == 0) {
                        onProgress(
                            progress.copy(
                                percentage = 100f,
                                isFinished = true,
                                statusText = "Android Compression Success!"
                            )
                        )
                    } else {
                        onProgress(
                            progress.copy(
                                isFinished = true,
                                isError = true,
                                errorMessage = "Android FFmpeg process exited with code $exitCode"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onProgress(
                        progress.copy(
                            isFinished = true,
                            isError = true,
                            errorMessage = e.message ?: "Failed to execute Android FFmpeg process"
                        )
                    )
                }
            }
        }
    }

    override fun cancel() {
        try {
            currentProcess?.destroyForcibly()
            runnerScope?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
