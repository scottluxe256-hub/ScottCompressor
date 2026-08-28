package com.ffmpeg.compressor.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

object FFmpegExtractor {

    suspend fun extractIfNeeded(
        context: Context,
        zipFileName: String = "ffmpeg.zip",
        onProgress: (progress: Float, status: String) -> Unit
    ): String = withContext(Dispatchers.IO) {
        // Nama file target di internal storage diset jadi 'ffmpeg'
        val targetFile = File(context.filesDir, "ffmpeg")

        if (targetFile.exists() && targetFile.length() > 10 * 1024 * 1024) {
            return@withContext targetFile.absolutePath
        }

        withContext(Dispatchers.Main) {
            onProgress(0f, "Menyiapkan ekstraksi file biner...")
        }

        var totalBytes = 0L
        try {
            context.assets.open(zipFileName).use { input ->
                ZipInputStream(input).use { zipInput ->
                    var entry = zipInput.nextEntry
                    while (entry != null) {
                        if (entry.size > 0) totalBytes += entry.size
                        entry = zipInput.nextEntry
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (totalBytes <= 0) totalBytes = 30 * 1024 * 1024L

        context.assets.open(zipFileName).use { input ->
            ZipInputStream(input).use { zipInput ->
                var entry = zipInput.nextEntry
                while (entry != null) {
                    // Cek nama entry file di dalam zip 'ffmpeg'
                    if (entry.name == "ffmpeg" || entry.name.endsWith("ffmpeg")) {
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var extractedBytes = 0L

                        FileOutputStream(targetFile).use { output ->
                            while (zipInput.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                extractedBytes += bytesRead

                                val progress = ((extractedBytes.toFloat() / totalBytes.toFloat()) * 100f).coerceIn(0f, 99f)
                                withContext(Dispatchers.Main) {
                                    onProgress(progress, "Mengekstrak Biner FFmpeg... (${progress.toInt()}%)")
                                }
                            }
                        }
                        break
                    }
                    entry = zipInput.nextEntry
                }
            }
        }

        // Beri izin eksekusi penuh (chmod +x)
        targetFile.setExecutable(true, false)

        withContext(Dispatchers.Main) {
            onProgress(100f, "Ekstraksi Selesai!")
        }

        return@withContext targetFile.absolutePath
    }
}
