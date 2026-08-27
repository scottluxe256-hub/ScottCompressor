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
fun Tab6OutputFolder(
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
            text = "OUTPUT FILE DESTINATION & DIRECTORY PATH",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryNeonViolet,
                letterSpacing = 1.sp
            )
        )

        // Output Directory Path
        OutlinedTextField(
            value = settings.outputDirectory,
            onValueChange = { onSettingsChange(settings.copy(outputDirectory = it)) },
            label = { Text("Folder Direktori Output") },
            placeholder = { Text("C:/Videos/Output") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Output Filename
        OutlinedTextField(
            value = settings.outputFilename,
            onValueChange = { onSettingsChange(settings.copy(outputFilename = it)) },
            label = { Text("Nama File Output Video") },
            placeholder = { Text("Hasil_Aman_8Bit_H264_v2.mp4") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Full Computed Path Display
        Surface(
            color = DarkSurfaceVariant,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Lokasi Penuh Hasil Output:",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = settings.fullOutputPath,
                    fontSize = 13.sp,
                    color = SecondaryCyberGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Overwrite checkbox (-y)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = true,
                onCheckedChange = {},
                colors = CheckboxDefaults.colors(checkedColor = PrimaryNeonViolet)
            )
            Text("Otomatis Overwrite Jika File Sudah Ada (-y)")
        }
    }
}
