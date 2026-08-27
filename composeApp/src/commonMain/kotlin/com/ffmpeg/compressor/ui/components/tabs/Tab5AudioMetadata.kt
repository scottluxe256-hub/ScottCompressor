package com.ffmpeg.compressor.ui.components.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ffmpeg.compressor.model.CompressionSettings
import com.ffmpeg.compressor.ui.theme.*

@Composable
fun Tab5AudioMetadata(
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
            text = "AUDIO CODEC, FILTERS & VIDEO METADATA",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryNeonViolet,
                letterSpacing = 1.sp
            )
        )

        // Audio Codec & Profile
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.audioCodec,
                onValueChange = { onSettingsChange(settings.copy(audioCodec = it)) },
                label = { Text("Audio Codec (-c:a)") },
                placeholder = { Text("libfdk_aac") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.audioProfile,
                onValueChange = { onSettingsChange(settings.copy(audioProfile = it)) },
                label = { Text("Audio Profile (-profile:a)") },
                placeholder = { Text("aac_he") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Audio Bitrate & Audio Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.audioBitrate,
                onValueChange = { onSettingsChange(settings.copy(audioBitrate = it)) },
                label = { Text("Audio Bitrate (-b:a)") },
                placeholder = { Text("64k") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.audioFilter,
                onValueChange = { onSettingsChange(settings.copy(audioFilter = it)) },
                label = { Text("Audio Filter (-af)") },
                placeholder = { Text("aresample=async=1000") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Timescale & Metadata Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.timescale.toString(),
                onValueChange = { onSettingsChange(settings.copy(timescale = it.toIntOrNull() ?: 60000)) },
                label = { Text("Track Timescale") },
                placeholder = { Text("60000") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.metadataTitle,
                onValueChange = { onSettingsChange(settings.copy(metadataTitle = it)) },
                label = { Text("Metadata Title (-metadata)") },
                placeholder = { Text("TikTok 8Bit Safe Color H264") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Movflags
        OutlinedTextField(
            value = settings.movflags,
            onValueChange = { onSettingsChange(settings.copy(movflags = it)) },
            label = { Text("Movflags (-movflags)") },
            placeholder = { Text("+faststart") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
