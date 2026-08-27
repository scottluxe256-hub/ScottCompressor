package com.ffmpeg.compressor.ui.components.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ffmpeg.compressor.model.CompressionSettings
import com.ffmpeg.compressor.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tab1InputDecoder(
    settings: CompressionSettings,
    onSettingsChange: (CompressionSettings) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "INPUT VIDEO & HARDWARE DECODER CONFIGURATION",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryNeonViolet,
                letterSpacing = 1.sp
            )
        )

        // FFmpeg Executable Binary Path
        OutlinedTextField(
            value = settings.ffmpegPath,
            onValueChange = { onSettingsChange(settings.copy(ffmpegPath = it)) },
            label = { Text("Path Executable FFmpeg Lokal") },
            placeholder = { Text("ffmpeg/bin/ffmpeg") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryNeonViolet,
                unfocusedBorderColor = DarkSurfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Primary Input Video (-i)
        OutlinedTextField(
            value = settings.inputVideo,
            onValueChange = { onSettingsChange(settings.copy(inputVideo = it)) },
            label = { Text("Input Video Utama (-i)") },
            placeholder = { Text("[input_video]") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryNeonViolet,
                unfocusedBorderColor = DarkSurfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Input Video Hardware Decoder (-c:v)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = settings.useInputDecoder,
                    onCheckedChange = { onSettingsChange(settings.copy(useInputDecoder = it)) },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryNeonViolet)
                )
                Text("Gunakan Hardware Video Decoder (-c:v)")
            }

            if (settings.useInputDecoder) {
                OutlinedTextField(
                    value = settings.inputDecoder,
                    onValueChange = { onSettingsChange(settings.copy(inputDecoder = it)) },
                    label = { Text("Decoder Name") },
                    placeholder = { Text("h264_mediacodec") },
                    singleLine = true,
                    modifier = Modifier.width(220.dp)
                )
            }
        }

        Divider(color = DarkSurfaceVariant)

        // Separate Audio File Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = settings.useSeparateAudio,
                    onCheckedChange = { onSettingsChange(settings.copy(useSeparateAudio = it)) },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryNeonViolet)
                )
                Text("Gunakan Input Audio Terpisah (-i audio)")
            }
        }

        if (settings.useSeparateAudio) {
            OutlinedTextField(
                value = settings.inputAudio,
                onValueChange = { onSettingsChange(settings.copy(inputAudio = it)) },
                label = { Text("Input File Audio (-i)") },
                placeholder = { Text("[input_audio]") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Stream Mapping (-map)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.streamMapVideo,
                onValueChange = { onSettingsChange(settings.copy(streamMapVideo = it)) },
                label = { Text("Stream Map Video (-map)") },
                placeholder = { Text("0:v:0") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.streamMapAudio,
                onValueChange = { onSettingsChange(settings.copy(streamMapAudio = it)) },
                label = { Text("Stream Map Audio (-map)") },
                placeholder = { Text("1:a:0") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
