package com.ffmpeg.compressor.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ffmpeg.compressor.model.CompressionSettings
import com.ffmpeg.compressor.ui.components.tabs.*
import com.ffmpeg.compressor.ui.theme.*

@Composable
fun TabNavigation(
    activeTab: Int,
    onTabSelected: (Int) -> Unit,
    settings: CompressionSettings,
    onSettingsChange: (CompressionSettings) -> Unit
) {
    val tabTitles = listOf(
        "1. Input & Decoder",
        "2. Codec & Quality",
        "3. Tuning x264",
        "4. Color & Scale",
        "5. Audio & Meta",
        "6. Output Folder"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = DarkSurface,
            contentColor = PrimaryNeonViolet,
            edgePadding = 0.dp
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            color = if (activeTab == index) PrimaryNeonViolet else TextSecondary
                        )
                    }
                )
            }
        }

        Surface(
            color = DarkSurface,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 10.dp)
        ) {
            when (activeTab) {
                0 -> Tab1InputDecoder(settings = settings, onSettingsChange = onSettingsChange)
                1 -> Tab2CodecQuality(settings = settings, onSettingsChange = onSettingsChange)
                2 -> Tab3TuningX264(settings = settings, onSettingsChange = onSettingsChange)
                3 -> Tab4ColorResolution(settings = settings, onSettingsChange = onSettingsChange)
                4 -> Tab5AudioMetadata(settings = settings, onSettingsChange = onSettingsChange)
                5 -> Tab6OutputFolder(settings = settings, onSettingsChange = onSettingsChange)
            }
        }
    }
}
