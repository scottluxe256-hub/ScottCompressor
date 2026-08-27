package com.ffmpeg.compressor

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ffmpeg.compressor.engine.AndroidFFmpegRunner
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ffmpegAbsolutePath = prepareFFmpegBinary(this)

        setContent {
            App(
                ffmpegExecutablePath = ffmpegAbsolutePath,
                runner = AndroidFFmpegRunner()
            )
        }
    }

    private fun prepareFFmpegBinary(context: Context): String {
        val ffmpegFile = File(context.filesDir, "ffmpeg")

        if (!ffmpegFile.exists()) {
            context.assets.open("ffmpeg").use { input ->
                FileOutputStream(ffmpegFile).use { output ->
                    input.copyTo(output)
                }
            }
        }

        ffmpegFile.setExecutable(true, false)
        return ffmpegFile.absolutePath
    }
}
