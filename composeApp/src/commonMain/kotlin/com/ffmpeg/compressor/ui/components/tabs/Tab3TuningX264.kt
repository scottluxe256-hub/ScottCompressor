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

@Composable
fun Tab3TuningX264(
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
            text = "ADVANCED TUNING X264 PARAMETERS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryNeonViolet,
                letterSpacing = 1.sp
            )
        )

        // Ref & Bframes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.ref.toString(),
                onValueChange = { onSettingsChange(settings.copy(ref = it.toIntOrNull() ?: 4)) },
                label = { Text("Reference Frames (ref)") },
                placeholder = { Text("4") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.bframes.toString(),
                onValueChange = { onSettingsChange(settings.copy(bframes = it.toIntOrNull() ?: 3)) },
                label = { Text("B-Frames (bframes)") },
                placeholder = { Text("3") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Motion Estimation & AQ Mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.me,
                onValueChange = { onSettingsChange(settings.copy(me = it)) },
                label = { Text("Motion Estimation (me)") },
                placeholder = { Text("umh") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.aqMode.toString(),
                onValueChange = { onSettingsChange(settings.copy(aqMode = it.toIntOrNull() ?: 2)) },
                label = { Text("AQ Mode (aq-mode)") },
                placeholder = { Text("2") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // AQ Strength & Chroma QP Offset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.aqStrength.toString(),
                onValueChange = { onSettingsChange(settings.copy(aqStrength = it.toFloatOrNull() ?: 1.1f)) },
                label = { Text("AQ Strength (aq-strength)") },
                placeholder = { Text("1.1") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.chromaQpOffset.toString(),
                onValueChange = { onSettingsChange(settings.copy(chromaQpOffset = it.toIntOrNull() ?: -2)) },
                label = { Text("Chroma QP Offset") },
                placeholder = { Text("-2") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Psy-RD & Deblock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = settings.psyRd,
                onValueChange = { onSettingsChange(settings.copy(psyRd = it)) },
                label = { Text("Psy-RD (psy-rd)") },
                placeholder = { Text("1.0,0.0") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = settings.deblock,
                onValueChange = { onSettingsChange(settings.copy(deblock = it)) },
                label = { Text("Deblock Filter") },
                placeholder = { Text("-2,-1") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Checkboxes for no-fast-pskip, mbtree, scenecut
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Checkbox(
                    checked = settings.noFastPskip == 1,
                    onCheckedChange = { onSettingsChange(settings.copy(noFastPskip = if (it) 1 else 0)) },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryNeonViolet)
                )
                Text("no-fast-pskip=1")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Checkbox(
                    checked = settings.mbtree == 1,
                    onCheckedChange = { onSettingsChange(settings.copy(mbtree = if (it) 1 else 0)) },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryNeonViolet)
                )
                Text("mbtree=1")
            }

            OutlinedTextField(
                value = settings.scenecut.toString(),
                onValueChange = { onSettingsChange(settings.copy(scenecut = it.toIntOrNull() ?: 0)) },
                label = { Text("Scenecut") },
                placeholder = { Text("0") },
                singleLine = true,
                modifier = Modifier.width(120.dp)
            )
        }
    }
}
