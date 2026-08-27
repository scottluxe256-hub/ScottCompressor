package com.ffmpeg.compressor

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.ffmpeg.compressor.engine.AndroidFFmpegRunner
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Salin biner FFmpeg dari Assets ke Internal Storage & beri izin eksekusi
        val ffmpegPath = prepareFFmpegBinary(this)

        setContent {
            // Oper path ffmpeg ke runner
            val runner = remember { AndroidFFmpegRunner(ffmpegPath) }

            App(
                runner = runner,
                targetOsBadge = "Android Target",
                playerContent = { filePath ->
                    val context = this@MainActivity
                    val exoPlayer = remember(filePath) {
                        ExoPlayer.Builder(context).build().apply {
                            if (filePath.isNotBlank()) {
                                val mediaItem = MediaItem.fromUri(Uri.parse(filePath))
                                setMediaItem(mediaItem)
                                prepare()
                                playWhenReady = true
                            }
                        }
                    }

                    DisposableEffect(exoPlayer) {
                        onDispose {
                            exoPlayer.release()
                        }
                    }

                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false // Custom Compose overlay handles controls
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
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

        // Beri izin eksekusi di penyimpanan internal aplikasi
        ffmpegFile.setExecutable(true, false)

        return ffmpegFile.absolutePath
    }
}
