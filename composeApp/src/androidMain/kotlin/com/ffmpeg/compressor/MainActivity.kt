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

        // Hapus file lama jika ada agar izin/permission ter-reset bersih
        if (ffmpegFile.exists()) {
            ffmpegFile.delete()
        }

        // Salin biner dari assets ke internal storage
        context.assets.open("ffmpeg").use { input ->
            FileOutputStream(ffmpegFile).use { output ->
                input.copyTo(output)
            }
        }

        // Atur izin: Executable = true, ReadOnly = true (Non-writable wajib untuk Android 10+)
        ffmpegFile.setReadable(true, false)
        ffmpegFile.setExecutable(true, false)
        ffmpegFile.setWritable(false, false) // Kunci file agar tidak writable

        // Execute chmod via shell sebagai fallback
        try {
            Runtime.getRuntime().exec("chmod 555 ${ffmpegFile.absolutePath}").waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ffmpegFile.absolutePath
    }
}
