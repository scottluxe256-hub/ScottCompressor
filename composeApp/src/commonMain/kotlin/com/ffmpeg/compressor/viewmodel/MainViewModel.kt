package com.ffmpeg.compressor.viewmodel

import com.ffmpeg.compressor.data.DraftManager
import com.ffmpeg.compressor.engine.FFmpegRunner
import com.ffmpeg.compressor.model.CompressionPreset
import com.ffmpeg.compressor.model.CompressionSettings
import com.ffmpeg.compressor.model.DraftItem
import com.ffmpeg.compressor.model.EncodingProgress
import com.ffmpeg.compressor.model.PresetType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

enum class Screen {
    MAIN_COMPRESSOR,
    RESULT_PREVIEW
}

class MainViewModel {
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _settings = MutableStateFlow(CompressionSettings.getPresetSettings(PresetType.TIKTOK_8BIT_SAFE))
    val settings: StateFlow<CompressionSettings> = _settings.asStateFlow()

    private val _isMultilineCommand = MutableStateFlow(false)
    val isMultilineCommand: StateFlow<Boolean> = _isMultilineCommand.asStateFlow()

    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _progressState = MutableStateFlow<EncodingProgress?>(null)
    val progressState: StateFlow<EncodingProgress?> = _progressState.asStateFlow()

    private val _currentScreen = MutableStateFlow(Screen.MAIN_COMPRESSOR)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentDraftItem = MutableStateFlow<DraftItem?>(null)
    val currentDraftItem: StateFlow<DraftItem?> = _currentDraftItem.asStateFlow()

    private val _cleanedDraftsCount = MutableStateFlow(0)
    val cleanedDraftsCount: StateFlow<Int> = _cleanedDraftsCount.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        // Run 7-day auto-cleanup background task on app startup
        scope.launch(Dispatchers.IO) {
            val count = DraftManager.performAutoCleanup()
            _cleanedDraftsCount.value = count
        }
    }

    fun updateSettings(transform: (CompressionSettings) -> CompressionSettings) {
        _settings.value = transform(_settings.value)
    }

    fun selectPreset(preset: PresetType) {
        val newSettings = CompressionSettings.getPresetSettings(preset)
        _settings.value = newSettings
    }

    fun toggleMultilineCommand() {
        _isMultilineCommand.value = !_isMultilineCommand.value
    }

    fun setActiveTab(index: Int) {
        _activeTab.value = index
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun getFormattedCommand(): String {
        val singleLine = _settings.value.buildCommandString()
        return if (_isMultilineCommand.value) {
            singleLine.replace(" -", " \\\n  -")
        } else {
            singleLine
        }
    }

    fun startCompression(runner: FFmpegRunner) {
        val current = _settings.value
        val inputPath = current.inputVideo
        val outputPath = current.fullOutputPath

        _progressState.value = EncodingProgress(statusText = "Starting FFmpeg Engine...")

        runner.execute(
            settings = current,
            inputPath = inputPath,
            outputPath = outputPath,
            onProgress = { progress ->
                _progressState.value = progress
                if (progress.isFinished && !progress.isError) {
                    // Create draft record automatically upon completion
                    val draft = DraftManager.createDraft(outputPath, current.outputFilename)
                    _currentDraftItem.value = draft
                    _progressState.value = null
                    _currentScreen.value = Screen.RESULT_PREVIEW
                }
            }
        )
    }

    fun cancelCompression(runner: FFmpegRunner) {
        runner.cancel()
        _progressState.value = null
    }

    fun saveDraftPermanently(destinationPath: String) {
        val draft = _currentDraftItem.value
        if (draft != null) {
            val success = DraftManager.saveDraftPermanently(draft, destinationPath)
            if (success) {
                _userMessage.value = "Video successfully saved permanently to: $destinationPath"
            } else {
                _userMessage.value = "Failed to save file to destination path."
            }
        }
        _currentScreen.value = Screen.MAIN_COMPRESSOR
    }

    fun saveToDraftAndClose() {
        _userMessage.value = "Video saved to Drafts folder. Will be auto-purged after 7 days if un-saved."
        _currentScreen.value = Screen.MAIN_COMPRESSOR
    }
}
