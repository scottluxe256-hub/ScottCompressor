package com.ffmpeg.compressor.data

import com.ffmpeg.compressor.model.DraftItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object DraftManager {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private const val DRAFTS_DIR_NAME = "ffmpeg_studio_drafts"
    private const val INDEX_FILE_NAME = "drafts_index.json"

    private fun getDraftsDirectory(): File {
        val userHome = System.getProperty("user.home") ?: "."
        val dir = File(userHome, DRAFTS_DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun getIndexFile(): File {
        return File(getDraftsDirectory(), INDEX_FILE_NAME)
    }

    @Synchronized
    fun loadDrafts(): List<DraftItem> {
        val file = getIndexFile()
        if (!file.exists()) return emptyList()
        return try {
            val content = file.readText()
            json.decodeFromString<List<DraftItem>>(content)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @Synchronized
    private fun saveDrafts(drafts: List<DraftItem>) {
        try {
            val content = json.encodeToString(drafts)
            getIndexFile().writeText(content)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Startup background task: Deletes draft video files older than 7 days
     * (System.currentTimeMillis() - creationDate > 7 * 24 * 60 * 60 * 1000).
     */
    fun performAutoCleanup(): Int {
        val currentDrafts = loadDrafts().toMutableList()
        var deletedCount = 0
        val iterator = currentDrafts.iterator()

        while (iterator.hasNext()) {
            val item = iterator.next()
            if (!item.isPermanentlySaved && item.isExpired) {
                try {
                    val file = File(item.draftPath)
                    if (file.exists()) {
                        file.delete()
                    }
                    iterator.remove()
                    deletedCount++
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        saveDrafts(currentDrafts)
        return deletedCount
    }

    /**
     * Save a temporary/encoded file into the Draft storage area (/ffmpeg_studio_drafts/).
     */
    fun createDraft(tempFilePath: String, outputFilename: String): DraftItem {
        val draftsDir = getDraftsDirectory()
        val uniqueId = System.currentTimeMillis().toString() + "_" + (1000..9999).random()
        val targetDraftFile = File(draftsDir, "draft_$uniqueId-$outputFilename")

        try {
            val srcFile = File(tempFilePath)
            if (srcFile.exists()) {
                srcFile.copyTo(targetDraftFile, overwrite = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val newItem = DraftItem(
            id = uniqueId,
            originalPath = tempFilePath,
            draftPath = targetDraftFile.absolutePath,
            outputFilename = outputFilename,
            creationTimestamp = System.currentTimeMillis(),
            isPermanentlySaved = false
        )

        val list = loadDrafts().toMutableList()
        list.add(newItem)
        saveDrafts(list)

        return newItem
    }

    /**
     * Move video from draft storage to permanent storage / user selected gallery folder.
     */
    fun saveDraftPermanently(draftItem: DraftItem, permanentDestinationPath: String): Boolean {
        return try {
            val draftFile = File(draftItem.draftPath)
            val destFile = File(permanentDestinationPath)

            destFile.parentFile?.mkdirs()
            if (draftFile.exists()) {
                draftFile.copyTo(destFile, overwrite = true)
                draftFile.delete()
            }

            val list = loadDrafts().map { item ->
                if (item.id == draftItem.id) {
                    item.copy(isPermanentlySaved = true)
                } else item
            }
            saveDrafts(list)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
