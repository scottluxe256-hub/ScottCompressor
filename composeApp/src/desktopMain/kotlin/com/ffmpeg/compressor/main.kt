package com.ffmpeg.compressor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.ffmpeg.compressor.engine.DesktopFFmpegRunner

fun main() = application {
    val windowState = rememberWindowState(width = 1200.dp, height = 820.dp)

    Window(
        onCloseRequest = ::exitApplication,
        title = "FFmpeg Compressor Studio - Windows Target",
        state = windowState
    ) {
        val runner = remember { DesktopFFmpegRunner() }

        App(
            runner = runner,
            targetOsBadge = "Windows Target",
            playerContent = { filePath ->
                // Desktop Video Player Surface View Component
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎬 Desktop LibVLC Player Preview",
                            color = Color.White,
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Playing: $filePath",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        )
    }
}
