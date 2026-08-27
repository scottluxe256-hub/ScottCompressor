package com.ffmpeg.compressor.ui.components

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
import com.ffmpeg.compressor.model.DraftItem
import com.ffmpeg.compressor.ui.theme.*

@Composable
fun VideoResultScreen(
    draftItem: DraftItem?,
    onSavePermanently: (String) -> Unit,
    onSaveToDraftAndClose: () -> Unit,
    playerContent: @Composable (String) -> Unit
) {
    val filePath = draftItem?.draftPath ?: ""
    val filename = draftItem?.outputFilename ?: "output.mp4"

    var destinationDirectory by remember { mutableStateOf("C:/Videos/Saved") }
    var isPlaying by remember { mutableStateOf(true) }
    var currentSeek by remember { mutableStateOf(0.3f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Title Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🎉 Kompresi Selesai! Pratinjau Video",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = SecondaryCyberGreen,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "File: $filename",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }

            Surface(
                color = WarningAmber.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "🕒 Autodelete Draft 7 Hari",
                    color = WarningAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Interactive Video Player Container
        Surface(
            color = Color.Black,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 14.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Platform Video Player Embed (ExoPlayer / Platform player)
                playerContent(filePath)

                // Overlay Custom Compose Player Controller UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(1.dp))

                    // Center Play / Pause Indicator
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(56.dp)
                            .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(28.dp))
                    ) {
                        Text(
                            text = if (isPlaying) "⏸" else "▶",
                            color = Color.White,
                            fontSize = 24.sp
                        )
                    }

                    // Bottom Timeline Controller Overlay
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("00:00:18", color = Color.White, fontSize = 11.sp)
                            Text("00:01:00", color = TextSecondary, fontSize = 11.sp)
                        }
                        Slider(
                            value = currentSeek,
                            onValueChange = { currentSeek = it },
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryNeonViolet,
                                activeTrackColor = PrimaryNeonViolet
                            )
                        )
                    }
                }
            }
        }

        // Target Permanent Path Selection Input
        OutlinedTextField(
            value = destinationDirectory,
            onValueChange = { destinationDirectory = it },
            label = { Text("Pilih Folder Galeri / Storage Permanen") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Two Action Buttons at the Bottom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Button 1: Save Permanently
            Button(
                onClick = {
                    val finalPath = destinationDirectory.trimEnd('/', '\\') + "/" + filename
                    onSavePermanently(finalPath)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryCyberGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(
                    text = "💾 SIMPAN PERMANEN",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // Button 2: Cancel / Save to Draft
            OutlinedButton(
                onClick = onSaveToDraftAndClose,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(
                    text = "📁 BATAL / SIMPAN KE DRAFT (7 HARI)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}
