package com.ffmpeg.compressor

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.ffmpeg.compressor.engine.AndroidFFmpegRunner
import com.ffmpeg.compressor.utils.FFmpegExtractor
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val runner = AndroidFFmpegRunner()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkStoragePermission()

        setContent {
            var ffmpegExecutablePath by remember { mutableStateOf<String?>(null) }
            var extractProgress by remember { mutableStateOf(0f) }
            var extractStatus by remember { mutableStateOf("Memeriksa komponen...") }
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    // Ekstrak ffmpeg dari assets ke filesDir internal
                    val path = FFmpegExtractor.extractIfNeeded(applicationContext) { progress, status ->
                        extractProgress = progress
                        extractStatus = status
                    }
                    ffmpegExecutablePath = path
                }
            }

            if (ffmpegExecutablePath == null) {
                // Layar loading indikator ekstraksi zip
                ExtractionLoadingScreen(
                    progress = extractProgress,
                    statusText = extractStatus
                )
            } else {
                // Tampilan utama aplikasi setelah biner ffmpeg siap
                App(
                    ffmpegExecutablePath = ffmpegExecutablePath!!,
                    runner = runner
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        runner.cancel() // Pembersihan RAM & pembunuhan proses ffmpeg saat app ditutup
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
