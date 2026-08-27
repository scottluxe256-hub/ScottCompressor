package com.ffmpeg.compressor.engine

import com.ffmpeg.compressor.model.CompressionSettings
import com.ffmpeg.compressor.model.EncodingProgress
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class DesktopFFmpegRunner : FFmpegRunner {
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
            var progress = EncodingProgress(statusText = "Launching Desktop FFmpeg Process...")
            onProgress(progress)

            try {
                // Determine FFmpeg executable path
                val relativeFfmpeg = File(settings.ffmpegPath)
                val relativeExeFfmpeg = File(settings.ffmpegPath + ".exe")
                val binaryPath = when {
                    relativeFfmpeg.exists() -> relativeFfmpeg.absolutePath
                    relativeExeFfmpeg.exists() -> relativeExeFfmpeg.absolutePath
                    File("ffmpeg/bin/ffmpeg.exe").exists() -> File("ffmpeg/bin/ffmpeg.exe").absolutePath
                    File("ffmpeg/bin/ffmpeg").exists() -> File("ffmpeg/bin/ffmpeg").absolutePath
                    else -> "ffmpeg" // System fallback
                }

                val args = settings.buildCommandArgsList(inputPath, outputPath).toMutableList()
                args[0] = binaryPath

                val pb = ProcessBuilder(args)
                pb.redirectErrorStream(true) // Merge stderr into stdout stream for unified reading
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
                                statusText = "Compression Completed Successfully!"
                            )
                        )
                    } else {
                        onProgress(
                            progress.copy(
                                isFinished = true,
                                isError = true,
                                errorMessage = "FFmpeg process exited with code $exitCode"
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
                            errorMessage = e.message ?: "Failed to execute FFmpeg process"
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
