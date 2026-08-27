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

        // Jika file belum ada atau ukurannya 0, salin dari assets
        if (!ffmpegFile.exists() || ffmpegFile.length() == 0L) {
            context.assets.open("ffmpeg").use { input ->
                FileOutputStream(ffmpegFile).use { output ->
                    input.copyTo(output)
                }
            }
        }

        // Beri izin eksekusi secara eksplisit (Owner & World executable)
        ffmpegFile.setReadable(true, false)
        ffmpegFile.setWritable(true, true)
        val isExecutable = ffmpegFile.setExecutable(true, false)

        // Fallback jika Java File API gagal mengubah izin di Android
        if (!isExecutable) {
            try {
                Runtime.getRuntime().exec("chmod 777 ${ffmpegFile.absolutePath}").waitFor()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return ffmpegFile.absolutePath
    }
}
