package com.ffmpeg.compressor

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ffmpeg.compressor.engine.AndroidFFmpegRunner
import java.io.File

class MainActivity : ComponentActivity() {
    private val runner = AndroidFFmpegRunner()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkStoragePermission()

        // Ambil langsung path biner libffmpeg.so resmi dari nativeLibraryDir
        val ffmpegPath = File(applicationInfo.nativeLibraryDir, "libffmpeg.so").absolutePath

        setContent {
            // Tampilan utama aplikasi langsung terbuka tanpa loading ekstraksi
            App(
                ffmpegExecutablePath = ffmpegPath,
                runner = runner
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        runner.cancel() // Membersihkan memori RAM & membunuh proses ffmpeg jika app ditutup
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }
}
