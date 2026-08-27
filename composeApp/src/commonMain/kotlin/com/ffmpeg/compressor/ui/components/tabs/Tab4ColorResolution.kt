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
fun Tab4ColorResolution(
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
            text = "COLOR SPACE, TRC & RESOLUTION SCALING",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryNeonViolet,
                letterSpacing = 1.sp
            )
        )

        // Color Primaries & TRC
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.colorPrimaries,
                onValueChange = { onSettingsChange(settings.copy(colorPrimaries = it)) },
                label = { Text("Color Primaries") },
                placeholder = { Text("bt709") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.colorTrc,
                onValueChange = { onSettingsChange(settings.copy(colorTrc = it)) },
                label = { Text("Color TRC") },
                placeholder = { Text("bt709") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Colorspace & SWS Flags
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.colorspace,
                onValueChange = { onSettingsChange(settings.copy(colorspace = it)) },
                label = { Text("Colorspace") },
                placeholder = { Text("bt709") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.swsFlags,
                onValueChange = { onSettingsChange(settings.copy(swsFlags = it)) },
                label = { Text("SWS Flags (-sws_flags)") },
                placeholder = { Text("lanczos+accurate_rnd") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // VF Video Filter (PTS & Scaling)
        OutlinedTextField(
            value = settings.vfFilter,
            onValueChange = { onSettingsChange(settings.copy(vfFilter = it)) },
            label = { Text("Video Filter (-vf)") },
            placeholder = { Text("setpts=N/(60*TB),scale='if(gt(iw,ih),-2,720)':'if(gt(iw,ih),720,-2)'") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryNeonViolet,
                unfocusedBorderColor = DarkSurfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
