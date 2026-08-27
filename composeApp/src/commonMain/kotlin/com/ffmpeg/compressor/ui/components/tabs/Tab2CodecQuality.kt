package com.ffmpeg.compressor.ui.components.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ffmpeg.compressor.model.CompressionSettings
import com.ffmpeg.compressor.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tab2CodecQuality(
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
            text = "VIDEO CODEC, PRESET & QUALITY RATE CONTROL",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryNeonViolet,
                letterSpacing = 1.sp
            )
        )

        // Codec & Preset Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.videoCodec,
                onValueChange = { onSettingsChange(settings.copy(videoCodec = it)) },
                label = { Text("Video Codec (-c:v)") },
                placeholder = { Text("libx264") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.preset,
                onValueChange = { onSettingsChange(settings.copy(preset = it)) },
                label = { Text("Preset (-preset)") },
                placeholder = { Text("slow") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // CRF Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Constant Rate Factor (CRF): ${settings.crf}",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (settings.crf <= 18) "Ultra HQ" else if (settings.crf <= 23) "Balanced" else "Low Bitrate",
                    color = SecondaryCyberGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = settings.crf.toFloat(),
                onValueChange = { onSettingsChange(settings.copy(crf = it.toInt())) },
                valueRange = 0f..51f,
                steps = 51,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryNeonViolet,
                    activeTrackColor = PrimaryNeonViolet
                )
            )
        }

        // Frame Rate & Pixel Format
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.frameRate.toString(),
                onValueChange = { onSettingsChange(settings.copy(frameRate = it.toIntOrNull() ?: 60)) },
                label = { Text("Frame Rate (-r)") },
                placeholder = { Text("60") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.pixelFormat,
                onValueChange = { onSettingsChange(settings.copy(pixelFormat = it)) },
                label = { Text("Pixel Format (-pix_fmt)") },
                placeholder = { Text("yuv420p") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Profile & Level
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.profile,
                onValueChange = { onSettingsChange(settings.copy(profile = it)) },
                label = { Text("Profile (-profile:v)") },
                placeholder = { Text("high") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.level,
                onValueChange = { onSettingsChange(settings.copy(level = it)) },
                label = { Text("Level (-level:v)") },
                placeholder = { Text("4.2") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // GOP & Min Keyint
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.gopSize.toString(),
                onValueChange = { onSettingsChange(settings.copy(gopSize = it.toIntOrNull() ?: 60)) },
                label = { Text("GOP Size (-g)") },
                placeholder = { Text("60") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.minKeyint.toString(),
                onValueChange = { onSettingsChange(settings.copy(minKeyint = it.toIntOrNull() ?: 30)) },
                label = { Text("Min Keyint (-keyint_min)") },
                placeholder = { Text("30") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
