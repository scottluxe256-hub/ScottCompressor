package com.ffmpeg.compressor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ffmpeg.compressor.engine.FFmpegRunner
import com.ffmpeg.compressor.ui.components.*
import com.ffmpeg.compressor.ui.theme.DarkBackground
import com.ffmpeg.compressor.ui.theme.FFmpegStudioTheme
import com.ffmpeg.compressor.viewmodel.MainViewModel
import com.ffmpeg.compressor.viewmodel.Screen

@Composable
fun App(
    runner: FFmpegRunner,
    targetOsBadge: String = "Android & Windows Target",
    playerContent: @Composable (String) -> Unit
) {
    val viewModel = remember { MainViewModel() }

    val settings by viewModel.settings.collectAsState()
    val isMultiline by viewModel.isMultilineCommand.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val progressState by viewModel.progressState.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentDraftItem by viewModel.currentDraftItem.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    FFmpegStudioTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = DarkBackground
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    Screen.MAIN_COMPRESSOR -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkBackground)
                        ) {
                            // Header Bar
                            HeaderBar(
                                targetOsBadge = targetOsBadge,
                                onCopyCommand = {
                                    // Copy command to clipboard logic
                                },
                                onDownloadTermuxSh = {
                                    // Export termux script logic
                                },
                                onStartCompression = {
                                    viewModel.startCompression(runner)
                                }
                            )

                            // Quick Preset Cards
                            QuickPresetCards(
                                activePreset = settings.activePreset,
                                onPresetSelect = { preset ->
                                    viewModel.selectPreset(preset)
                                }
                            )

                            // Live CLI Command Preview Box
                            CommandPreviewBox(
                                commandText = viewModel.getFormattedCommand(),
                                isMultiline = isMultiline,
                                onToggleMultiline = { viewModel.toggleMultilineCommand() },
                                onCopyCommand = {
                                    // Copy command logic
                                }
                            )

                            // 6 Parameter Tabs Navigation
                            TabNavigation(
                                activeTab = activeTab,
                                onTabSelected = { viewModel.setActiveTab(it) },
                                settings = settings,
                                onSettingsChange = { updated ->
                                    viewModel.updateSettings { updated }
                                }
                            )
                        }
                    }

                    Screen.RESULT_PREVIEW -> {
                        VideoResultScreen(
                            draftItem = currentDraftItem,
                            onSavePermanently = { destPath ->
                                viewModel.saveDraftPermanently(destPath)
                            },
                            onSaveToDraftAndClose = {
                                viewModel.saveToDraftAndClose()
                            },
                            playerContent = playerContent
                        )
                    }
                }

                // Progress Modal Overlay Dialog when encoding is active
                progressState?.let { progress ->
                    ProgressOverlay(
                        progress = progress,
                        onCancel = { viewModel.cancelCompression(runner) }
                    )
                }
            }
        }
    }
}
