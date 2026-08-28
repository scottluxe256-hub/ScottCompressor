package com.ffmpeg.compressor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ffmpeg.compressor.engine.AndroidFFmpegRunner

@Composable
fun App(
    ffmpegExecutablePath: String,
    runner: AndroidFFmpegRunner
) {
    var rawCommand by remember { 
        mutableStateOf("-i /sdcard/input.mp4 -c:v libx264 -crf 28 /sdcard/output.mp4") 
    }
    var isProcessing by remember { mutableStateOf(false) }
    var progressPercentage by remember { mutableStateOf(0f) }
    var statusText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF121212)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "FFmpeg Executor Minimalis",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = rawCommand,
                        onValueChange = { rawCommand = it },
                        label = { Text("Isi Perintah FFmpeg") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        enabled = !isProcessing
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            isProcessing = true
                            isError = false
                            runner.executeRawCommand(
                                ffmpegPath = ffmpegExecutablePath,
                                rawCommand = rawCommand
                            ) { progress, status, finished, error ->
                                progressPercentage = progress
                                statusText = status
                                isError = error
                                if (finished) {
                                    isProcessing = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isProcessing && rawCommand.isNotBlank(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Mulai Kompresi", fontSize = 16.sp)
                    }

                    if (statusText.isNotBlank() && !isProcessing) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = statusText,
                            color = if (isError) Color.Red else Color.Green,
                            fontSize = 14.sp
                        )
                    }
                }

                // Overlay Loading Kompresi
                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Proses Ekspor",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                LinearProgressIndicator(
                                    progress = { progressPercentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = Color(0xFFBB86FC),
                                    trackColor = Color.DarkGray
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "${progressPercentage.toInt()}%",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = statusText,
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        runner.cancel()
                                        isProcessing = false
                                        statusText = "Dibatalkan oleh pengguna"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                                ) {
                                    Text("Batal & Hentikan")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Layar Loading Ekstraksi Zip (Dipakai di MainActivity saat pertama kali buka app)
@Composable
fun ExtractionLoadingScreen(
    progress: Float,
    statusText: String
) {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF121212)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Menyiapkan Komponen Native",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color(0xFF00E676),
                            trackColor = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${progress.toInt()}%",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}
