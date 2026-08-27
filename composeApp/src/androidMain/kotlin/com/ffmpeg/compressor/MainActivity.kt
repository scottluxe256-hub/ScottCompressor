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
        // Gunakan codeCacheDir agar terhindar dari pemblokiran exec SELinux Android
        val ffmpegFile = File(context.codeCacheDir, "ffmpeg")

        if (ffmpegFile.exists()) {
            ffmpegFile.delete()
        }

        context.assets.open("ffmpeg").use { input ->
            FileOutputStream(ffmpegFile).use { output ->
                input.copyTo(output)
            }
        }

        // Set izin eksekusi menggunakan File API & fallback shell
        ffmpegFile.setReadable(true, false)
        ffmpegFile.setExecutable(true, false)

        try {
            Runtime.getRuntime().exec("chmod 755 ${ffmpegFile.absolutePath}").waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ffmpegFile.absolutePath
    }
}
