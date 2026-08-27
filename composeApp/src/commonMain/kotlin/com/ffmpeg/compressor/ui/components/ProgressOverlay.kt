package com.ffmpeg.compressor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ffmpeg.compressor.model.EncodingProgress
import com.ffmpeg.compressor.ui.theme.*

@Composable
fun ProgressOverlay(
    progress: EncodingProgress,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            color = DarkSurface,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header & Title
                Text(
                    text = "PROCESS COMPRESSION ENCODING",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNeonViolet,
                        fontSize = 15.sp
                    )
                )

                // Circular Ring Progress Indicator (%)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { progress.percentage / 100f },
                        modifier = Modifier.fillMaxSize(),
                        color = SecondaryCyberGreen,
                        strokeWidth = 10.dp,
                        trackColor = DarkSurfaceVariant,
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${progress.percentage.toInt()}%",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 22.sp
                            )
                        )
                        Text(
                            text = "ETA: ${progress.formattedTimeRemaining}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Status & Linear Progress Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = progress.statusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Elapsed: ${progress.formattedTimeElapsed}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progress.percentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = PrimaryNeonViolet,
                        trackColor = DarkSurfaceVariant
                    )
                }

                // Real-time Metrics Card Grid (Speed, FPS, Bitrate, Frame)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(title = "SPEED", value = progress.speed, modifier = Modifier.weight(1f))
                    MetricCard(title = "FPS", value = "${progress.fps} fps", modifier = Modifier.weight(1f))
                    MetricCard(title = "BITRATE", value = progress.bitrate, modifier = Modifier.weight(1f))
                    MetricCard(title = "FRAME", value = "${progress.frame}", modifier = Modifier.weight(1f))
                }

                // Live Console Terminal Log Box with auto-scroll bottom
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(TerminalBackground, shape = RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "LIVE STDERR CONSOLE STREAM",
                        color = TerminalText.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val vertScroll = rememberScrollState()
                    LaunchedEffect(progress.consoleLog.size) {
                        vertScroll.animateScrollTo(vertScroll.maxValue)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(vertScroll)
                    ) {
                        progress.consoleLog.takeLast(50).forEach { line ->
                            Text(
                                text = line,
                                color = TerminalText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // Tombol Batal
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✖ Batal & Hentikan Kompresi", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 11.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
        }
    }
}
